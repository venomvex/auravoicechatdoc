/**
 * Events Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { query } from '../config/database.config';
import { AppError } from '../middleware/errorHandler';
import { logger } from '../utils/logger';

// Get all active events
export const getEvents = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { type, status = 'active' } = req.query;

    let queryText = 'SELECT * FROM events WHERE status = $1';
    const params: any[] = [status];

    if (type) {
      queryText += ' AND type = $2';
      params.push(type);
    }

    queryText += ' ORDER BY start_date ASC';

    const result = await query(queryText, params);

    res.json({ events: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get event details
export const getEvent = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { eventId } = req.params;

    const result = await query('SELECT * FROM events WHERE id = $1', [eventId]);

    if (result.rows.length === 0) {
      throw new AppError('Event not found', 404, 'EVENT_NOT_FOUND');
    }

    res.json({ event: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get event leaderboard
export const getEventLeaderboard = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { eventId } = req.params;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT ep.*, u.username, u.display_name, u.avatar_url
       FROM event_participants ep
       JOIN users u ON ep.user_id = u.id
       WHERE ep.event_id = $1
       ORDER BY ep.score DESC, ep.updated_at ASC
       LIMIT $2 OFFSET $3`,
      [eventId, limit, offset]
    );

    res.json({ leaderboard: result.rows });
  } catch (error) {
    next(error);
  }
};

// Join event
export const joinEvent = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { eventId } = req.params;

    // Check if event exists and is active
    const eventResult = await query(
      'SELECT * FROM events WHERE id = $1 AND status = $2 AND start_date <= NOW() AND end_date >= NOW()',
      [eventId, 'active']
    );

    if (eventResult.rows.length === 0) {
      throw new AppError('Event not available', 400, 'EVENT_NOT_AVAILABLE');
    }

    // Check if already joined
    const existingResult = await query(
      'SELECT * FROM event_participants WHERE event_id = $1 AND user_id = $2',
      [eventId, userId]
    );

    if (existingResult.rows.length > 0) {
      throw new AppError('Already joined event', 400, 'ALREADY_JOINED');
    }

    // Join event
    const result = await query(
      `INSERT INTO event_participants (event_id, user_id, score, created_at)
       VALUES ($1, $2, 0, NOW()) RETURNING *`,
      [eventId, userId]
    );

    res.json({ participant: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get user's event progress
export const getEventProgress = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { eventId } = req.params;

    const result = await query(
      `SELECT ep.*, 
              (SELECT COUNT(*) + 1 FROM event_participants WHERE event_id = $1 AND score > ep.score) as rank
       FROM event_participants ep
       WHERE ep.event_id = $1 AND ep.user_id = $2`,
      [eventId, userId]
    );

    if (result.rows.length === 0) {
      res.json({ joined: false });
      return;
    }

    res.json({ joined: true, progress: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get event rewards
export const getEventRewards = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { eventId } = req.params;

    const result = await query(
      'SELECT * FROM event_rewards WHERE event_id = $1 ORDER BY rank_min ASC',
      [eventId]
    );

    res.json({ rewards: result.rows });
  } catch (error) {
    next(error);
  }
};

// Claim event reward
export const claimEventReward = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { eventId } = req.params;

    // Get user's rank
    const rankResult = await query(
      `SELECT ep.*, 
              (SELECT COUNT(*) + 1 FROM event_participants WHERE event_id = $1 AND score > ep.score) as rank
       FROM event_participants ep
       WHERE ep.event_id = $1 AND ep.user_id = $2`,
      [eventId, userId]
    );

    if (rankResult.rows.length === 0) {
      throw new AppError('Not participated in event', 400, 'NOT_PARTICIPATED');
    }

    const participant = rankResult.rows[0];

    // Check if event ended
    const eventResult = await query(
      'SELECT * FROM events WHERE id = $1 AND end_date < NOW()',
      [eventId]
    );

    if (eventResult.rows.length === 0) {
      throw new AppError('Event not ended yet', 400, 'EVENT_NOT_ENDED');
    }

    // Check if already claimed
    if (participant.reward_claimed) {
      throw new AppError('Reward already claimed', 400, 'ALREADY_CLAIMED');
    }

    // Get reward based on rank
    const rewardResult = await query(
      'SELECT * FROM event_rewards WHERE event_id = $1 AND $2 BETWEEN rank_min AND rank_max',
      [eventId, participant.rank]
    );

    if (rewardResult.rows.length === 0) {
      throw new AppError('No reward for your rank', 400, 'NO_REWARD');
    }

    const reward = rewardResult.rows[0];

    // Give reward
    await query(
      'UPDATE users SET coins = coins + $1 WHERE id = $2',
      [reward.coins, userId]
    );

    // Mark as claimed
    await query(
      'UPDATE event_participants SET reward_claimed = true, updated_at = NOW() WHERE event_id = $1 AND user_id = $2',
      [eventId, userId]
    );

    res.json({ success: true, reward });
  } catch (error) {
    next(error);
  }
};

// Admin: Create event
export const createEvent = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { name, description, type, start_date, end_date, rules, rewards } = req.body;

    const result = await query(
      `INSERT INTO events (name, description, type, start_date, end_date, rules, status, created_by, created_at)
       VALUES ($1, $2, $3, $4, $5, $6, 'draft', $7, NOW()) RETURNING *`,
      [name, description, type, start_date, end_date, JSON.stringify(rules), req.user?.id]
    );

    res.json({ event: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Admin: Update event
export const updateEvent = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { eventId } = req.params;
    const { name, description, type, start_date, end_date, rules, status } = req.body;

    await query(
      `UPDATE events SET name = $1, description = $2, type = $3, start_date = $4, end_date = $5, rules = $6, status = $7, updated_at = NOW()
       WHERE id = $8`,
      [name, description, type, start_date, end_date, JSON.stringify(rules), status, eventId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Admin: Delete event
export const deleteEvent = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { eventId } = req.params;

    await query('DELETE FROM events WHERE id = $1', [eventId]);

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get active events
export const getActiveEvents = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query(
      `SELECT * FROM events WHERE status = 'active' AND start_date <= NOW() AND end_date >= NOW()
       ORDER BY end_date ASC`
    );

    res.json({ events: result.rows });
  } catch (error) {
    next(error);
  }
};

// Claim milestone
export const claimMilestone = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { eventId, milestoneId } = req.params;

    // Check if milestone is reached
    const milestoneResult = await query(
      `SELECT m.*, ep.score
       FROM event_milestones m
       JOIN event_participants ep ON ep.event_id = m.event_id AND ep.user_id = $1
       WHERE m.id = $2 AND m.event_id = $3`,
      [userId, milestoneId, eventId]
    );

    if (milestoneResult.rows.length === 0) {
      throw new AppError('Milestone not found or not participating', 404, 'MILESTONE_NOT_FOUND');
    }

    const milestone = milestoneResult.rows[0];

    if (milestone.score < milestone.required_score) {
      throw new AppError('Milestone not reached', 400, 'MILESTONE_NOT_REACHED');
    }

    // Check if already claimed
    const claimResult = await query(
      'SELECT * FROM milestone_claims WHERE milestone_id = $1 AND user_id = $2',
      [milestoneId, userId]
    );

    if (claimResult.rows.length > 0) {
      throw new AppError('Already claimed', 400, 'ALREADY_CLAIMED');
    }

    // Give reward
    await query(
      'UPDATE users SET coins = coins + $1 WHERE id = $2',
      [milestone.reward_coins || 0, userId]
    );

    // Record claim
    await query(
      'INSERT INTO milestone_claims (milestone_id, user_id, created_at) VALUES ($1, $2, NOW())',
      [milestoneId, userId]
    );

    res.json({ success: true, reward: { coins: milestone.reward_coins } });
  } catch (error) {
    next(error);
  }
};

// Get leaderboard around me
export const getLeaderboardAroundMe = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { eventId } = req.params;
    const { range = 5 } = req.query;

    // Get user's rank
    const rankResult = await query(
      `SELECT ep.*, 
              (SELECT COUNT(*) + 1 FROM event_participants WHERE event_id = $1 AND score > ep.score) as rank
       FROM event_participants ep
       WHERE ep.event_id = $1 AND ep.user_id = $2`,
      [eventId, userId]
    );

    if (rankResult.rows.length === 0) {
      throw new AppError('Not participating', 400, 'NOT_PARTICIPATING');
    }

    const userRank = parseInt(rankResult.rows[0].rank, 10);
    const rangeNum = Number(range);

    // Get surrounding users
    const result = await query(
      `SELECT ep.*, u.username, u.display_name, u.avatar_url,
              (SELECT COUNT(*) + 1 FROM event_participants WHERE event_id = $1 AND score > ep.score) as rank
       FROM event_participants ep
       JOIN users u ON ep.user_id = u.id
       WHERE ep.event_id = $1
       ORDER BY ep.score DESC
       LIMIT $2 OFFSET $3`,
      [eventId, rangeNum * 2 + 1, Math.max(0, userRank - rangeNum - 1)]
    );

    res.json({ leaderboard: result.rows, myRank: userRank });
  } catch (error) {
    next(error);
  }
};

// Open lucky bag
export const openLuckyBag = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    // Check if user has lucky bags
    const userResult = await query(
      'SELECT lucky_bags FROM users WHERE id = $1',
      [userId]
    );

    if (userResult.rows.length === 0 || userResult.rows[0].lucky_bags < 1) {
      throw new AppError('No lucky bags available', 400, 'NO_LUCKY_BAGS');
    }

    // Deduct bag
    await query(
      'UPDATE users SET lucky_bags = lucky_bags - 1 WHERE id = $1',
      [userId]
    );

    // Random reward
    const rewards = [
      { type: 'coins', amount: 100 },
      { type: 'coins', amount: 500 },
      { type: 'coins', amount: 1000 },
      { type: 'diamonds', amount: 10 },
      { type: 'exp', amount: 50 }
    ];

    const reward = rewards[Math.floor(Math.random() * rewards.length)];

    // Give reward
    if (reward.type === 'coins') {
      await query('UPDATE users SET coins = coins + $1 WHERE id = $2', [reward.amount, userId]);
    } else if (reward.type === 'diamonds') {
      await query('UPDATE users SET diamonds = diamonds + $1 WHERE id = $2', [reward.amount, userId]);
    } else if (reward.type === 'exp') {
      await query('UPDATE users SET exp = exp + $1 WHERE id = $2', [reward.amount, userId]);
    }

    res.json({ success: true, reward });
  } catch (error) {
    next(error);
  }
};

// Spin lucky draw
export const spinLuckyDraw = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    // Check if user has spins
    const userResult = await query(
      'SELECT lucky_spins FROM users WHERE id = $1',
      [userId]
    );

    if (userResult.rows.length === 0 || userResult.rows[0].lucky_spins < 1) {
      throw new AppError('No spins available', 400, 'NO_SPINS');
    }

    // Deduct spin
    await query(
      'UPDATE users SET lucky_spins = lucky_spins - 1 WHERE id = $1',
      [userId]
    );

    // Random result (weighted)
    const results = [
      { segment: 'coins_100', multiplier: 1, probability: 30 },
      { segment: 'coins_500', multiplier: 5, probability: 25 },
      { segment: 'coins_1000', multiplier: 10, probability: 20 },
      { segment: 'diamonds_5', multiplier: 0, probability: 15 },
      { segment: 'diamonds_25', multiplier: 0, probability: 8 },
      { segment: 'jackpot', multiplier: 100, probability: 2 }
    ];

    const random = Math.random() * 100;
    let cumulative = 0;
    let result = results[0];

    for (const r of results) {
      cumulative += r.probability;
      if (random <= cumulative) {
        result = r;
        break;
      }
    }

    // Give reward based on segment
    const rewards: Record<string, { type: string; amount: number }> = {
      'coins_100': { type: 'coins', amount: 100 },
      'coins_500': { type: 'coins', amount: 500 },
      'coins_1000': { type: 'coins', amount: 1000 },
      'diamonds_5': { type: 'diamonds', amount: 5 },
      'diamonds_25': { type: 'diamonds', amount: 25 },
      'jackpot': { type: 'coins', amount: 10000 }
    };

    const reward = rewards[result.segment];

    if (reward.type === 'coins') {
      await query('UPDATE users SET coins = coins + $1 WHERE id = $2', [reward.amount, userId]);
    } else if (reward.type === 'diamonds') {
      await query('UPDATE users SET diamonds = diamonds + $1 WHERE id = $2', [reward.amount, userId]);
    }

    res.json({ success: true, segment: result.segment, reward });
  } catch (error) {
    next(error);
  }
};

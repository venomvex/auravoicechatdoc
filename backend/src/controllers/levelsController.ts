/**
 * Levels Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { query } from '../config/database.config';
import { AppError } from '../middleware/errorHandler';
import { logger } from '../utils/logger';

// Get level info
export const getLevelInfo = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const userResult = await query(
      'SELECT level, exp FROM users WHERE id = $1',
      [userId]
    );

    if (userResult.rows.length === 0) {
      throw new AppError('User not found', 404, 'USER_NOT_FOUND');
    }

    const user = userResult.rows[0];

    // Get level config
    const levelResult = await query(
      'SELECT * FROM level_configs WHERE level = $1',
      [user.level]
    );

    const nextLevelResult = await query(
      'SELECT * FROM level_configs WHERE level = $1',
      [user.level + 1]
    );

    res.json({
      currentLevel: user.level,
      currentExp: user.exp,
      levelConfig: levelResult.rows[0] || null,
      nextLevelConfig: nextLevelResult.rows[0] || null,
      expToNextLevel: nextLevelResult.rows[0] ? nextLevelResult.rows[0].required_exp - user.exp : null
    });
  } catch (error) {
    next(error);
  }
};

// Get all levels
export const getAllLevels = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query(
      'SELECT * FROM level_configs ORDER BY level ASC'
    );

    res.json({ levels: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get level rewards
export const getLevelRewards = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { level } = req.params;

    const result = await query(
      'SELECT * FROM level_rewards WHERE level = $1',
      [level]
    );

    res.json({ rewards: result.rows });
  } catch (error) {
    next(error);
  }
};

// Claim level reward
export const claimLevelReward = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { level } = req.params;

    // Check if user has reached level
    const userResult = await query(
      'SELECT level FROM users WHERE id = $1',
      [userId]
    );

    if (userResult.rows.length === 0 || userResult.rows[0].level < parseInt(level, 10)) {
      throw new AppError('Level not reached', 400, 'LEVEL_NOT_REACHED');
    }

    // Check if already claimed
    const claimResult = await query(
      'SELECT * FROM level_claims WHERE user_id = $1 AND level = $2',
      [userId, level]
    );

    if (claimResult.rows.length > 0) {
      throw new AppError('Reward already claimed', 400, 'ALREADY_CLAIMED');
    }

    // Get rewards
    const rewardResult = await query(
      'SELECT * FROM level_rewards WHERE level = $1',
      [level]
    );

    if (rewardResult.rows.length === 0) {
      throw new AppError('No rewards for this level', 404, 'NO_REWARDS');
    }

    const reward = rewardResult.rows[0];

    // Give rewards
    await query(
      'UPDATE users SET coins = coins + $1 WHERE id = $2',
      [reward.coins || 0, userId]
    );

    // Record claim
    await query(
      'INSERT INTO level_claims (user_id, level, created_at) VALUES ($1, $2, NOW())',
      [userId, level]
    );

    res.json({ success: true, reward });
  } catch (error) {
    next(error);
  }
};

// Get level leaderboard
export const getLevelLeaderboard = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { page = 1, limit = 50 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT id, username, display_name, avatar_url, level, exp, vip_tier
       FROM users
       ORDER BY level DESC, exp DESC
       LIMIT $1 OFFSET $2`,
      [limit, offset]
    );

    res.json({ leaderboard: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get user level history
export const getLevelHistory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM level_history WHERE user_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [userId, limit, offset]
    );

    res.json({ history: result.rows });
  } catch (error) {
    next(error);
  }
};

// Admin: Create level config
export const createLevelConfig = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { level, required_exp, title, badge_url, benefits } = req.body;

    const result = await query(
      `INSERT INTO level_configs (level, required_exp, title, badge_url, benefits, created_at)
       VALUES ($1, $2, $3, $4, $5, NOW()) RETURNING *`,
      [level, required_exp, title, badge_url, JSON.stringify(benefits)]
    );

    res.json({ levelConfig: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Admin: Update level config
export const updateLevelConfig = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { level } = req.params;
    const { required_exp, title, badge_url, benefits } = req.body;

    await query(
      `UPDATE level_configs SET required_exp = $1, title = $2, badge_url = $3, benefits = $4, updated_at = NOW()
       WHERE level = $5`,
      [required_exp, title, badge_url, JSON.stringify(benefits), level]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Admin: Create level reward
export const createLevelReward = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { level, coins, diamonds, items } = req.body;

    const result = await query(
      `INSERT INTO level_rewards (level, coins, diamonds, items, created_at)
       VALUES ($1, $2, $3, $4, NOW()) RETURNING *`,
      [level, coins, diamonds, JSON.stringify(items)]
    );

    res.json({ reward: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get my level (alias for getLevelInfo)
export const getMyLevel = getLevelInfo;

// Get exp history
export const getExpHistory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM exp_history WHERE user_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [userId, limit, offset]
    );

    res.json({ history: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get today's exp
export const getTodayExp = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT COALESCE(SUM(amount), 0) as total
       FROM exp_history 
       WHERE user_id = $1 AND created_at > DATE_TRUNC('day', NOW())`,
      [userId]
    );

    res.json({ todayExp: parseInt(result.rows[0].total, 10) });
  } catch (error) {
    next(error);
  }
};

// Get friends leaderboard
export const getFriendsLeaderboard = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { page = 1, limit = 50 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT u.id, u.username, u.display_name, u.avatar_url, u.level, u.exp, u.vip_tier
       FROM users u
       JOIN friendships f ON (f.user_id = $1 AND f.friend_id = u.id) OR (f.friend_id = $1 AND f.user_id = u.id)
       WHERE f.status = 'accepted'
       ORDER BY u.level DESC, u.exp DESC
       LIMIT $2 OFFSET $3`,
      [userId, limit, offset]
    );

    res.json({ leaderboard: result.rows });
  } catch (error) {
    next(error);
  }
};

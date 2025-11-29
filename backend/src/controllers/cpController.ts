/**
 * CP (Couple Partnership) Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { query } from '../config/database.config';
import { AppError } from '../middleware/errorHandler';
import { logger } from '../utils/logger';

// Get CP status
export const getCpStatus = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT cp.*, u.username as partner_username, u.display_name as partner_display_name, u.avatar_url as partner_avatar
       FROM couple_partnerships cp
       JOIN users u ON (cp.user1_id = $1 AND u.id = cp.user2_id) OR (cp.user2_id = $1 AND u.id = cp.user1_id)
       WHERE cp.user1_id = $1 OR cp.user2_id = $1`,
      [userId]
    );

    if (result.rows.length === 0) {
      res.json({ hasPartner: false });
      return;
    }

    res.json({ hasPartner: true, partnership: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Send CP request
export const sendCpRequest = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { targetUserId } = req.body;

    // Check if already in partnership
    const existingCp = await query(
      'SELECT * FROM couple_partnerships WHERE user1_id = $1 OR user2_id = $1 OR user1_id = $2 OR user2_id = $2',
      [userId, targetUserId]
    );

    if (existingCp.rows.length > 0) {
      throw new AppError('One or both users are already in a partnership', 400, 'ALREADY_IN_PARTNERSHIP');
    }

    // Create request
    const result = await query(
      `INSERT INTO cp_requests (from_user_id, to_user_id, status, created_at)
       VALUES ($1, $2, 'pending', NOW()) RETURNING *`,
      [userId, targetUserId]
    );

    res.json({ request: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Accept CP request
export const acceptCpRequest = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { requestId } = req.params;

    // Get request
    const requestResult = await query(
      'SELECT * FROM cp_requests WHERE id = $1 AND to_user_id = $2 AND status = $3',
      [requestId, userId, 'pending']
    );

    if (requestResult.rows.length === 0) {
      throw new AppError('Request not found', 404, 'REQUEST_NOT_FOUND');
    }

    const cpRequest = requestResult.rows[0];

    // Create partnership
    await query(
      `INSERT INTO couple_partnerships (user1_id, user2_id, cp_exp, level, created_at)
       VALUES ($1, $2, 0, 1, NOW())`,
      [cpRequest.from_user_id, cpRequest.to_user_id]
    );

    // Update request
    await query(
      'UPDATE cp_requests SET status = $1, updated_at = NOW() WHERE id = $2',
      ['accepted', requestId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Reject CP request
export const rejectCpRequest = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { requestId } = req.params;

    await query(
      'UPDATE cp_requests SET status = $1, updated_at = NOW() WHERE id = $2 AND to_user_id = $3',
      ['rejected', requestId, userId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get CP requests
export const getCpRequests = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT r.*, u.username, u.display_name, u.avatar_url
       FROM cp_requests r
       JOIN users u ON r.from_user_id = u.id
       WHERE r.to_user_id = $1 AND r.status = 'pending'
       ORDER BY r.created_at DESC`,
      [userId]
    );

    res.json({ requests: result.rows });
  } catch (error) {
    next(error);
  }
};

// End partnership
export const endPartnership = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    await query(
      'DELETE FROM couple_partnerships WHERE user1_id = $1 OR user2_id = $1',
      [userId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get CP leaderboard
export const getCpLeaderboard = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT cp.*, 
              u1.username as user1_username, u1.display_name as user1_display_name, u1.avatar_url as user1_avatar,
              u2.username as user2_username, u2.display_name as user2_display_name, u2.avatar_url as user2_avatar
       FROM couple_partnerships cp
       JOIN users u1 ON cp.user1_id = u1.id
       JOIN users u2 ON cp.user2_id = u2.id
       ORDER BY cp.cp_exp DESC
       LIMIT $1 OFFSET $2`,
      [limit, offset]
    );

    res.json({ leaderboard: result.rows });
  } catch (error) {
    next(error);
  }
};

// Send gift to CP
export const sendGiftToCp = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { giftId, amount } = req.body;

    // Get partnership
    const cpResult = await query(
      'SELECT * FROM couple_partnerships WHERE user1_id = $1 OR user2_id = $1',
      [userId]
    );

    if (cpResult.rows.length === 0) {
      throw new AppError('No active partnership', 400, 'NO_PARTNERSHIP');
    }

    const cp = cpResult.rows[0];
    const partnerId = cp.user1_id === userId ? cp.user2_id : cp.user1_id;

    // Add CP EXP (1 coin = 1 CP EXP)
    await query(
      'UPDATE couple_partnerships SET cp_exp = cp_exp + $1, updated_at = NOW() WHERE id = $2',
      [amount, cp.id]
    );

    res.json({ success: true, newCpExp: cp.cp_exp + amount });
  } catch (error) {
    next(error);
  }
};

// Get my CP (alias for getCpStatus)
export const getMyCP = getCpStatus;

// Send CP request (alias)
export const sendCPRequest = sendCpRequest;

// Accept CP request (alias)
export const acceptCPRequest = acceptCpRequest;

// Reject CP request (alias)
export const rejectCPRequest = rejectCpRequest;

// Dissolve CP (alias for endPartnership)
export const dissolveCP = endPartnership;

// Get CP level
export const getCPLevel = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT cp.cp_exp, cp.level, lc.title, lc.badge_url
       FROM couple_partnerships cp
       LEFT JOIN cp_level_configs lc ON cp.level = lc.level
       WHERE cp.user1_id = $1 OR cp.user2_id = $1`,
      [userId]
    );

    if (result.rows.length === 0) {
      res.json({ hasCP: false });
      return;
    }

    res.json({ hasCP: true, levelInfo: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get CP rewards
export const getCPRewards = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT cr.*, 
              CASE WHEN cc.id IS NOT NULL THEN true ELSE false END as claimed
       FROM cp_rewards cr
       LEFT JOIN cp_claims cc ON cr.id = cc.reward_id AND cc.user_id = $1
       ORDER BY cr.level ASC`,
      [userId]
    );

    res.json({ rewards: result.rows });
  } catch (error) {
    next(error);
  }
};

// Claim CP reward
export const claimReward = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { rewardId } = req.params;

    // Get user's CP level
    const cpResult = await query(
      'SELECT level FROM couple_partnerships WHERE user1_id = $1 OR user2_id = $1',
      [userId]
    );

    if (cpResult.rows.length === 0) {
      throw new AppError('No active partnership', 400, 'NO_PARTNERSHIP');
    }

    const cpLevel = cpResult.rows[0].level;

    // Get reward
    const rewardResult = await query(
      'SELECT * FROM cp_rewards WHERE id = $1',
      [rewardId]
    );

    if (rewardResult.rows.length === 0) {
      throw new AppError('Reward not found', 404, 'REWARD_NOT_FOUND');
    }

    const reward = rewardResult.rows[0];

    if (cpLevel < reward.level) {
      throw new AppError('Level not reached', 400, 'LEVEL_NOT_REACHED');
    }

    // Check if already claimed
    const claimResult = await query(
      'SELECT * FROM cp_claims WHERE reward_id = $1 AND user_id = $2',
      [rewardId, userId]
    );

    if (claimResult.rows.length > 0) {
      throw new AppError('Already claimed', 400, 'ALREADY_CLAIMED');
    }

    // Give reward
    await query(
      'UPDATE users SET coins = coins + $1 WHERE id = $2',
      [reward.coins || 0, userId]
    );

    // Record claim
    await query(
      'INSERT INTO cp_claims (reward_id, user_id, created_at) VALUES ($1, $2, NOW())',
      [rewardId, userId]
    );

    res.json({ success: true, reward });
  } catch (error) {
    next(error);
  }
};

// Get CP ranking (alias for getCpLeaderboard)
export const getCPRanking = getCpLeaderboard;

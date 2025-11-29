/**
 * Earnings Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { query } from '../config/database.config';
import { AppError } from '../middleware/errorHandler';
import { logger } from '../utils/logger';

// Get earnings summary
export const getEarningsSummary = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT 
         COALESCE(SUM(CASE WHEN created_at > NOW() - INTERVAL '24 hours' THEN amount ELSE 0 END), 0) as today,
         COALESCE(SUM(CASE WHEN created_at > NOW() - INTERVAL '7 days' THEN amount ELSE 0 END), 0) as this_week,
         COALESCE(SUM(CASE WHEN created_at > NOW() - INTERVAL '30 days' THEN amount ELSE 0 END), 0) as this_month,
         COALESCE(SUM(amount), 0) as total
       FROM earnings WHERE user_id = $1`,
      [userId]
    );

    res.json({
      today: parseFloat(result.rows[0].today),
      thisWeek: parseFloat(result.rows[0].this_week),
      thisMonth: parseFloat(result.rows[0].this_month),
      total: parseFloat(result.rows[0].total)
    });
  } catch (error) {
    next(error);
  }
};

// Get earnings history
export const getEarningsHistory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { page = 1, limit = 20, type } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    let queryText = `
      SELECT * FROM earnings WHERE user_id = $1 
      ORDER BY created_at DESC LIMIT $2 OFFSET $3
    `;
    const params: any[] = [userId, limit, offset];

    if (type) {
      queryText = `
        SELECT * FROM earnings WHERE user_id = $1 AND type = $2
        ORDER BY created_at DESC LIMIT $3 OFFSET $4
      `;
      params.splice(1, 0, type);
    }

    const result = await query(queryText, params);

    res.json({ earnings: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get earnings targets
export const getEarningsTargets = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT et.*, 
              COALESCE(SUM(e.amount), 0) as current_progress
       FROM earning_targets et
       LEFT JOIN earnings e ON e.user_id = $1 AND e.type = et.type AND e.created_at > NOW() - INTERVAL '30 days'
       WHERE et.is_active = true
       GROUP BY et.id
       ORDER BY et.target_amount ASC`,
      [userId]
    );

    res.json({ targets: result.rows });
  } catch (error) {
    next(error);
  }
};

// Claim earnings reward
export const claimReward = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { targetId } = req.params;

    // Check if target is reached and not claimed
    const targetResult = await query(
      `SELECT et.*, 
              COALESCE(SUM(e.amount), 0) as current_progress
       FROM earning_targets et
       LEFT JOIN earnings e ON e.user_id = $1 AND e.type = et.type AND e.created_at > NOW() - INTERVAL '30 days'
       WHERE et.id = $2 AND et.is_active = true
       GROUP BY et.id`,
      [userId, targetId]
    );

    if (targetResult.rows.length === 0) {
      throw new AppError('Target not found', 404, 'TARGET_NOT_FOUND');
    }

    const target = targetResult.rows[0];

    if (parseFloat(target.current_progress) < parseFloat(target.target_amount)) {
      throw new AppError('Target not reached', 400, 'TARGET_NOT_REACHED');
    }

    // Check if already claimed
    const claimResult = await query(
      'SELECT * FROM earning_claims WHERE user_id = $1 AND target_id = $2 AND created_at > NOW() - INTERVAL \'30 days\'',
      [userId, targetId]
    );

    if (claimResult.rows.length > 0) {
      throw new AppError('Reward already claimed', 400, 'ALREADY_CLAIMED');
    }

    // Add reward to user
    await query(
      'UPDATE users SET coins = coins + $1 WHERE id = $2',
      [target.reward_coins, userId]
    );

    // Record claim
    await query(
      'INSERT INTO earning_claims (user_id, target_id, reward_coins, created_at) VALUES ($1, $2, $3, NOW())',
      [userId, targetId, target.reward_coins]
    );

    res.json({ success: true, rewardCoins: target.reward_coins });
  } catch (error) {
    next(error);
  }
};

// Get withdrawal history
export const getWithdrawalHistory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM withdrawals WHERE user_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [userId, limit, offset]
    );

    res.json({ withdrawals: result.rows });
  } catch (error) {
    next(error);
  }
};

// Request withdrawal
export const requestWithdrawal = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { amount, method, account_details } = req.body;

    // Check balance
    const userResult = await query('SELECT diamonds FROM users WHERE id = $1', [userId]);
    
    if (userResult.rows.length === 0) {
      throw new AppError('User not found', 404, 'USER_NOT_FOUND');
    }

    const user = userResult.rows[0];

    if (user.diamonds < amount) {
      throw new AppError('Insufficient balance', 400, 'INSUFFICIENT_BALANCE');
    }

    // Deduct diamonds
    await query(
      'UPDATE users SET diamonds = diamonds - $1 WHERE id = $2',
      [amount, userId]
    );

    // Create withdrawal request
    const result = await query(
      `INSERT INTO withdrawals (user_id, amount, method, account_details, status, created_at)
       VALUES ($1, $2, $3, $4, 'pending', NOW()) RETURNING *`,
      [userId, amount, method, JSON.stringify(account_details)]
    );

    res.json({ withdrawal: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get targets (alias for getEarningsTargets)
export const getTargets = getEarningsTargets;

// Get active targets
export const getActiveTargets = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT et.*, ut.activated_at
       FROM earning_targets et
       JOIN user_targets ut ON et.id = ut.target_id AND ut.user_id = $1
       WHERE et.is_active = true AND ut.is_active = true
       ORDER BY et.created_at DESC`,
      [userId]
    );

    res.json({ targets: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get target progress
export const getTargetProgress = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { targetId } = req.params;

    const result = await query(
      `SELECT et.*, 
              COALESCE(SUM(e.amount), 0) as current_progress,
              ut.activated_at
       FROM earning_targets et
       LEFT JOIN user_targets ut ON et.id = ut.target_id AND ut.user_id = $1
       LEFT JOIN earnings e ON e.user_id = $1 AND e.type = et.type AND e.created_at >= ut.activated_at
       WHERE et.id = $2
       GROUP BY et.id, ut.activated_at`,
      [userId, targetId]
    );

    if (result.rows.length === 0) {
      throw new AppError('Target not found', 404, 'TARGET_NOT_FOUND');
    }

    res.json({ progress: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Activate target
export const activateTarget = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { targetId } = req.params;

    // Check if target exists
    const targetResult = await query(
      'SELECT * FROM earning_targets WHERE id = $1 AND is_active = true',
      [targetId]
    );

    if (targetResult.rows.length === 0) {
      throw new AppError('Target not found', 404, 'TARGET_NOT_FOUND');
    }

    // Activate for user
    await query(
      `INSERT INTO user_targets (user_id, target_id, is_active, activated_at)
       VALUES ($1, $2, true, NOW())
       ON CONFLICT (user_id, target_id) DO UPDATE SET is_active = true, activated_at = NOW()`,
      [userId, targetId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get earning wallet
export const getEarningWallet = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT diamonds, COALESCE(pending_earnings, 0) as pending_earnings
       FROM users WHERE id = $1`,
      [userId]
    );

    if (result.rows.length === 0) {
      throw new AppError('User not found', 404, 'USER_NOT_FOUND');
    }

    res.json({ wallet: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get earning history (alias)
export const getEarningHistory = getEarningsHistory;

// Get pending earnings
export const getPendingEarnings = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT * FROM earnings WHERE user_id = $1 AND status = 'pending' ORDER BY created_at DESC`,
      [userId]
    );

    res.json({ pending: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get withdrawal methods
export const getWithdrawalMethods = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query(
      'SELECT * FROM withdrawal_methods WHERE is_active = true ORDER BY name ASC'
    );

    res.json({ methods: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get withdrawal status
export const getWithdrawalStatus = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { withdrawalId } = req.params;

    const result = await query(
      'SELECT * FROM withdrawals WHERE id = $1 AND user_id = $2',
      [withdrawalId, userId]
    );

    if (result.rows.length === 0) {
      throw new AppError('Withdrawal not found', 404, 'WITHDRAWAL_NOT_FOUND');
    }

    res.json({ withdrawal: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get payment methods
export const getPaymentMethods = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      'SELECT * FROM user_payment_methods WHERE user_id = $1 ORDER BY is_default DESC, created_at DESC',
      [userId]
    );

    res.json({ methods: result.rows });
  } catch (error) {
    next(error);
  }
};

// Add payment method
export const addPaymentMethod = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { type, details, is_default } = req.body;

    // If default, unset other defaults
    if (is_default) {
      await query(
        'UPDATE user_payment_methods SET is_default = false WHERE user_id = $1',
        [userId]
      );
    }

    const result = await query(
      `INSERT INTO user_payment_methods (user_id, type, details, is_default, created_at)
       VALUES ($1, $2, $3, $4, NOW()) RETURNING *`,
      [userId, type, JSON.stringify(details), is_default || false]
    );

    res.json({ method: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Update payment method
export const updatePaymentMethod = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { methodId } = req.params;
    const { details, is_default } = req.body;

    // If default, unset other defaults
    if (is_default) {
      await query(
        'UPDATE user_payment_methods SET is_default = false WHERE user_id = $1',
        [userId]
      );
    }

    await query(
      `UPDATE user_payment_methods SET details = $1, is_default = $2, updated_at = NOW()
       WHERE id = $3 AND user_id = $4`,
      [JSON.stringify(details), is_default || false, methodId, userId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Delete payment method
export const deletePaymentMethod = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { methodId } = req.params;

    await query(
      'DELETE FROM user_payment_methods WHERE id = $1 AND user_id = $2',
      [methodId, userId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

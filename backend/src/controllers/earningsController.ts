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

// Get earnings targets (supports both send and receive types)
export const getEarningsTargets = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { targetType = 'receive' } = req.query; // Default to receiver-based targets

    // For receiver-based targets, calculate progress from gifts received
    const result = await query(
      `SELECT et.*, 
              CASE 
                WHEN et.target_type = 'receive' THEN 
                  COALESCE((SELECT SUM(gt.diamonds_earned) 
                           FROM gift_transactions gt 
                           WHERE gt.receiver_id = $1 
                           AND gt.created_at > NOW() - 
                             CASE WHEN et.period = 'weekly' THEN INTERVAL '7 days'
                                  WHEN et.period = 'monthly' THEN INTERVAL '30 days'
                                  ELSE INTERVAL '365 days' END), 0)
                ELSE 
                  COALESCE(SUM(e.amount), 0)
              END as current_progress,
              et.clearance_days
       FROM earning_targets et
       LEFT JOIN earnings e ON e.user_id = $1 AND e.type = et.type 
         AND e.created_at > NOW() - INTERVAL '30 days'
       WHERE et.is_active = true AND et.target_type = $2
       GROUP BY et.id
       ORDER BY et.tier ASC, et.target_amount ASC`,
      [userId, targetType]
    );

    res.json({ targets: result.rows });
  } catch (error) {
    next(error);
  }
};

// Claim earnings reward (with 5-day clearance for receiver-based targets)
export const claimReward = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { targetId } = req.params;

    // Get target details
    const targetResult = await query(
      `SELECT et.*, 
              CASE 
                WHEN et.target_type = 'receive' THEN 
                  COALESCE((SELECT SUM(gt.diamonds_earned) 
                           FROM gift_transactions gt 
                           WHERE gt.receiver_id = $1 
                           AND gt.created_at > NOW() - 
                             CASE WHEN et.period = 'weekly' THEN INTERVAL '7 days'
                                  WHEN et.period = 'monthly' THEN INTERVAL '30 days'
                                  ELSE INTERVAL '365 days' END), 0)
                ELSE 
                  COALESCE((SELECT SUM(e.amount) FROM earnings e 
                           WHERE e.user_id = $1 AND e.type = et.type 
                           AND e.created_at > NOW() - INTERVAL '30 days'), 0)
              END as current_progress
       FROM earning_targets et
       WHERE et.id = $2 AND et.is_active = true`,
      [userId, targetId]
    );

    if (targetResult.rows.length === 0) {
      throw new AppError('Target not found', 404, 'TARGET_NOT_FOUND');
    }

    const target = targetResult.rows[0];

    if (parseFloat(target.current_progress) < parseFloat(target.target_amount)) {
      throw new AppError('Target not reached', 400, 'TARGET_NOT_REACHED');
    }

    // Check if already claimed (use parameterized interval to avoid SQL injection)
    let periodDays: number;
    switch (target.period) {
      case 'weekly':
        periodDays = 7;
        break;
      case 'monthly':
        periodDays = 30;
        break;
      default:
        periodDays = 365;
    }
    
    const claimResult = await query(
      `SELECT * FROM earning_claims WHERE user_id = $1 AND target_id = $2 AND created_at > NOW() - INTERVAL '1 day' * $3`,
      [userId, targetId, periodDays]
    );

    if (claimResult.rows.length > 0) {
      throw new AppError('Reward already claimed this period', 400, 'ALREADY_CLAIMED');
    }

    const clearanceDays = target.clearance_days || 5;

    // For receiver-based targets, create pending earning with clearance period
    if (target.target_type === 'receive') {
      const clearanceDate = new Date();
      clearanceDate.setDate(clearanceDate.getDate() + clearanceDays);

      await query(
        `INSERT INTO user_pending_earnings (user_id, amount, source_type, source_id, clearance_at, status, created_at)
         VALUES ($1, $2, 'target_reward', $3, $4, 'pending', NOW())`,
        [userId, target.reward_coins, targetId, clearanceDate]
      );

      // Record claim
      await query(
        'INSERT INTO earning_claims (user_id, target_id, reward_coins, created_at) VALUES ($1, $2, $3, NOW())',
        [userId, targetId, target.reward_coins]
      );

      res.json({ 
        success: true, 
        rewardCoins: target.reward_coins,
        clearanceDays,
        clearanceDate: clearanceDate.toISOString(),
        message: `Reward claimed! Coins will be available after ${clearanceDays}-day clearance period.`
      });
    } else {
      // For send-based targets, add coins immediately
      await query(
        'UPDATE users SET coins = coins + $1 WHERE id = $2',
        [target.reward_coins, userId]
      );

      await query(
        'INSERT INTO earning_claims (user_id, target_id, reward_coins, created_at) VALUES ($1, $2, $3, NOW())',
        [userId, targetId, target.reward_coins]
      );

      res.json({ success: true, rewardCoins: target.reward_coins });
    }
  } catch (error) {
    next(error);
  }
};

// Get pending earnings (in clearance)
export const getPendingEarnings = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT * FROM user_pending_earnings 
       WHERE user_id = $1 AND status = 'pending'
       ORDER BY clearance_at ASC`,
      [userId]
    );

    const totalPending = result.rows.reduce((sum: number, row: any) => sum + parseFloat(row.amount), 0);

    res.json({ 
      pending: result.rows,
      totalPending,
      message: 'Pending earnings will be available after clearance period'
    });
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

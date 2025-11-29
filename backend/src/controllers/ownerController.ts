/**
 * Owner Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { query } from '../config/database.config';
import { AppError } from '../middleware/errorHandler';
import { logger } from '../utils/logger';

// Get dashboard
export const getDashboard = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const [users, rooms, revenue, dailyActive] = await Promise.all([
      query('SELECT COUNT(*) FROM users'),
      query('SELECT COUNT(*) FROM rooms'),
      query('SELECT COALESCE(SUM(amount), 0) as total FROM transactions WHERE type = $1', ['deposit']),
      query('SELECT COUNT(*) FROM users WHERE last_login_at > NOW() - INTERVAL \'24 hours\'')
    ]);

    res.json({
      totalUsers: parseInt(users.rows[0].count, 10),
      totalRooms: parseInt(rooms.rows[0].count, 10),
      totalRevenue: parseFloat(revenue.rows[0].total),
      dailyActiveUsers: parseInt(dailyActive.rows[0].count, 10)
    });
  } catch (error) {
    next(error);
  }
};

// Get economy stats
export const getEconomyStats = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const [coinsCirculating, diamondsCirculating, transactions] = await Promise.all([
      query('SELECT COALESCE(SUM(coins), 0) as total FROM users'),
      query('SELECT COALESCE(SUM(diamonds), 0) as total FROM users'),
      query(`SELECT 
               COALESCE(SUM(CASE WHEN type = 'deposit' THEN amount ELSE 0 END), 0) as deposits,
               COALESCE(SUM(CASE WHEN type = 'withdrawal' THEN amount ELSE 0 END), 0) as withdrawals
             FROM transactions WHERE created_at > NOW() - INTERVAL '30 days'`)
    ]);

    res.json({
      coinsCirculating: parseFloat(coinsCirculating.rows[0].total),
      diamondsCirculating: parseFloat(diamondsCirculating.rows[0].total),
      deposits30d: parseFloat(transactions.rows[0].deposits),
      withdrawals30d: parseFloat(transactions.rows[0].withdrawals)
    });
  } catch (error) {
    next(error);
  }
};

// Update economy settings
export const updateEconomySettings = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { settings } = req.body;

    await query(
      `INSERT INTO system_settings (key, value, updated_at)
       VALUES ('economy_settings', $1, NOW())
       ON CONFLICT (key) DO UPDATE SET value = $1, updated_at = NOW()`,
      [JSON.stringify(settings)]
    );

    logger.info(`Economy settings updated by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get economy presets
export const getEconomyPresets = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query('SELECT * FROM economy_presets ORDER BY name ASC');
    res.json({ presets: result.rows });
  } catch (error) {
    next(error);
  }
};

// Apply economy preset
export const applyEconomyPreset = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { presetId } = req.body;

    const presetResult = await query('SELECT * FROM economy_presets WHERE id = $1', [presetId]);
    
    if (presetResult.rows.length === 0) {
      throw new AppError('Preset not found', 404, 'PRESET_NOT_FOUND');
    }

    await query(
      `INSERT INTO system_settings (key, value, updated_at)
       VALUES ('economy_settings', $1, NOW())
       ON CONFLICT (key) DO UPDATE SET value = $1, updated_at = NOW()`,
      [presetResult.rows[0].settings]
    );

    logger.info(`Economy preset ${presetId} applied by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get revenue dashboard
export const getRevenueDashboard = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query(`
      SELECT 
        DATE(created_at) as date,
        COALESCE(SUM(CASE WHEN type = 'deposit' THEN amount ELSE 0 END), 0) as deposits,
        COALESCE(SUM(CASE WHEN type = 'withdrawal' THEN amount ELSE 0 END), 0) as withdrawals
      FROM transactions
      WHERE created_at > NOW() - INTERVAL '30 days'
      GROUP BY DATE(created_at)
      ORDER BY date DESC
    `);

    res.json({ revenue: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get revenue reports
export const getRevenueReports = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { startDate, endDate } = req.query;

    const result = await query(`
      SELECT 
        COALESCE(SUM(CASE WHEN type = 'deposit' THEN amount ELSE 0 END), 0) as total_deposits,
        COALESCE(SUM(CASE WHEN type = 'withdrawal' THEN amount ELSE 0 END), 0) as total_withdrawals,
        COUNT(CASE WHEN type = 'deposit' THEN 1 END) as deposit_count,
        COUNT(CASE WHEN type = 'withdrawal' THEN 1 END) as withdrawal_count
      FROM transactions
      WHERE created_at BETWEEN $1 AND $2
    `, [startDate, endDate]);

    res.json({ report: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Update pricing
export const updatePricing = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { pricing } = req.body;

    await query(
      `INSERT INTO system_settings (key, value, updated_at)
       VALUES ('pricing', $1, NOW())
       ON CONFLICT (key) DO UPDATE SET value = $1, updated_at = NOW()`,
      [JSON.stringify(pricing)]
    );

    logger.info(`Pricing updated by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get payout queue
export const getPayoutQueue = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { status = 'pending', page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT w.*, u.username, u.display_name, u.email
       FROM withdrawals w
       JOIN users u ON w.user_id = u.id
       WHERE w.status = $1
       ORDER BY w.created_at ASC
       LIMIT $2 OFFSET $3`,
      [status, limit, offset]
    );

    res.json({ payouts: result.rows });
  } catch (error) {
    next(error);
  }
};

// Approve payout
export const approvePayout = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { id } = req.params;
    const { transactionId } = req.body;

    await query(
      `UPDATE withdrawals SET status = 'completed', transaction_id = $1, processed_by = $2, processed_at = NOW()
       WHERE id = $3`,
      [transactionId, req.user?.id, id]
    );

    logger.info(`Payout ${id} approved by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Reject payout
export const rejectPayout = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { id } = req.params;
    const { reason } = req.body;

    // Get withdrawal details
    const withdrawalResult = await query('SELECT * FROM withdrawals WHERE id = $1', [id]);
    
    if (withdrawalResult.rows.length === 0) {
      throw new AppError('Withdrawal not found', 404, 'NOT_FOUND');
    }

    const withdrawal = withdrawalResult.rows[0];

    // Refund diamonds
    await query(
      'UPDATE users SET diamonds = diamonds + $1 WHERE id = $2',
      [withdrawal.amount, withdrawal.user_id]
    );

    // Update withdrawal status
    await query(
      `UPDATE withdrawals SET status = 'rejected', rejection_reason = $1, processed_by = $2, processed_at = NOW()
       WHERE id = $3`,
      [reason, req.user?.id, id]
    );

    logger.info(`Payout ${id} rejected by owner ${req.user?.id}: ${reason}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Batch approve payouts
export const batchApprovePayout = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { ids, transactionPrefix } = req.body;

    for (let i = 0; i < ids.length; i++) {
      await query(
        `UPDATE withdrawals SET status = 'completed', transaction_id = $1, processed_by = $2, processed_at = NOW()
         WHERE id = $3`,
        [`${transactionPrefix}-${i + 1}`, req.user?.id, ids[i]]
      );
    }

    logger.info(`${ids.length} payouts batch approved by owner ${req.user?.id}`);
    res.json({ success: true, count: ids.length });
  } catch (error) {
    next(error);
  }
};

// Get features
export const getFeatures = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query('SELECT * FROM features ORDER BY name ASC');
    res.json({ features: result.rows });
  } catch (error) {
    next(error);
  }
};

// Update feature
export const updateFeature = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { feature } = req.params;
    const { enabled, config } = req.body;

    await query(
      `UPDATE features SET enabled = $1, config = $2, updated_at = NOW() WHERE name = $3`,
      [enabled, JSON.stringify(config), feature]
    );

    logger.info(`Feature ${feature} updated by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get feature flags
export const getFeatureFlags = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query('SELECT * FROM feature_flags ORDER BY name ASC');
    res.json({ flags: result.rows });
  } catch (error) {
    next(error);
  }
};

// Update feature flag
export const updateFeatureFlag = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { flag } = req.params;
    const { enabled, rolloutPercentage } = req.body;

    await query(
      `UPDATE feature_flags SET enabled = $1, rollout_percentage = $2, updated_at = NOW() WHERE name = $3`,
      [enabled, rolloutPercentage, flag]
    );

    logger.info(`Feature flag ${flag} updated by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get country admins
export const getCountryAdmins = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query(
      `SELECT id, username, display_name, email, country, created_at FROM users WHERE role = 'country_admin'`
    );
    res.json({ countryAdmins: result.rows });
  } catch (error) {
    next(error);
  }
};

// Assign country admin
export const assignCountryAdmin = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;
    const { country } = req.body;

    await query(
      'UPDATE users SET role = $1, country = $2, updated_at = NOW() WHERE id = $3',
      ['country_admin', country, userId]
    );

    logger.info(`User ${userId} assigned as country admin for ${country} by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Revoke country admin
export const revokeCountryAdmin = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;

    await query(
      'UPDATE users SET role = $1, updated_at = NOW() WHERE id = $2',
      ['user', userId]
    );

    logger.info(`Country admin role revoked from user ${userId} by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get all admins
export const getAllAdmins = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query(
      `SELECT id, username, display_name, email, role, country, created_at 
       FROM users WHERE role IN ('admin', 'country_admin', 'owner')`
    );
    res.json({ admins: result.rows });
  } catch (error) {
    next(error);
  }
};

// Create admin
export const createAdmin = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId, role, country } = req.body;

    await query(
      'UPDATE users SET role = $1, country = $2, updated_at = NOW() WHERE id = $3',
      [role, country, userId]
    );

    logger.info(`User ${userId} promoted to ${role} by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Update admin
export const updateAdmin = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { id } = req.params;
    const { role, country } = req.body;

    await query(
      'UPDATE users SET role = $1, country = $2, updated_at = NOW() WHERE id = $3',
      [role, country, id]
    );

    logger.info(`Admin ${id} updated by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Delete admin (demote to user)
export const deleteAdmin = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { id } = req.params;

    await query(
      'UPDATE users SET role = $1, updated_at = NOW() WHERE id = $2',
      ['user', id]
    );

    logger.info(`Admin ${id} demoted by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get admin logs
export const getAdminLogs = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { id } = req.params;
    const { page = 1, limit = 50 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM admin_logs WHERE admin_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [id, limit, offset]
    );

    res.json({ logs: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get system settings
export const getSystemSettings = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query('SELECT * FROM system_settings');
    const settings: Record<string, any> = {};
    result.rows.forEach(row => {
      settings[row.key] = row.value;
    });
    res.json({ settings });
  } catch (error) {
    next(error);
  }
};

// Update system settings
export const updateSystemSettings = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { settings } = req.body;

    for (const [key, value] of Object.entries(settings)) {
      await query(
        `INSERT INTO system_settings (key, value, updated_at)
         VALUES ($1, $2, NOW())
         ON CONFLICT (key) DO UPDATE SET value = $2, updated_at = NOW()`,
        [key, JSON.stringify(value)]
      );
    }

    logger.info(`System settings updated by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Toggle maintenance mode
export const toggleMaintenance = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { enabled, message } = req.body;

    await query(
      `INSERT INTO system_settings (key, value, updated_at)
       VALUES ('maintenance', $1, NOW())
       ON CONFLICT (key) DO UPDATE SET value = $1, updated_at = NOW()`,
      [JSON.stringify({ enabled, message })]
    );

    logger.info(`Maintenance mode ${enabled ? 'enabled' : 'disabled'} by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get version history
export const getVersionHistory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { area } = req.params;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM version_history WHERE area = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [area, limit, offset]
    );

    res.json({ versions: result.rows });
  } catch (error) {
    next(error);
  }
};

// Rollback
export const rollback = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { area } = req.params;
    const { versionId } = req.body;

    const versionResult = await query(
      'SELECT * FROM version_history WHERE id = $1 AND area = $2',
      [versionId, area]
    );

    if (versionResult.rows.length === 0) {
      throw new AppError('Version not found', 404, 'VERSION_NOT_FOUND');
    }

    // Apply the version's settings
    await query(
      `INSERT INTO system_settings (key, value, updated_at)
       VALUES ($1, $2, NOW())
       ON CONFLICT (key) DO UPDATE SET value = $2, updated_at = NOW()`,
      [area, versionResult.rows[0].settings]
    );

    logger.info(`Rollback to version ${versionId} for ${area} by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get resellers
export const getResellers = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query(
      `SELECT id, username, display_name, email, reseller_tier, created_at FROM users WHERE role = 'seller'`
    );
    res.json({ resellers: result.rows });
  } catch (error) {
    next(error);
  }
};

// Assign reseller
export const assignReseller = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;
    const { tier } = req.body;

    await query(
      'UPDATE users SET role = $1, reseller_tier = $2, updated_at = NOW() WHERE id = $3',
      ['seller', tier || 1, userId]
    );

    logger.info(`User ${userId} assigned as reseller by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Revoke reseller
export const revokeReseller = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;

    await query(
      'UPDATE users SET role = $1, reseller_tier = NULL, updated_at = NOW() WHERE id = $2',
      ['user', userId]
    );

    logger.info(`Reseller role revoked from user ${userId} by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Update reseller tier
export const updateResellerTier = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;
    const { tier } = req.body;

    await query(
      'UPDATE users SET reseller_tier = $1, updated_at = NOW() WHERE id = $2 AND role = $3',
      [tier, userId, 'seller']
    );

    logger.info(`Reseller tier for user ${userId} updated to ${tier} by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get guide program
export const getGuideProgram = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query('SELECT value FROM system_settings WHERE key = $1', ['guide_program']);
    res.json({ program: result.rows[0]?.value || {} });
  } catch (error) {
    next(error);
  }
};

// Update guide program
export const updateGuideProgram = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { program } = req.body;

    await query(
      `INSERT INTO system_settings (key, value, updated_at)
       VALUES ('guide_program', $1, NOW())
       ON CONFLICT (key) DO UPDATE SET value = $1, updated_at = NOW()`,
      [JSON.stringify(program)]
    );

    logger.info(`Guide program updated by owner ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

/**
 * Admin Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { query } from '../config/database.config';
import { AppError } from '../middleware/errorHandler';
import { logger } from '../utils/logger';

// Dashboard
export const getDashboard = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const [usersResult, roomsResult, transactionsResult] = await Promise.all([
      query('SELECT COUNT(*) FROM users'),
      query('SELECT COUNT(*) FROM rooms'),
      query('SELECT COALESCE(SUM(amount), 0) as total FROM transactions WHERE created_at > NOW() - INTERVAL \'24 hours\'')
    ]);

    res.json({
      totalUsers: parseInt(usersResult.rows[0].count, 10),
      totalRooms: parseInt(roomsResult.rows[0].count, 10),
      revenueToday: parseFloat(transactionsResult.rows[0].total)
    });
  } catch (error) {
    next(error);
  }
};

// Get stats
export const getStats = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const [activeUsersResult, newUsersResult] = await Promise.all([
      query('SELECT COUNT(*) FROM users WHERE last_login_at > NOW() - INTERVAL \'24 hours\''),
      query('SELECT COUNT(*) FROM users WHERE created_at > NOW() - INTERVAL \'24 hours\'')
    ]);

    res.json({
      activeUsers24h: parseInt(activeUsersResult.rows[0].count, 10),
      newUsers24h: parseInt(newUsersResult.rows[0].count, 10)
    });
  } catch (error) {
    next(error);
  }
};

// Search users
export const searchUsers = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { q, page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT id, username, display_name, email, phone_number, status, role, created_at 
       FROM users 
       WHERE username ILIKE $1 OR display_name ILIKE $1 OR email ILIKE $1
       ORDER BY created_at DESC
       LIMIT $2 OFFSET $3`,
      [`%${q}%`, limit, offset]
    );

    res.json({ users: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get user details
export const getUser = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;

    const result = await query(
      'SELECT * FROM users WHERE id = $1',
      [userId]
    );

    if (result.rows.length === 0) {
      throw new AppError('User not found', 404, 'USER_NOT_FOUND');
    }

    res.json({ user: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Warn user
export const warnUser = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;
    const { reason } = req.body;

    await query(
      'UPDATE users SET warnings = warnings + 1, updated_at = NOW() WHERE id = $1',
      [userId]
    );

    logger.info(`User ${userId} warned by admin ${req.user?.id}: ${reason}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Mute user
export const muteUser = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;
    const { duration, reason } = req.body;

    const muteUntil = new Date(Date.now() + duration * 60000);

    await query(
      'UPDATE users SET muted_until = $1, updated_at = NOW() WHERE id = $2',
      [muteUntil, userId]
    );

    logger.info(`User ${userId} muted until ${muteUntil} by admin ${req.user?.id}: ${reason}`);
    res.json({ success: true, mutedUntil: muteUntil });
  } catch (error) {
    next(error);
  }
};

// Adjust user coins
export const adjustCoins = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;
    const { amount, reason } = req.body;

    await query(
      'UPDATE users SET coins = coins + $1, updated_at = NOW() WHERE id = $2',
      [amount, userId]
    );

    logger.info(`Coins adjusted for user ${userId} by ${amount} by admin ${req.user?.id}: ${reason}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Ban user
export const banUser = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;
    const { reason, permanent = false } = req.body;

    await query(
      'UPDATE users SET status = $1, ban_reason = $2, is_permanent_ban = $3, updated_at = NOW() WHERE id = $4',
      ['banned', reason, permanent, userId]
    );

    logger.info(`User ${userId} banned by admin ${req.user?.id}: ${reason}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Unban user
export const unbanUser = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;

    await query(
      'UPDATE users SET status = $1, ban_reason = NULL, is_permanent_ban = false, updated_at = NOW() WHERE id = $2',
      ['active', userId]
    );

    logger.info(`User ${userId} unbanned by admin ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get reports
export const getReports = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { status = 'pending', page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM reports WHERE status = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [status, limit, offset]
    );

    res.json({ reports: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get report
export const getReport = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { reportId } = req.params;

    const result = await query('SELECT * FROM reports WHERE id = $1', [reportId]);

    if (result.rows.length === 0) {
      throw new AppError('Report not found', 404, 'REPORT_NOT_FOUND');
    }

    res.json({ report: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Act on report
export const actOnReport = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { reportId } = req.params;
    const { action, notes } = req.body;

    await query(
      'UPDATE reports SET status = $1, admin_notes = $2, resolved_by = $3, resolved_at = NOW() WHERE id = $4',
      [action, notes, req.user?.id, reportId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get tickets
export const getTickets = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { status = 'open', page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM tickets WHERE status = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [status, limit, offset]
    );

    res.json({ tickets: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get ticket
export const getTicket = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { ticketId } = req.params;

    const result = await query('SELECT * FROM tickets WHERE id = $1', [ticketId]);

    if (result.rows.length === 0) {
      throw new AppError('Ticket not found', 404, 'TICKET_NOT_FOUND');
    }

    res.json({ ticket: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Respond to ticket
export const respondToTicket = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { ticketId } = req.params;
    const { message } = req.body;

    await query(
      'UPDATE tickets SET status = $1, admin_response = $2, responded_by = $3, responded_at = NOW() WHERE id = $4',
      ['responded', message, req.user?.id, ticketId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Close ticket
export const closeTicket = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { ticketId } = req.params;

    await query(
      'UPDATE tickets SET status = $1, closed_at = NOW() WHERE id = $2',
      ['closed', ticketId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Search rooms
export const searchRooms = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { q, page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM rooms WHERE name ILIKE $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [`%${q}%`, limit, offset]
    );

    res.json({ rooms: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get room
export const getRoom = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { roomId } = req.params;

    const result = await query('SELECT * FROM rooms WHERE id = $1', [roomId]);

    if (result.rows.length === 0) {
      throw new AppError('Room not found', 404, 'ROOM_NOT_FOUND');
    }

    res.json({ room: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Close room
export const closeRoom = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { roomId } = req.params;
    const { reason } = req.body;

    await query(
      'UPDATE rooms SET status = $1, closed_reason = $2, closed_by = $3, closed_at = NOW() WHERE id = $4',
      ['closed', reason, req.user?.id, roomId]
    );

    logger.info(`Room ${roomId} closed by admin ${req.user?.id}: ${reason}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get announcements
export const getAnnouncements = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query('SELECT * FROM announcements ORDER BY created_at DESC');
    res.json({ announcements: result.rows });
  } catch (error) {
    next(error);
  }
};

// Create announcement
export const createAnnouncement = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { title, content, type, target_audience } = req.body;

    const result = await query(
      `INSERT INTO announcements (title, content, type, target_audience, created_by, created_at)
       VALUES ($1, $2, $3, $4, $5, NOW()) RETURNING *`,
      [title, content, type, target_audience, req.user?.id]
    );

    res.json({ announcement: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Update announcement
export const updateAnnouncement = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { id } = req.params;
    const { title, content, type, target_audience } = req.body;

    await query(
      `UPDATE announcements SET title = $1, content = $2, type = $3, target_audience = $4, updated_at = NOW() WHERE id = $5`,
      [title, content, type, target_audience, id]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Delete announcement
export const deleteAnnouncement = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { id } = req.params;

    await query('DELETE FROM announcements WHERE id = $1', [id]);

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get logs
export const getLogs = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { type, page = 1, limit = 50 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    let queryText = 'SELECT * FROM admin_logs ORDER BY created_at DESC LIMIT $1 OFFSET $2';
    const params: any[] = [limit, offset];

    if (type) {
      queryText = 'SELECT * FROM admin_logs WHERE action_type = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3';
      params.unshift(type);
    }

    const result = await query(queryText, params);

    res.json({ logs: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get guides
export const getGuides = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query(
      `SELECT id, username, display_name, email, created_at FROM users WHERE role = 'guide'`
    );

    res.json({ guides: result.rows });
  } catch (error) {
    next(error);
  }
};

// Assign guide
export const assignGuide = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;

    await query(
      'UPDATE users SET role = $1, updated_at = NOW() WHERE id = $2',
      ['guide', userId]
    );

    logger.info(`User ${userId} assigned as guide by admin ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Revoke guide
export const revokeGuide = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;

    await query(
      'UPDATE users SET role = $1, updated_at = NOW() WHERE id = $2',
      ['user', userId]
    );

    logger.info(`Guide role revoked from user ${userId} by admin ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get admins
export const getAdmins = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const result = await query(
      `SELECT id, username, display_name, email, role, created_at FROM users WHERE role IN ('admin', 'country_admin')`
    );

    res.json({ admins: result.rows });
  } catch (error) {
    next(error);
  }
};

// Assign admin
export const assignAdmin = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;

    await query(
      'UPDATE users SET role = $1, updated_at = NOW() WHERE id = $2',
      ['admin', userId]
    );

    logger.info(`User ${userId} assigned as admin by ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Revoke admin
export const revokeAdmin = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { userId } = req.params;

    await query(
      'UPDATE users SET role = $1, updated_at = NOW() WHERE id = $2',
      ['user', userId]
    );

    logger.info(`Admin role revoked from user ${userId} by ${req.user?.id}`);
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

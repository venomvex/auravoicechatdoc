/**
 * Notifications Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { query } from '../config/database.config';
import { logger } from '../utils/logger';

/**
 * Get user's notifications
 */
export const getNotifications = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const page = parseInt(req.query.page as string) || 1;
    const limit = parseInt(req.query.limit as string) || 20;
    const type = req.query.type as string;
    const offset = (page - 1) * limit;

    // Build query
    let queryStr = `
      SELECT id, type, title, message, data, created_at, is_read
      FROM notifications
      WHERE user_id = $1
    `;
    const params: any[] = [userId];

    if (type) {
      queryStr += ` AND type = $${params.length + 1}`;
      params.push(type);
    }

    queryStr += ' ORDER BY created_at DESC LIMIT $' + (params.length + 1) + ' OFFSET $' + (params.length + 2);
    params.push(limit, offset);

    const result = await query(queryStr, params);

    // Get total count
    let countQuery = 'SELECT COUNT(*) as total FROM notifications WHERE user_id = $1';
    const countParams: any[] = [userId];
    if (type) {
      countQuery += ' AND type = $2';
      countParams.push(type);
    }
    const countResult = await query(countQuery, countParams);
    const total = parseInt(countResult.rows[0]?.total || '0');

    // Get unread count
    const unreadResult = await query(
      'SELECT COUNT(*) as unread FROM notifications WHERE user_id = $1 AND is_read = false',
      [userId]
    );
    const unreadCount = parseInt(unreadResult.rows[0]?.unread || '0');

    const notifications = result.rows.map(row => ({
      id: row.id,
      type: row.type,
      title: row.title,
      message: row.message,
      data: row.data || {},
      createdAt: row.created_at?.toISOString() || new Date().toISOString(),
      isRead: row.is_read || false
    }));

    res.json({
      notifications,
      unreadCount,
      pagination: {
        page,
        pageSize: limit,
        totalItems: total,
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    logger.error('Failed to get notifications', { error });
    next(error);
  }
};

/**
 * Mark notification as read
 */
export const markAsRead = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { notificationId } = req.params;

    await query(
      'UPDATE notifications SET is_read = true WHERE id = $1 AND user_id = $2',
      [notificationId, userId]
    );

    res.json({ success: true });
  } catch (error) {
    logger.error('Failed to mark notification as read', { error });
    next(error);
  }
};

/**
 * Mark all notifications as read
 */
export const markAllAsRead = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    await query(
      'UPDATE notifications SET is_read = true WHERE user_id = $1 AND is_read = false',
      [userId]
    );

    res.json({ success: true });
  } catch (error) {
    logger.error('Failed to mark all notifications as read', { error });
    next(error);
  }
};

/**
 * Get system messages
 */
export const getSystemMessages = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const page = parseInt(req.query.page as string) || 1;
    const limit = parseInt(req.query.limit as string) || 20;
    const offset = (page - 1) * limit;

    // Get system messages (broadcast to all users)
    const result = await query(
      `SELECT id, title, content, created_at, priority
       FROM system_messages
       WHERE is_active = true
       ORDER BY priority DESC, created_at DESC
       LIMIT $1 OFFSET $2`,
      [limit, offset]
    );

    // Get total count
    const countResult = await query(
      'SELECT COUNT(*) as total FROM system_messages WHERE is_active = true'
    );
    const total = parseInt(countResult.rows[0]?.total || '0');

    const messages = result.rows.map(row => ({
      id: row.id,
      title: row.title,
      content: row.content,
      createdAt: row.created_at?.toISOString() || new Date().toISOString(),
      priority: row.priority
    }));

    res.json({
      messages,
      pagination: {
        page,
        pageSize: limit,
        totalItems: total,
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    logger.error('Failed to get system messages', { error });
    next(error);
  }
};

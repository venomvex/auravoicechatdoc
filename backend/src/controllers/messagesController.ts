/**
 * Messages Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { v4 as uuidv4 } from 'uuid';
import { query } from '../config/database.config';
import { logger } from '../utils/logger';

/**
 * Get user's conversations
 */
export const getConversations = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const page = parseInt(req.query.page as string) || 1;
    const limit = parseInt(req.query.limit as string) || 20;
    const offset = (page - 1) * limit;

    // Query conversations from database
    const result = await query(
      `SELECT 
        c.id,
        c.last_message,
        c.last_message_at,
        c.unread_count,
        CASE WHEN c.user1_id = $1 THEN u2.id ELSE u1.id END as other_user_id,
        CASE WHEN c.user1_id = $1 THEN u2.display_name ELSE u1.display_name END as name,
        CASE WHEN c.user1_id = $1 THEN u2.avatar_url ELSE u1.avatar_url END as avatar,
        CASE WHEN c.user1_id = $1 THEN u2.is_online ELSE u1.is_online END as is_online
      FROM conversations c
      LEFT JOIN users u1 ON c.user1_id = u1.id
      LEFT JOIN users u2 ON c.user2_id = u2.id
      WHERE c.user1_id = $1 OR c.user2_id = $1
      ORDER BY c.last_message_at DESC
      LIMIT $2 OFFSET $3`,
      [userId, limit, offset]
    );

    // Get total count
    const countResult = await query(
      'SELECT COUNT(*) as total FROM conversations WHERE user1_id = $1 OR user2_id = $1',
      [userId]
    );
    const total = parseInt(countResult.rows[0]?.total || '0');

    const conversations = result.rows.map(row => ({
      id: row.id,
      name: row.name || 'Unknown User',
      avatar: row.avatar,
      lastMessage: row.last_message || '',
      lastMessageAt: row.last_message_at?.toISOString() || new Date().toISOString(),
      unreadCount: parseInt(row.unread_count) || 0,
      isOnline: row.is_online || false,
      userId: row.other_user_id
    }));

    res.json({
      conversations,
      pagination: {
        page,
        pageSize: limit,
        totalItems: total,
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    logger.error('Failed to get conversations', { error });
    next(error);
  }
};

/**
 * Get messages in a conversation
 */
export const getMessages = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { conversationId } = req.params;
    const page = parseInt(req.query.page as string) || 1;
    const limit = parseInt(req.query.limit as string) || 50;
    const offset = (page - 1) * limit;

    // Verify user is part of the conversation
    const conversationCheck = await query(
      'SELECT id FROM conversations WHERE id = $1 AND (user1_id = $2 OR user2_id = $2)',
      [conversationId, userId]
    );

    if (conversationCheck.rows.length === 0) {
      res.status(403).json({ error: 'Not authorized to access this conversation' });
      return;
    }

    // Get messages
    const result = await query(
      `SELECT 
        m.id,
        m.conversation_id,
        m.sender_id,
        u.display_name as sender_name,
        u.avatar_url as sender_avatar,
        m.content,
        m.type,
        m.created_at,
        m.is_read
      FROM messages m
      JOIN users u ON m.sender_id = u.id
      WHERE m.conversation_id = $1
      ORDER BY m.created_at DESC
      LIMIT $2 OFFSET $3`,
      [conversationId, limit, offset]
    );

    // Get total count
    const countResult = await query(
      'SELECT COUNT(*) as total FROM messages WHERE conversation_id = $1',
      [conversationId]
    );
    const total = parseInt(countResult.rows[0]?.total || '0');

    // Mark messages as read
    await query(
      'UPDATE messages SET is_read = true WHERE conversation_id = $1 AND sender_id != $2',
      [conversationId, userId]
    );

    // Update unread count
    await query(
      'UPDATE conversations SET unread_count = 0 WHERE id = $1',
      [conversationId]
    );

    const messages = result.rows.map(row => ({
      id: row.id,
      conversationId: row.conversation_id,
      senderId: row.sender_id,
      senderName: row.sender_name,
      senderAvatar: row.sender_avatar,
      content: row.content,
      type: row.type || 'text',
      createdAt: row.created_at?.toISOString() || new Date().toISOString(),
      isRead: row.is_read || false
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
    logger.error('Failed to get messages', { error });
    next(error);
  }
};

/**
 * Send a message
 */
export const sendMessage = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { recipientId, content, type = 'text' } = req.body;

    if (!userId) {
      res.status(401).json({ error: 'Authentication required' });
      return;
    }

    if (!recipientId || !content) {
      res.status(400).json({ error: 'Recipient ID and content are required' });
      return;
    }

    // Find or create conversation
    // Ensure user1_id < user2_id to satisfy the constraint
    const [user1, user2] = userId < recipientId ? [userId, recipientId] : [recipientId, userId];
    
    let conversationResult = await query(
      `SELECT id FROM conversations 
       WHERE user1_id = $1 AND user2_id = $2`,
      [user1, user2]
    );

    let conversationId: string;
    if (conversationResult.rows.length === 0) {
      // Create new conversation
      conversationId = uuidv4();
      await query(
        `INSERT INTO conversations (id, user1_id, user2_id, created_at, last_message, last_message_at, unread_count)
         VALUES ($1, $2, $3, NOW(), $4, NOW(), 1)`,
        [conversationId, user1, user2, content]
      );
    } else {
      conversationId = conversationResult.rows[0].id;
      // Update conversation
      await query(
        `UPDATE conversations 
         SET last_message = $1, last_message_at = NOW(), unread_count = unread_count + 1
         WHERE id = $2`,
        [content, conversationId]
      );
    }

    // Insert message
    const messageId = uuidv4();
    await query(
      `INSERT INTO messages (id, conversation_id, sender_id, content, type, created_at, is_read)
       VALUES ($1, $2, $3, $4, $5, NOW(), false)`,
      [messageId, conversationId, userId, content, type]
    );

    // Get sender info
    const senderResult = await query(
      'SELECT display_name, avatar_url FROM users WHERE id = $1',
      [userId]
    );
    const sender = senderResult.rows[0];

    const message = {
      id: messageId,
      conversationId,
      senderId: userId,
      senderName: sender?.display_name || 'Unknown',
      senderAvatar: sender?.avatar_url,
      content,
      type,
      createdAt: new Date().toISOString(),
      isRead: false
    };

    res.json({
      success: true,
      message
    });
  } catch (error) {
    logger.error('Failed to send message', { error });
    next(error);
  }
};

/**
 * Rooms Service
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Voice/Video rooms with 8/16 seat layouts
 * Room rankings and contribution tracking
 */

import { v4 as uuidv4 } from 'uuid';
import { query } from '../config/database.config';
import { logger } from '../utils/logger';

interface RoomCard {
  id: string;
  name: string;
  coverImage: string | null;
  ownerName: string;
  ownerAvatar: string | null;
  type: string;
  userCount: number;
  capacity: number;
  isLive: boolean;
  tags: string[];
}

interface Room {
  id: string;
  name: string;
  coverImage: string | null;
  ownerId: string;
  ownerName: string;
  ownerAvatar: string | null;
  type: string;
  mode: string;
  capacity: number;
  currentUsers: number;
  isLocked: boolean;
  tags: string[];
  seats: Seat[];
  createdAt: number;
}

interface Seat {
  position: number;
  userId: string | null;
  userName: string | null;
  userAvatar: string | null;
  userLevel: number | null;
  userVip: number | null;
  isMuted: boolean;
  isLocked: boolean;
}

interface RoomRanking {
  roomId: string;
  roomName: string;
  coverImage: string | null;
  ownerName: string;
  totalGiftsReceived: number;
  totalVisitors: number;
  score: number;
  rank: number;
}

interface ContributionRecord {
  userId: string;
  username: string;
  displayName: string;
  avatarUrl: string | null;
  giftValue: number;
  chatCount: number;
  timeSpentMinutes: number;
  totalScore: number;
}

// Get popular rooms from database
export const getPopularRooms = async (): Promise<RoomCard[]> => {
  try {
    const result = await query(
      `SELECT r.*, u.username as owner_name, u.avatar_url as owner_avatar,
              COUNT(rm.id) as user_count
       FROM rooms r
       LEFT JOIN users u ON r.owner_id = u.id
       LEFT JOIN room_members rm ON r.id = rm.room_id
       WHERE r.is_live = true
       GROUP BY r.id, u.username, u.avatar_url
       ORDER BY user_count DESC
       LIMIT 50`
    );
    
    return result.rows.map(row => ({
      id: row.id,
      name: row.name,
      coverImage: row.cover_url,
      ownerName: row.owner_name || 'Anonymous',
      ownerAvatar: row.owner_avatar,
      type: row.type,
      userCount: parseInt(row.user_count) || 0,
      capacity: row.max_participants || 100,
      isLive: row.is_live,
      tags: row.tags || []
    }));
  } catch (error) {
    logger.error('Failed to get popular rooms', { error });
    return [];
  }
};

export const getMyRooms = async (userId: string): Promise<RoomCard[]> => {
  try {
    const result = await query(
      `SELECT r.*, u.username as owner_name, u.avatar_url as owner_avatar,
              COUNT(rm.id) as user_count
       FROM rooms r
       LEFT JOIN users u ON r.owner_id = u.id
       LEFT JOIN room_members rm ON r.id = rm.room_id
       WHERE r.owner_id = $1
       GROUP BY r.id, u.username, u.avatar_url
       ORDER BY r.created_at DESC`,
      [userId]
    );
    
    return result.rows.map(row => ({
      id: row.id,
      name: row.name,
      coverImage: row.cover_url,
      ownerName: row.owner_name || 'Anonymous',
      ownerAvatar: row.owner_avatar,
      type: row.type,
      userCount: parseInt(row.user_count) || 0,
      capacity: row.max_participants || 100,
      isLive: row.is_live,
      tags: row.tags || []
    }));
  } catch (error) {
    logger.error('Failed to get user rooms', { userId, error });
    return [];
  }
};

export const getRoom = async (roomId: string): Promise<Room> => {
  try {
    const result = await query(
      `SELECT r.*, u.username as owner_name, u.avatar_url as owner_avatar
       FROM rooms r
       LEFT JOIN users u ON r.owner_id = u.id
       WHERE r.id = $1`,
      [roomId]
    );
    
    if (result.rows.length === 0) {
      throw new Error('Room not found');
    }
    
    const room = result.rows[0];
    
    // Get seats/members
    const membersResult = await query(
      `SELECT rm.*, u.username, u.display_name, u.avatar_url, u.level, u.vip_tier
       FROM room_members rm
       JOIN users u ON rm.user_id = u.id
       WHERE rm.room_id = $1
       ORDER BY rm.seat_position ASC`,
      [roomId]
    );
    
    const capacity = room.max_participants || 8;
    const seats: Seat[] = Array(capacity).fill(null).map((_, i) => {
      const member = membersResult.rows.find(m => m.seat_position === i);
      return {
        position: i,
        userId: member?.user_id || null,
        userName: member?.display_name || member?.username || null,
        userAvatar: member?.avatar_url || null,
        userLevel: member?.level || null,
        userVip: member?.vip_tier || null,
        isMuted: member?.is_muted || false,
        isLocked: false
      };
    });
    
    return {
      id: room.id,
      name: room.name,
      coverImage: room.cover_url,
      ownerId: room.owner_id,
      ownerName: room.owner_name || 'Anonymous',
      ownerAvatar: room.owner_avatar,
      type: room.type,
      mode: room.is_private ? 'PRIVATE' : 'FREE',
      capacity,
      currentUsers: membersResult.rows.length,
      isLocked: room.is_private,
      tags: room.tags || [],
      seats,
      createdAt: new Date(room.created_at).getTime()
    };
  } catch (error) {
    logger.error('Failed to get room', { roomId, error });
    throw error;
  }
};

export const createRoom = async (
  userId: string,
  name: string,
  type: string,
  capacity: number
): Promise<Room> => {
  try {
    const roomId = uuidv4();
    
    await query(
      `INSERT INTO rooms (id, name, type, owner_id, max_participants, created_at)
       VALUES ($1, $2, $3, $4, $5, NOW())`,
      [roomId, name, type, userId, capacity]
    );
    
    // Add owner as member
    await query(
      `INSERT INTO room_members (room_id, user_id, seat_position, role, joined_at)
       VALUES ($1, $2, 0, 'owner', NOW())`,
      [roomId, userId]
    );
    
    return getRoom(roomId);
  } catch (error) {
    logger.error('Failed to create room', { userId, name, error });
    throw error;
  }
};

export const joinRoom = async (roomId: string, userId: string): Promise<void> => {
  try {
    // Find empty seat
    const seatResult = await query(
      `SELECT seat_position FROM room_members WHERE room_id = $1 ORDER BY seat_position`,
      [roomId]
    );
    
    const occupiedSeats = seatResult.rows.map(r => r.seat_position);
    let emptySeat = 0;
    while (occupiedSeats.includes(emptySeat)) {
      emptySeat++;
    }
    
    await query(
      `INSERT INTO room_members (room_id, user_id, seat_position, role, joined_at)
       VALUES ($1, $2, $3, 'member', NOW())
       ON CONFLICT (room_id, user_id) DO UPDATE SET joined_at = NOW()`,
      [roomId, userId, emptySeat]
    );
    
    // Update room participant count
    await query(
      `UPDATE rooms SET current_participants = current_participants + 1 WHERE id = $1`,
      [roomId]
    );
  } catch (error) {
    logger.error('Failed to join room', { roomId, userId, error });
    throw error;
  }
};

export const leaveRoom = async (roomId: string, userId: string): Promise<void> => {
  try {
    await query(
      `DELETE FROM room_members WHERE room_id = $1 AND user_id = $2`,
      [roomId, userId]
    );
    
    // Update room participant count
    await query(
      `UPDATE rooms SET current_participants = GREATEST(current_participants - 1, 0) WHERE id = $1`,
      [roomId]
    );
  } catch (error) {
    logger.error('Failed to leave room', { roomId, userId, error });
    throw error;
  }
};

export const addToPlaylist = async (roomId: string, url: string): Promise<void> => {
  // Add video to playlist (stored in room data)
  await query(
    `UPDATE rooms SET is_video_mode = true WHERE id = $1`,
    [roomId]
  );
};

export const exitVideo = async (roomId: string): Promise<void> => {
  await query(
    `UPDATE rooms SET is_video_mode = false WHERE id = $1`,
    [roomId]
  );
};

// ==================== ROOM RANKINGS ====================

// Get room rankings
export const getRoomRankings = async (
  type: 'daily' | 'weekly' | 'monthly',
  limit: number,
  offset: number
): Promise<RoomRanking[]> => {
  try {
    const periodDate = getPeriodDate(type);
    
    const result = await query(
      `SELECT rr.*, r.name as room_name, r.cover_url, u.username as owner_name
       FROM room_rankings rr
       JOIN rooms r ON rr.room_id = r.id
       LEFT JOIN users u ON r.owner_id = u.id
       WHERE rr.ranking_type = $1 AND rr.period_date = $2
       ORDER BY rr.score DESC, rr.total_gifts_received DESC
       LIMIT $3 OFFSET $4`,
      [type, periodDate, limit, offset]
    );
    
    return result.rows.map((row, index) => ({
      roomId: row.room_id,
      roomName: row.room_name,
      coverImage: row.cover_url,
      ownerName: row.owner_name || 'Anonymous',
      totalGiftsReceived: Number(row.total_gifts_received),
      totalVisitors: row.total_visitors,
      score: Number(row.score),
      rank: offset + index + 1
    }));
  } catch (error) {
    logger.error('Failed to get room rankings', { type, error });
    return [];
  }
};

// Get contributions for a specific room
export const getRoomContributions = async (
  roomId: string,
  type: 'daily' | 'weekly' | 'monthly',
  limit: number,
  offset: number
): Promise<ContributionRecord[]> => {
  try {
    const dateCondition = getDateCondition(type);
    
    const result = await query(
      `SELECT rc.user_id, u.username, u.display_name, u.avatar_url,
              SUM(rc.gift_value) as gift_value,
              SUM(rc.chat_count) as chat_count,
              SUM(rc.time_spent_minutes) as time_spent_minutes,
              SUM(rc.gift_value + rc.chat_count * 10 + rc.time_spent_minutes * 5) as total_score
       FROM room_contributions rc
       JOIN users u ON rc.user_id = u.id
       WHERE rc.room_id = $1 AND ${dateCondition}
       GROUP BY rc.user_id, u.username, u.display_name, u.avatar_url
       ORDER BY total_score DESC
       LIMIT $2 OFFSET $3`,
      [roomId, limit, offset]
    );
    
    return result.rows.map(row => ({
      userId: row.user_id,
      username: row.username,
      displayName: row.display_name || row.username,
      avatarUrl: row.avatar_url,
      giftValue: Number(row.gift_value),
      chatCount: Number(row.chat_count),
      timeSpentMinutes: Number(row.time_spent_minutes),
      totalScore: Number(row.total_score)
    }));
  } catch (error) {
    logger.error('Failed to get room contributions', { roomId, type, error });
    return [];
  }
};

// Record a contribution
export const recordContribution = async (
  roomId: string,
  userId: string,
  giftValue: number,
  chatCount: number,
  timeSpentMinutes: number
): Promise<void> => {
  try {
    const today = new Date().toISOString().split('T')[0];
    
    await query(
      `INSERT INTO room_contributions (room_id, user_id, gift_value, chat_count, time_spent_minutes, date)
       VALUES ($1, $2, $3, $4, $5, $6)
       ON CONFLICT (room_id, user_id, date) DO UPDATE SET
         gift_value = room_contributions.gift_value + $3,
         chat_count = room_contributions.chat_count + $4,
         time_spent_minutes = room_contributions.time_spent_minutes + $5`,
      [roomId, userId, giftValue, chatCount, timeSpentMinutes, today]
    );
    
    // Update room ranking
    await updateRoomRanking(roomId, giftValue, chatCount > 0 ? 1 : 0);
  } catch (error) {
    logger.error('Failed to record contribution', { roomId, userId, error });
  }
};

// Update room ranking (internal)
async function updateRoomRanking(roomId: string, giftValue: number, visitorIncrement: number): Promise<void> {
  const today = new Date().toISOString().split('T')[0];
  const weekStart = getWeekStart();
  const monthStart = getMonthStart();
  
  // Update daily
  await query(
    `INSERT INTO room_rankings (room_id, ranking_type, period_date, total_gifts_received, total_visitors, score)
     VALUES ($1, 'daily', $2, $3, $4, $3)
     ON CONFLICT (room_id, ranking_type, period_date) DO UPDATE SET
       total_gifts_received = room_rankings.total_gifts_received + $3,
       total_visitors = room_rankings.total_visitors + $4,
       score = room_rankings.score + $3`,
    [roomId, today, giftValue, visitorIncrement]
  );
  
  // Update weekly
  await query(
    `INSERT INTO room_rankings (room_id, ranking_type, period_date, total_gifts_received, total_visitors, score)
     VALUES ($1, 'weekly', $2, $3, $4, $3)
     ON CONFLICT (room_id, ranking_type, period_date) DO UPDATE SET
       total_gifts_received = room_rankings.total_gifts_received + $3,
       total_visitors = room_rankings.total_visitors + $4,
       score = room_rankings.score + $3`,
    [roomId, weekStart, giftValue, visitorIncrement]
  );
  
  // Update monthly
  await query(
    `INSERT INTO room_rankings (room_id, ranking_type, period_date, total_gifts_received, total_visitors, score)
     VALUES ($1, 'monthly', $2, $3, $4, $3)
     ON CONFLICT (room_id, ranking_type, period_date) DO UPDATE SET
       total_gifts_received = room_rankings.total_gifts_received + $3,
       total_visitors = room_rankings.total_visitors + $4,
       score = room_rankings.score + $3`,
    [roomId, monthStart, giftValue, visitorIncrement]
  );
}

// Helper functions
function getPeriodDate(type: 'daily' | 'weekly' | 'monthly'): string {
  switch (type) {
    case 'daily':
      return new Date().toISOString().split('T')[0];
    case 'weekly':
      return getWeekStart();
    case 'monthly':
      return getMonthStart();
  }
}

function getWeekStart(): string {
  const now = new Date();
  const dayOfWeek = now.getDay();
  const diff = now.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);
  const monday = new Date(now.setDate(diff));
  return monday.toISOString().split('T')[0];
}

function getMonthStart(): string {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`;
}

function getDateCondition(type: 'daily' | 'weekly' | 'monthly'): string {
  switch (type) {
    case 'daily':
      return 'rc.date = CURRENT_DATE';
    case 'weekly':
      return 'rc.date >= DATE_TRUNC(\'week\', CURRENT_DATE)';
    case 'monthly':
      return 'rc.date >= DATE_TRUNC(\'month\', CURRENT_DATE)';
  }
}

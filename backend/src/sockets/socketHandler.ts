/**
 * Socket.io Handler
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Real-time communication handler for voice rooms and chat
 */

import { Server as HttpServer } from 'http';
import { Server, Socket } from 'socket.io';
import { socketConfig, SOCKET_EVENTS, NAMESPACES } from '../config/socket.config';
import { logger } from '../utils/logger';
import { verifyToken } from '../middleware/auth';
import { query } from '../config/database.config';

interface AuthenticatedSocket extends Socket {
  userId?: string;
  username?: string;
  roomId?: string;
}

interface RoomUser {
  id: string;
  username: string;
  displayName: string;
  avatarUrl: string;
  role: string;
  seatNumber: number | null;
  isMuted: boolean;
  isSpeaking: boolean;
  isHandRaised: boolean;
  isVideoOn: boolean;
}

// Store connected users
const connectedUsers = new Map<string, Set<string>>(); // userId -> Set of socketIds
const roomUsers = new Map<string, Map<string, RoomUser>>(); // roomId -> Map of userId -> RoomUser

/**
 * Initialize Socket.io server
 */
export const initializeSocket = (httpServer: HttpServer): Server => {
  const io = new Server(httpServer, socketConfig);
  
  // Main namespace
  io.on('connection', (socket: AuthenticatedSocket) => {
    logger.info('Socket connected', { socketId: socket.id });
    
    // Authentication
    socket.on(SOCKET_EVENTS.AUTHENTICATE, async (token: string) => {
      try {
        const user = await verifyToken(token);
        
        if (!user) {
          socket.emit(SOCKET_EVENTS.UNAUTHORIZED, { message: 'Invalid token' });
          socket.disconnect();
          return;
        }
        
        socket.userId = user.id;
        socket.username = user.username;
        
        // Track connected user
        if (!connectedUsers.has(user.id)) {
          connectedUsers.set(user.id, new Set());
        }
        connectedUsers.get(user.id)!.add(socket.id);
        
        // Update user status to online
        await query(
          'UPDATE users SET status = $1, last_login_at = CURRENT_TIMESTAMP WHERE id = $2',
          ['online', user.id]
        );
        
        socket.emit(SOCKET_EVENTS.AUTHENTICATED, { userId: user.id });
        
        logger.info('Socket authenticated', { socketId: socket.id, userId: user.id });
      } catch (error: any) {
        logger.error('Socket authentication failed', { error: error.message });
        socket.emit(SOCKET_EVENTS.UNAUTHORIZED, { message: 'Authentication failed' });
        socket.disconnect();
      }
    });
    
    // Join room
    socket.on(SOCKET_EVENTS.ROOM_JOIN, async (roomId: string) => {
      if (!socket.userId) {
        socket.emit(SOCKET_EVENTS.ERROR, { message: 'Not authenticated' });
        return;
      }
      
      try {
        // Get room info
        const roomResult = await query('SELECT * FROM rooms WHERE id = $1', [roomId]);
        
        if (roomResult.rows.length === 0) {
          socket.emit(SOCKET_EVENTS.ERROR, { message: 'Room not found' });
          return;
        }
        
        const room = roomResult.rows[0];
        
        // Check if room is full
        if (room.current_participants >= room.max_participants) {
          socket.emit(SOCKET_EVENTS.ERROR, { message: 'Room is full' });
          return;
        }
        
        // Get user info
        const userResult = await query(
          'SELECT id, username, display_name, avatar_url FROM users WHERE id = $1',
          [socket.userId]
        );
        
        if (userResult.rows.length === 0) {
          socket.emit(SOCKET_EVENTS.ERROR, { message: 'User not found' });
          return;
        }
        
        const user = userResult.rows[0];
        
        // Check if already a member
        const memberResult = await query(
          'SELECT * FROM room_members WHERE room_id = $1 AND user_id = $2',
          [roomId, socket.userId]
        );
        
        let role = 'member';
        if (memberResult.rows.length > 0) {
          role = memberResult.rows[0].role;
        } else {
          // Add as new member
          role = room.owner_id === socket.userId ? 'owner' : 'member';
          await query(
            `INSERT INTO room_members (room_id, user_id, role, joined_at)
             VALUES ($1, $2, $3, CURRENT_TIMESTAMP)
             ON CONFLICT (room_id, user_id) DO UPDATE SET joined_at = CURRENT_TIMESTAMP`,
            [roomId, socket.userId, role]
          );
        }
        
        // Leave previous room if any
        if (socket.roomId) {
          await handleLeaveRoom(socket, io);
        }
        
        // Join the room
        socket.join(roomId);
        socket.roomId = roomId;
        
        // Track room user
        if (!roomUsers.has(roomId)) {
          roomUsers.set(roomId, new Map());
        }
        
        const roomUser: RoomUser = {
          id: user.id,
          username: user.username,
          displayName: user.display_name || user.username,
          avatarUrl: user.avatar_url || '',
          role,
          seatNumber: null,
          isMuted: true,
          isSpeaking: false,
          isHandRaised: false,
          isVideoOn: false,
        };
        
        roomUsers.get(roomId)!.set(socket.userId, roomUser);
        
        // Get all room users
        const users = Array.from(roomUsers.get(roomId)!.values());
        
        // Notify user of successful join
        socket.emit(SOCKET_EVENTS.ROOM_JOINED, {
          room,
          users,
          currentUser: roomUser,
        });
        
        // Notify other users
        socket.to(roomId).emit(SOCKET_EVENTS.ROOM_USER_JOINED, roomUser);
        
        logger.info('User joined room', { userId: socket.userId, roomId });
      } catch (error: any) {
        logger.error('Join room failed', { error: error.message });
        socket.emit(SOCKET_EVENTS.ERROR, { message: 'Failed to join room' });
      }
    });
    
    // Leave room
    socket.on(SOCKET_EVENTS.ROOM_LEAVE, async () => {
      await handleLeaveRoom(socket, io);
    });
    
    // Toggle mute
    socket.on(SOCKET_EVENTS.MUTE_TOGGLE, async (isMuted: boolean) => {
      if (!socket.userId || !socket.roomId) return;
      
      const roomUserMap = roomUsers.get(socket.roomId);
      if (roomUserMap && roomUserMap.has(socket.userId)) {
        const user = roomUserMap.get(socket.userId)!;
        user.isMuted = isMuted;
        
        await query(
          'UPDATE room_members SET is_muted = $1 WHERE room_id = $2 AND user_id = $3',
          [isMuted, socket.roomId, socket.userId]
        );
        
        io.to(socket.roomId).emit(SOCKET_EVENTS.SEAT_UPDATED, {
          userId: socket.userId,
          isMuted,
        });
      }
    });
    
    // Speaking status
    socket.on(SOCKET_EVENTS.SPEAKING_START, () => {
      if (!socket.userId || !socket.roomId) return;
      
      const roomUserMap = roomUsers.get(socket.roomId);
      if (roomUserMap && roomUserMap.has(socket.userId)) {
        const user = roomUserMap.get(socket.userId)!;
        user.isSpeaking = true;
        
        io.to(socket.roomId).emit(SOCKET_EVENTS.SPEAKING_START, {
          userId: socket.userId,
        });
      }
    });
    
    socket.on(SOCKET_EVENTS.SPEAKING_STOP, () => {
      if (!socket.userId || !socket.roomId) return;
      
      const roomUserMap = roomUsers.get(socket.roomId);
      if (roomUserMap && roomUserMap.has(socket.userId)) {
        const user = roomUserMap.get(socket.userId)!;
        user.isSpeaking = false;
        
        io.to(socket.roomId).emit(SOCKET_EVENTS.SPEAKING_STOP, {
          userId: socket.userId,
        });
      }
    });
    
    // Raise/lower hand
    socket.on(SOCKET_EVENTS.HAND_RAISE, () => {
      if (!socket.userId || !socket.roomId) return;
      
      const roomUserMap = roomUsers.get(socket.roomId);
      if (roomUserMap && roomUserMap.has(socket.userId)) {
        const user = roomUserMap.get(socket.userId)!;
        user.isHandRaised = true;
        
        io.to(socket.roomId).emit(SOCKET_EVENTS.HAND_RAISE, {
          userId: socket.userId,
        });
      }
    });
    
    socket.on(SOCKET_EVENTS.HAND_LOWER, () => {
      if (!socket.userId || !socket.roomId) return;
      
      const roomUserMap = roomUsers.get(socket.roomId);
      if (roomUserMap && roomUserMap.has(socket.userId)) {
        const user = roomUserMap.get(socket.userId)!;
        user.isHandRaised = false;
        
        io.to(socket.roomId).emit(SOCKET_EVENTS.HAND_LOWER, {
          userId: socket.userId,
        });
      }
    });
    
    // Request seat
    socket.on(SOCKET_EVENTS.SEAT_REQUEST, async (seatNumber: number) => {
      if (!socket.userId || !socket.roomId) return;
      
      try {
        // Check if seat is available
        const existingMember = await query(
          'SELECT * FROM room_members WHERE room_id = $1 AND seat_number = $2',
          [socket.roomId, seatNumber]
        );
        
        if (existingMember.rows.length > 0) {
          socket.emit(SOCKET_EVENTS.ERROR, { message: 'Seat is occupied' });
          return;
        }
        
        // Assign seat
        await query(
          'UPDATE room_members SET seat_number = $1 WHERE room_id = $2 AND user_id = $3',
          [seatNumber, socket.roomId, socket.userId]
        );
        
        const roomUserMap = roomUsers.get(socket.roomId);
        if (roomUserMap && roomUserMap.has(socket.userId)) {
          const user = roomUserMap.get(socket.userId)!;
          user.seatNumber = seatNumber;
          user.isMuted = false;
          
          io.to(socket.roomId).emit(SOCKET_EVENTS.SEAT_UPDATED, {
            userId: socket.userId,
            seatNumber,
            isMuted: false,
          });
        }
      } catch (error: any) {
        logger.error('Seat request failed', { error: error.message });
        socket.emit(SOCKET_EVENTS.ERROR, { message: 'Failed to request seat' });
      }
    });
    
    // Release seat
    socket.on(SOCKET_EVENTS.SEAT_RELEASE, async () => {
      if (!socket.userId || !socket.roomId) return;
      
      try {
        await query(
          'UPDATE room_members SET seat_number = NULL, is_muted = true WHERE room_id = $1 AND user_id = $2',
          [socket.roomId, socket.userId]
        );
        
        const roomUserMap = roomUsers.get(socket.roomId);
        if (roomUserMap && roomUserMap.has(socket.userId)) {
          const user = roomUserMap.get(socket.userId)!;
          user.seatNumber = null;
          user.isMuted = true;
          
          io.to(socket.roomId).emit(SOCKET_EVENTS.SEAT_UPDATED, {
            userId: socket.userId,
            seatNumber: null,
            isMuted: true,
          });
        }
      } catch (error: any) {
        logger.error('Seat release failed', { error: error.message });
      }
    });
    
    // Send message
    socket.on(SOCKET_EVENTS.MESSAGE_SEND, async (data: { content: string; type?: string }) => {
      if (!socket.userId || !socket.roomId) return;
      
      try {
        const result = await query(
          `INSERT INTO messages (room_id, user_id, content, type, created_at)
           VALUES ($1, $2, $3, $4, CURRENT_TIMESTAMP)
           RETURNING *`,
          [socket.roomId, socket.userId, data.content, data.type || 'text']
        );
        
        const message = result.rows[0];
        
        // Get user info for the message
        const userResult = await query(
          'SELECT username, display_name, avatar_url FROM users WHERE id = $1',
          [socket.userId]
        );
        
        const user = userResult.rows[0];
        
        const messageWithUser = {
          ...message,
          user: {
            id: socket.userId,
            username: user?.username,
            displayName: user?.display_name,
            avatarUrl: user?.avatar_url,
          },
        };
        
        io.to(socket.roomId).emit(SOCKET_EVENTS.MESSAGE_RECEIVED, messageWithUser);
      } catch (error: any) {
        logger.error('Send message failed', { error: error.message });
        socket.emit(SOCKET_EVENTS.ERROR, { message: 'Failed to send message' });
      }
    });
    
    // Typing indicator
    socket.on(SOCKET_EVENTS.TYPING_START, () => {
      if (!socket.userId || !socket.roomId) return;
      socket.to(socket.roomId).emit(SOCKET_EVENTS.TYPING_START, { userId: socket.userId });
    });
    
    socket.on(SOCKET_EVENTS.TYPING_STOP, () => {
      if (!socket.userId || !socket.roomId) return;
      socket.to(socket.roomId).emit(SOCKET_EVENTS.TYPING_STOP, { userId: socket.userId });
    });
    
    // Send gift
    socket.on(SOCKET_EVENTS.GIFT_SEND, async (data: { giftId: string; receiverId: string }) => {
      if (!socket.userId || !socket.roomId) return;
      
      try {
        // Get gift info - select only needed columns
        const giftResult = await query(
          'SELECT id, name, price_coins, diamond_value, animation_url FROM gifts WHERE id = $1',
          [data.giftId]
        );
        
        if (giftResult.rows.length === 0) {
          socket.emit(SOCKET_EVENTS.ERROR, { message: 'Gift not found' });
          return;
        }
        
        const gift = giftResult.rows[0];
        
        // Check user balance
        const userResult = await query('SELECT coins FROM users WHERE id = $1', [socket.userId]);
        
        if (userResult.rows.length === 0 || userResult.rows[0].coins < gift.price_coins) {
          socket.emit(SOCKET_EVENTS.ERROR, { message: 'Insufficient coins' });
          return;
        }
        
        // Deduct coins from sender
        await query(
          'UPDATE users SET coins = coins - $1 WHERE id = $2',
          [gift.price_coins, socket.userId]
        );
        
        // Add diamonds to receiver
        await query(
          'UPDATE users SET diamonds = diamonds + $1 WHERE id = $2',
          [gift.diamond_value, data.receiverId]
        );
        
        // Record transaction
        await query(
          `INSERT INTO gift_transactions (gift_id, sender_id, receiver_id, room_id, coins_spent, diamonds_earned, created_at)
           VALUES ($1, $2, $3, $4, $5, $6, CURRENT_TIMESTAMP)`,
          [data.giftId, socket.userId, data.receiverId, socket.roomId, gift.price_coins, gift.diamond_value]
        );
        
        // Broadcast gift animation
        io.to(socket.roomId).emit(SOCKET_EVENTS.GIFT_RECEIVED, {
          giftId: data.giftId,
          giftName: gift.name,
          giftAnimationUrl: gift.animation_url,
          senderId: socket.userId,
          receiverId: data.receiverId,
          coinsSpent: gift.price_coins,
          diamondsEarned: gift.diamond_value,
        });
        
        logger.info('Gift sent', { senderId: socket.userId, receiverId: data.receiverId, giftId: data.giftId });
      } catch (error: any) {
        logger.error('Send gift failed', { error: error.message });
        socket.emit(SOCKET_EVENTS.ERROR, { message: 'Failed to send gift' });
      }
    });
    
    // Disconnect
    socket.on(SOCKET_EVENTS.DISCONNECT, async (reason) => {
      logger.info('Socket disconnected', { socketId: socket.id, userId: socket.userId, reason });
      
      if (socket.userId) {
        // Remove from connected users
        const userSockets = connectedUsers.get(socket.userId);
        if (userSockets) {
          userSockets.delete(socket.id);
          
          // If no more connections, update status to offline
          if (userSockets.size === 0) {
            connectedUsers.delete(socket.userId);
            await query(
              'UPDATE users SET status = $1 WHERE id = $2',
              ['offline', socket.userId]
            );
          }
        }
        
        // Leave room
        if (socket.roomId) {
          await handleLeaveRoom(socket, io);
        }
      }
    });
  });
  
  return io;
};

/**
 * Handle user leaving a room
 */
async function handleLeaveRoom(socket: AuthenticatedSocket, io: Server): Promise<void> {
  if (!socket.userId || !socket.roomId) return;
  
  try {
    const roomId = socket.roomId;
    
    // Remove from room_members
    await query(
      'DELETE FROM room_members WHERE room_id = $1 AND user_id = $2',
      [roomId, socket.userId]
    );
    
    // Remove from tracking
    const roomUserMap = roomUsers.get(roomId);
    if (roomUserMap) {
      roomUserMap.delete(socket.userId);
      
      if (roomUserMap.size === 0) {
        roomUsers.delete(roomId);
      }
    }
    
    // Leave socket room
    socket.leave(roomId);
    
    // Notify others
    io.to(roomId).emit(SOCKET_EVENTS.ROOM_USER_LEFT, { userId: socket.userId });
    
    socket.roomId = undefined;
    
    logger.info('User left room', { userId: socket.userId, roomId });
  } catch (error: any) {
    logger.error('Leave room failed', { error: error.message });
  }
}

/**
 * Get number of connected users
 */
export const getConnectedUsersCount = (): number => {
  return connectedUsers.size;
};

/**
 * Get users in a room
 */
export const getRoomUsers = (roomId: string): RoomUser[] => {
  const roomUserMap = roomUsers.get(roomId);
  return roomUserMap ? Array.from(roomUserMap.values()) : [];
};

/**
 * Check if user is online
 */
export const isUserOnline = (userId: string): boolean => {
  return connectedUsers.has(userId);
};

export default {
  initializeSocket,
  getConnectedUsersCount,
  getRoomUsers,
  isUserOnline,
};

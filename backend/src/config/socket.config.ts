/**
 * Socket.io Configuration Module
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Real-time communication configuration
 */

import { ServerOptions } from 'socket.io';

export const socketConfig: Partial<ServerOptions> = {
  cors: {
    origin: process.env.CORS_ORIGIN || '*',
    methods: ['GET', 'POST'],
    credentials: true,
  },
  
  // Connection settings
  pingTimeout: 60000,
  pingInterval: 25000,
  
  // Transport configuration
  transports: ['websocket', 'polling'],
  
  // Allow upgrades from polling to websocket
  allowUpgrades: true,
  
  // Connection state recovery
  connectionStateRecovery: {
    maxDisconnectionDuration: 2 * 60 * 1000, // 2 minutes
    skipMiddlewares: false,
  },
  
  // Path
  path: '/socket.io/',
};

// Socket events
export const SOCKET_EVENTS = {
  // Connection
  CONNECT: 'connect',
  DISCONNECT: 'disconnect',
  ERROR: 'error',
  
  // Authentication
  AUTHENTICATE: 'authenticate',
  AUTHENTICATED: 'authenticated',
  UNAUTHORIZED: 'unauthorized',
  
  // Room events
  ROOM_JOIN: 'room:join',
  ROOM_LEAVE: 'room:leave',
  ROOM_JOINED: 'room:joined',
  ROOM_LEFT: 'room:left',
  ROOM_USER_JOINED: 'room:user_joined',
  ROOM_USER_LEFT: 'room:user_left',
  ROOM_UPDATED: 'room:updated',
  
  // Voice/Video events
  MUTE_TOGGLE: 'voice:mute_toggle',
  SPEAKING_START: 'voice:speaking_start',
  SPEAKING_STOP: 'voice:speaking_stop',
  VIDEO_TOGGLE: 'video:toggle',
  HAND_RAISE: 'hand:raise',
  HAND_LOWER: 'hand:lower',
  
  // Seat events
  SEAT_REQUEST: 'seat:request',
  SEAT_ASSIGN: 'seat:assign',
  SEAT_RELEASE: 'seat:release',
  SEAT_UPDATED: 'seat:updated',
  
  // Chat events
  MESSAGE_SEND: 'message:send',
  MESSAGE_RECEIVED: 'message:received',
  MESSAGE_DELETED: 'message:deleted',
  TYPING_START: 'typing:start',
  TYPING_STOP: 'typing:stop',
  
  // Gift events
  GIFT_SEND: 'gift:send',
  GIFT_RECEIVED: 'gift:received',
  
  // Notification events
  NOTIFICATION: 'notification',
  
  // Presence events
  PRESENCE_UPDATE: 'presence:update',
  USER_STATUS_CHANGED: 'user:status_changed',
  
  // Admin events
  USER_KICKED: 'admin:user_kicked',
  USER_MUTED: 'admin:user_muted',
  ROOM_CLOSED: 'admin:room_closed',
};

// Room namespaces
export const NAMESPACES = {
  ROOMS: '/rooms',
  CHAT: '/chat',
  NOTIFICATIONS: '/notifications',
};

export default {
  socketConfig,
  SOCKET_EVENTS,
  NAMESPACES,
};

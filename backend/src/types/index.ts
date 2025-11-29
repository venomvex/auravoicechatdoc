/**
 * Type Definitions Index
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Exports all custom types for the application
 */

export * from './express.d';

// JWT payload type
export interface JwtPayload {
  userId: string;
  phone?: string;
  email?: string;
  username?: string;
  iat?: number;
  exp?: number;
}

// API Response types
export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: any;
  };
}

// Pagination types
export interface PaginationParams {
  page?: number;
  pageSize?: number;
}

export interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
  };
}

// User role types
export type UserRole = 'user' | 'guide' | 'admin' | 'country_admin' | 'seller' | 'owner';

// Room types
export type RoomType = 'voice' | 'video' | 'music';
export type RoomMemberRole = 'owner' | 'admin' | 'moderator' | 'member';

// Transaction types
export type TransactionType = 'deposit' | 'withdrawal' | 'transfer' | 'gift' | 'purchase' | 'reward';
export type TransactionStatus = 'pending' | 'completed' | 'failed' | 'cancelled';

// VIP types
export type VipTier = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10;

// Game types
export type GameType = 'lucky_spin' | 'dice' | 'card_flip' | 'treasure' | 'lucky_number' | 'coin_toss' | 'slot';
export type GameStatus = 'active' | 'completed';

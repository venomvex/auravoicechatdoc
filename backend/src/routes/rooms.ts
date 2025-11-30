/**
 * Rooms Routes
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Voice/Video room management
 * Room rankings and contributions
 */

import { Router } from 'express';
import { authenticate, optionalAuth } from '../middleware/auth';
import { generalLimiter } from '../middleware/rateLimiter';
import { validateAddToPlaylist } from '../middleware/validation';
import * as roomsController from '../controllers/roomsController';

const router = Router();

// ==================== ROOM DISCOVERY ====================
// Get popular rooms
router.get('/popular', generalLimiter, optionalAuth, roomsController.getPopularRooms);

// Get my rooms
router.get('/mine', generalLimiter, authenticate, roomsController.getMyRooms);

// ==================== ROOM RANKINGS ====================
// Get room rankings (daily/weekly/monthly)
router.get('/rankings', generalLimiter, optionalAuth, roomsController.getRoomRankings);

// Get room contribution leaderboard
router.get('/:roomId/contributions', generalLimiter, optionalAuth, roomsController.getRoomContributions);

// Record contribution (internal, called when gifts sent)
router.post('/:roomId/contributions', generalLimiter, authenticate, roomsController.recordContribution);

// ==================== ROOM OPERATIONS ====================
// Get room by ID
router.get('/:roomId', generalLimiter, optionalAuth, roomsController.getRoom);

// Create room
router.post('/', generalLimiter, authenticate, roomsController.createRoom);

// Join room
router.post('/:roomId/join', generalLimiter, authenticate, roomsController.joinRoom);

// Leave room
router.post('/:roomId/leave', generalLimiter, authenticate, roomsController.leaveRoom);

// ==================== VIDEO MODE ====================
// Video - Add to playlist
router.post('/:roomId/video/playlist', generalLimiter, authenticate, validateAddToPlaylist, roomsController.addToPlaylist);

// Video - Exit
router.post('/:roomId/video/exit', generalLimiter, authenticate, roomsController.exitVideo);

export default router;

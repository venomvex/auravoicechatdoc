/**
 * Games Routes
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Endpoints for all game types:
 * - Lucky 777 Pro (5-line slot machine)
 * - Lucky 77 Pro (Single-line slot machine)
 * - Greedy Baby (Food wheel selection game)
 * - Lucky Fruit (3x3 grid fruit selection)
 * - Gift Wheel System (Gift wheel with draw records)
 */

import { Router } from 'express';
import { authenticate } from '../middleware/auth';
import { generalLimiter } from '../middleware/rateLimiter';
import * as gamesController from '../controllers/gamesController';

const router = Router();

// Apply rate limiting before authentication for security
router.use(generalLimiter);
router.use(authenticate);

// Get available games
router.get('/', gamesController.getGames);

// Get game stats
router.get('/stats', gamesController.getGameStats);

// Get jackpots
router.get('/jackpots', gamesController.getJackpots);
router.get('/jackpots/:gameType', gamesController.getJackpot);

// Game sessions
router.post('/:gameType/start', gamesController.startGame);
router.post('/:gameType/action', gamesController.gameAction);
router.post('/:gameType/cashout', gamesController.cashout);

// Game history
router.get('/:gameType/history', gamesController.getGameHistory);

// Gift Wheel specific endpoints
router.get('/gift-wheel/draw-records', gamesController.getGiftWheelRecords);

export default router;

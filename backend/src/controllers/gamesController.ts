/**
 * Games Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import * as gamesService from '../services/gamesService';
import { logger } from '../utils/logger';

// Get available games
export const getGames = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const games = await gamesService.getAvailableGames();
    res.json({ games });
  } catch (error) {
    next(error);
  }
};

// Get game stats for user
export const getGameStats = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    if (!userId) {
      res.status(401).json({ error: { code: 'UNAUTHORIZED', message: 'User not authenticated' } });
      return;
    }
    const stats = await gamesService.getUserGameStats(userId);
    res.json({ stats });
  } catch (error) {
    next(error);
  }
};

// Get all jackpots
export const getJackpots = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const jackpots = await gamesService.getAllJackpots();
    res.json({ jackpots });
  } catch (error) {
    next(error);
  }
};

// Get jackpot for specific game
export const getJackpot = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { gameType } = req.params;
    const jackpot = await gamesService.getJackpot(gameType);
    res.json({ jackpot });
  } catch (error) {
    next(error);
  }
};

// Start game session
export const startGame = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    if (!userId) {
      res.status(401).json({ error: { code: 'UNAUTHORIZED', message: 'User not authenticated' } });
      return;
    }
    const { gameType } = req.params;
    const { betAmount, roomId } = req.body;

    const session = await gamesService.startGameSession({
      userId,
      gameType,
      betAmount,
      roomId
    });

    res.json({ session });
  } catch (error) {
    next(error);
  }
};

// Perform game action
export const gameAction = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    if (!userId) {
      res.status(401).json({ error: { code: 'UNAUTHORIZED', message: 'User not authenticated' } });
      return;
    }
    const { gameType } = req.params;
    const { sessionId, action, data } = req.body;

    const result = await gamesService.performGameAction({
      userId,
      sessionId,
      gameType,
      action,
      data
    });

    res.json({ result });
  } catch (error) {
    next(error);
  }
};

// Cashout game
export const cashout = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    if (!userId) {
      res.status(401).json({ error: { code: 'UNAUTHORIZED', message: 'User not authenticated' } });
      return;
    }
    const { gameType } = req.params;
    const { sessionId } = req.body;

    const result = await gamesService.cashoutGame({
      userId,
      sessionId,
      gameType
    });

    res.json({ result });
  } catch (error) {
    next(error);
  }
};

// Get game history
export const getGameHistory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    if (!userId) {
      res.status(401).json({ error: { code: 'UNAUTHORIZED', message: 'User not authenticated' } });
      return;
    }
    const { gameType } = req.params;
    const { page = 1, limit = 20 } = req.query;

    const history = await gamesService.getGameHistory({
      userId,
      gameType,
      page: Number(page),
      limit: Number(limit)
    });

    res.json(history);
  } catch (error) {
    next(error);
  }
};

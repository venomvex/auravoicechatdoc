/**
 * Games Service
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Complete implementation of all games:
 * - Lucky Spin
 * - Dice Roll
 * - Card Flip
 * - Treasure Box
 * - Lucky Number
 * - Coin Toss
 * - Slot Machine
 */

import { v4 as uuidv4 } from 'uuid';
import { logger } from '../utils/logger';

// Game types
type GameType = 'lucky_spin' | 'dice' | 'card_flip' | 'treasure' | 'lucky_number' | 'coin_toss' | 'slot';

// Game configurations
const GAME_CONFIGS = {
  lucky_spin: {
    name: 'Lucky Spin',
    minBet: 1000,
    maxBet: 1000000,
    segments: [
      { label: '2x', multiplier: 2, probability: 25 },
      { label: '3x', multiplier: 3, probability: 20 },
      { label: '5x', multiplier: 5, probability: 15 },
      { label: '10x', multiplier: 10, probability: 10 },
      { label: '25x', multiplier: 25, probability: 5 },
      { label: '50x', multiplier: 50, probability: 3 },
      { label: '100x', multiplier: 100, probability: 1.5 },
      { label: 'Jackpot', multiplier: 500, probability: 0.5 },
      { label: 'Lose', multiplier: 0, probability: 20 }
    ]
  },
  dice: {
    name: 'Dice Roll',
    minBet: 500,
    maxBet: 500000,
    betTypes: {
      high: { outcomes: [8, 9, 10, 11, 12], multiplier: 1.8 },
      low: { outcomes: [2, 3, 4, 5, 6], multiplier: 1.8 },
      seven: { outcomes: [7], multiplier: 5 },
      double: { multiplier: 10 },
      snake_eyes: { multiplier: 30 },
      boxcars: { multiplier: 30 }
    }
  },
  card_flip: {
    name: 'Card Flip',
    minBet: 1000,
    maxBet: 500000,
    streakMultipliers: [1.5, 1.5, 2, 2, 3, 5]
  },
  treasure: {
    name: 'Treasure Box',
    minBet: 5000,
    maxBet: 100000,
    boxTypes: ['coin', 'bomb', '2x', '3x']
  },
  lucky_number: {
    name: 'Lucky Number',
    minBet: 1000,
    maxBet: 250000,
    payouts: [
      { distance: 0, multiplier: 100 },
      { distance: 1, multiplier: 50 },
      { distance: 3, multiplier: 20 },
      { distance: 5, multiplier: 10 },
      { distance: 10, multiplier: 5 },
      { distance: 20, multiplier: 2 }
    ]
  },
  coin_toss: {
    name: 'Coin Toss',
    minBet: 500,
    maxBet: 250000,
    singleMultiplier: 1.9,
    doubleMultiplier: 3.6,
    tripleMultiplier: 6.8
  },
  slot: {
    name: 'Slot Machine',
    minBet: 1000,
    maxBet: 500000,
    symbols: ['🍒', '🍋', '🍊', '⭐', '💎', '7️⃣', '👑'],
    payouts: {
      '🍒🍒🍒': 5,
      '🍋🍋🍋': 10,
      '🍊🍊🍊': 15,
      '⭐⭐⭐': 25,
      '💎💎💎': 50,
      '7️⃣7️⃣7️⃣': 100,
      '👑👑👑': 'jackpot'
    } as Record<string, number | string>
  }
};

// In-memory storage (use Redis in production for scalability)
// TODO: Replace with Redis/Database for production deployment
// Example: const redis = new Redis(process.env.REDIS_URL);
const gameSessions = new Map<string, GameSession>();
const jackpots: Record<string, number> = {
  slot: 10000000 // Starting jackpot - persist this in Redis/DB
};

interface GameSession {
  id: string;
  userId: string;
  gameType: GameType;
  betAmount: number;
  roomId?: string;
  status: 'active' | 'completed';
  data: any;
  createdAt: Date;
  completedAt?: Date;
  result?: any;
  winAmount?: number;
}

interface StartGameParams {
  userId: string;
  gameType: string;
  betAmount: number;
  roomId?: string;
}

interface GameActionParams {
  userId: string;
  sessionId: string;
  gameType: string;
  action: string;
  data?: any;
}

interface CashoutParams {
  userId: string;
  sessionId: string;
  gameType: string;
}

// Get available games
export const getAvailableGames = async () => {
  return Object.entries(GAME_CONFIGS).map(([type, config]) => ({
    type,
    name: config.name,
    minBet: config.minBet,
    maxBet: config.maxBet
  }));
};

// Get user's game stats
export const getUserGameStats = async (userId: string) => {
  // In production, fetch from database
  return {
    totalPlayed: 0,
    totalWon: 0,
    totalLost: 0,
    biggestWin: 0,
    favoriteGame: null
  };
};

// Get all jackpots
export const getAllJackpots = async () => {
  return Object.entries(jackpots).map(([game, amount]) => ({
    game,
    amount,
    lastWinner: null,
    lastWinDate: null
  }));
};

// Get specific jackpot
export const getJackpot = async (gameType: string) => {
  return {
    game: gameType,
    amount: jackpots[gameType] || 0,
    lastWinner: null,
    lastWinDate: null
  };
};

// Start game session
export const startGameSession = async (params: StartGameParams) => {
  const { userId, gameType, betAmount, roomId } = params;
  
  const config = GAME_CONFIGS[gameType as GameType];
  if (!config) {
    throw new Error('Invalid game type');
  }
  
  if (betAmount < config.minBet || betAmount > config.maxBet) {
    throw new Error(`Bet amount must be between ${config.minBet} and ${config.maxBet}`);
  }
  
  // TODO: Check user balance and deduct bet
  
  const sessionId = uuidv4();
  const session: GameSession = {
    id: sessionId,
    userId,
    gameType: gameType as GameType,
    betAmount,
    roomId,
    status: 'active',
    data: initializeGameData(gameType as GameType),
    createdAt: new Date()
  };
  
  gameSessions.set(sessionId, session);
  
  return {
    sessionId,
    gameType,
    betAmount,
    data: session.data
  };
};

// Initialize game-specific data
const initializeGameData = (gameType: GameType): any => {
  switch (gameType) {
    case 'card_flip':
      const cards = generateDeck();
      return {
        currentCard: drawCard(cards),
        deck: cards,
        streak: 0,
        currentMultiplier: 1
      };
    case 'treasure':
      return {
        grid: generateTreasureGrid(),
        revealed: [],
        currentPot: 0
      };
    default:
      return {};
  }
};

// Perform game action
export const performGameAction = async (params: GameActionParams) => {
  const { userId, sessionId, gameType, action, data } = params;
  
  const session = gameSessions.get(sessionId);
  if (!session || session.userId !== userId) {
    throw new Error('Session not found');
  }
  
  if (session.status !== 'active') {
    throw new Error('Session already completed');
  }
  
  switch (gameType) {
    case 'lucky_spin':
      return spinWheel(session);
    case 'dice':
      return rollDice(session, data.betType);
    case 'card_flip':
      return flipCard(session, data.guess);
    case 'treasure':
      return openBox(session, data.boxIndex);
    case 'lucky_number':
      return checkNumber(session, data.guess);
    case 'coin_toss':
      return tossCoin(session, data.guess, data.rounds || 1);
    case 'slot':
      return spinSlot(session);
    default:
      throw new Error('Unknown game type');
  }
};

// Lucky Spin
const spinWheel = (session: GameSession) => {
  const config = GAME_CONFIGS.lucky_spin;
  const random = Math.random() * 100;
  
  let cumulative = 0;
  let result = config.segments[config.segments.length - 1];
  
  for (const segment of config.segments) {
    cumulative += segment.probability;
    if (random <= cumulative) {
      result = segment;
      break;
    }
  }
  
  const winAmount = session.betAmount * result.multiplier;
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = result;
  session.winAmount = winAmount;
  
  // TODO: Credit winnings to user
  
  return {
    segment: result.label,
    multiplier: result.multiplier,
    winAmount,
    expEarned: winAmount > 0 ? 25 : 10
  };
};

// Dice Roll
const rollDice = (session: GameSession, betType: string) => {
  const dice1 = Math.floor(Math.random() * 6) + 1;
  const dice2 = Math.floor(Math.random() * 6) + 1;
  const sum = dice1 + dice2;
  
  let won = false;
  let multiplier = 0;
  
  const config = GAME_CONFIGS.dice.betTypes;
  
  switch (betType) {
    case 'high':
      won = config.high.outcomes.includes(sum);
      multiplier = config.high.multiplier;
      break;
    case 'low':
      won = config.low.outcomes.includes(sum);
      multiplier = config.low.multiplier;
      break;
    case 'seven':
      won = sum === 7;
      multiplier = config.seven.multiplier;
      break;
    case 'double':
      won = dice1 === dice2;
      multiplier = config.double.multiplier;
      break;
    case 'snake_eyes':
      won = dice1 === 1 && dice2 === 1;
      multiplier = config.snake_eyes.multiplier;
      break;
    case 'boxcars':
      won = dice1 === 6 && dice2 === 6;
      multiplier = config.boxcars.multiplier;
      break;
  }
  
  const winAmount = won ? session.betAmount * multiplier : 0;
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = { dice1, dice2, sum, betType, won };
  session.winAmount = winAmount;
  
  return {
    dice1,
    dice2,
    sum,
    won,
    multiplier: won ? multiplier : 0,
    winAmount,
    expEarned: won ? 25 : 10
  };
};

// Card Flip
const flipCard = (session: GameSession, guess: 'higher' | 'lower') => {
  const data = session.data;
  const currentValue = getCardValue(data.currentCard);
  const newCard = drawCard(data.deck);
  const newValue = getCardValue(newCard);
  
  let correct = false;
  if (guess === 'higher') {
    correct = newValue > currentValue;
  } else {
    correct = newValue < currentValue;
  }
  
  if (currentValue === newValue) {
    // Same value = automatic loss
    correct = false;
  }
  
  if (correct) {
    data.streak++;
    const streakIndex = Math.min(data.streak - 1, GAME_CONFIGS.card_flip.streakMultipliers.length - 1);
    data.currentMultiplier *= GAME_CONFIGS.card_flip.streakMultipliers[streakIndex];
    data.currentCard = newCard;
    
    return {
      correct: true,
      newCard,
      streak: data.streak,
      currentMultiplier: data.currentMultiplier,
      potentialWin: Math.floor(session.betAmount * data.currentMultiplier),
      canCashout: true
    };
  } else {
    session.status = 'completed';
    session.completedAt = new Date();
    session.result = { finalStreak: data.streak, lastCard: newCard };
    session.winAmount = 0;
    
    return {
      correct: false,
      newCard,
      streak: data.streak,
      winAmount: 0,
      expEarned: 10
    };
  }
};

// Treasure Box
const openBox = (session: GameSession, boxIndex: number) => {
  const data = session.data;
  
  if (data.revealed.includes(boxIndex)) {
    throw new Error('Box already opened');
  }
  
  const boxContent = data.grid[boxIndex];
  data.revealed.push(boxIndex);
  
  if (boxContent === 'bomb') {
    session.status = 'completed';
    session.completedAt = new Date();
    session.result = { grid: data.grid, revealed: data.revealed };
    session.winAmount = 0;
    
    return {
      boxContent: 'bomb',
      gameOver: true,
      winAmount: 0,
      expEarned: 10
    };
  }
  
  if (boxContent === 'coin') {
    data.currentPot += Math.floor(session.betAmount * 0.5);
  } else if (boxContent === '2x') {
    data.currentPot = data.currentPot > 0 ? data.currentPot * 2 : session.betAmount;
  } else if (boxContent === '3x') {
    data.currentPot = data.currentPot > 0 ? data.currentPot * 3 : session.betAmount * 1.5;
  }
  
  return {
    boxContent,
    currentPot: data.currentPot,
    revealedCount: data.revealed.length,
    canCashout: data.currentPot > 0
  };
};

// Lucky Number
const checkNumber = (session: GameSession, guess: number) => {
  const actualNumber = Math.floor(Math.random() * 100) + 1;
  const distance = Math.abs(guess - actualNumber);
  
  let multiplier = 0;
  for (const payout of GAME_CONFIGS.lucky_number.payouts) {
    if (distance <= payout.distance) {
      multiplier = payout.multiplier;
      break;
    }
  }
  
  const winAmount = Math.floor(session.betAmount * multiplier);
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = { guess, actualNumber, distance };
  session.winAmount = winAmount;
  
  return {
    guess,
    actualNumber,
    distance,
    multiplier,
    winAmount,
    expEarned: winAmount > 0 ? 25 : 10
  };
};

// Coin Toss
const tossCoin = (session: GameSession, guesses: string[], rounds: number) => {
  const results: string[] = [];
  let won = true;
  
  for (let i = 0; i < rounds; i++) {
    const result = Math.random() < 0.5 ? 'heads' : 'tails';
    results.push(result);
    if (result !== guesses[i]) {
      won = false;
      break;
    }
  }
  
  const config = GAME_CONFIGS.coin_toss;
  let multiplier = 0;
  if (won) {
    if (rounds === 1) multiplier = config.singleMultiplier;
    else if (rounds === 2) multiplier = config.doubleMultiplier;
    else if (rounds === 3) multiplier = config.tripleMultiplier;
  }
  
  const winAmount = Math.floor(session.betAmount * multiplier);
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = { guesses, results, rounds, won };
  session.winAmount = winAmount;
  
  return {
    results,
    won,
    multiplier,
    winAmount,
    expEarned: won ? 25 : 10
  };
};

// Slot Machine
const spinSlot = (session: GameSession) => {
  const symbols = GAME_CONFIGS.slot.symbols;
  const reels = [
    symbols[Math.floor(Math.random() * symbols.length)],
    symbols[Math.floor(Math.random() * symbols.length)],
    symbols[Math.floor(Math.random() * symbols.length)]
  ];
  
  const combo = reels.join('');
  const payouts = GAME_CONFIGS.slot.payouts;
  let multiplier = 0;
  let isJackpot = false;
  
  if (payouts[combo]) {
    if (payouts[combo] === 'jackpot') {
      isJackpot = true;
      multiplier = jackpots.slot / session.betAmount;
      jackpots.slot = 10000000; // Reset jackpot
    } else {
      multiplier = payouts[combo] as number;
    }
  }
  
  // Add 1% of bet to jackpot
  if (!isJackpot) {
    jackpots.slot += Math.floor(session.betAmount * 0.01);
  }
  
  const winAmount = Math.floor(session.betAmount * multiplier);
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = { reels, combo, isJackpot };
  session.winAmount = winAmount;
  
  return {
    reels,
    isJackpot,
    multiplier,
    winAmount,
    newJackpot: jackpots.slot,
    expEarned: isJackpot ? 500 : (winAmount > 0 ? 25 : 10)
  };
};

// Cashout game
export const cashoutGame = async (params: CashoutParams) => {
  const { userId, sessionId, gameType } = params;
  
  const session = gameSessions.get(sessionId);
  if (!session || session.userId !== userId) {
    throw new Error('Session not found');
  }
  
  if (session.status !== 'active') {
    throw new Error('Cannot cashout completed session');
  }
  
  let winAmount = 0;
  
  if (gameType === 'card_flip') {
    winAmount = Math.floor(session.betAmount * session.data.currentMultiplier);
  } else if (gameType === 'treasure') {
    winAmount = session.data.currentPot;
  } else {
    throw new Error('This game does not support cashout');
  }
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.winAmount = winAmount;
  
  // TODO: Credit winnings to user
  
  return {
    winAmount,
    expEarned: 25
  };
};

// Get game history
export const getGameHistory = async (params: {
  userId: string;
  gameType: string;
  page: number;
  limit: number;
}) => {
  // In production, fetch from database
  return {
    data: [],
    pagination: {
      page: params.page,
      limit: params.limit,
      totalItems: 0,
      totalPages: 0
    }
  };
};

// Helper functions
const generateDeck = (): string[] => {
  const suits = ['H', 'D', 'C', 'S'];
  const values = ['A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K'];
  const deck: string[] = [];
  for (const suit of suits) {
    for (const value of values) {
      deck.push(`${value}${suit}`);
    }
  }
  return shuffleArray(deck);
};

const shuffleArray = <T>(array: T[]): T[] => {
  const shuffled = [...array];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
};

const drawCard = (deck: string[]): string => {
  return deck.pop() || generateDeck()[0];
};

const getCardValue = (card: string): number => {
  const value = card.slice(0, -1);
  if (value === 'A') return 14;
  if (value === 'K') return 13;
  if (value === 'Q') return 12;
  if (value === 'J') return 11;
  return parseInt(value);
};

const generateTreasureGrid = (): string[] => {
  const grid: string[] = [];
  // 3 coins, 3 bombs, 2 2x, 1 3x
  grid.push('coin', 'coin', 'coin', 'bomb', 'bomb', 'bomb', '2x', '2x', '3x');
  return shuffleArray(grid);
};

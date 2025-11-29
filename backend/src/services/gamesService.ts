/**
 * Games Service
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Complete implementation of all games:
 * - Lucky 777 Pro (5-line slot machine)
 * - Lucky 77 Pro (Single-line slot machine)
 * - Greedy Baby (Food wheel selection game)
 * - Lucky Fruit (3x3 grid fruit selection)
 * - Gift Wheel System (Gift wheel with draw records)
 */

import { v4 as uuidv4 } from 'uuid';
import { logger } from '../utils/logger';

// Game types
type GameType = 'lucky_777_pro' | 'lucky_77_pro' | 'greedy_baby' | 'lucky_fruit' | 'gift_wheel';

// Game constants
const PAYOUT_DIVISOR = 1000000; // Divisor for converting raw payouts to bet-relative amounts
const JACKPOT_CONTRIBUTION_RATE = 0.01; // 1% of bet goes to jackpot
const LUCKY_777_PRO_JACKPOT_RESET = 100000000; // Reset jackpot value
const LUCKY_77_PRO_JACKPOT_RESET = 10000000; // Reset jackpot value

// Lucky 777 Pro symbols and payouts (based on screenshot)
const LUCKY_777_PRO_SYMBOLS = ['777', 'bell', 'diamond', 'watermelon', 'orange', 'grape', 'mango', 'cherry'];
const LUCKY_777_PRO_PAYOUTS: Record<string, number> = {
  '777': 1000000000,    // 777 x3 = 1,000,000,000
  'bell': 300000000,    // Bell x3 = 300,000,000
  'diamond': 100000000, // Diamond x3 = 100,000,000
  'watermelon': 50000000, // Watermelon x3 = 50,000,000
  'orange': 30000000,   // Orange x3 = 30,000,000
  'grape': 15000000,    // Grape x3 = 15,000,000
  'mango': 5000000,     // Mango x3 = 5,000,000
  'cherry': 3000000     // Cherry x3 = 3,000,000 (also partial match)
};

// Lucky 77 Pro config
const LUCKY_77_PRO_SYMBOLS = ['7', 'crown', 'star', 'gem', 'bell', 'cherry', 'bar'];
const LUCKY_77_PRO_PAYOUTS: Record<string, number> = {
  '7': 77,         // 77x multiplier
  'crown': 50,
  'star': 25,
  'gem': 15,
  'bell': 10,
  'cherry': 5,
  'bar': 3
};

// Greedy Baby food items with multipliers (based on screenshot)
const GREEDY_BABY_ITEMS = [
  { id: 'chicken', name: 'Chicken', multiplier: 45, probability: 2 },
  { id: 'apple', name: 'Apple', multiplier: 5, probability: 20 },
  { id: 'lemon', name: 'Lemon', multiplier: 5, probability: 20 },
  { id: 'strawberry', name: 'Strawberry', multiplier: 5, probability: 20 },
  { id: 'pizza', name: 'Pizza', multiplier: 25, probability: 5 },
  { id: 'burger', name: 'Burger', multiplier: 25, probability: 5 },
  { id: 'fish', name: 'Fish', multiplier: 10, probability: 12 },
  { id: 'orange', name: 'Orange', multiplier: 15, probability: 8 },
  { id: 'fruit_bowl', name: 'Fruit Bowl', multiplier: 5, probability: 8 }
];

// Lucky Fruit items with multipliers (based on screenshot)
const LUCKY_FRUIT_ITEMS = [
  { id: 'orange', name: 'Orange', multiplier: 5, probability: 20 },
  { id: 'lemon', name: 'Lemon', multiplier: 5, probability: 20 },
  { id: 'grape', name: 'Grape', multiplier: 5, probability: 18 },
  { id: 'cherry', name: 'Cherry', multiplier: 5, probability: 18 },
  { id: 'strawberry', name: 'Strawberry', multiplier: 45, probability: 3 },
  { id: 'mango', name: 'Mango', multiplier: 25, probability: 6 },
  { id: 'watermelon', name: 'Watermelon', multiplier: 15, probability: 8 },
  { id: 'apple', name: 'Apple', multiplier: 10, probability: 5 },
  { id: 'lucky', name: 'Lucky', multiplier: 0, isSpecial: true, probability: 1.5 },
  { id: 'super_lucky', name: 'Super Lucky', multiplier: 0, isSpecial: true, probability: 0.5 }
];

// Gift Wheel items (based on screenshot - draw record shows gifts)
const GIFT_WHEEL_ITEMS = [
  { id: 'rose_bouquet', name: 'Rose Bouquet', value: 100000, probability: 5 },
  { id: 'golden_butterfly', name: 'Golden Butterfly', value: 50000, probability: 10 },
  { id: 'heart_gift', name: 'Heart Gift', value: 35000, probability: 15 },
  { id: 'love_wings', name: 'Love Wings', value: 920000, probability: 1 },
  { id: 'crown', name: 'Crown', value: 25000, probability: 18 },
  { id: 'star', name: 'Star', value: 15000, probability: 20 },
  { id: 'gem', name: 'Gem', value: 10000, probability: 22 },
  { id: 'lucky_coin', name: 'Lucky Coin', value: 5000, probability: 9 }
];

// Game configurations
const GAME_CONFIGS = {
  lucky_777_pro: {
    name: 'Lucky 777 Pro',
    minBet: 100,
    maxBet: 500000,
    lines: 5,
    symbols: LUCKY_777_PRO_SYMBOLS,
    payouts: LUCKY_777_PRO_PAYOUTS
  },
  lucky_77_pro: {
    name: 'Lucky 77 Pro',
    minBet: 100,
    maxBet: 100000,
    symbols: LUCKY_77_PRO_SYMBOLS,
    payouts: LUCKY_77_PRO_PAYOUTS
  },
  greedy_baby: {
    name: 'Greedy Baby',
    minBet: 100,
    maxBet: 100000,
    betOptions: [100, 1000, 5000, 10000, 50000, 100000],
    items: GREEDY_BABY_ITEMS
  },
  lucky_fruit: {
    name: 'Lucky Fruit',
    minBet: 5000,
    maxBet: 500000,
    betOptions: [5000, 10000, 50000, 100000, 500000],
    items: LUCKY_FRUIT_ITEMS,
    gridSize: 9 // 3x3 grid
  },
  gift_wheel: {
    name: 'Gift Wheel',
    spinCost: 10000, // Cost per spin in coins
    advancedSpinCost: 50000, // Advanced/premium spin
    items: GIFT_WHEEL_ITEMS
  }
};

// In-memory storage (use Redis in production for scalability)
// TODO: Replace with Redis/Database for production deployment
// Example: const redis = new Redis(process.env.REDIS_URL);
const gameSessions = new Map<string, GameSession>();
const giftWheelHistory = new Map<string, GiftWheelRecord[]>(); // userId -> draw records
const jackpots: Record<string, number> = {
  lucky_777_pro: LUCKY_777_PRO_JACKPOT_RESET, // Starting jackpot for Lucky 777 Pro
  lucky_77_pro: LUCKY_77_PRO_JACKPOT_RESET    // Starting jackpot for Lucky 77 Pro
};

interface GiftWheelRecord {
  id: string;
  drawType: 'standard' | 'advanced';
  drawCount: number;
  items: { id: string; name: string; value: number }[];
  totalValue: number;
  timestamp: Date;
}

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
  return Object.entries(GAME_CONFIGS).map(([type, config]) => {
    const baseInfo: any = {
      type,
      name: config.name
    };
    
    if ('minBet' in config) {
      baseInfo.minBet = config.minBet;
    }
    if ('maxBet' in config) {
      baseInfo.maxBet = config.maxBet;
    }
    if ('betOptions' in config) {
      baseInfo.betOptions = config.betOptions;
    }
    if ('spinCost' in config) {
      baseInfo.spinCost = config.spinCost;
      baseInfo.advancedSpinCost = config.advancedSpinCost;
    }
    
    return baseInfo;
  });
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
  
  // Validate bet amount based on game type
  if (gameType === 'gift_wheel') {
    // Gift wheel uses fixed spin costs
    const giftConfig = config as typeof GAME_CONFIGS.gift_wheel;
    if (betAmount !== giftConfig.spinCost && betAmount !== giftConfig.advancedSpinCost) {
      throw new Error(`Invalid spin cost. Use ${giftConfig.spinCost} for standard or ${giftConfig.advancedSpinCost} for advanced`);
    }
  } else {
    // Other games have min/max bet
    const gameConfig = config as { minBet: number; maxBet: number };
    if (betAmount < gameConfig.minBet || betAmount > gameConfig.maxBet) {
      throw new Error(`Bet amount must be between ${gameConfig.minBet} and ${gameConfig.maxBet}`);
    }
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
    case 'lucky_777_pro':
      return {
        reels: [],
        lines: 5
      };
    case 'lucky_77_pro':
      return {
        reels: []
      };
    case 'greedy_baby':
      return {
        selectedItem: null,
        todaysWin: 0
      };
    case 'lucky_fruit':
      return {
        selectedFruit: null,
        resultHistory: []
      };
    case 'gift_wheel':
      return {
        drawType: 'standard',
        items: []
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
    case 'lucky_777_pro':
      return spinLucky777Pro(session);
    case 'lucky_77_pro':
      return spinLucky77Pro(session);
    case 'greedy_baby':
      return playGreedyBaby(session, data?.selectedItem);
    case 'lucky_fruit':
      return playLuckyFruit(session, data?.selectedFruit);
    case 'gift_wheel':
      return spinGiftWheel(session, data?.drawType || 'standard', data?.drawCount || 1);
    default:
      throw new Error('Unknown game type');
  }
};

// Lucky 777 Pro - 5-line slot machine
const spinLucky777Pro = (session: GameSession) => {
  const config = GAME_CONFIGS.lucky_777_pro;
  const symbols = config.symbols;
  
  // Generate 5 reels, each with 3 symbols (for 5 paylines)
  const reels: string[][] = [];
  for (let i = 0; i < 5; i++) {
    const reel: string[] = [];
    for (let j = 0; j < 3; j++) {
      reel.push(symbols[Math.floor(Math.random() * symbols.length)]);
    }
    reels.push(reel);
  }
  
  // Check for winning combinations on middle row (main payline)
  const middleRow = reels.map(reel => reel[1]);
  let winAmount = 0;
  let winningSymbol = null;
  let matchCount = 0;
  
  // Check for 3+ matching symbols from left
  const firstSymbol = middleRow[0];
  let consecutive = 1;
  for (let i = 1; i < middleRow.length; i++) {
    if (middleRow[i] === firstSymbol) {
      consecutive++;
    } else {
      break;
    }
  }
  
  if (consecutive >= 3) {
    const basePayoutPerBet = config.payouts[firstSymbol] || 0;
    const multiplier = consecutive === 5 ? 5 : consecutive === 4 ? 2 : 1;
    winAmount = Math.floor((session.betAmount / 5) * basePayoutPerBet * multiplier / PAYOUT_DIVISOR);
    winningSymbol = firstSymbol;
    matchCount = consecutive;
  }
  
  // Add to jackpot if no win
  if (winAmount === 0) {
    jackpots.lucky_777_pro += Math.floor(session.betAmount * JACKPOT_CONTRIBUTION_RATE);
  }
  
  // Check for jackpot (all 777)
  const isJackpot = middleRow.every(s => s === '777');
  if (isJackpot) {
    winAmount = jackpots.lucky_777_pro;
    jackpots.lucky_777_pro = LUCKY_777_PRO_JACKPOT_RESET;
  }
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = { reels, middleRow, winningSymbol, matchCount, isJackpot };
  session.winAmount = winAmount;
  
  return {
    reels,
    middleRow,
    winningSymbol,
    matchCount,
    isJackpot,
    winAmount,
    jackpot: jackpots.lucky_777_pro,
    expEarned: isJackpot ? 500 : (winAmount > 0 ? 25 : 10)
  };
};

// Lucky 77 Pro - Single-line slot machine
const spinLucky77Pro = (session: GameSession) => {
  const config = GAME_CONFIGS.lucky_77_pro;
  const symbols = config.symbols;
  
  // Generate 3 reels
  const reels: string[] = [];
  for (let i = 0; i < 3; i++) {
    reels.push(symbols[Math.floor(Math.random() * symbols.length)]);
  }
  
  let winAmount = 0;
  let multiplier = 0;
  let isJackpot = false;
  
  // Check for matching symbols
  if (reels[0] === reels[1] && reels[1] === reels[2]) {
    multiplier = config.payouts[reels[0]] || 0;
    winAmount = Math.floor(session.betAmount * multiplier);
    
    if (reels[0] === '7') {
      isJackpot = true;
      winAmount += jackpots.lucky_77_pro;
      jackpots.lucky_77_pro = LUCKY_77_PRO_JACKPOT_RESET;
    }
  }
  
  // Add to jackpot
  if (!isJackpot) {
    jackpots.lucky_77_pro += Math.floor(session.betAmount * JACKPOT_CONTRIBUTION_RATE);
  }
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = { reels, multiplier, isJackpot };
  session.winAmount = winAmount;
  
  return {
    reels,
    multiplier,
    isJackpot,
    winAmount,
    jackpot: jackpots.lucky_77_pro,
    expEarned: isJackpot ? 200 : (winAmount > 0 ? 25 : 10)
  };
};

// Greedy Baby - Food wheel selection game
const playGreedyBaby = (session: GameSession, selectedItemId: string) => {
  const config = GAME_CONFIGS.greedy_baby;
  
  if (!selectedItemId) {
    throw new Error('Must select an item');
  }
  
  const selectedItem = config.items.find(item => item.id === selectedItemId);
  if (!selectedItem) {
    throw new Error('Invalid item selected');
  }
  
  // Determine winning item based on probability
  const random = Math.random() * 100;
  let cumulative = 0;
  let winningItem = config.items[0];
  
  for (const item of config.items) {
    cumulative += item.probability;
    if (random <= cumulative) {
      winningItem = item;
      break;
    }
  }
  
  const won = selectedItem.id === winningItem.id;
  const winAmount = won ? Math.floor(session.betAmount * selectedItem.multiplier) : 0;
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = { selectedItem, winningItem, won };
  session.winAmount = winAmount;
  session.data.todaysWin += winAmount;
  
  return {
    selectedItem: selectedItemId,
    winningItem: winningItem.id,
    winningItemName: winningItem.name,
    won,
    multiplier: won ? selectedItem.multiplier : 0,
    winAmount,
    todaysWin: session.data.todaysWin,
    expEarned: won ? 25 : 10
  };
};

// Lucky Fruit - 3x3 grid fruit selection
const playLuckyFruit = (session: GameSession, selectedFruitId: string) => {
  const config = GAME_CONFIGS.lucky_fruit;
  
  if (!selectedFruitId) {
    throw new Error('Must select a fruit');
  }
  
  const selectedFruit = config.items.find(item => item.id === selectedFruitId);
  if (!selectedFruit) {
    throw new Error('Invalid fruit selected');
  }
  
  // Generate the winning fruit based on probability
  const random = Math.random() * 100;
  let cumulative = 0;
  let winningFruit = config.items[0];
  
  for (const item of config.items) {
    cumulative += item.probability;
    if (random <= cumulative) {
      winningFruit = item;
      break;
    }
  }
  
  let won = selectedFruit.id === winningFruit.id;
  let winAmount = 0;
  let specialBonus = null;
  
  // Handle special items (Lucky and Super Lucky)
  if (winningFruit.id === 'lucky' || winningFruit.id === 'super_lucky') {
    // Lucky/Super Lucky gives random bonus
    specialBonus = winningFruit.id;
    const bonusMultiplier = winningFruit.id === 'super_lucky' ? 
      Math.floor(Math.random() * 100) + 50 : // 50-150x for Super Lucky
      Math.floor(Math.random() * 30) + 10;   // 10-40x for Lucky
    winAmount = Math.floor(session.betAmount * bonusMultiplier);
    won = true;
  } else if (won) {
    winAmount = Math.floor(session.betAmount * selectedFruit.multiplier);
  }
  
  // Store result for history
  const resultHistory = session.data.resultHistory || [];
  resultHistory.unshift({
    fruit: winningFruit.id,
    timestamp: new Date()
  });
  session.data.resultHistory = resultHistory.slice(0, 10); // Keep last 10
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = { selectedFruit, winningFruit, won, specialBonus };
  session.winAmount = winAmount;
  
  return {
    selectedFruit: selectedFruitId,
    winningFruit: winningFruit.id,
    winningFruitName: winningFruit.name,
    won,
    specialBonus,
    multiplier: won ? (specialBonus ? winAmount / session.betAmount : selectedFruit.multiplier) : 0,
    winAmount,
    resultHistory: session.data.resultHistory,
    expEarned: specialBonus ? 100 : (won ? 25 : 10)
  };
};

// Gift Wheel System
const spinGiftWheel = (session: GameSession, drawType: 'standard' | 'advanced', drawCount: number) => {
  const config = GAME_CONFIGS.gift_wheel;
  const items = config.items;
  
  const wonItems: { id: string; name: string; value: number }[] = [];
  let totalValue = 0;
  
  // Perform draws
  for (let i = 0; i < drawCount; i++) {
    const random = Math.random() * 100;
    let cumulative = 0;
    let wonItem = items[0];
    
    for (const item of items) {
      // Advanced draws have better odds for high-value items
      const adjustedProbability = drawType === 'advanced' ? 
        (item.value > 50000 ? item.probability * 2 : item.probability * 0.8) :
        item.probability;
      cumulative += adjustedProbability;
      if (random <= cumulative) {
        wonItem = item;
        break;
      }
    }
    
    wonItems.push({
      id: wonItem.id,
      name: wonItem.name,
      value: wonItem.value
    });
    totalValue += wonItem.value;
  }
  
  // Record in history
  const userId = session.userId;
  const history = giftWheelHistory.get(userId) || [];
  const record: GiftWheelRecord = {
    id: uuidv4(),
    drawType,
    drawCount,
    items: wonItems,
    totalValue,
    timestamp: new Date()
  };
  history.unshift(record);
  giftWheelHistory.set(userId, history.slice(0, 100)); // Keep last 100 records
  
  session.status = 'completed';
  session.completedAt = new Date();
  session.result = { drawType, drawCount, wonItems, totalValue };
  session.winAmount = totalValue;
  
  return {
    drawType,
    drawCount,
    wonItems,
    totalValue,
    expEarned: Math.floor(totalValue / 1000) + 10
  };
};

// Get Gift Wheel draw records
export const getGiftWheelHistory = async (userId: string, page: number = 1, limit: number = 20) => {
  const history = giftWheelHistory.get(userId) || [];
  const start = (page - 1) * limit;
  const paginatedHistory = history.slice(start, start + limit);
  
  return {
    records: paginatedHistory,
    pagination: {
      page,
      limit,
      totalItems: history.length,
      totalPages: Math.ceil(history.length / limit)
    }
  };
};

// Cashout game (not applicable for new games - they complete immediately)
export const cashoutGame = async (params: CashoutParams) => {
  const { userId, sessionId } = params;
  
  const session = gameSessions.get(sessionId);
  if (!session || session.userId !== userId) {
    throw new Error('Session not found');
  }
  
  if (session.status !== 'active') {
    throw new Error('Cannot cashout completed session');
  }
  
  // New games complete immediately, no cashout needed
  throw new Error('This game does not support cashout');
};

// Get game history
export const getGameHistory = async (params: {
  userId: string;
  gameType: string;
  page: number;
  limit: number;
}) => {
  // For gift wheel, return from in-memory history
  if (params.gameType === 'gift_wheel') {
    return getGiftWheelHistory(params.userId, params.page, params.limit);
  }
  
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
const shuffleArray = <T>(array: T[]): T[] => {
  const shuffled = [...array];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
};

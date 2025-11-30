/**
 * Wallet Routes
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Complete wallet endpoints:
 * - Balance management
 * - Exchange (diamonds → coins)
 * - Coin purchases (multiple payment providers)
 * - Withdrawals (country-specific methods)
 */

import { Router } from 'express';
import { authenticate } from '../middleware/auth';
import { generalLimiter, withdrawalLimiter } from '../middleware/rateLimiter';
import { validateExchange } from '../middleware/validation';
import * as walletController from '../controllers/walletController';

const router = Router();

// ==================== BALANCE ====================
// Get balances
router.get('/balances', generalLimiter, authenticate, walletController.getBalances);

// Get transaction history
router.get('/transactions', generalLimiter, authenticate, walletController.getTransactions);

// ==================== EXCHANGE ====================
// Exchange diamonds to coins (30% rate)
router.post('/exchange', generalLimiter, authenticate, validateExchange, walletController.exchangeDiamondsToCoins);

// Transfer coins to another user
router.post('/transfer', withdrawalLimiter, authenticate, walletController.transferCoins);

// ==================== COIN PURCHASE ====================
// Get available coin packages
router.get('/packages', generalLimiter, authenticate, walletController.getCoinPackages);

// Purchase coins
router.post('/purchase', generalLimiter, authenticate, walletController.purchaseCoins);

// ==================== WITHDRAWAL ====================
// Get available withdrawal methods (country-specific)
router.get('/withdrawal-methods', generalLimiter, authenticate, walletController.getWithdrawalMethods);

// Request withdrawal
router.post('/withdraw', withdrawalLimiter, authenticate, walletController.requestWithdrawal);

// ==================== WEBHOOKS (No Auth) ====================
// Payment provider webhooks
router.post('/webhook/:provider', walletController.paymentWebhook);

export default router;

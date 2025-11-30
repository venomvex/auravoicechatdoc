/**
 * Wallet Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Complete wallet management:
 * - Balance management
 * - Exchange (diamonds → coins at 30%)
 * - Coin purchases with multiple payment providers
 * - Withdrawals to various methods (country-specific)
 */

import { Response, NextFunction } from 'express';
import { AuthRequest } from '../middleware/auth';
import { AppError } from '../middleware/errorHandler';
import * as walletService from '../services/walletService';
import { logger } from '../utils/logger';

const EXCHANGE_RATE = 0.30; // 30%

// Get balances
export const getBalances = async (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const userId = req.userId!;
    const balances = await walletService.getBalances(userId);
    
    res.json({
      coins: balances.coins,
      diamonds: balances.diamonds,
      lastUpdated: new Date().toISOString()
    });
  } catch (error) {
    next(error);
  }
};

// Exchange diamonds to coins
export const exchangeDiamondsToCoins = async (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const userId = req.userId!;
    const { diamonds } = req.body;
    
    if (!diamonds || diamonds <= 0) {
      throw new AppError('Invalid diamond amount', 400, 'INVALID_AMOUNT');
    }
    
    // Validate balance
    const balances = await walletService.getBalances(userId);
    if (balances.diamonds < diamonds) {
      throw new AppError('Insufficient diamonds', 400, 'INSUFFICIENT_BALANCE');
    }
    
    const coinsReceived = Math.floor(diamonds * EXCHANGE_RATE);
    const result = await walletService.exchangeDiamondsToCoins(userId, diamonds, coinsReceived);
    
    res.json({
      success: true,
      diamondsUsed: diamonds,
      coinsReceived,
      newBalance: {
        coins: result.newCoins,
        diamonds: result.newDiamonds
      }
    });
  } catch (error) {
    next(error);
  }
};

// Get transaction history
export const getTransactions = async (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const userId = req.userId!;
    const page = parseInt(req.query.page as string) || 1;
    const pageSize = parseInt(req.query.pageSize as string) || 20;
    
    const result = await walletService.getTransactions(userId, page, pageSize);
    
    res.json({
      data: result.transactions,
      pagination: {
        page,
        pageSize,
        totalItems: result.totalCount,
        totalPages: Math.ceil(result.totalCount / pageSize)
      }
    });
  } catch (error) {
    next(error);
  }
};

// Transfer coins
export const transferCoins = async (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const userId = req.userId!;
    const { toUserId, amount } = req.body;
    
    if (!toUserId || !amount || amount <= 0) {
      throw new AppError('Invalid transfer details', 400, 'INVALID_REQUEST');
    }
    
    if (userId === toUserId) {
      throw new AppError('Cannot transfer to yourself', 400, 'INVALID_RECIPIENT');
    }
    
    await walletService.transferCoins(userId, toUserId, amount);
    
    res.json({ success: true, message: 'Transfer successful' });
  } catch (error) {
    next(error);
  }
};

// Get withdrawal methods based on user's country
export const getWithdrawalMethods = async (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const countryCode = (req.query.country as string) || 'ALL';
    
    const methods = await walletService.getWithdrawalMethods(countryCode.toUpperCase());
    
    res.json({ methods });
  } catch (error) {
    next(error);
  }
};

// Get coin packages
export const getCoinPackages = async (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const countryCode = (req.query.country as string) || 'US';
    
    const packages = await walletService.getCoinPackages(countryCode.toUpperCase());
    
    res.json({ packages });
  } catch (error) {
    next(error);
  }
};

// Purchase coins
export const purchaseCoins = async (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const userId = req.userId!;
    const { packageId, paymentMethod, paymentProvider, amount, currency } = req.body;
    
    if (!packageId || !paymentMethod || !paymentProvider) {
      throw new AppError('Missing required fields', 400, 'INVALID_REQUEST');
    }
    
    const result = await walletService.createCoinPurchase(
      userId,
      packageId,
      paymentMethod,
      paymentProvider,
      amount,
      currency || 'USD'
    );
    
    res.json({
      success: true,
      purchaseId: result.purchaseId,
      redirectUrl: result.redirectUrl
    });
  } catch (error) {
    next(error);
  }
};

// Request withdrawal
export const requestWithdrawal = async (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const userId = req.userId!;
    const { amount, method, accountDetails } = req.body;
    
    if (!amount || !method || !accountDetails) {
      throw new AppError('Missing required fields', 400, 'INVALID_REQUEST');
    }
    
    const result = await walletService.requestWithdrawal(
      userId,
      amount,
      method,
      accountDetails
    );
    
    res.json({
      success: true,
      withdrawalId: result.withdrawalId,
      message: 'Withdrawal request submitted successfully'
    });
  } catch (error) {
    next(error);
  }
};

// Payment webhook handler (for providers to call)
export const paymentWebhook = async (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const { provider } = req.params;
    const payload = req.body;
    
    logger.info('Payment webhook received', { provider, payload });
    
    // Verify webhook signature based on provider
    // In production: Each provider has different signature verification methods
    // Stripe: Use stripe.webhooks.constructEvent()
    // PayPal: Verify IPN/webhook signature
    // JazzCash/EasyPaisa: Verify hash signature
    
    let purchaseId: string | undefined;
    let transactionId: string | undefined;
    
    switch (provider) {
      case 'jazzcash':
        purchaseId = payload.pp_TxnRefNo;
        transactionId = payload.pp_TxnRefNo;
        break;
      case 'easypaisa':
        purchaseId = payload.orderId;
        transactionId = payload.transactionId;
        break;
      case 'paytm':
        purchaseId = payload.ORDERID;
        transactionId = payload.TXNID;
        break;
      case 'stripe':
        purchaseId = payload.data?.object?.metadata?.purchaseId;
        transactionId = payload.data?.object?.id;
        break;
      case 'paypal':
        purchaseId = payload.resource?.purchase_units?.[0]?.custom_id;
        transactionId = payload.resource?.id;
        break;
      default:
        logger.warn('Unknown payment provider webhook', { provider });
        res.status(400).json({ error: 'Unknown payment provider' });
        return;
    }
    
    if (!purchaseId || !transactionId) {
      logger.warn('Missing purchase or transaction ID in webhook', { provider, purchaseId, transactionId });
      res.status(400).json({ error: 'Missing required fields' });
      return;
    }
    
    await walletService.completeCoinPurchase(purchaseId, transactionId, payload);
    
    // Return 200 to acknowledge receipt
    res.json({ received: true, status: 'processed' });
  } catch (error: any) {
    logger.error('Payment webhook error', { error: error.message });
    
    // Return 500 for server errors to allow retries
    // Return 400 for client errors (bad data)
    if (error.message?.includes('not found') || error.message?.includes('Invalid')) {
      res.status(400).json({ error: error.message });
    } else {
      res.status(500).json({ error: 'Processing error, please retry' });
    }
  }
};

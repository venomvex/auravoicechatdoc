/**
 * Wallet Service
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Complete wallet management with:
 * - Coin/Diamond balances from database
 * - Multiple payment providers for Asian countries
 * - Exchange system (diamonds → coins at 30%)
 * - Transaction history
 */

import { query } from '../config/database.config';
import { logger } from '../utils/logger';

interface Balances {
  coins: number;
  diamonds: number;
}

interface ExchangeResult {
  newCoins: number;
  newDiamonds: number;
}

interface Transaction {
  id: string;
  type: string;
  amount: number;
  currency: string;
  description: string;
  timestamp: Date;
}

interface WithdrawalMethod {
  id: string;
  name: string;
  code: string;
  type: string;
  minAmount: number;
  maxAmount: number;
  feeAmount: number;
  requiredFields: string[];
}

interface CoinPackage {
  id: string;
  name: string;
  coins: number;
  bonusCoins: number;
  priceUsd: number;
  pricePkr: number;
  priceInr: number;
  discountPercentage: number;
  isPopular: boolean;
}

// Get user balances from database
export const getBalances = async (userId: string): Promise<Balances> => {
  try {
    const result = await query(
      'SELECT coins, diamonds FROM users WHERE id = $1',
      [userId]
    );
    
    if (result.rows.length === 0) {
      return { coins: 0, diamonds: 0 };
    }
    
    return {
      coins: Number(result.rows[0].coins) || 0,
      diamonds: Number(result.rows[0].diamonds) || 0
    };
  } catch (error) {
    logger.error('Failed to get balances', { userId, error });
    return { coins: 0, diamonds: 0 };
  }
};

// Exchange diamonds to coins (30% rate)
export const exchangeDiamondsToCoins = async (
  userId: string,
  diamonds: number,
  coinsReceived: number
): Promise<ExchangeResult> => {
  try {
    // Update balances in database
    await query(
      'UPDATE users SET diamonds = diamonds - $1, coins = coins + $2 WHERE id = $3',
      [diamonds, coinsReceived, userId]
    );
    
    // Record transaction
    await query(
      `INSERT INTO wallet_transactions (user_id, type, amount, currency, description, created_at)
       VALUES ($1, 'exchange', $2, 'coins', $3, NOW())`,
      [userId, coinsReceived, `Exchanged ${diamonds.toLocaleString()} diamonds`]
    );
    
    // Get new balances
    const newBalances = await getBalances(userId);
    
    return {
      newCoins: newBalances.coins,
      newDiamonds: newBalances.diamonds
    };
  } catch (error) {
    logger.error('Failed to exchange diamonds', { userId, diamonds, error });
    throw error;
  }
};

// Get transaction history
export const getTransactions = async (
  userId: string,
  page: number,
  pageSize: number
): Promise<{ transactions: Transaction[]; totalCount: number }> => {
  try {
    const offset = (page - 1) * pageSize;
    
    const result = await query(
      `SELECT id, type, amount, currency, description, created_at as timestamp
       FROM wallet_transactions
       WHERE user_id = $1
       ORDER BY created_at DESC
       LIMIT $2 OFFSET $3`,
      [userId, pageSize, offset]
    );
    
    const countResult = await query(
      'SELECT COUNT(*) FROM wallet_transactions WHERE user_id = $1',
      [userId]
    );
    
    return {
      transactions: result.rows.map(row => ({
        id: row.id,
        type: row.type,
        amount: Number(row.amount),
        currency: row.currency,
        description: row.description,
        timestamp: row.timestamp
      })),
      totalCount: parseInt(countResult.rows[0].count)
    };
  } catch (error) {
    logger.error('Failed to get transactions', { userId, error });
    return { transactions: [], totalCount: 0 };
  }
};

// Transfer coins between users
export const transferCoins = async (
  fromUserId: string,
  toUserId: string,
  amount: number
): Promise<void> => {
  try {
    // Check balance
    const fromBalance = await getBalances(fromUserId);
    if (fromBalance.coins < amount) {
      throw new Error('Insufficient balance');
    }
    
    // Perform transfer
    await query(
      'UPDATE users SET coins = coins - $1 WHERE id = $2',
      [amount, fromUserId]
    );
    
    await query(
      'UPDATE users SET coins = coins + $1 WHERE id = $2',
      [amount, toUserId]
    );
    
    // Record transactions
    await query(
      `INSERT INTO wallet_transactions (user_id, type, amount, currency, description, created_at)
       VALUES ($1, 'transfer', $2, 'coins', 'Sent coins', NOW())`,
      [fromUserId, -amount]
    );
    
    await query(
      `INSERT INTO wallet_transactions (user_id, type, amount, currency, description, created_at)
       VALUES ($1, 'transfer', $2, 'coins', 'Received coins', NOW())`,
      [toUserId, amount]
    );
  } catch (error) {
    logger.error('Failed to transfer coins', { fromUserId, toUserId, amount, error });
    throw error;
  }
};

// Get withdrawal methods for a country
export const getWithdrawalMethods = async (countryCode: string): Promise<WithdrawalMethod[]> => {
  try {
    const result = await query(
      `SELECT id, name, code, type, min_amount, max_amount, fee_amount, required_fields
       FROM withdrawal_methods
       WHERE is_active = true AND ($1 = ANY(country_codes) OR 'ALL' = ANY(country_codes))
       ORDER BY name ASC`,
      [countryCode]
    );
    
    return result.rows.map(row => ({
      id: row.id,
      name: row.name,
      code: row.code,
      type: row.type,
      minAmount: Number(row.min_amount),
      maxAmount: Number(row.max_amount),
      feeAmount: Number(row.fee_amount),
      requiredFields: row.required_fields || []
    }));
  } catch (error) {
    logger.error('Failed to get withdrawal methods', { countryCode, error });
    return [];
  }
};

// Get coin packages
export const getCoinPackages = async (countryCode: string): Promise<CoinPackage[]> => {
  try {
    const result = await query(
      `SELECT id, name, coins, bonus_coins, price_usd, price_pkr, price_inr, 
              discount_percentage, is_popular
       FROM coin_packages
       WHERE is_active = true
       ORDER BY sort_order ASC`
    );
    
    return result.rows.map(row => ({
      id: row.id,
      name: row.name,
      coins: Number(row.coins),
      bonusCoins: Number(row.bonus_coins),
      priceUsd: Number(row.price_usd),
      pricePkr: Number(row.price_pkr) || Number(row.price_usd) * 280,
      priceInr: Number(row.price_inr) || Number(row.price_usd) * 83,
      discountPercentage: row.discount_percentage || 0,
      isPopular: row.is_popular
    }));
  } catch (error) {
    logger.error('Failed to get coin packages', { error });
    return [];
  }
};

// Create coin purchase
export const createCoinPurchase = async (
  userId: string,
  packageId: string,
  paymentMethod: string,
  paymentProvider: string,
  amount: number,
  currency: string
): Promise<{ purchaseId: string; redirectUrl?: string }> => {
  try {
    // Get package details
    const packageResult = await query(
      'SELECT * FROM coin_packages WHERE id = $1 AND is_active = true',
      [packageId]
    );
    
    if (packageResult.rows.length === 0) {
      throw new Error('Package not found');
    }
    
    const pkg = packageResult.rows[0];
    
    // Create purchase record
    const purchaseResult = await query(
      `INSERT INTO coin_purchases 
       (user_id, package_id, amount, currency, coins_purchased, bonus_coins, payment_method, payment_provider, payment_status, created_at)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, 'pending', NOW())
       RETURNING id`,
      [userId, packageId, amount, currency, pkg.coins, pkg.bonus_coins, paymentMethod, paymentProvider]
    );
    
    const purchaseId = purchaseResult.rows[0].id;
    
    // Generate redirect URL based on payment provider
    let redirectUrl: string | undefined;
    
    switch (paymentProvider) {
      case 'jazzcash':
        redirectUrl = await generateJazzCashPaymentUrl(purchaseId, amount, userId);
        break;
      case 'easypaisa':
        redirectUrl = await generateEasyPaisaPaymentUrl(purchaseId, amount, userId);
        break;
      case 'paytm':
        redirectUrl = await generatePaytmPaymentUrl(purchaseId, amount, userId);
        break;
      case 'stripe':
        redirectUrl = await generateStripePaymentUrl(purchaseId, amount, currency);
        break;
      case 'paypal':
        redirectUrl = await generatePayPalPaymentUrl(purchaseId, amount, currency);
        break;
      default:
        // For bank transfer, no redirect needed
        break;
    }
    
    return { purchaseId, redirectUrl };
  } catch (error) {
    logger.error('Failed to create coin purchase', { userId, packageId, error });
    throw error;
  }
};

// Complete coin purchase (called by webhook)
export const completeCoinPurchase = async (
  purchaseId: string,
  transactionId: string,
  providerResponse: any
): Promise<void> => {
  try {
    // Get purchase details
    const purchaseResult = await query(
      'SELECT * FROM coin_purchases WHERE id = $1',
      [purchaseId]
    );
    
    if (purchaseResult.rows.length === 0) {
      throw new Error('Purchase not found');
    }
    
    const purchase = purchaseResult.rows[0];
    
    if (purchase.payment_status === 'completed') {
      logger.warn('Purchase already completed', { purchaseId });
      return;
    }
    
    // Update purchase status
    await query(
      `UPDATE coin_purchases 
       SET payment_status = 'completed', transaction_id = $1, provider_response = $2, completed_at = NOW()
       WHERE id = $3`,
      [transactionId, JSON.stringify(providerResponse), purchaseId]
    );
    
    // Add coins to user
    const totalCoins = Number(purchase.coins_purchased) + Number(purchase.bonus_coins);
    await query(
      'UPDATE users SET coins = coins + $1 WHERE id = $2',
      [totalCoins, purchase.user_id]
    );
    
    // Record transaction
    await query(
      `INSERT INTO wallet_transactions (user_id, type, amount, currency, description, created_at)
       VALUES ($1, 'recharge', $2, 'coins', $3, NOW())`,
      [purchase.user_id, totalCoins, `Purchased ${purchase.coins_purchased.toLocaleString()} coins`]
    );
    
    logger.info('Coin purchase completed', { purchaseId, userId: purchase.user_id, coins: totalCoins });
  } catch (error) {
    logger.error('Failed to complete coin purchase', { purchaseId, error });
    throw error;
  }
};

// Request withdrawal
export const requestWithdrawal = async (
  userId: string,
  amount: number,
  methodCode: string,
  accountDetails: any
): Promise<{ withdrawalId: string }> => {
  try {
    // Get method details
    const methodResult = await query(
      'SELECT * FROM withdrawal_methods WHERE code = $1 AND is_active = true',
      [methodCode]
    );
    
    if (methodResult.rows.length === 0) {
      throw new Error('Withdrawal method not found');
    }
    
    const method = methodResult.rows[0];
    
    // Validate amount
    if (amount < Number(method.min_amount) || amount > Number(method.max_amount)) {
      throw new Error(`Amount must be between ${method.min_amount} and ${method.max_amount}`);
    }
    
    // Check balance
    const balances = await getBalances(userId);
    if (balances.diamonds < amount) {
      throw new Error('Insufficient diamond balance');
    }
    
    // Calculate fee
    const fee = method.fee_type === 'percentage' 
      ? amount * (Number(method.fee_amount) / 100)
      : Number(method.fee_amount);
    const netAmount = amount - fee;
    
    // Deduct diamonds
    await query(
      'UPDATE users SET diamonds = diamonds - $1 WHERE id = $2',
      [amount, userId]
    );
    
    // Create withdrawal record
    const withdrawalResult = await query(
      `INSERT INTO withdrawals 
       (user_id, amount, fee, net_amount, method, method_id, account_details, status, created_at)
       VALUES ($1, $2, $3, $4, $5, $6, $7, 'pending', NOW())
       RETURNING id`,
      [userId, amount, fee, netAmount, methodCode, method.id, JSON.stringify(accountDetails)]
    );
    
    // Record transaction
    await query(
      `INSERT INTO wallet_transactions (user_id, type, amount, currency, description, created_at)
       VALUES ($1, 'withdrawal', $2, 'diamonds', $3, NOW())`,
      [userId, -amount, `Withdrawal request via ${method.name}`]
    );
    
    return { withdrawalId: withdrawalResult.rows[0].id };
  } catch (error) {
    logger.error('Failed to request withdrawal', { userId, amount, methodCode, error });
    throw error;
  }
};

// Payment provider URL generators (placeholders - implement with actual provider SDKs)
async function generateJazzCashPaymentUrl(purchaseId: string, amount: number, userId: string): Promise<string> {
  // In production: Integrate with JazzCash API
  // https://sandbox.jazzcash.com.pk/
  return `https://payments.jazzcash.com.pk/checkout?order=${purchaseId}&amount=${amount}`;
}

async function generateEasyPaisaPaymentUrl(purchaseId: string, amount: number, userId: string): Promise<string> {
  // In production: Integrate with EasyPaisa API
  return `https://easypay.easypaisa.com.pk/checkout?order=${purchaseId}&amount=${amount}`;
}

async function generatePaytmPaymentUrl(purchaseId: string, amount: number, userId: string): Promise<string> {
  // In production: Integrate with Paytm API
  return `https://securegw.paytm.in/theia/processTransaction?orderId=${purchaseId}`;
}

async function generateStripePaymentUrl(purchaseId: string, amount: number, currency: string): Promise<string> {
  // In production: Create Stripe checkout session
  return `https://checkout.stripe.com/pay/${purchaseId}`;
}

async function generatePayPalPaymentUrl(purchaseId: string, amount: number, currency: string): Promise<string> {
  // In production: Create PayPal order
  return `https://www.paypal.com/checkoutnow?token=${purchaseId}`;
}

export default {
  getBalances,
  exchangeDiamondsToCoins,
  getTransactions,
  transferCoins,
  getWithdrawalMethods,
  getCoinPackages,
  createCoinPurchase,
  completeCoinPurchase,
  requestWithdrawal
};

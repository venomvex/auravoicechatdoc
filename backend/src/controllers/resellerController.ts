/**
 * Reseller Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { query } from '../config/database.config';
import { AppError } from '../middleware/errorHandler';
import { logger } from '../utils/logger';

// Get reseller dashboard
export const getDashboard = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const [sales, customers, earnings] = await Promise.all([
      query(
        `SELECT COUNT(*) as count, COALESCE(SUM(amount), 0) as total
         FROM reseller_sales WHERE reseller_id = $1`,
        [userId]
      ),
      query(
        'SELECT COUNT(DISTINCT customer_id) as count FROM reseller_sales WHERE reseller_id = $1',
        [userId]
      ),
      query(
        `SELECT COALESCE(SUM(commission), 0) as total FROM reseller_sales WHERE reseller_id = $1`,
        [userId]
      )
    ]);

    res.json({
      totalSales: parseInt(sales.rows[0].count, 10),
      salesAmount: parseFloat(sales.rows[0].total),
      totalCustomers: parseInt(customers.rows[0].count, 10),
      totalEarnings: parseFloat(earnings.rows[0].total)
    });
  } catch (error) {
    next(error);
  }
};

// Get reseller tier info
export const getTierInfo = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const userResult = await query(
      'SELECT reseller_tier FROM users WHERE id = $1',
      [userId]
    );

    if (userResult.rows.length === 0) {
      throw new AppError('User not found', 404, 'USER_NOT_FOUND');
    }

    const tier = userResult.rows[0].reseller_tier;

    const tierConfigResult = await query(
      'SELECT * FROM reseller_tiers WHERE tier = $1',
      [tier]
    );

    res.json({
      currentTier: tier,
      tierConfig: tierConfigResult.rows[0] || null
    });
  } catch (error) {
    next(error);
  }
};

// Sell coins to user
export const sellCoins = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const resellerId = req.user?.id;
    const { customerId, coinsAmount, paymentMethod, paymentReference } = req.body;

    // Get reseller tier and pricing
    const resellerResult = await query(
      `SELECT u.reseller_tier, rt.purchase_rate, rt.commission_rate
       FROM users u
       JOIN reseller_tiers rt ON u.reseller_tier = rt.tier
       WHERE u.id = $1 AND u.role = 'seller'`,
      [resellerId]
    );

    if (resellerResult.rows.length === 0) {
      throw new AppError('Not a valid reseller', 403, 'NOT_RESELLER');
    }

    const reseller = resellerResult.rows[0];
    const cost = coinsAmount * reseller.purchase_rate;
    const commission = coinsAmount * reseller.commission_rate;

    // Check reseller balance
    const balanceResult = await query(
      'SELECT reseller_balance FROM users WHERE id = $1',
      [resellerId]
    );

    if (parseFloat(balanceResult.rows[0].reseller_balance) < cost) {
      throw new AppError('Insufficient reseller balance', 400, 'INSUFFICIENT_BALANCE');
    }

    // Deduct from reseller balance
    await query(
      'UPDATE users SET reseller_balance = reseller_balance - $1 WHERE id = $2',
      [cost, resellerId]
    );

    // Add coins to customer
    await query(
      'UPDATE users SET coins = coins + $1 WHERE id = $2',
      [coinsAmount, customerId]
    );

    // Record sale
    const saleResult = await query(
      `INSERT INTO reseller_sales (reseller_id, customer_id, coins_amount, amount, commission, payment_method, payment_reference, created_at)
       VALUES ($1, $2, $3, $4, $5, $6, $7, NOW()) RETURNING *`,
      [resellerId, customerId, coinsAmount, cost, commission, paymentMethod, paymentReference]
    );

    logger.info(`Reseller ${resellerId} sold ${coinsAmount} coins to ${customerId}`);
    res.json({ sale: saleResult.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get sales history
export const getSalesHistory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const resellerId = req.user?.id;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT rs.*, u.username as customer_username, u.display_name as customer_display_name
       FROM reseller_sales rs
       JOIN users u ON rs.customer_id = u.id
       WHERE rs.reseller_id = $1
       ORDER BY rs.created_at DESC
       LIMIT $2 OFFSET $3`,
      [resellerId, limit, offset]
    );

    res.json({ sales: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get customers
export const getCustomers = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const resellerId = req.user?.id;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT u.id, u.username, u.display_name, u.avatar_url,
              COUNT(rs.id) as purchase_count,
              COALESCE(SUM(rs.coins_amount), 0) as total_coins_bought
       FROM users u
       JOIN reseller_sales rs ON u.id = rs.customer_id
       WHERE rs.reseller_id = $1
       GROUP BY u.id
       ORDER BY total_coins_bought DESC
       LIMIT $2 OFFSET $3`,
      [resellerId, limit, offset]
    );

    res.json({ customers: result.rows });
  } catch (error) {
    next(error);
  }
};

// Top up reseller balance
export const topUpBalance = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const resellerId = req.user?.id;
    const { amount, paymentMethod, transactionId } = req.body;

    // Create top-up request
    const result = await query(
      `INSERT INTO reseller_topups (reseller_id, amount, payment_method, transaction_id, status, created_at)
       VALUES ($1, $2, $3, $4, 'pending', NOW()) RETURNING *`,
      [resellerId, amount, paymentMethod, transactionId]
    );

    res.json({ topup: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get balance history
export const getBalanceHistory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const resellerId = req.user?.id;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM reseller_topups WHERE reseller_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [resellerId, limit, offset]
    );

    res.json({ history: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get pricing
export const getPricing = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT u.reseller_tier, rt.*
       FROM users u
       JOIN reseller_tiers rt ON u.reseller_tier = rt.tier
       WHERE u.id = $1`,
      [userId]
    );

    if (result.rows.length === 0) {
      throw new AppError('Pricing not found', 404, 'PRICING_NOT_FOUND');
    }

    res.json({ pricing: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Search users (for selling)
export const searchUsers = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { q } = req.query;

    const result = await query(
      `SELECT id, username, display_name, avatar_url
       FROM users
       WHERE username ILIKE $1 OR display_name ILIKE $1 OR phone_number LIKE $1
       LIMIT 20`,
      [`%${q}%`]
    );

    res.json({ users: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get commission history
export const getCommissionHistory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const resellerId = req.user?.id;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT rs.*, u.username as customer_username
       FROM reseller_sales rs
       JOIN users u ON rs.customer_id = u.id
       WHERE rs.reseller_id = $1 AND rs.commission > 0
       ORDER BY rs.created_at DESC
       LIMIT $2 OFFSET $3`,
      [resellerId, limit, offset]
    );

    res.json({ commissions: result.rows });
  } catch (error) {
    next(error);
  }
};

// Withdraw commission
export const withdrawCommission = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const resellerId = req.user?.id;
    const { amount, method, accountDetails } = req.body;

    // Check available commission
    const commissionResult = await query(
      'SELECT COALESCE(SUM(commission), 0) as total FROM reseller_sales WHERE reseller_id = $1 AND commission_withdrawn = false',
      [resellerId]
    );

    const availableCommission = parseFloat(commissionResult.rows[0].total);

    if (amount > availableCommission) {
      throw new AppError('Insufficient commission balance', 400, 'INSUFFICIENT_BALANCE');
    }

    // Mark commissions as withdrawn
    await query(
      `UPDATE reseller_sales SET commission_withdrawn = true WHERE reseller_id = $1 AND commission_withdrawn = false`,
      [resellerId]
    );

    // Create withdrawal request
    const result = await query(
      `INSERT INTO reseller_withdrawals (reseller_id, amount, method, account_details, status, created_at)
       VALUES ($1, $2, $3, $4, 'pending', NOW()) RETURNING *`,
      [resellerId, amount, method, JSON.stringify(accountDetails)]
    );

    res.json({ withdrawal: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get reseller list (public)
export const getResellerList = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { page = 1, limit = 20, country } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    let queryText = `
      SELECT u.id, u.username, u.display_name, u.avatar_url, u.reseller_tier, u.country,
             COALESCE(AVG(rr.rating), 0) as avg_rating,
             COUNT(rr.id) as review_count
      FROM users u
      LEFT JOIN reseller_ratings rr ON u.id = rr.reseller_id
      WHERE u.role = 'seller'
    `;
    const params: any[] = [];

    if (country) {
      queryText += ` AND u.country = $${params.length + 1}`;
      params.push(country);
    }

    queryText += ` GROUP BY u.id ORDER BY avg_rating DESC, review_count DESC LIMIT $${params.length + 1} OFFSET $${params.length + 2}`;
    params.push(limit, offset);

    const result = await query(queryText, params);

    res.json({ resellers: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get reseller profile (public)
export const getResellerProfile = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { sellerId } = req.params;

    const result = await query(
      `SELECT u.id, u.username, u.display_name, u.avatar_url, u.reseller_tier, u.country, u.bio,
              COALESCE(AVG(rr.rating), 0) as avg_rating,
              COUNT(rr.id) as review_count
       FROM users u
       LEFT JOIN reseller_ratings rr ON u.id = rr.reseller_id
       WHERE u.id = $1 AND u.role = 'seller'
       GROUP BY u.id`,
      [sellerId]
    );

    if (result.rows.length === 0) {
      throw new AppError('Reseller not found', 404, 'RESELLER_NOT_FOUND');
    }

    res.json({ reseller: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Contact reseller
export const contactReseller = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { sellerId } = req.params;
    const { message } = req.body;

    const result = await query(
      `INSERT INTO reseller_contacts (reseller_id, user_id, message, created_at)
       VALUES ($1, $2, $3, NOW()) RETURNING *`,
      [sellerId, userId, message]
    );

    res.json({ contact: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Rate reseller
export const rateReseller = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { sellerId } = req.params;
    const { rating, comment } = req.body;

    // Check if already rated
    const existingResult = await query(
      'SELECT * FROM reseller_ratings WHERE reseller_id = $1 AND user_id = $2',
      [sellerId, userId]
    );

    if (existingResult.rows.length > 0) {
      // Update existing rating
      await query(
        'UPDATE reseller_ratings SET rating = $1, comment = $2, updated_at = NOW() WHERE reseller_id = $3 AND user_id = $4',
        [rating, comment, sellerId, userId]
      );
    } else {
      // Create new rating
      await query(
        `INSERT INTO reseller_ratings (reseller_id, user_id, rating, comment, created_at)
         VALUES ($1, $2, $3, $4, NOW())`,
        [sellerId, userId, rating, comment]
      );
    }

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get seller status
export const getSellerStatus = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT reseller_tier, reseller_balance, 
              COALESCE(AVG(rr.rating), 0) as avg_rating
       FROM users u
       LEFT JOIN reseller_ratings rr ON u.id = rr.reseller_id
       WHERE u.id = $1
       GROUP BY u.id`,
      [userId]
    );

    if (result.rows.length === 0) {
      throw new AppError('Seller not found', 404, 'SELLER_NOT_FOUND');
    }

    res.json({ status: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get seller dashboard (alias for getDashboard)
export const getSellerDashboard = getDashboard;

// Get inventory
export const getInventory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      'SELECT * FROM reseller_inventory WHERE reseller_id = $1 ORDER BY item_type ASC',
      [userId]
    );

    res.json({ inventory: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get packages
export const getPackages = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    // Get user's tier
    const tierResult = await query(
      'SELECT reseller_tier FROM users WHERE id = $1',
      [userId]
    );

    const tier = tierResult.rows[0]?.reseller_tier || 1;

    const result = await query(
      'SELECT * FROM reseller_packages WHERE min_tier <= $1 AND is_active = true ORDER BY coins ASC',
      [tier]
    );

    res.json({ packages: result.rows });
  } catch (error) {
    next(error);
  }
};

// Purchase package
export const purchasePackage = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { packageId, paymentMethod, transactionId } = req.body;

    // Get package
    const packageResult = await query(
      'SELECT * FROM reseller_packages WHERE id = $1',
      [packageId]
    );

    if (packageResult.rows.length === 0) {
      throw new AppError('Package not found', 404, 'PACKAGE_NOT_FOUND');
    }

    const pkg = packageResult.rows[0];

    // Create purchase order
    const result = await query(
      `INSERT INTO reseller_purchases (reseller_id, package_id, coins, amount, payment_method, transaction_id, status, created_at)
       VALUES ($1, $2, $3, $4, $5, $6, 'pending', NOW()) RETURNING *`,
      [userId, packageId, pkg.coins, pkg.price, paymentMethod, transactionId]
    );

    res.json({ purchase: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Get orders
export const getOrders = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { page = 1, limit = 20, status } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    let queryText = `
      SELECT o.*, u.username as customer_username, u.display_name as customer_display_name
      FROM reseller_orders o
      JOIN users u ON o.customer_id = u.id
      WHERE o.reseller_id = $1
    `;
    const params: any[] = [userId];

    if (status) {
      queryText += ` AND o.status = $${params.length + 1}`;
      params.push(status);
    }

    queryText += ` ORDER BY o.created_at DESC LIMIT $${params.length + 1} OFFSET $${params.length + 2}`;
    params.push(limit, offset);

    const result = await query(queryText, params);

    res.json({ orders: result.rows });
  } catch (error) {
    next(error);
  }
};

// Create order
export const createOrder = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { customerId, coins, paymentMethod } = req.body;

    const result = await query(
      `INSERT INTO reseller_orders (reseller_id, customer_id, coins, payment_method, status, created_at)
       VALUES ($1, $2, $3, $4, 'pending', NOW()) RETURNING *`,
      [userId, customerId, coins, paymentMethod]
    );

    res.json({ order: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Confirm order
export const confirmOrder = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { orderId } = req.params;
    const { paymentReference } = req.body;

    // Get order
    const orderResult = await query(
      'SELECT * FROM reseller_orders WHERE id = $1 AND reseller_id = $2 AND status = $3',
      [orderId, userId, 'pending']
    );

    if (orderResult.rows.length === 0) {
      throw new AppError('Order not found', 404, 'ORDER_NOT_FOUND');
    }

    const order = orderResult.rows[0];

    // Give coins to customer
    await query(
      'UPDATE users SET coins = coins + $1 WHERE id = $2',
      [order.coins, order.customer_id]
    );

    // Update order
    await query(
      `UPDATE reseller_orders SET status = 'completed', payment_reference = $1, completed_at = NOW() WHERE id = $2`,
      [paymentReference, orderId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Cancel order
export const cancelOrder = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { orderId } = req.params;
    const { reason } = req.body;

    await query(
      `UPDATE reseller_orders SET status = 'cancelled', cancel_reason = $1, cancelled_at = NOW() 
       WHERE id = $2 AND reseller_id = $3 AND status = 'pending'`,
      [reason, orderId, userId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get transactions
export const getTransactions = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { page = 1, limit = 20, type } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    let queryText = `
      SELECT * FROM reseller_transactions WHERE reseller_id = $1
    `;
    const params: any[] = [userId];

    if (type) {
      queryText += ` AND type = $${params.length + 1}`;
      params.push(type);
    }

    queryText += ` ORDER BY created_at DESC LIMIT $${params.length + 1} OFFSET $${params.length + 2}`;
    params.push(limit, offset);

    const result = await query(queryText, params);

    res.json({ transactions: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get bonuses
export const getBonuses = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT * FROM reseller_bonuses WHERE reseller_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3`,
      [userId, limit, offset]
    );

    res.json({ bonuses: result.rows });
  } catch (error) {
    next(error);
  }
};

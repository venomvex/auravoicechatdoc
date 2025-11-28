/**
 * Database Configuration Module
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * PostgreSQL connection configuration with connection pooling
 */

import { Pool, PoolConfig } from 'pg';
import { logger } from '../utils/logger';

const isProduction = process.env.NODE_ENV === 'production';

// Connection pool configuration
const poolConfig: PoolConfig = {
  connectionString: process.env.DATABASE_URL,
  
  // Fallback to individual settings if DATABASE_URL not provided
  host: process.env.DB_HOST || 'localhost',
  port: parseInt(process.env.DB_PORT || '5432', 10),
  database: process.env.DB_NAME || 'auravoicechat',
  user: process.env.DB_USER || 'postgres',
  password: process.env.DB_PASSWORD || '',
  
  // Connection pool settings
  min: parseInt(process.env.DB_POOL_MIN || '2', 10),
  max: parseInt(process.env.DB_POOL_MAX || '20', 10),
  idleTimeoutMillis: 30000,
  connectionTimeoutMillis: 10000,
  
  // SSL for production
  ssl: isProduction ? { rejectUnauthorized: false } : undefined,
};

// Create the connection pool
export const pool = new Pool(poolConfig);

// Pool event handlers
pool.on('connect', () => {
  logger.debug('New client connected to PostgreSQL pool');
});

pool.on('error', (err) => {
  logger.error('Unexpected PostgreSQL pool error', { error: err.message });
});

pool.on('remove', () => {
  logger.debug('Client removed from PostgreSQL pool');
});

// Query helper with logging
export const query = async (text: string, params?: any[]) => {
  const start = Date.now();
  try {
    const result = await pool.query(text, params);
    const duration = Date.now() - start;
    logger.debug('Executed query', {
      text: text.substring(0, 100),
      duration,
      rows: result.rowCount,
    });
    return result;
  } catch (error: any) {
    logger.error('Query error', {
      text: text.substring(0, 100),
      error: error.message,
    });
    throw error;
  }
};

// Transaction helper
export const transaction = async <T>(
  callback: (client: any) => Promise<T>
): Promise<T> => {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');
    const result = await callback(client);
    await client.query('COMMIT');
    return result;
  } catch (error) {
    await client.query('ROLLBACK');
    throw error;
  } finally {
    client.release();
  }
};

// Health check
export const checkDatabaseConnection = async (): Promise<boolean> => {
  try {
    const result = await pool.query('SELECT NOW()');
    return result.rows.length > 0;
  } catch (error) {
    logger.error('Database health check failed', { error });
    return false;
  }
};

// Get pool statistics
export const getPoolStats = () => ({
  total: pool.totalCount,
  idle: pool.idleCount,
  waiting: pool.waitingCount,
});

// Graceful shutdown
export const closePool = async () => {
  logger.info('Closing PostgreSQL connection pool');
  await pool.end();
  logger.info('PostgreSQL connection pool closed');
};

export default {
  pool,
  query,
  transaction,
  checkDatabaseConnection,
  getPoolStats,
  closePool,
};

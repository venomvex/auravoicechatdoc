/**
 * Configuration Module
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import dotenv from 'dotenv';

dotenv.config();

// Validate required environment variables in production
const nodeEnv = process.env.NODE_ENV || 'development';
if (nodeEnv === 'production') {
  if (!process.env.JWT_SECRET) {
    throw new Error('JWT_SECRET environment variable is required in production');
  }
  if (!process.env.DATABASE_URL && !process.env.DB_HOST) {
    throw new Error('DATABASE_URL or DB_HOST environment variable is required in production');
  }
}

export const config = {
  nodeEnv,
  port: parseInt(process.env.PORT || '3000', 10),
  host: process.env.HOST || '0.0.0.0',
  
  // Database
  databaseUrl: process.env.DATABASE_URL || '',
  database: {
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT || '5432', 10),
    name: process.env.DB_NAME || 'auravoicechat',
    user: process.env.DB_USER || 'postgres',
    password: process.env.DB_PASSWORD || '',
    poolMin: parseInt(process.env.DB_POOL_MIN || '2', 10),
    poolMax: parseInt(process.env.DB_POOL_MAX || '20', 10),
  },
  
  // Redis
  redisUrl: process.env.REDIS_URL || 'redis://localhost:6379',
  
  // JWT - only use defaults in development
  jwtSecret: process.env.JWT_SECRET || (nodeEnv === 'development' ? 'dev-secret-change-in-prod' : ''),
  jwtExpiresIn: process.env.JWT_EXPIRES_IN || '7d',
  jwtRefreshSecret: process.env.JWT_REFRESH_SECRET || (nodeEnv === 'development' ? 'dev-refresh-secret-change-in-prod' : ''),
  jwtRefreshExpiresIn: process.env.JWT_REFRESH_EXPIRES_IN || '30d',
  
  // AWS (loaded from aws.config.ts)
  aws: {
    region: process.env.AWS_REGION || 'us-east-1',
  },
  
  // Twilio
  twilio: {
    accountSid: process.env.TWILIO_ACCOUNT_SID || '',
    authToken: process.env.TWILIO_AUTH_TOKEN || '',
    phoneNumber: process.env.TWILIO_PHONE_NUMBER || '',
  },
  
  // OAuth
  google: {
    clientId: process.env.GOOGLE_CLIENT_ID || '',
    clientSecret: process.env.GOOGLE_CLIENT_SECRET || '',
  },
  facebook: {
    appId: process.env.FACEBOOK_APP_ID || '',
    appSecret: process.env.FACEBOOK_APP_SECRET || '',
  },
  
  // Rate Limiting
  rateLimit: {
    windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS || '60000', 10),
    maxRequests: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS || '100', 10),
  },
  
  // Logging
  logLevel: process.env.LOG_LEVEL || 'info',
  
  // CORS
  corsOrigin: process.env.CORS_ORIGIN || '*',
};

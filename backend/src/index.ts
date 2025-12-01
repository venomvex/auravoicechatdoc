/**
 * Aura Voice Chat Backend - Main Entry Point
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import compression from 'compression';
import morgan from 'morgan';
import { config } from './config';
import { logger } from './utils/logger';
import { errorHandler } from './middleware/errorHandler';
import { notFoundHandler } from './middleware/notFoundHandler';

// Routes
import authRoutes from './routes/auth';
import rewardsRoutes from './routes/rewards';
import vipRoutes from './routes/vip';
import medalsRoutes from './routes/medals';
import walletRoutes from './routes/wallet';
import referralsRoutes from './routes/referrals';
import roomsRoutes from './routes/rooms';
import usersRoutes from './routes/users';
import kycRoutes from './routes/kyc';
import gamesRoutes from './routes/games';
import familyRoutes from './routes/family';
import cpRoutes from './routes/cp';
import earningsRoutes from './routes/earnings';
import resellerRoutes from './routes/reseller';
import eventsRoutes from './routes/events';
import levelsRoutes from './routes/levels';
import adminRoutes from './routes/admin';
import ownerRoutes from './routes/owner';
import messagesRoutes from './routes/messages';
import notificationsRoutes from './routes/notifications';
import guideRoutes from './routes/guide';
import giftsRoutes from './routes/gifts';
import moderationRoutes from './routes/moderation';

const app = express();

// Security middleware
app.use(helmet());
app.use(cors({
  origin: config.corsOrigin,
  credentials: true
}));

// Request parsing
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Compression
app.use(compression());

// Logging
app.use(morgan('combined', {
  stream: { write: (message) => logger.info(message.trim()) }
}));

// Health check
app.get('/health', (req, res) => {
  res.json({ 
    status: 'healthy',
    timestamp: new Date().toISOString(),
    version: '1.0.0'
  });
});

// API Routes
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/rewards', rewardsRoutes);
app.use('/api/v1/vip', vipRoutes);
app.use('/api/v1/profile/medals', medalsRoutes);
app.use('/api/v1/wallet', walletRoutes);
app.use('/api/v1/referrals', referralsRoutes);
app.use('/api/v1/rooms', roomsRoutes);
app.use('/api/v1/users', usersRoutes);
app.use('/api/v1/kyc', kycRoutes);
app.use('/api/v1/games', gamesRoutes);
app.use('/api/v1/family', familyRoutes);
app.use('/api/v1/cp', cpRoutes);
app.use('/api/v1/earnings', earningsRoutes);
app.use('/api/v1/reseller', resellerRoutes);
app.use('/api/v1/events', eventsRoutes);
app.use('/api/v1/levels', levelsRoutes);
app.use('/api/v1/admin', adminRoutes);
app.use('/api/v1/owner', ownerRoutes);
app.use('/api/v1/messages', messagesRoutes);
app.use('/api/v1/notifications', notificationsRoutes);
app.use('/api/v1/guide', guideRoutes);
app.use('/api/v1/gifts', giftsRoutes);
app.use('/api/v1/moderation', moderationRoutes);

// 404 handler
app.use(notFoundHandler);

// Error handler
app.use(errorHandler);

// Start server
const PORT = config.port;
app.listen(PORT, () => {
  logger.info(`🚀 Aura Voice Chat API running on port ${PORT}`);
  logger.info(`📝 Environment: ${config.nodeEnv}`);
});

export default app;

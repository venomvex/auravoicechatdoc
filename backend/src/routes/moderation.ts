/**
 * Moderation Routes
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Content moderation API endpoints
 */

import { Router } from 'express';
import { authenticate, requireAdmin } from '../middleware/auth';
import { generalLimiter } from '../middleware/rateLimiter';
import * as moderationController from '../controllers/moderationController';

const router = Router();

// Apply rate limiting
router.use(generalLimiter);

// All routes require authentication
router.use(authenticate);

// ==================== USER ROUTES ====================

// Check content for violations (called before sending message)
router.post('/check-content', moderationController.checkContent);

// Check image for violations (called before uploading)
router.post('/check-image', moderationController.checkImage);

// Get current user's ban status
router.get('/ban-status', moderationController.getBanStatus);

// ==================== ADMIN ROUTES ====================

// Get all violations
router.get('/violations', requireAdmin, moderationController.getViolations);

// Get pending image reviews
router.get('/image-reviews', requireAdmin, moderationController.getPendingImageReviews);

// Review an image
router.post('/image-reviews/:reviewId', requireAdmin, moderationController.reviewImage);

// Unban a user
router.post('/unban/:userId', requireAdmin, moderationController.unbanUser);

export default router;

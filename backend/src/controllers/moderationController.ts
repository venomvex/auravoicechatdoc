/**
 * Moderation Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Handles content moderation API endpoints
 */

import { Request, Response } from 'express';
import { logger } from '../utils/logger';
import * as moderationService from '../services/moderationService';

/**
 * Check text content for violations
 */
export async function checkContent(req: Request, res: Response) {
    try {
        const userId = (req as any).userId;
        const { content, context } = req.body;
        
        if (!content) {
            return res.status(400).json({ 
                success: false, 
                message: 'Content is required' 
            });
        }
        
        const result = await moderationService.checkTextContent(
            userId,
            content,
            context || 'chat'
        );
        
        return res.json({
            success: true,
            isViolation: result.isViolation,
            action: result.action,
            message: result.message,
            banExpiry: result.banExpiry
        });
    } catch (error) {
        logger.error('Error checking content:', error);
        return res.status(500).json({ 
            success: false, 
            message: 'Failed to check content' 
        });
    }
}

/**
 * Check image for violations
 */
export async function checkImage(req: Request, res: Response) {
    try {
        const userId = (req as any).userId;
        const { imageUrl, context } = req.body;
        
        if (!imageUrl) {
            return res.status(400).json({ 
                success: false, 
                message: 'Image URL is required' 
            });
        }
        
        const result = await moderationService.checkImageContent(
            userId,
            imageUrl,
            context || 'chat'
        );
        
        return res.json({
            success: true,
            isViolation: result.isViolation,
            action: result.action,
            message: result.message
        });
    } catch (error) {
        logger.error('Error checking image:', error);
        return res.status(500).json({ 
            success: false, 
            message: 'Failed to check image' 
        });
    }
}

/**
 * Check user ban status
 */
export async function getBanStatus(req: Request, res: Response) {
    try {
        const userId = (req as any).userId;
        
        const banStatus = await moderationService.isUserBanned(userId);
        
        return res.json({
            success: true,
            ...banStatus
        });
    } catch (error) {
        logger.error('Error getting ban status:', error);
        return res.status(500).json({ 
            success: false, 
            message: 'Failed to get ban status' 
        });
    }
}

/**
 * Get violations list (Admin)
 */
export async function getViolations(req: Request, res: Response) {
    try {
        const page = parseInt(req.query.page as string) || 1;
        const limit = parseInt(req.query.limit as string) || 20;
        const status = req.query.status as string;
        
        const result = await moderationService.getViolations(page, limit, status);
        
        return res.json({
            success: true,
            ...result
        });
    } catch (error) {
        logger.error('Error getting violations:', error);
        return res.status(500).json({ 
            success: false, 
            message: 'Failed to get violations' 
        });
    }
}

/**
 * Get pending image reviews (Admin)
 */
export async function getPendingImageReviews(req: Request, res: Response) {
    try {
        const page = parseInt(req.query.page as string) || 1;
        const limit = parseInt(req.query.limit as string) || 20;
        
        const result = await moderationService.getPendingImageReviews(page, limit);
        
        return res.json({
            success: true,
            ...result
        });
    } catch (error) {
        logger.error('Error getting pending reviews:', error);
        return res.status(500).json({ 
            success: false, 
            message: 'Failed to get pending reviews' 
        });
    }
}

/**
 * Review an image (Admin)
 */
export async function reviewImage(req: Request, res: Response) {
    try {
        const reviewerId = (req as any).userId;
        const { reviewId } = req.params;
        const { approved } = req.body;
        
        if (typeof approved !== 'boolean') {
            return res.status(400).json({ 
                success: false, 
                message: 'Approved status is required' 
            });
        }
        
        const success = await moderationService.reviewImage(reviewId, approved, reviewerId);
        
        if (success) {
            return res.json({
                success: true,
                message: `Image ${approved ? 'approved' : 'rejected'} successfully`
            });
        } else {
            return res.status(500).json({ 
                success: false, 
                message: 'Failed to review image' 
            });
        }
    } catch (error) {
        logger.error('Error reviewing image:', error);
        return res.status(500).json({ 
            success: false, 
            message: 'Failed to review image' 
        });
    }
}

/**
 * Unban a user (Admin)
 */
export async function unbanUser(req: Request, res: Response) {
    try {
        const { userId } = req.params;
        
        const success = await moderationService.unbanUser(userId);
        
        if (success) {
            return res.json({
                success: true,
                message: 'User unbanned successfully'
            });
        } else {
            return res.status(500).json({ 
                success: false, 
                message: 'Failed to unban user' 
            });
        }
    } catch (error) {
        logger.error('Error unbanning user:', error);
        return res.status(500).json({ 
            success: false, 
            message: 'Failed to unban user' 
        });
    }
}

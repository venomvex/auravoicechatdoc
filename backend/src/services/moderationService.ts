/**
 * Content Moderation Service
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Vulgar/inappropriate image detection
 * - Abusive word filtering
 * - Escalating ban system (warn → 5min → 10min → permanent)
 * - Violation tracking
 * - Admin/Owner notifications
 */

import pool from '../database/pool';
import { logger } from '../utils/logger';

// Cached banned words from database (loaded on startup and refreshed periodically)
let cachedBannedWords: string[] = [];
let cachedWordPatterns: RegExp[] = [];
let lastCacheRefresh = 0;
const CACHE_TTL_MS = 60000; // Refresh every 60 seconds

/**
 * Load banned words from database and create regex patterns
 * Falls back to minimal hardcoded list only if database is unavailable
 */
async function loadBannedWords(): Promise<void> {
    const now = Date.now();
    if (now - lastCacheRefresh < CACHE_TTL_MS && cachedBannedWords.length > 0) {
        return; // Use cached version
    }
    
    try {
        const result = await pool.query(
            `SELECT word FROM banned_words WHERE is_active = true`
        );
        
        if (result.rows.length > 0) {
            cachedBannedWords = result.rows.map(row => row.word.toLowerCase());
            cachedWordPatterns = cachedBannedWords.map(word => createWordPattern(word));
            lastCacheRefresh = now;
            logger.info(`Loaded ${cachedBannedWords.length} banned words from database`);
        } else {
            // Initialize with minimal fallback list if database is empty
            await initializeBannedWordsTable();
        }
    } catch (error) {
        logger.error('Error loading banned words from database:', error);
        // Use minimal fallback if database unavailable
        if (cachedBannedWords.length === 0) {
            cachedBannedWords = ['spam', 'scam'];
            cachedWordPatterns = cachedBannedWords.map(word => createWordPattern(word));
        }
    }
}

/**
 * Create a regex pattern for a banned word that matches common variations
 */
function createWordPattern(word: string): RegExp {
    const pattern = word.split('').map(char => {
        if (char === 'a') return '[a@4]';
        if (char === 'e') return '[e3]';
        if (char === 'i') return '[i1!]';
        if (char === 'o') return '[o0]';
        if (char === 's') return '[s$5]';
        if (char === '*') return '.';
        return char;
    }).join('[\\s._-]*');
    return new RegExp(pattern, 'gi');
}

/**
 * Initialize banned words table with common profanity
 * This runs only once when the table is empty
 */
async function initializeBannedWordsTable(): Promise<void> {
    try {
        // Check if table is empty
        const countResult = await pool.query('SELECT COUNT(*) FROM banned_words');
        if (parseInt(countResult.rows[0].count) > 0) return;
        
        // Insert common profanity words (categorized by language)
        const words = [
            { word: 'spam', category: 'spam', language: 'en' },
            { word: 'scam', category: 'scam', language: 'en' },
            // Add more via admin panel
        ];
        
        for (const w of words) {
            await pool.query(
                `INSERT INTO banned_words (word, category, language) VALUES ($1, $2, $3)
                 ON CONFLICT (word) DO NOTHING`,
                [w.word, w.category, w.language]
            );
        }
        
        logger.info('Initialized banned words table');
        // Reload cache
        lastCacheRefresh = 0;
        await loadBannedWords();
    } catch (error) {
        logger.error('Error initializing banned words table:', error);
    }
}

export interface ViolationType {
    type: 'abusive_language' | 'vulgar_image' | 'spam' | 'harassment';
    severity: 'low' | 'medium' | 'high';
    content?: string;
    imageUrl?: string;
}

export interface ModerationResult {
    isViolation: boolean;
    violationType?: ViolationType['type'];
    severity?: ViolationType['severity'];
    detectedWords?: string[];
    action?: 'warn' | 'ban_5min' | 'ban_10min' | 'ban_permanent';
    banExpiry?: Date;
    message?: string;
}

/**
 * Check text content for abusive language
 */
export async function checkTextContent(
    userId: string,
    content: string,
    context: 'chat' | 'bio' | 'room_name' | 'room_announcement'
): Promise<ModerationResult> {
    // Ensure banned words are loaded
    await loadBannedWords();
    
    const lowerContent = content.toLowerCase();
    const detectedWords: string[] = [];
    
    // Check against cached banned words
    for (let i = 0; i < cachedBannedWords.length; i++) {
        if (cachedWordPatterns[i].test(lowerContent)) {
            detectedWords.push(cachedBannedWords[i]);
        }
    }
    
    if (detectedWords.length === 0) {
        return { isViolation: false };
    }
    
    logger.warn(`Abusive content detected from user ${userId}: ${detectedWords.join(', ')}`);
    
    // Get user's violation history
    const violationCount = await getUserViolationCount(userId, 'abusive_language');
    
    // Determine action based on violation count
    let action: ModerationResult['action'];
    let banExpiry: Date | undefined;
    let message: string;
    
    if (violationCount === 0) {
        action = 'warn';
        message = 'Warning: Your message contains inappropriate language. Further violations will result in a ban.';
    } else if (violationCount === 1) {
        action = 'ban_5min';
        banExpiry = new Date(Date.now() + 5 * 60 * 1000);
        message = 'You have been banned for 5 minutes due to repeated use of inappropriate language.';
    } else if (violationCount === 2) {
        action = 'ban_10min';
        banExpiry = new Date(Date.now() + 10 * 60 * 1000);
        message = 'You have been banned for 10 minutes due to repeated violations.';
    } else {
        action = 'ban_permanent';
        message = 'You have been permanently banned due to repeated violations.';
    }
    
    // Record the violation
    await recordViolation(userId, {
        type: 'abusive_language',
        severity: violationCount >= 2 ? 'high' : 'medium',
        content
    }, action, banExpiry);
    
    // Apply ban if needed
    if (action !== 'warn') {
        await applyBan(userId, action, banExpiry, 'Abusive language');
    }
    
    return {
        isViolation: true,
        violationType: 'abusive_language',
        severity: violationCount >= 2 ? 'high' : 'medium',
        detectedWords,
        action,
        banExpiry,
        message
    };
}

/**
 * Check if image might be vulgar/inappropriate
 * Uses simple heuristics - in production, integrate with AWS Rekognition or similar
 */
export async function checkImageContent(
    userId: string,
    imageUrl: string,
    context: 'profile' | 'room_cover' | 'chat'
): Promise<ModerationResult> {
    try {
        // For now, we'll flag images that need manual review
        // In production, integrate with AWS Rekognition Content Moderation
        // or Google Cloud Vision SafeSearch
        
        // Simple checks we can do:
        // 1. Check if URL is from known adult domains
        const adultDomains = ['porn', 'xxx', 'adult', 'nude', 'nsfw', 'sex'];
        const urlLower = imageUrl.toLowerCase();
        
        for (const domain of adultDomains) {
            if (urlLower.includes(domain)) {
                logger.warn(`Potentially vulgar image URL from user ${userId}: ${imageUrl}`);
                
                await recordViolation(userId, {
                    type: 'vulgar_image',
                    severity: 'high',
                    imageUrl
                }, 'warn');
                
                return {
                    isViolation: true,
                    violationType: 'vulgar_image',
                    severity: 'high',
                    action: 'warn',
                    message: 'This image appears to violate our content policy. Please upload an appropriate image.'
                };
            }
        }
        
        // Mark for manual review if it's a new upload (not from our CDN)
        // In production, queue this for AI analysis
        if (!imageUrl.includes('aura-assets') && !imageUrl.includes('amazonaws.com')) {
            await queueImageForReview(userId, imageUrl, context);
        }
        
        return { isViolation: false };
    } catch (error) {
        logger.error('Error checking image content:', error);
        return { isViolation: false };
    }
}

/**
 * Get user's violation count for a specific type
 */
async function getUserViolationCount(
    userId: string, 
    violationType: ViolationType['type']
): Promise<number> {
    try {
        const result = await pool.query(
            `SELECT COUNT(*) as count 
             FROM user_violations 
             WHERE user_id = $1 
             AND violation_type = $2 
             AND created_at > NOW() - INTERVAL '30 days'`,
            [userId, violationType]
        );
        return parseInt(result.rows[0]?.count || '0');
    } catch (error) {
        logger.error('Error getting violation count:', error);
        return 0;
    }
}

/**
 * Record a violation
 */
async function recordViolation(
    userId: string,
    violation: ViolationType,
    action: ModerationResult['action'],
    banExpiry?: Date
): Promise<void> {
    try {
        await pool.query(
            `INSERT INTO user_violations 
             (user_id, violation_type, severity, content, image_url, action_taken, ban_expiry, created_at)
             VALUES ($1, $2, $3, $4, $5, $6, $7, NOW())`,
            [
                userId,
                violation.type,
                violation.severity,
                violation.content || null,
                violation.imageUrl || null,
                action,
                banExpiry || null
            ]
        );
        
        // Notify room owners/admins if this is in a room context
        await notifyAdminsOfViolation(userId, violation, action);
        
    } catch (error) {
        logger.error('Error recording violation:', error);
    }
}

/**
 * Apply a ban to a user
 */
async function applyBan(
    userId: string,
    banType: 'ban_5min' | 'ban_10min' | 'ban_permanent',
    banExpiry: Date | undefined,
    reason: string
): Promise<void> {
    try {
        await pool.query(
            `UPDATE users 
             SET is_banned = true, 
                 ban_reason = $2, 
                 ban_expiry = $3,
                 ban_type = $4,
                 updated_at = NOW()
             WHERE id = $1`,
            [userId, reason, banExpiry || null, banType]
        );
        
        logger.info(`User ${userId} banned: ${banType} - ${reason}`);
    } catch (error) {
        logger.error('Error applying ban:', error);
    }
}

/**
 * Check if user is currently banned
 */
export async function isUserBanned(userId: string): Promise<{
    isBanned: boolean;
    banType?: string;
    banReason?: string;
    banExpiry?: Date;
}> {
    try {
        const result = await pool.query(
            `SELECT is_banned, ban_type, ban_reason, ban_expiry 
             FROM users WHERE id = $1`,
            [userId]
        );
        
        if (!result.rows[0]) {
            return { isBanned: false };
        }
        
        const user = result.rows[0];
        
        // Check if ban has expired
        if (user.is_banned && user.ban_expiry) {
            if (new Date(user.ban_expiry) < new Date()) {
                // Ban expired, remove it
                await pool.query(
                    `UPDATE users SET is_banned = false, ban_type = null, ban_reason = null, ban_expiry = null WHERE id = $1`,
                    [userId]
                );
                return { isBanned: false };
            }
        }
        
        return {
            isBanned: user.is_banned,
            banType: user.ban_type,
            banReason: user.ban_reason,
            banExpiry: user.ban_expiry
        };
    } catch (error) {
        logger.error('Error checking ban status:', error);
        return { isBanned: false };
    }
}

/**
 * Notify admins and room owners of violations
 */
async function notifyAdminsOfViolation(
    userId: string,
    violation: ViolationType,
    action: ModerationResult['action']
): Promise<void> {
    try {
        // Get user info
        const userResult = await pool.query(
            `SELECT display_name FROM users WHERE id = $1`,
            [userId]
        );
        const userName = userResult.rows[0]?.display_name || 'Unknown';
        
        // Create notification for admins
        await pool.query(
            `INSERT INTO admin_notifications 
             (type, title, message, user_id, severity, is_read, created_at)
             VALUES ($1, $2, $3, $4, $5, false, NOW())`,
            [
                'content_violation',
                `Content Violation: ${violation.type}`,
                `User "${userName}" (${userId}) violated content policy. Action: ${action}. ${violation.content ? `Content: "${violation.content.substring(0, 100)}..."` : ''}`,
                userId,
                violation.severity
            ]
        );
        
        logger.info(`Admin notification created for violation by user ${userId}`);
    } catch (error) {
        logger.error('Error notifying admins:', error);
    }
}

/**
 * Queue image for manual review
 */
async function queueImageForReview(
    userId: string,
    imageUrl: string,
    context: string
): Promise<void> {
    try {
        await pool.query(
            `INSERT INTO image_review_queue 
             (user_id, image_url, context, status, created_at)
             VALUES ($1, $2, $3, 'pending', NOW())
             ON CONFLICT (user_id, image_url) DO NOTHING`,
            [userId, imageUrl, context]
        );
    } catch (error) {
        logger.error('Error queueing image for review:', error);
    }
}

/**
 * Get violations for admin panel
 */
export async function getViolations(
    page: number = 1,
    limit: number = 20,
    status?: string
): Promise<{
    violations: any[];
    total: number;
    page: number;
    totalPages: number;
}> {
    try {
        const offset = (page - 1) * limit;
        
        let query = `
            SELECT v.*, u.display_name as user_name, u.avatar_url as user_avatar
            FROM user_violations v
            LEFT JOIN users u ON v.user_id = u.id
        `;
        const params: any[] = [];
        
        if (status) {
            query += ` WHERE v.action_taken = $1`;
            params.push(status);
        }
        
        query += ` ORDER BY v.created_at DESC LIMIT $${params.length + 1} OFFSET $${params.length + 2}`;
        params.push(limit, offset);
        
        const result = await pool.query(query, params);
        
        // Get total count
        const countResult = await pool.query(
            `SELECT COUNT(*) FROM user_violations ${status ? `WHERE action_taken = $1` : ''}`,
            status ? [status] : []
        );
        const total = parseInt(countResult.rows[0]?.count || '0');
        
        return {
            violations: result.rows,
            total,
            page,
            totalPages: Math.ceil(total / limit)
        };
    } catch (error) {
        logger.error('Error getting violations:', error);
        return { violations: [], total: 0, page: 1, totalPages: 0 };
    }
}

/**
 * Get images pending review
 */
export async function getPendingImageReviews(
    page: number = 1,
    limit: number = 20
): Promise<{
    reviews: any[];
    total: number;
}> {
    try {
        const offset = (page - 1) * limit;
        
        const result = await pool.query(
            `SELECT q.*, u.display_name as user_name, u.avatar_url as user_avatar
             FROM image_review_queue q
             LEFT JOIN users u ON q.user_id = u.id
             WHERE q.status = 'pending'
             ORDER BY q.created_at ASC
             LIMIT $1 OFFSET $2`,
            [limit, offset]
        );
        
        const countResult = await pool.query(
            `SELECT COUNT(*) FROM image_review_queue WHERE status = 'pending'`
        );
        
        return {
            reviews: result.rows,
            total: parseInt(countResult.rows[0]?.count || '0')
        };
    } catch (error) {
        logger.error('Error getting pending reviews:', error);
        return { reviews: [], total: 0 };
    }
}

/**
 * Approve or reject image from review queue
 */
export async function reviewImage(
    reviewId: string,
    approved: boolean,
    reviewerId: string
): Promise<boolean> {
    try {
        if (!approved) {
            // Get the review details first
            const reviewResult = await pool.query(
                `SELECT user_id, image_url, context FROM image_review_queue WHERE id = $1`,
                [reviewId]
            );
            
            if (reviewResult.rows[0]) {
                const review = reviewResult.rows[0];
                
                // Record violation for rejected image
                await recordViolation(review.user_id, {
                    type: 'vulgar_image',
                    severity: 'high',
                    imageUrl: review.image_url
                }, 'warn');
                
                // Remove the image based on context
                if (review.context === 'profile') {
                    await pool.query(
                        `UPDATE users SET avatar_url = null WHERE id = $1`,
                        [review.user_id]
                    );
                } else if (review.context === 'room_cover') {
                    await pool.query(
                        `UPDATE rooms SET cover_image = null WHERE owner_id = $1 AND cover_image = $2`,
                        [review.user_id, review.image_url]
                    );
                }
            }
        }
        
        await pool.query(
            `UPDATE image_review_queue 
             SET status = $2, reviewed_by = $3, reviewed_at = NOW()
             WHERE id = $1`,
            [reviewId, approved ? 'approved' : 'rejected', reviewerId]
        );
        
        return true;
    } catch (error) {
        logger.error('Error reviewing image:', error);
        return false;
    }
}

/**
 * Unban a user (admin action)
 */
export async function unbanUser(userId: string): Promise<boolean> {
    try {
        await pool.query(
            `UPDATE users 
             SET is_banned = false, ban_type = null, ban_reason = null, ban_expiry = null
             WHERE id = $1`,
            [userId]
        );
        logger.info(`User ${userId} unbanned by admin`);
        return true;
    } catch (error) {
        logger.error('Error unbanning user:', error);
        return false;
    }
}

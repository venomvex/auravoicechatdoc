/**
 * Authentication Service
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * OTP-based authentication with Twilio SMS support
 */

import { v4 as uuidv4 } from 'uuid';
import { config } from '../config';
import { query } from '../config/database.config';
import { logger } from '../utils/logger';

interface OtpResult {
  success: boolean;
  cooldownSeconds: number;
  attemptsRemaining: number;
  message?: string;
}

interface VerifyResult {
  success: boolean;
  userId: string;
  userName: string;
  isNewUser: boolean;
}

// In-memory OTP storage with rate limiting
// In production: Use Redis for distributed caching
const otpStore = new Map<string, { otp: string; expires: number; attempts: number }>();
const rateLimitStore = new Map<string, { count: number; resetAt: number }>();

// Twilio client (lazy initialization)
let twilioClient: any = null;

const getTwilioClient = () => {
  if (!twilioClient && config.twilio?.accountSid && config.twilio?.authToken) {
    try {
      // Dynamic import to avoid errors if twilio is not installed
      const twilio = require('twilio');
      twilioClient = twilio(config.twilio.accountSid, config.twilio.authToken);
      logger.info('Twilio client initialized');
    } catch (error) {
      logger.warn('Twilio SDK not available, SMS will be simulated', { error });
    }
  }
  return twilioClient;
};

// Generate random OTP
const generateOtp = (): string => {
  return Math.floor(100000 + Math.random() * 900000).toString();
};

// Check rate limit
const checkRateLimit = (phone: string): { allowed: boolean; attemptsRemaining: number; cooldownSeconds: number } => {
  const now = Date.now();
  const rateLimit = rateLimitStore.get(phone);
  
  if (!rateLimit || rateLimit.resetAt < now) {
    // Reset or new entry
    rateLimitStore.set(phone, { count: 1, resetAt: now + 3600000 }); // 1 hour window
    return { allowed: true, attemptsRemaining: 4, cooldownSeconds: 0 };
  }
  
  if (rateLimit.count >= 5) {
    const cooldown = Math.ceil((rateLimit.resetAt - now) / 1000);
    return { allowed: false, attemptsRemaining: 0, cooldownSeconds: cooldown };
  }
  
  rateLimit.count++;
  return { allowed: true, attemptsRemaining: 5 - rateLimit.count, cooldownSeconds: 60 };
};

// Send OTP via SMS
export const sendOtp = async (phone: string): Promise<OtpResult> => {
  try {
    // Check rate limit
    const rateCheck = checkRateLimit(phone);
    if (!rateCheck.allowed) {
      return {
        success: false,
        cooldownSeconds: rateCheck.cooldownSeconds,
        attemptsRemaining: 0,
        message: 'Too many attempts. Please try again later.'
      };
    }
    
    const otp = generateOtp();
    const expires = Date.now() + 5 * 60 * 1000; // 5 minutes
    
    otpStore.set(phone, { otp, expires, attempts: 0 });
    
    // Send SMS via Twilio if configured
    const client = getTwilioClient();
    if (client && config.twilio?.phoneNumber) {
      try {
        await client.messages.create({
          body: `Your Aura Voice Chat verification code is: ${otp}. Valid for 5 minutes.`,
          from: config.twilio.phoneNumber,
          to: phone
        });
        logger.info('OTP sent via Twilio', { phone: phone.slice(-4) });
      } catch (smsError) {
        logger.error('Failed to send SMS via Twilio', { error: smsError });
        // Continue anyway in development
        if (config.nodeEnv === 'production') {
          throw smsError;
        }
      }
    } else {
      // Development mode: Log OTP for testing
      if (config.nodeEnv === 'development') {
        logger.debug(`[DEV ONLY] OTP for ${phone}: ${otp}`);
      }
    }
    
    return {
      success: true,
      cooldownSeconds: 60,
      attemptsRemaining: rateCheck.attemptsRemaining
    };
  } catch (error) {
    logger.error('Failed to send OTP', { phone, error });
    return {
      success: false,
      cooldownSeconds: 0,
      attemptsRemaining: 0,
      message: 'Failed to send OTP. Please try again.'
    };
  }
};

// Verify OTP
export const verifyOtp = async (phone: string, otp: string): Promise<VerifyResult> => {
  try {
    const stored = otpStore.get(phone);
    
    if (!stored) {
      return {
        success: false,
        userId: '',
        userName: '',
        isNewUser: false
      };
    }
    
    // Check expiry
    if (stored.expires < Date.now()) {
      otpStore.delete(phone);
      return {
        success: false,
        userId: '',
        userName: '',
        isNewUser: false
      };
    }
    
    // Check attempts
    if (stored.attempts >= 3) {
      otpStore.delete(phone);
      return {
        success: false,
        userId: '',
        userName: '',
        isNewUser: false
      };
    }
    
    // Verify OTP
    if (stored.otp !== otp) {
      stored.attempts++;
      return {
        success: false,
        userId: '',
        userName: '',
        isNewUser: false
      };
    }
    
    // Clear OTP after successful verification
    otpStore.delete(phone);
    
    // Check if user exists in database
    const userResult = await query(
      'SELECT id, username, display_name FROM users WHERE phone_number = $1',
      [phone]
    );
    
    if (userResult.rows.length > 0) {
      const user = userResult.rows[0];
      
      // Update last login
      await query(
        'UPDATE users SET last_login_at = NOW(), login_streak = login_streak + 1 WHERE id = $1',
        [user.id]
      );
      
      return {
        success: true,
        userId: user.id,
        userName: user.username || user.display_name,
        isNewUser: false
      };
    }
    
    // Create new user
    const userId = uuidv4();
    const username = `User_${userId.slice(0, 8)}`;
    
    await query(
      `INSERT INTO users (id, phone_number, username, display_name, coins, diamonds, created_at, last_login_at)
       VALUES ($1, $2, $3, $3, 1000, 100, NOW(), NOW())`,
      [userId, phone, username]
    );
    
    logger.info('New user created', { userId, phone: phone.slice(-4) });
    
    return {
      success: true,
      userId,
      userName: username,
      isNewUser: true
    };
  } catch (error) {
    logger.error('Failed to verify OTP', { phone, error });
    return {
      success: false,
      userId: '',
      userName: '',
      isNewUser: false
    };
  }
};

/**
 * Authentication Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Supports:
 * - Phone OTP authentication
 * - Google Sign-In
 * - Facebook Sign-In
 * - Token refresh
 * - Logout
 */

import { Request, Response, NextFunction } from 'express';
import jwt, { Secret, SignOptions } from 'jsonwebtoken';
import { config } from '../config';
import { AppError } from '../middleware/errorHandler';
import * as authService from '../services/authService';

// Helper to convert expiresIn string to number for JWT
const getExpiresInSeconds = (expiresIn: string): number => {
  const match = expiresIn.match(/^(\d+)([dhms])$/);
  if (!match) return 604800; // Default 7 days
  const [, value, unit] = match;
  const num = parseInt(value, 10);
  switch (unit) {
    case 'd': return num * 86400;
    case 'h': return num * 3600;
    case 'm': return num * 60;
    case 's': return num;
    default: return 604800;
  }
};

/**
 * Generate JWT tokens for a user.
 */
const generateTokens = (userId: string, additionalClaims: Record<string, unknown> = {}) => {
  const secret: Secret = config.jwtSecret;
  const options: SignOptions = { expiresIn: getExpiresInSeconds(config.jwtExpiresIn) };
  
  const token = jwt.sign(
    { userId, ...additionalClaims },
    secret,
    options
  );
  
  const refreshSecret: Secret = config.jwtRefreshSecret;
  const refreshOptions: SignOptions = { expiresIn: getExpiresInSeconds(config.jwtRefreshExpiresIn) };
  
  const refreshToken = jwt.sign(
    { userId },
    refreshSecret,
    refreshOptions
  );
  
  return { token, refreshToken };
};

// Send OTP
export const sendOtp = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { phone } = req.body;
    const result = await authService.sendOtp(phone);
    
    // Build response - include devOtp if provided (dev/staging only)
    const response: Record<string, unknown> = {
      success: true,
      cooldownSeconds: result.cooldownSeconds,
      attemptsRemaining: result.attemptsRemaining
    };
    
    // Include dev OTP for testing in non-production environments
    if (result.devOtp) {
      response.devOtp = result.devOtp;
    }
    
    res.json(response);
  } catch (error) {
    next(error);
  }
};

// Verify OTP
export const verifyOtp = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { phone, otp } = req.body;
    const result = await authService.verifyOtp(phone, otp);
    
    if (!result.success) {
      throw new AppError('Invalid OTP', 401, 'INVALID_OTP');
    }
    
    const { token, refreshToken } = generateTokens(result.userId, { phone });
    
    res.json({
      success: true,
      token,
      refreshToken,
      user: {
        id: result.userId,
        name: result.userName,
        isNewUser: result.isNewUser
      }
    });
  } catch (error) {
    next(error);
  }
};

// Refresh token
export const refreshToken = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { refreshToken } = req.body;
    
    if (!refreshToken) {
      throw new AppError('Refresh token required', 400, 'REFRESH_TOKEN_REQUIRED');
    }
    
    const decoded = jwt.verify(refreshToken, config.jwtRefreshSecret) as { userId: string };
    
    const secret: Secret = config.jwtSecret;
    const options: SignOptions = { expiresIn: getExpiresInSeconds(config.jwtExpiresIn) };
    
    const token = jwt.sign(
      { userId: decoded.userId },
      secret,
      options
    );
    
    res.json({ token });
  } catch (error) {
    next(new AppError('Invalid refresh token', 401, 'INVALID_REFRESH_TOKEN'));
  }
};

// Logout
export const logout = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    // In a real implementation, you would invalidate the token
    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

/**
 * Google Sign-In
 * Validates Google ID token and creates/authenticates user.
 */
export const signInWithGoogle = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { idToken, email, displayName } = req.body;
    
    if (!idToken) {
      throw new AppError('Google ID token is required', 400, 'GOOGLE_TOKEN_REQUIRED');
    }
    
    // In production, verify the Google ID token using Google's API:
    // https://developers.google.com/identity/sign-in/web/backend-auth
    // For now, we'll use the authService to handle Google sign-in
    const result = await authService.signInWithSocial('google', {
      token: idToken,
      email,
      displayName
    });
    
    if (!result.success) {
      throw new AppError('Google authentication failed', 401, 'GOOGLE_AUTH_FAILED');
    }
    
    const { token, refreshToken } = generateTokens(result.userId, { provider: 'google' });
    
    res.json({
      success: true,
      token,
      refreshToken,
      user: {
        id: result.userId,
        name: result.userName,
        avatar: result.userAvatar,
        level: result.userLevel,
        vipTier: result.userVipTier,
        isNewUser: result.isNewUser
      },
      isNewUser: result.isNewUser
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Facebook Sign-In
 * Validates Facebook access token and creates/authenticates user.
 */
export const signInWithFacebook = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { accessToken, userId, email, displayName } = req.body;
    
    if (!accessToken) {
      throw new AppError('Facebook access token is required', 400, 'FACEBOOK_TOKEN_REQUIRED');
    }
    
    // In production, verify the Facebook access token using Facebook's API:
    // https://developers.facebook.com/docs/facebook-login/access-tokens/debugging-and-error-handling
    // For now, we'll use the authService to handle Facebook sign-in
    const result = await authService.signInWithSocial('facebook', {
      token: accessToken,
      socialUserId: userId,
      email,
      displayName
    });
    
    if (!result.success) {
      throw new AppError('Facebook authentication failed', 401, 'FACEBOOK_AUTH_FAILED');
    }
    
    const { token, refreshToken } = generateTokens(result.userId, { provider: 'facebook' });
    
    res.json({
      success: true,
      token,
      refreshToken,
      user: {
        id: result.userId,
        name: result.userName,
        avatar: result.userAvatar,
        level: result.userLevel,
        vipTier: result.userVipTier,
        isNewUser: result.isNewUser
      },
      isNewUser: result.isNewUser
    });
  } catch (error) {
    next(error);
  }
};

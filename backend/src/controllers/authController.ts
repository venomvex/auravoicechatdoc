/**
 * Authentication Controller
 * Developer: Hawkaye Visions LTD — Pakistan
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

// Send OTP
export const sendOtp = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { phone } = req.body;
    const result = await authService.sendOtp(phone);
    
    res.json({
      success: true,
      cooldownSeconds: result.cooldownSeconds,
      attemptsRemaining: result.attemptsRemaining
    });
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
    
    // Generate JWT with proper typing
    const secret: Secret = config.jwtSecret;
    const options: SignOptions = { expiresIn: getExpiresInSeconds(config.jwtExpiresIn) };
    
    const token = jwt.sign(
      { userId: result.userId, phone },
      secret,
      options
    );
    
    const refreshSecret: Secret = config.jwtRefreshSecret;
    const refreshOptions: SignOptions = { expiresIn: getExpiresInSeconds(config.jwtRefreshExpiresIn) };
    
    const refreshToken = jwt.sign(
      { userId: result.userId },
      refreshSecret,
      refreshOptions
    );
    
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

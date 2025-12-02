/**
 * Authentication Middleware
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Supports both JWT (local) and AWS Cognito token verification
 */

import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import { CognitoJwtVerifier } from 'aws-jwt-verify';
import { config } from '../config';
import { awsSettings } from '../config/aws.config';
import { query } from '../config/database.config';
import { AppError } from './errorHandler';
import { logger } from '../utils/logger';

export interface AuthRequest extends Request {
  userId?: string;
  cognitoSub?: string;
  user?: {
    id: string;
    cognitoSub?: string;
    phone?: string;
    email?: string;
    username?: string;
  };
}

// Cognito JWT Verifier (lazy initialization)
let cognitoVerifier: any = null;

const getCognitoVerifier = () => {
  if (!cognitoVerifier && awsSettings.cognito.userPoolId && awsSettings.cognito.clientId) {
    cognitoVerifier = CognitoJwtVerifier.create({
      userPoolId: awsSettings.cognito.userPoolId,
      tokenUse: 'access',
      clientId: awsSettings.cognito.clientId,
    });
  }
  return cognitoVerifier;
};

/**
 * Verify Cognito token
 */
const verifyCognitoToken = async (token: string): Promise<any> => {
  const verifier = getCognitoVerifier();
  if (!verifier) {
    throw new Error('Cognito not configured');
  }
  
  try {
    const payload = await verifier.verify(token);
    return payload;
  } catch (error) {
    logger.debug('Cognito token verification failed, trying JWT', { error });
    throw error;
  }
};

/**
 * Verify JWT token (local)
 */
const verifyJwtToken = (token: string): any => {
  return jwt.verify(token, config.jwtSecret);
};

/**
 * Get or create user from Cognito sub
 */
const getUserByCognitoSub = async (cognitoSub: string, email?: string, phone?: string): Promise<any> => {
  // Try to find existing user
  let result = await query(
    'SELECT id, username, email, phone_number, cognito_sub FROM users WHERE cognito_sub = $1',
    [cognitoSub]
  );
  
  if (result.rows.length > 0) {
    return result.rows[0];
  }
  
  // Try to find by email or phone
  if (email) {
    result = await query(
      'SELECT id, username, email, phone_number, cognito_sub FROM users WHERE email = $1',
      [email]
    );
    
    if (result.rows.length > 0) {
      // Link cognito_sub to existing user
      await query(
        'UPDATE users SET cognito_sub = $1 WHERE id = $2',
        [cognitoSub, result.rows[0].id]
      );
      return { ...result.rows[0], cognito_sub: cognitoSub };
    }
  }
  
  // User doesn't exist - this shouldn't happen for authenticated requests
  // but return null to handle gracefully
  return null;
};

/**
 * Main authentication middleware
 * Supports both Cognito and local JWT tokens
 */
export const authenticate = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const authHeader = req.headers.authorization;
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new AppError('No token provided', 401, 'UNAUTHORIZED');
    }
    
    const token = authHeader.split(' ')[1];
    
    let decoded: any;
    let isCognitoToken = false;
    
    // Try Cognito first if configured
    if (awsSettings.cognito.userPoolId) {
      try {
        decoded = await verifyCognitoToken(token);
        isCognitoToken = true;
      } catch {
        // Fall back to local JWT
        decoded = verifyJwtToken(token);
      }
    } else {
      // No Cognito configured, use local JWT
      decoded = verifyJwtToken(token);
    }
    
    if (isCognitoToken) {
      // Cognito token - get user from database by sub
      const cognitoSub = decoded.sub;
      const user = await getUserByCognitoSub(cognitoSub, decoded.email, decoded.phone_number);
      
      if (!user) {
        logger.warn('Cognito user not found in database', { cognitoSub });
        throw new AppError('User not found', 401, 'USER_NOT_FOUND');
      }
      
      req.userId = user.id;
      req.cognitoSub = cognitoSub;
      req.user = {
        id: user.id,
        cognitoSub,
        phone: user.phone_number,
        email: user.email,
        username: user.username,
      };
    } else {
      // Local JWT token
      req.userId = decoded.userId;
      req.user = {
        id: decoded.userId,
        phone: decoded.phone,
        email: decoded.email,
        username: decoded.username,
      };
    }
    
    next();
  } catch (error) {
    if (error instanceof jwt.TokenExpiredError) {
      next(new AppError('Token expired', 401, 'TOKEN_EXPIRED'));
    } else if (error instanceof jwt.JsonWebTokenError) {
      next(new AppError('Invalid token', 401, 'INVALID_TOKEN'));
    } else if (error instanceof AppError) {
      next(error);
    } else {
      logger.error('Authentication error', { error });
      next(new AppError('Authentication failed', 401, 'AUTH_FAILED'));
    }
  }
};

/**
 * Optional authentication - doesn't fail if no token
 */
export const optionalAuth = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const authHeader = req.headers.authorization;
    
    if (authHeader && authHeader.startsWith('Bearer ')) {
      const token = authHeader.split(' ')[1];
      
      let decoded: any;
      let isCognitoToken = false;
      
      if (awsSettings.cognito.userPoolId) {
        try {
          decoded = await verifyCognitoToken(token);
          isCognitoToken = true;
        } catch {
          decoded = verifyJwtToken(token);
        }
      } else {
        decoded = verifyJwtToken(token);
      }
      
      if (isCognitoToken) {
        const user = await getUserByCognitoSub(decoded.sub, decoded.email, decoded.phone_number);
        if (user) {
          req.userId = user.id;
          req.cognitoSub = decoded.sub;
          req.user = {
            id: user.id,
            cognitoSub: decoded.sub,
            phone: user.phone_number,
            email: user.email,
            username: user.username,
          };
        }
      } else {
        req.userId = decoded.userId;
        req.user = {
          id: decoded.userId,
          phone: decoded.phone,
          email: decoded.email,
        };
      }
    }
    
    next();
  } catch {
    // Continue without authentication
    next();
  }
};

/**
 * Verify token and return user info (for Socket.io)
 */
export const verifyToken = async (token: string): Promise<any> => {
  try {
    let decoded: any;
    
    if (awsSettings.cognito.userPoolId) {
      try {
        decoded = await verifyCognitoToken(token);
        const user = await getUserByCognitoSub(decoded.sub, decoded.email, decoded.phone_number);
        return user;
      } catch {
        decoded = verifyJwtToken(token);
        return { id: decoded.userId, username: decoded.username };
      }
    } else {
      decoded = verifyJwtToken(token);
      return { id: decoded.userId, username: decoded.username };
    }
  } catch {
    return null;
  }
};

/**
 * Require Admin role middleware
 */
export const requireAdmin = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    if (!req.user?.id) {
      throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
    }
    
    const result = await query('SELECT role FROM users WHERE id = $1', [req.user.id]);
    
    if (result.rows.length === 0) {
      throw new AppError('User not found', 404, 'USER_NOT_FOUND');
    }
    
    const role = result.rows[0].role;
    if (!['admin', 'country_admin', 'owner'].includes(role)) {
      throw new AppError('Admin access required', 403, 'FORBIDDEN');
    }
    
    next();
  } catch (error) {
    if (error instanceof AppError) {
      next(error);
    } else {
      next(new AppError('Authorization failed', 403, 'FORBIDDEN'));
    }
  }
};

/**
 * Require Country Admin role middleware
 */
export const requireCountryAdmin = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    if (!req.user?.id) {
      throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
    }
    
    const result = await query('SELECT role FROM users WHERE id = $1', [req.user.id]);
    
    if (result.rows.length === 0) {
      throw new AppError('User not found', 404, 'USER_NOT_FOUND');
    }
    
    const role = result.rows[0].role;
    if (!['country_admin', 'owner'].includes(role)) {
      throw new AppError('Country Admin access required', 403, 'FORBIDDEN');
    }
    
    next();
  } catch (error) {
    if (error instanceof AppError) {
      next(error);
    } else {
      next(new AppError('Authorization failed', 403, 'FORBIDDEN'));
    }
  }
};

/**
 * Require Owner role middleware
 */
export const requireOwner = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    if (!req.user?.id) {
      throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
    }
    
    const result = await query('SELECT role FROM users WHERE id = $1', [req.user.id]);
    
    if (result.rows.length === 0) {
      throw new AppError('User not found', 404, 'USER_NOT_FOUND');
    }
    
    const role = result.rows[0].role;
    if (role !== 'owner') {
      throw new AppError('Owner access required', 403, 'FORBIDDEN');
    }
    
    next();
  } catch (error) {
    if (error instanceof AppError) {
      next(error);
    } else {
      next(new AppError('Authorization failed', 403, 'FORBIDDEN'));
    }
  }
};

/**
 * Require Seller role middleware
 */
export const requireSeller = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    if (!req.user?.id) {
      throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
    }
    
    const result = await query('SELECT role FROM users WHERE id = $1', [req.user.id]);
    
    if (result.rows.length === 0) {
      throw new AppError('User not found', 404, 'USER_NOT_FOUND');
    }
    
    const role = result.rows[0].role;
    if (!['seller', 'country_admin', 'owner'].includes(role)) {
      throw new AppError('Seller access required', 403, 'FORBIDDEN');
    }
    
    next();
  } catch (error) {
    if (error instanceof AppError) {
      next(error);
    } else {
      next(new AppError('Authorization failed', 403, 'FORBIDDEN'));
    }
  }
};

// Alias for backward compatibility
export const authMiddleware = authenticate;

/**
 * Validation Middleware
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { body, param, query, validationResult } from 'express-validator';
import { Request, Response, NextFunction } from 'express';

// Validate request
export const validate = (req: Request, res: Response, next: NextFunction): void => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    res.status(400).json({
      error: {
        code: 'VALIDATION_ERROR',
        message: 'Invalid request data',
        details: errors.array()
      }
    });
    return;
  }
  next();
};

// Auth validations
export const validateSendOtp = [
  body('phone')
    .isMobilePhone('any')
    .withMessage('Invalid phone number format'),
  validate
];

export const validateVerifyOtp = [
  body('phone')
    .isMobilePhone('any')
    .withMessage('Invalid phone number format'),
  body('otp')
    .isLength({ min: 4, max: 6 })
    .isNumeric()
    .withMessage('OTP must be 4-6 digits'),
  validate
];

// Wallet validations
export const validateExchange = [
  body('diamonds')
    .isInt({ min: 100 })
    .withMessage('Minimum exchange is 100 diamonds'),
  validate
];

// Referral validations
export const validateBindReferral = [
  body('code')
    .isLength({ min: 4, max: 10 })
    .isAlphanumeric()
    .withMessage('Invalid referral code'),
  validate
];

export const validateWithdrawCash = [
  body('destination')
    .isIn(['wallet', 'bank', 'card', 'paypal', 'payoneer'])
    .withMessage('Invalid withdrawal destination'),
  validate
];

// Room validations
export const validateAddToPlaylist = [
  body('url')
    .isURL()
    .withMessage('Invalid URL format'),
  validate
];

// Medal validations
export const validateMedalDisplay = [
  body('displayedMedals')
    .isArray()
    .withMessage('displayedMedals must be an array'),
  body('hiddenMedals')
    .isArray()
    .withMessage('hiddenMedals must be an array'),
  validate
];

// Pagination validations
export const validatePagination = [
  query('page')
    .optional()
    .isInt({ min: 1 })
    .withMessage('Page must be a positive integer'),
  query('pageSize')
    .optional()
    .isInt({ min: 1, max: 100 })
    .withMessage('Page size must be between 1 and 100'),
  validate
];

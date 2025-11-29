/**
 * Express Type Definitions
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Extends Express Request to include user property
 */

import { Request } from 'express';

export interface UserPayload {
  id: string;
  cognitoSub?: string;
  phone?: string;
  email?: string;
  username?: string;
  role?: string;
}

declare global {
  namespace Express {
    interface Request {
      userId?: string;
      cognitoSub?: string;
      user?: UserPayload;
    }
  }
}

export {};

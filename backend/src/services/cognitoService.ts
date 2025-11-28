/**
 * Cognito Authentication Service
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * AWS Cognito operations for user authentication
 */

import {
  AdminCreateUserCommand,
  AdminDeleteUserCommand,
  AdminGetUserCommand,
  AdminInitiateAuthCommand,
  AdminRespondToAuthChallengeCommand,
  AdminSetUserPasswordCommand,
  AdminUpdateUserAttributesCommand,
  AdminUserGlobalSignOutCommand,
  ConfirmForgotPasswordCommand,
  ForgotPasswordCommand,
  GlobalSignOutCommand,
  InitiateAuthCommand,
  RespondToAuthChallengeCommand,
  SignUpCommand,
  ConfirmSignUpCommand,
  GetUserCommand,
} from '@aws-sdk/client-cognito-identity-provider';
import { createHmac } from 'crypto';
import { cognitoClient, awsSettings } from '../config/aws.config';
import { logger } from '../utils/logger';

interface AuthResult {
  success: boolean;
  accessToken?: string;
  refreshToken?: string;
  idToken?: string;
  expiresIn?: number;
  userId?: string;
  error?: string;
}

interface UserInfo {
  sub: string;
  email?: string;
  phoneNumber?: string;
  emailVerified?: boolean;
  phoneVerified?: boolean;
  username: string;
}

// Calculate secret hash for Cognito
const calculateSecretHash = (username: string): string => {
  const { clientId, clientSecret } = awsSettings.cognito;
  
  if (!clientSecret) {
    return '';
  }
  
  const hmac = createHmac('sha256', clientSecret);
  hmac.update(username + clientId);
  return hmac.digest('base64');
};

/**
 * Sign up a new user with email/phone
 */
export const signUp = async (
  username: string,
  password: string,
  email?: string,
  phoneNumber?: string,
  displayName?: string
): Promise<AuthResult> => {
  try {
    const userAttributes = [];
    
    if (email) {
      userAttributes.push({ Name: 'email', Value: email });
    }
    if (phoneNumber) {
      userAttributes.push({ Name: 'phone_number', Value: phoneNumber });
    }
    if (displayName) {
      userAttributes.push({ Name: 'name', Value: displayName });
      userAttributes.push({ Name: 'custom:display_name', Value: displayName });
    }
    
    const command = new SignUpCommand({
      ClientId: awsSettings.cognito.clientId,
      Username: username,
      Password: password,
      SecretHash: calculateSecretHash(username),
      UserAttributes: userAttributes,
    });
    
    const response = await cognitoClient.send(command);
    
    logger.info('User signed up successfully', { username, userSub: response.UserSub });
    
    return {
      success: true,
      userId: response.UserSub,
    };
  } catch (error: any) {
    logger.error('Sign up failed', { error: error.message, username });
    return {
      success: false,
      error: error.message || 'Sign up failed',
    };
  }
};

/**
 * Confirm user sign up with verification code
 */
export const confirmSignUp = async (
  username: string,
  confirmationCode: string
): Promise<AuthResult> => {
  try {
    const command = new ConfirmSignUpCommand({
      ClientId: awsSettings.cognito.clientId,
      Username: username,
      ConfirmationCode: confirmationCode,
      SecretHash: calculateSecretHash(username),
    });
    
    await cognitoClient.send(command);
    
    logger.info('User confirmed successfully', { username });
    
    return { success: true };
  } catch (error: any) {
    logger.error('Confirm sign up failed', { error: error.message, username });
    return {
      success: false,
      error: error.message || 'Confirmation failed',
    };
  }
};

/**
 * Sign in user with username/password
 */
export const signIn = async (
  username: string,
  password: string
): Promise<AuthResult> => {
  try {
    const command = new InitiateAuthCommand({
      AuthFlow: 'USER_PASSWORD_AUTH',
      ClientId: awsSettings.cognito.clientId,
      AuthParameters: {
        USERNAME: username,
        PASSWORD: password,
        SECRET_HASH: calculateSecretHash(username),
      },
    });
    
    const response = await cognitoClient.send(command);
    
    if (response.ChallengeName) {
      // Handle challenges (MFA, new password required, etc.)
      return {
        success: false,
        error: `Challenge required: ${response.ChallengeName}`,
      };
    }
    
    const authResult = response.AuthenticationResult;
    
    if (!authResult) {
      return {
        success: false,
        error: 'Authentication failed',
      };
    }
    
    logger.info('User signed in successfully', { username });
    
    return {
      success: true,
      accessToken: authResult.AccessToken,
      refreshToken: authResult.RefreshToken,
      idToken: authResult.IdToken,
      expiresIn: authResult.ExpiresIn,
    };
  } catch (error: any) {
    logger.error('Sign in failed', { error: error.message, username });
    return {
      success: false,
      error: error.message || 'Sign in failed',
    };
  }
};

/**
 * Admin sign in (server-side)
 */
export const adminSignIn = async (
  username: string,
  password: string
): Promise<AuthResult> => {
  try {
    const command = new AdminInitiateAuthCommand({
      UserPoolId: awsSettings.cognito.userPoolId,
      ClientId: awsSettings.cognito.clientId,
      AuthFlow: 'ADMIN_USER_PASSWORD_AUTH',
      AuthParameters: {
        USERNAME: username,
        PASSWORD: password,
        SECRET_HASH: calculateSecretHash(username),
      },
    });
    
    const response = await cognitoClient.send(command);
    
    if (response.ChallengeName) {
      return {
        success: false,
        error: `Challenge required: ${response.ChallengeName}`,
      };
    }
    
    const authResult = response.AuthenticationResult;
    
    if (!authResult) {
      return {
        success: false,
        error: 'Authentication failed',
      };
    }
    
    logger.info('Admin sign in successful', { username });
    
    return {
      success: true,
      accessToken: authResult.AccessToken,
      refreshToken: authResult.RefreshToken,
      idToken: authResult.IdToken,
      expiresIn: authResult.ExpiresIn,
    };
  } catch (error: any) {
    logger.error('Admin sign in failed', { error: error.message, username });
    return {
      success: false,
      error: error.message || 'Admin sign in failed',
    };
  }
};

/**
 * Refresh access token
 */
export const refreshTokens = async (
  refreshToken: string,
  username: string
): Promise<AuthResult> => {
  try {
    const command = new InitiateAuthCommand({
      AuthFlow: 'REFRESH_TOKEN_AUTH',
      ClientId: awsSettings.cognito.clientId,
      AuthParameters: {
        REFRESH_TOKEN: refreshToken,
        SECRET_HASH: calculateSecretHash(username),
      },
    });
    
    const response = await cognitoClient.send(command);
    const authResult = response.AuthenticationResult;
    
    if (!authResult) {
      return {
        success: false,
        error: 'Token refresh failed',
      };
    }
    
    return {
      success: true,
      accessToken: authResult.AccessToken,
      idToken: authResult.IdToken,
      expiresIn: authResult.ExpiresIn,
    };
  } catch (error: any) {
    logger.error('Token refresh failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Token refresh failed',
    };
  }
};

/**
 * Sign out user globally
 */
export const signOut = async (accessToken: string): Promise<AuthResult> => {
  try {
    const command = new GlobalSignOutCommand({
      AccessToken: accessToken,
    });
    
    await cognitoClient.send(command);
    
    logger.info('User signed out successfully');
    
    return { success: true };
  } catch (error: any) {
    logger.error('Sign out failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Sign out failed',
    };
  }
};

/**
 * Admin sign out user
 */
export const adminSignOut = async (username: string): Promise<AuthResult> => {
  try {
    const command = new AdminUserGlobalSignOutCommand({
      UserPoolId: awsSettings.cognito.userPoolId,
      Username: username,
    });
    
    await cognitoClient.send(command);
    
    logger.info('Admin signed out user', { username });
    
    return { success: true };
  } catch (error: any) {
    logger.error('Admin sign out failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Admin sign out failed',
    };
  }
};

/**
 * Initiate forgot password flow
 */
export const forgotPassword = async (username: string): Promise<AuthResult> => {
  try {
    const command = new ForgotPasswordCommand({
      ClientId: awsSettings.cognito.clientId,
      Username: username,
      SecretHash: calculateSecretHash(username),
    });
    
    await cognitoClient.send(command);
    
    logger.info('Forgot password initiated', { username });
    
    return { success: true };
  } catch (error: any) {
    logger.error('Forgot password failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Forgot password failed',
    };
  }
};

/**
 * Confirm forgot password with new password
 */
export const confirmForgotPassword = async (
  username: string,
  confirmationCode: string,
  newPassword: string
): Promise<AuthResult> => {
  try {
    const command = new ConfirmForgotPasswordCommand({
      ClientId: awsSettings.cognito.clientId,
      Username: username,
      ConfirmationCode: confirmationCode,
      Password: newPassword,
      SecretHash: calculateSecretHash(username),
    });
    
    await cognitoClient.send(command);
    
    logger.info('Password reset successful', { username });
    
    return { success: true };
  } catch (error: any) {
    logger.error('Confirm forgot password failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Password reset failed',
    };
  }
};

/**
 * Get user info from access token
 */
export const getUserInfo = async (accessToken: string): Promise<UserInfo | null> => {
  try {
    const command = new GetUserCommand({
      AccessToken: accessToken,
    });
    
    const response = await cognitoClient.send(command);
    
    const attributes: Record<string, string> = {};
    response.UserAttributes?.forEach((attr) => {
      if (attr.Name && attr.Value) {
        attributes[attr.Name] = attr.Value;
      }
    });
    
    return {
      sub: attributes.sub || '',
      email: attributes.email,
      phoneNumber: attributes.phone_number,
      emailVerified: attributes.email_verified === 'true',
      phoneVerified: attributes.phone_number_verified === 'true',
      username: response.Username || '',
    };
  } catch (error: any) {
    logger.error('Get user info failed', { error: error.message });
    return null;
  }
};

/**
 * Admin get user
 */
export const adminGetUser = async (username: string): Promise<UserInfo | null> => {
  try {
    const command = new AdminGetUserCommand({
      UserPoolId: awsSettings.cognito.userPoolId,
      Username: username,
    });
    
    const response = await cognitoClient.send(command);
    
    const attributes: Record<string, string> = {};
    response.UserAttributes?.forEach((attr) => {
      if (attr.Name && attr.Value) {
        attributes[attr.Name] = attr.Value;
      }
    });
    
    return {
      sub: attributes.sub || '',
      email: attributes.email,
      phoneNumber: attributes.phone_number,
      emailVerified: attributes.email_verified === 'true',
      phoneVerified: attributes.phone_number_verified === 'true',
      username: response.Username || '',
    };
  } catch (error: any) {
    logger.error('Admin get user failed', { error: error.message });
    return null;
  }
};

/**
 * Admin create user
 */
export const adminCreateUser = async (
  email: string,
  phoneNumber?: string,
  temporaryPassword?: string
): Promise<AuthResult> => {
  try {
    const userAttributes = [
      { Name: 'email', Value: email },
      { Name: 'email_verified', Value: 'true' },
    ];
    
    if (phoneNumber) {
      userAttributes.push({ Name: 'phone_number', Value: phoneNumber });
    }
    
    const command = new AdminCreateUserCommand({
      UserPoolId: awsSettings.cognito.userPoolId,
      Username: email,
      TemporaryPassword: temporaryPassword,
      UserAttributes: userAttributes,
      MessageAction: temporaryPassword ? 'SUPPRESS' : undefined,
    });
    
    const response = await cognitoClient.send(command);
    
    logger.info('Admin created user', { email });
    
    return {
      success: true,
      userId: response.User?.Username,
    };
  } catch (error: any) {
    logger.error('Admin create user failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Admin create user failed',
    };
  }
};

/**
 * Admin delete user
 */
export const adminDeleteUser = async (username: string): Promise<AuthResult> => {
  try {
    const command = new AdminDeleteUserCommand({
      UserPoolId: awsSettings.cognito.userPoolId,
      Username: username,
    });
    
    await cognitoClient.send(command);
    
    logger.info('Admin deleted user', { username });
    
    return { success: true };
  } catch (error: any) {
    logger.error('Admin delete user failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Admin delete user failed',
    };
  }
};

/**
 * Admin set user password
 */
export const adminSetUserPassword = async (
  username: string,
  password: string,
  permanent: boolean = true
): Promise<AuthResult> => {
  try {
    const command = new AdminSetUserPasswordCommand({
      UserPoolId: awsSettings.cognito.userPoolId,
      Username: username,
      Password: password,
      Permanent: permanent,
    });
    
    await cognitoClient.send(command);
    
    logger.info('Admin set user password', { username });
    
    return { success: true };
  } catch (error: any) {
    logger.error('Admin set user password failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Admin set user password failed',
    };
  }
};

/**
 * Admin update user attributes
 */
export const adminUpdateUserAttributes = async (
  username: string,
  attributes: Record<string, string>
): Promise<AuthResult> => {
  try {
    const userAttributes = Object.entries(attributes).map(([name, value]) => ({
      Name: name,
      Value: value,
    }));
    
    const command = new AdminUpdateUserAttributesCommand({
      UserPoolId: awsSettings.cognito.userPoolId,
      Username: username,
      UserAttributes: userAttributes,
    });
    
    await cognitoClient.send(command);
    
    logger.info('Admin updated user attributes', { username });
    
    return { success: true };
  } catch (error: any) {
    logger.error('Admin update user attributes failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Admin update user attributes failed',
    };
  }
};

export default {
  signUp,
  confirmSignUp,
  signIn,
  adminSignIn,
  refreshTokens,
  signOut,
  adminSignOut,
  forgotPassword,
  confirmForgotPassword,
  getUserInfo,
  adminGetUser,
  adminCreateUser,
  adminDeleteUser,
  adminSetUserPassword,
  adminUpdateUserAttributes,
};

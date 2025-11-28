/**
 * AWS Configuration Module
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Centralized AWS SDK configuration
 */

import { S3Client } from '@aws-sdk/client-s3';
import { CognitoIdentityProviderClient } from '@aws-sdk/client-cognito-identity-provider';
import { SNSClient } from '@aws-sdk/client-sns';

const awsConfig = {
  region: process.env.AWS_REGION || 'us-east-1',
  credentials: process.env.AWS_ACCESS_KEY_ID && process.env.AWS_SECRET_ACCESS_KEY
    ? {
        accessKeyId: process.env.AWS_ACCESS_KEY_ID,
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
      }
    : undefined, // Use IAM role if no credentials provided
};

// S3 Client
export const s3Client = new S3Client(awsConfig);

// Cognito Client
export const cognitoClient = new CognitoIdentityProviderClient(awsConfig);

// SNS Client
export const snsClient = new SNSClient(awsConfig);

export const awsSettings = {
  region: awsConfig.region,
  
  // Cognito
  cognito: {
    userPoolId: process.env.COGNITO_USER_POOL_ID || '',
    clientId: process.env.COGNITO_CLIENT_ID || '',
    clientSecret: process.env.COGNITO_CLIENT_SECRET || '',
    identityPoolId: process.env.COGNITO_IDENTITY_POOL_ID || '',
  },
  
  // S3
  s3: {
    bucketName: process.env.S3_BUCKET_NAME || 'aura-voice-chat-files',
    bucketRegion: process.env.S3_BUCKET_REGION || process.env.AWS_REGION || 'us-east-1',
    presignedUrlExpiry: 3600, // 1 hour
    maxFileSize: 10 * 1024 * 1024, // 10MB
    allowedMimeTypes: [
      'image/jpeg',
      'image/png',
      'image/gif',
      'image/webp',
      'audio/mpeg',
      'audio/wav',
      'audio/webm',
      'video/mp4',
      'video/webm',
    ],
    paths: {
      avatars: 'avatars',
      rooms: 'rooms',
      kyc: 'kyc',
      chat: 'chat',
      gifts: 'gifts',
      store: 'store',
    },
  },
  
  // SNS
  sns: {
    topicArn: process.env.SNS_TOPIC_ARN || '',
    platformApplicationArn: process.env.SNS_PLATFORM_APPLICATION_ARN || '',
  },
};

export default awsSettings;

/**
 * S3 Service
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * AWS S3 operations for file storage
 */

import {
  PutObjectCommand,
  GetObjectCommand,
  DeleteObjectCommand,
  ListObjectsV2Command,
  CopyObjectCommand,
  HeadObjectCommand,
} from '@aws-sdk/client-s3';
import { getSignedUrl } from '@aws-sdk/s3-request-presigner';
import { s3Client, awsSettings } from '../config/aws.config';
import { logger } from '../utils/logger';
import { v4 as uuidv4 } from 'uuid';

interface UploadResult {
  success: boolean;
  key?: string;
  url?: string;
  error?: string;
}

interface PresignedUrlResult {
  success: boolean;
  uploadUrl?: string;
  downloadUrl?: string;
  key?: string;
  error?: string;
}

const { s3 } = awsSettings;

/**
 * Generate a unique file key
 */
const generateFileKey = (folder: string, fileName: string): string => {
  const extension = fileName.split('.').pop() || '';
  const uniqueId = uuidv4();
  const timestamp = Date.now();
  return `${folder}/${uniqueId}-${timestamp}.${extension}`;
};

/**
 * Get the public URL for a file
 */
export const getPublicUrl = (key: string): string => {
  return `https://${s3.bucketName}.s3.${s3.bucketRegion}.amazonaws.com/${key}`;
};

/**
 * Upload a file directly to S3
 */
export const uploadFile = async (
  folder: string,
  fileName: string,
  body: Buffer | Uint8Array | string,
  contentType: string
): Promise<UploadResult> => {
  try {
    const key = generateFileKey(folder, fileName);
    
    const command = new PutObjectCommand({
      Bucket: s3.bucketName,
      Key: key,
      Body: body,
      ContentType: contentType,
    });
    
    await s3Client.send(command);
    
    const url = getPublicUrl(key);
    
    logger.info('File uploaded successfully', { key, contentType });
    
    return {
      success: true,
      key,
      url,
    };
  } catch (error: any) {
    logger.error('File upload failed', { error: error.message, folder, fileName });
    return {
      success: false,
      error: error.message || 'Upload failed',
    };
  }
};

/**
 * Generate a presigned URL for upload (client-side upload)
 */
export const getPresignedUploadUrl = async (
  folder: string,
  fileName: string,
  contentType: string
): Promise<PresignedUrlResult> => {
  try {
    // Validate content type
    if (!s3.allowedMimeTypes.includes(contentType)) {
      return {
        success: false,
        error: `Invalid content type: ${contentType}`,
      };
    }
    
    const key = generateFileKey(folder, fileName);
    
    const command = new PutObjectCommand({
      Bucket: s3.bucketName,
      Key: key,
      ContentType: contentType,
    });
    
    const uploadUrl = await getSignedUrl(s3Client, command, {
      expiresIn: s3.presignedUrlExpiry,
    });
    
    const downloadUrl = getPublicUrl(key);
    
    logger.info('Presigned upload URL generated', { key, contentType });
    
    return {
      success: true,
      uploadUrl,
      downloadUrl,
      key,
    };
  } catch (error: any) {
    logger.error('Presigned URL generation failed', { error: error.message });
    return {
      success: false,
      error: error.message || 'Failed to generate presigned URL',
    };
  }
};

/**
 * Generate a presigned URL for download
 */
export const getPresignedDownloadUrl = async (
  key: string
): Promise<PresignedUrlResult> => {
  try {
    const command = new GetObjectCommand({
      Bucket: s3.bucketName,
      Key: key,
    });
    
    const downloadUrl = await getSignedUrl(s3Client, command, {
      expiresIn: s3.presignedUrlExpiry,
    });
    
    return {
      success: true,
      downloadUrl,
      key,
    };
  } catch (error: any) {
    logger.error('Presigned download URL generation failed', { error: error.message, key });
    return {
      success: false,
      error: error.message || 'Failed to generate download URL',
    };
  }
};

/**
 * Download a file from S3
 */
export const downloadFile = async (key: string): Promise<Buffer | null> => {
  try {
    const command = new GetObjectCommand({
      Bucket: s3.bucketName,
      Key: key,
    });
    
    const response = await s3Client.send(command);
    
    if (!response.Body) {
      return null;
    }
    
    // Convert readable stream to buffer
    const chunks: Uint8Array[] = [];
    for await (const chunk of response.Body as any) {
      chunks.push(chunk);
    }
    
    return Buffer.concat(chunks);
  } catch (error: any) {
    logger.error('File download failed', { error: error.message, key });
    return null;
  }
};

/**
 * Delete a file from S3
 */
export const deleteFile = async (key: string): Promise<boolean> => {
  try {
    const command = new DeleteObjectCommand({
      Bucket: s3.bucketName,
      Key: key,
    });
    
    await s3Client.send(command);
    
    logger.info('File deleted successfully', { key });
    
    return true;
  } catch (error: any) {
    logger.error('File deletion failed', { error: error.message, key });
    return false;
  }
};

/**
 * Check if a file exists
 */
export const fileExists = async (key: string): Promise<boolean> => {
  try {
    const command = new HeadObjectCommand({
      Bucket: s3.bucketName,
      Key: key,
    });
    
    await s3Client.send(command);
    return true;
  } catch (error: any) {
    if (error.name === 'NotFound') {
      return false;
    }
    logger.error('File existence check failed', { error: error.message, key });
    return false;
  }
};

/**
 * Copy a file within S3
 */
export const copyFile = async (
  sourceKey: string,
  destinationKey: string
): Promise<boolean> => {
  try {
    const command = new CopyObjectCommand({
      Bucket: s3.bucketName,
      CopySource: `${s3.bucketName}/${sourceKey}`,
      Key: destinationKey,
    });
    
    await s3Client.send(command);
    
    logger.info('File copied successfully', { sourceKey, destinationKey });
    
    return true;
  } catch (error: any) {
    logger.error('File copy failed', { error: error.message, sourceKey, destinationKey });
    return false;
  }
};

/**
 * List files in a folder
 */
export const listFiles = async (
  prefix: string,
  maxKeys: number = 100
): Promise<string[]> => {
  try {
    const command = new ListObjectsV2Command({
      Bucket: s3.bucketName,
      Prefix: prefix,
      MaxKeys: maxKeys,
    });
    
    const response = await s3Client.send(command);
    
    return response.Contents?.map((obj) => obj.Key || '') || [];
  } catch (error: any) {
    logger.error('List files failed', { error: error.message, prefix });
    return [];
  }
};

/**
 * Upload avatar
 */
export const uploadAvatar = async (
  userId: string,
  fileName: string,
  body: Buffer,
  contentType: string
): Promise<UploadResult> => {
  const folder = `${s3.paths.avatars}/${userId}`;
  return uploadFile(folder, fileName, body, contentType);
};

/**
 * Get presigned URL for avatar upload
 */
export const getAvatarUploadUrl = async (
  userId: string,
  fileName: string,
  contentType: string
): Promise<PresignedUrlResult> => {
  const folder = `${s3.paths.avatars}/${userId}`;
  return getPresignedUploadUrl(folder, fileName, contentType);
};

/**
 * Upload room cover
 */
export const uploadRoomCover = async (
  roomId: string,
  fileName: string,
  body: Buffer,
  contentType: string
): Promise<UploadResult> => {
  const folder = `${s3.paths.rooms}/${roomId}/cover`;
  return uploadFile(folder, fileName, body, contentType);
};

/**
 * Get presigned URL for room cover upload
 */
export const getRoomCoverUploadUrl = async (
  roomId: string,
  fileName: string,
  contentType: string
): Promise<PresignedUrlResult> => {
  const folder = `${s3.paths.rooms}/${roomId}/cover`;
  return getPresignedUploadUrl(folder, fileName, contentType);
};

/**
 * Upload KYC document
 */
export const uploadKycDocument = async (
  userId: string,
  documentType: 'id-front' | 'id-back' | 'selfie',
  fileName: string,
  body: Buffer,
  contentType: string
): Promise<UploadResult> => {
  const folder = `${s3.paths.kyc}/${userId}/${documentType}`;
  return uploadFile(folder, fileName, body, contentType);
};

/**
 * Get presigned URL for KYC document upload
 */
export const getKycDocumentUploadUrl = async (
  userId: string,
  documentType: 'id-front' | 'id-back' | 'selfie',
  fileName: string,
  contentType: string
): Promise<PresignedUrlResult> => {
  const folder = `${s3.paths.kyc}/${userId}/${documentType}`;
  return getPresignedUploadUrl(folder, fileName, contentType);
};

/**
 * Upload chat media
 */
export const uploadChatMedia = async (
  roomId: string,
  messageId: string,
  fileName: string,
  body: Buffer,
  contentType: string
): Promise<UploadResult> => {
  const folder = `${s3.paths.chat}/${roomId}/${messageId}`;
  return uploadFile(folder, fileName, body, contentType);
};

/**
 * Get presigned URL for chat media upload
 */
export const getChatMediaUploadUrl = async (
  roomId: string,
  messageId: string,
  fileName: string,
  contentType: string
): Promise<PresignedUrlResult> => {
  const folder = `${s3.paths.chat}/${roomId}/${messageId}`;
  return getPresignedUploadUrl(folder, fileName, contentType);
};

export default {
  getPublicUrl,
  uploadFile,
  getPresignedUploadUrl,
  getPresignedDownloadUrl,
  downloadFile,
  deleteFile,
  fileExists,
  copyFile,
  listFiles,
  uploadAvatar,
  getAvatarUploadUrl,
  uploadRoomCover,
  getRoomCoverUploadUrl,
  uploadKycDocument,
  getKycDocumentUploadUrl,
  uploadChatMedia,
  getChatMediaUploadUrl,
};

/**
 * SNS Push Notification Service
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * AWS SNS operations for push notifications
 */

import {
  PublishCommand,
  CreatePlatformEndpointCommand,
  DeleteEndpointCommand,
  SetEndpointAttributesCommand,
  GetEndpointAttributesCommand,
} from '@aws-sdk/client-sns';
import { snsClient, awsSettings } from '../config/aws.config';
import { logger } from '../utils/logger';
import { query } from '../config/database.config';

interface NotificationResult {
  success: boolean;
  messageId?: string;
  error?: string;
}

interface EndpointResult {
  success: boolean;
  endpointArn?: string;
  error?: string;
}

interface PushNotification {
  title: string;
  body: string;
  data?: Record<string, string>;
  badge?: number;
  sound?: string;
}

const { sns } = awsSettings;

/**
 * Create a platform endpoint for a device
 */
export const createPlatformEndpoint = async (
  deviceToken: string,
  userId: string
): Promise<EndpointResult> => {
  try {
    if (!sns.platformApplicationArn) {
      return {
        success: false,
        error: 'Platform application ARN not configured',
      };
    }
    
    const command = new CreatePlatformEndpointCommand({
      PlatformApplicationArn: sns.platformApplicationArn,
      Token: deviceToken,
      CustomUserData: userId,
    });
    
    const response = await snsClient.send(command);
    
    logger.info('Platform endpoint created', { userId, endpointArn: response.EndpointArn });
    
    return {
      success: true,
      endpointArn: response.EndpointArn,
    };
  } catch (error: any) {
    logger.error('Create platform endpoint failed', { error: error.message, userId });
    return {
      success: false,
      error: error.message || 'Failed to create endpoint',
    };
  }
};

/**
 * Update endpoint attributes (e.g., when token changes)
 */
export const updateEndpointToken = async (
  endpointArn: string,
  newToken: string
): Promise<boolean> => {
  try {
    const command = new SetEndpointAttributesCommand({
      EndpointArn: endpointArn,
      Attributes: {
        Token: newToken,
        Enabled: 'true',
      },
    });
    
    await snsClient.send(command);
    
    logger.info('Endpoint token updated', { endpointArn });
    
    return true;
  } catch (error: any) {
    logger.error('Update endpoint token failed', { error: error.message, endpointArn });
    return false;
  }
};

/**
 * Check if endpoint is enabled
 */
export const checkEndpointStatus = async (endpointArn: string): Promise<boolean> => {
  try {
    const command = new GetEndpointAttributesCommand({
      EndpointArn: endpointArn,
    });
    
    const response = await snsClient.send(command);
    
    return response.Attributes?.Enabled === 'true';
  } catch (error: any) {
    logger.error('Check endpoint status failed', { error: error.message, endpointArn });
    return false;
  }
};

/**
 * Delete a platform endpoint
 */
export const deleteEndpoint = async (endpointArn: string): Promise<boolean> => {
  try {
    const command = new DeleteEndpointCommand({
      EndpointArn: endpointArn,
    });
    
    await snsClient.send(command);
    
    logger.info('Endpoint deleted', { endpointArn });
    
    return true;
  } catch (error: any) {
    logger.error('Delete endpoint failed', { error: error.message, endpointArn });
    return false;
  }
};

/**
 * Send push notification to a single device
 */
export const sendPushNotification = async (
  endpointArn: string,
  notification: PushNotification
): Promise<NotificationResult> => {
  try {
    // Format message for different platforms
    const message = {
      default: notification.body,
      GCM: JSON.stringify({
        notification: {
          title: notification.title,
          body: notification.body,
          sound: notification.sound || 'default',
        },
        data: notification.data || {},
      }),
      APNS: JSON.stringify({
        aps: {
          alert: {
            title: notification.title,
            body: notification.body,
          },
          sound: notification.sound || 'default',
          badge: notification.badge,
        },
        ...notification.data,
      }),
      APNS_SANDBOX: JSON.stringify({
        aps: {
          alert: {
            title: notification.title,
            body: notification.body,
          },
          sound: notification.sound || 'default',
          badge: notification.badge,
        },
        ...notification.data,
      }),
    };
    
    const command = new PublishCommand({
      TargetArn: endpointArn,
      Message: JSON.stringify(message),
      MessageStructure: 'json',
    });
    
    const response = await snsClient.send(command);
    
    logger.info('Push notification sent', { endpointArn, messageId: response.MessageId });
    
    return {
      success: true,
      messageId: response.MessageId,
    };
  } catch (error: any) {
    logger.error('Send push notification failed', { error: error.message, endpointArn });
    return {
      success: false,
      error: error.message || 'Failed to send notification',
    };
  }
};

/**
 * Send push notification to a topic (broadcast)
 */
export const sendTopicNotification = async (
  topicArn: string,
  notification: PushNotification
): Promise<NotificationResult> => {
  try {
    const message = {
      default: notification.body,
      GCM: JSON.stringify({
        notification: {
          title: notification.title,
          body: notification.body,
          sound: notification.sound || 'default',
        },
        data: notification.data || {},
      }),
      APNS: JSON.stringify({
        aps: {
          alert: {
            title: notification.title,
            body: notification.body,
          },
          sound: notification.sound || 'default',
        },
        ...notification.data,
      }),
    };
    
    const command = new PublishCommand({
      TopicArn: topicArn,
      Message: JSON.stringify(message),
      MessageStructure: 'json',
    });
    
    const response = await snsClient.send(command);
    
    logger.info('Topic notification sent', { topicArn, messageId: response.MessageId });
    
    return {
      success: true,
      messageId: response.MessageId,
    };
  } catch (error: any) {
    logger.error('Send topic notification failed', { error: error.message, topicArn });
    return {
      success: false,
      error: error.message || 'Failed to send topic notification',
    };
  }
};

/**
 * Send notification to a user by userId
 */
export const sendNotificationToUser = async (
  userId: string,
  notification: PushNotification
): Promise<NotificationResult> => {
  try {
    // Get user's device token from database (SNS endpoint ARN)
    const result = await query(
      'SELECT sns_endpoint_arn, device_token FROM users WHERE id = $1 AND device_token IS NOT NULL',
      [userId]
    );
    
    if (result.rows.length === 0 || !result.rows[0].device_token) {
      return {
        success: false,
        error: 'User has no registered device token',
      };
    }
    
    const deviceToken = result.rows[0].device_token;
    let endpointArn = result.rows[0].sns_endpoint_arn;
    
    // Create endpoint if needed
    if (!endpointArn) {
      const endpointResult = await createPlatformEndpoint(deviceToken, userId);
      
      if (!endpointResult.success || !endpointResult.endpointArn) {
        return {
          success: false,
          error: endpointResult.error || 'Failed to create endpoint',
        };
      }
      
      endpointArn = endpointResult.endpointArn;
      
      // Store the endpoint ARN for future use
      await query(
        'UPDATE users SET sns_endpoint_arn = $1 WHERE id = $2',
        [endpointArn, userId]
      );
    }
    
    return sendPushNotification(endpointArn, notification);
  } catch (error: any) {
    logger.error('Send notification to user failed', { error: error.message, userId });
    return {
      success: false,
      error: error.message || 'Failed to send notification',
    };
  }
};

/**
 * Send notification to multiple users
 */
export const sendNotificationToUsers = async (
  userIds: string[],
  notification: PushNotification
): Promise<{ success: number; failed: number }> => {
  const results = await Promise.allSettled(
    userIds.map((userId) => sendNotificationToUser(userId, notification))
  );
  
  let success = 0;
  let failed = 0;
  
  results.forEach((result) => {
    if (result.status === 'fulfilled' && result.value.success) {
      success++;
    } else {
      failed++;
    }
  });
  
  logger.info('Bulk notifications sent', { total: userIds.length, success, failed });
  
  return { success, failed };
};

/**
 * Store notification in database and send push
 */
export const createAndSendNotification = async (
  userId: string,
  type: string,
  title: string,
  body: string,
  data?: Record<string, any>
): Promise<NotificationResult> => {
  try {
    // Store in database
    await query(
      `INSERT INTO notifications (user_id, type, title, body, data, is_read, created_at)
       VALUES ($1, $2, $3, $4, $5, false, CURRENT_TIMESTAMP)`,
      [userId, type, title, body, JSON.stringify(data || {})]
    );
    
    // Send push notification
    return sendNotificationToUser(userId, {
      title,
      body,
      data: data ? Object.fromEntries(
        Object.entries(data).map(([k, v]) => [k, String(v)])
      ) : undefined,
    });
  } catch (error: any) {
    logger.error('Create and send notification failed', { error: error.message, userId });
    return {
      success: false,
      error: error.message || 'Failed to create notification',
    };
  }
};

export default {
  createPlatformEndpoint,
  updateEndpointToken,
  checkEndpointStatus,
  deleteEndpoint,
  sendPushNotification,
  sendTopicNotification,
  sendNotificationToUser,
  sendNotificationToUsers,
  createAndSendNotification,
};

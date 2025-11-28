#!/usr/bin/env node

/**
 * Firebase to AWS Migration Script
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * This script migrates data from Firebase to AWS:
 * - Firestore collections → PostgreSQL tables
 * - Firebase Storage → S3
 * - Firebase Auth users → Cognito + PostgreSQL
 * 
 * Usage:
 *   node migrate-firebase-to-aws.js [options]
 * 
 * Options:
 *   --dry-run       Preview changes without making them
 *   --users-only    Migrate only users
 *   --data-only     Migrate only Firestore data
 *   --files-only    Migrate only storage files
 *   --verbose       Show detailed output
 */

const admin = require('firebase-admin');
const { Pool } = require('pg');
const { 
  S3Client, 
  PutObjectCommand 
} = require('@aws-sdk/client-s3');
const {
  CognitoIdentityProviderClient,
  AdminCreateUserCommand,
  AdminSetUserPasswordCommand,
} = require('@aws-sdk/client-cognito-identity-provider');
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');
const path = require('path');

// ============================================================================
// Configuration
// ============================================================================

const config = {
  // Firebase
  firebase: {
    serviceAccountPath: process.env.FIREBASE_SERVICE_ACCOUNT_PATH || './firebase-service-account.json',
    storageBucket: process.env.FIREBASE_STORAGE_BUCKET || '',
  },
  
  // AWS
  aws: {
    region: process.env.AWS_REGION || 'us-east-1',
  },
  
  // Cognito
  cognito: {
    userPoolId: process.env.COGNITO_USER_POOL_ID || '',
    clientId: process.env.COGNITO_CLIENT_ID || '',
  },
  
  // PostgreSQL
  postgres: {
    connectionString: process.env.DATABASE_URL || '',
  },
  
  // S3
  s3: {
    bucketName: process.env.S3_BUCKET_NAME || '',
  },
  
  // Migration options
  batchSize: 100,
  dryRun: process.argv.includes('--dry-run'),
  usersOnly: process.argv.includes('--users-only'),
  dataOnly: process.argv.includes('--data-only'),
  filesOnly: process.argv.includes('--files-only'),
  verbose: process.argv.includes('--verbose'),
};

// ============================================================================
// Clients
// ============================================================================

let db;
let pool;
let s3Client;
let cognitoClient;

function initClients() {
  // Initialize Firebase
  if (fs.existsSync(config.firebase.serviceAccountPath)) {
    const serviceAccount = require(config.firebase.serviceAccountPath);
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      storageBucket: config.firebase.storageBucket,
    });
    db = admin.firestore();
    log('Firebase initialized');
  } else {
    log('Warning: Firebase service account not found, skipping Firebase initialization');
  }
  
  // Initialize PostgreSQL
  if (config.postgres.connectionString) {
    pool = new Pool({ connectionString: config.postgres.connectionString });
    log('PostgreSQL pool initialized');
  }
  
  // Initialize S3
  s3Client = new S3Client({ region: config.aws.region });
  log('S3 client initialized');
  
  // Initialize Cognito
  if (config.cognito.userPoolId) {
    cognitoClient = new CognitoIdentityProviderClient({ region: config.aws.region });
    log('Cognito client initialized');
  }
}

// ============================================================================
// Logging
// ============================================================================

const stats = {
  users: { total: 0, migrated: 0, failed: 0 },
  rooms: { total: 0, migrated: 0, failed: 0 },
  messages: { total: 0, migrated: 0, failed: 0 },
  files: { total: 0, migrated: 0, failed: 0 },
};

function log(message, data = null) {
  const timestamp = new Date().toISOString();
  console.log(`[${timestamp}] ${message}`);
  if (config.verbose && data) {
    console.log(JSON.stringify(data, null, 2));
  }
}

function logError(message, error) {
  const timestamp = new Date().toISOString();
  console.error(`[${timestamp}] ERROR: ${message}`);
  console.error(error);
}

// ============================================================================
// User Migration
// ============================================================================

async function migrateUsers() {
  log('Starting user migration...');
  
  if (!db) {
    log('Skipping user migration - Firebase not initialized');
    return;
  }
  
  try {
    const usersSnapshot = await db.collection('users').get();
    stats.users.total = usersSnapshot.size;
    
    log(`Found ${stats.users.total} users to migrate`);
    
    for (const doc of usersSnapshot.docs) {
      const userData = doc.data();
      const firebaseUid = doc.id;
      
      try {
        // Generate new UUID for PostgreSQL
        const newUserId = uuidv4();
        
        // Create user in Cognito (if configured)
        let cognitoSub = null;
        if (cognitoClient && userData.email) {
          if (!config.dryRun) {
            try {
              const cognitoResult = await cognitoClient.send(new AdminCreateUserCommand({
                UserPoolId: config.cognito.userPoolId,
                Username: userData.email,
                UserAttributes: [
                  { Name: 'email', Value: userData.email },
                  { Name: 'email_verified', Value: 'true' },
                  { Name: 'custom:firebase_uid', Value: firebaseUid },
                ],
                MessageAction: 'SUPPRESS',
              }));
              cognitoSub = cognitoResult.User?.Attributes?.find(a => a.Name === 'sub')?.Value;
              log(`Created Cognito user for ${userData.email}`);
            } catch (cognitoError) {
              if (cognitoError.name === 'UsernameExistsException') {
                log(`Cognito user already exists: ${userData.email}`);
              } else {
                throw cognitoError;
              }
            }
          } else {
            log(`[DRY RUN] Would create Cognito user: ${userData.email}`);
          }
        }
        
        // Insert into PostgreSQL
        if (pool) {
          const query = `
            INSERT INTO users (
              id, cognito_sub, email, phone_number, username, display_name,
              avatar_url, bio, status, level, exp, coins, diamonds,
              vip_tier, is_verified, created_at, updated_at
            ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17)
            ON CONFLICT (email) DO UPDATE SET
              cognito_sub = COALESCE(EXCLUDED.cognito_sub, users.cognito_sub),
              updated_at = CURRENT_TIMESTAMP
          `;
          
          const values = [
            newUserId,
            cognitoSub,
            userData.email || null,
            userData.phone || userData.phoneNumber || null,
            userData.username || null,
            userData.displayName || userData.name || null,
            userData.avatarUrl || userData.photoURL || null,
            userData.bio || null,
            'offline',
            userData.level || 1,
            userData.exp || 0,
            userData.coins || 0,
            userData.diamonds || 0,
            userData.vipTier || 0,
            userData.isVerified || false,
            userData.createdAt?.toDate() || new Date(),
            new Date(),
          ];
          
          if (!config.dryRun) {
            await pool.query(query, values);
          } else {
            log(`[DRY RUN] Would insert user: ${userData.email || userData.username}`);
          }
        }
        
        stats.users.migrated++;
        
        if (stats.users.migrated % 100 === 0) {
          log(`Migrated ${stats.users.migrated}/${stats.users.total} users`);
        }
        
      } catch (error) {
        stats.users.failed++;
        logError(`Failed to migrate user ${firebaseUid}`, error);
      }
    }
    
    log(`User migration complete: ${stats.users.migrated} migrated, ${stats.users.failed} failed`);
    
  } catch (error) {
    logError('User migration failed', error);
  }
}

// ============================================================================
// Room Migration
// ============================================================================

async function migrateRooms() {
  log('Starting room migration...');
  
  if (!db) {
    log('Skipping room migration - Firebase not initialized');
    return;
  }
  
  try {
    const roomsSnapshot = await db.collection('rooms').get();
    stats.rooms.total = roomsSnapshot.size;
    
    log(`Found ${stats.rooms.total} rooms to migrate`);
    
    for (const doc of roomsSnapshot.docs) {
      const roomData = doc.data();
      const firebaseRoomId = doc.id;
      
      try {
        const newRoomId = uuidv4();
        
        // Get owner's new ID from PostgreSQL
        let ownerId = null;
        if (pool && roomData.ownerId) {
          const ownerResult = await pool.query(
            'SELECT id FROM users WHERE email = $1 OR username = $2 LIMIT 1',
            [roomData.ownerEmail, roomData.ownerId]
          );
          if (ownerResult.rows.length > 0) {
            ownerId = ownerResult.rows[0].id;
          }
        }
        
        if (!ownerId) {
          log(`Warning: Owner not found for room ${firebaseRoomId}, using placeholder`);
          ownerId = uuidv4(); // Placeholder, should be updated manually
        }
        
        const query = `
          INSERT INTO rooms (
            id, name, description, cover_url, type, owner_id,
            is_private, max_participants, announcement, welcome_message,
            created_at, updated_at
          ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
        `;
        
        const values = [
          newRoomId,
          roomData.name || 'Unnamed Room',
          roomData.description || null,
          roomData.coverUrl || roomData.coverImage || null,
          roomData.type || 'voice',
          ownerId,
          roomData.isPrivate || false,
          roomData.maxParticipants || 20,
          roomData.announcement || null,
          roomData.welcomeMessage || null,
          roomData.createdAt?.toDate() || new Date(),
          new Date(),
        ];
        
        if (!config.dryRun && pool) {
          await pool.query(query, values);
        } else {
          log(`[DRY RUN] Would insert room: ${roomData.name}`);
        }
        
        // Migrate room messages
        await migrateRoomMessages(firebaseRoomId, newRoomId);
        
        stats.rooms.migrated++;
        
      } catch (error) {
        stats.rooms.failed++;
        logError(`Failed to migrate room ${firebaseRoomId}`, error);
      }
    }
    
    log(`Room migration complete: ${stats.rooms.migrated} migrated, ${stats.rooms.failed} failed`);
    
  } catch (error) {
    logError('Room migration failed', error);
  }
}

async function migrateRoomMessages(firebaseRoomId, newRoomId) {
  if (!db) return;
  
  try {
    const messagesSnapshot = await db
      .collection('rooms')
      .doc(firebaseRoomId)
      .collection('messages')
      .orderBy('createdAt', 'asc')
      .get();
    
    for (const doc of messagesSnapshot.docs) {
      const messageData = doc.data();
      
      try {
        const query = `
          INSERT INTO messages (id, room_id, user_id, content, type, created_at)
          VALUES ($1, $2, $3, $4, $5, $6)
        `;
        
        const values = [
          uuidv4(),
          newRoomId,
          null, // Would need to map user IDs
          messageData.content || messageData.text || '',
          messageData.type || 'text',
          messageData.createdAt?.toDate() || new Date(),
        ];
        
        if (!config.dryRun && pool) {
          await pool.query(query, values);
        }
        
        stats.messages.migrated++;
        
      } catch (error) {
        stats.messages.failed++;
      }
    }
    
  } catch (error) {
    logError(`Failed to migrate messages for room ${firebaseRoomId}`, error);
  }
}

// ============================================================================
// Storage Migration
// ============================================================================

async function migrateStorageFiles() {
  log('Starting storage file migration...');
  
  if (!admin.storage) {
    log('Skipping storage migration - Firebase Storage not initialized');
    return;
  }
  
  try {
    const bucket = admin.storage().bucket();
    const [files] = await bucket.getFiles();
    
    stats.files.total = files.length;
    log(`Found ${stats.files.total} files to migrate`);
    
    for (const file of files) {
      try {
        const [fileBuffer] = await file.download();
        const [metadata] = await file.getMetadata();
        
        const s3Key = file.name; // Keep same path structure
        
        if (!config.dryRun) {
          await s3Client.send(new PutObjectCommand({
            Bucket: config.s3.bucketName,
            Key: s3Key,
            Body: fileBuffer,
            ContentType: metadata.contentType,
          }));
        } else {
          log(`[DRY RUN] Would upload file: ${file.name}`);
        }
        
        stats.files.migrated++;
        
        if (stats.files.migrated % 50 === 0) {
          log(`Migrated ${stats.files.migrated}/${stats.files.total} files`);
        }
        
      } catch (error) {
        stats.files.failed++;
        logError(`Failed to migrate file ${file.name}`, error);
      }
    }
    
    log(`Storage migration complete: ${stats.files.migrated} migrated, ${stats.files.failed} failed`);
    
  } catch (error) {
    logError('Storage migration failed', error);
  }
}

// ============================================================================
// Main
// ============================================================================

async function generateReport() {
  const report = {
    timestamp: new Date().toISOString(),
    dryRun: config.dryRun,
    stats,
    summary: {
      totalItems: stats.users.total + stats.rooms.total + stats.messages.total + stats.files.total,
      totalMigrated: stats.users.migrated + stats.rooms.migrated + stats.messages.migrated + stats.files.migrated,
      totalFailed: stats.users.failed + stats.rooms.failed + stats.messages.failed + stats.files.failed,
    },
  };
  
  console.log('\n========================================');
  console.log('MIGRATION REPORT');
  console.log('========================================');
  console.log(JSON.stringify(report, null, 2));
  console.log('========================================\n');
  
  // Save report to file
  const reportPath = `./migration-report-${Date.now()}.json`;
  fs.writeFileSync(reportPath, JSON.stringify(report, null, 2));
  log(`Report saved to ${reportPath}`);
  
  return report;
}

async function main() {
  console.log('\n');
  console.log('╔══════════════════════════════════════════════════════════════════╗');
  console.log('║                                                                  ║');
  console.log('║     AURA VOICE CHAT - FIREBASE TO AWS MIGRATION                  ║');
  console.log('║                                                                  ║');
  console.log('║     Developer: Hawkaye Visions LTD — Lahore, Pakistan           ║');
  console.log('║                                                                  ║');
  console.log('╚══════════════════════════════════════════════════════════════════╝');
  console.log('\n');
  
  if (config.dryRun) {
    log('DRY RUN MODE - No changes will be made');
  }
  
  try {
    initClients();
    
    if (!config.filesOnly && !config.dataOnly) {
      await migrateUsers();
    }
    
    if (!config.usersOnly && !config.filesOnly) {
      await migrateRooms();
    }
    
    if (!config.usersOnly && !config.dataOnly) {
      await migrateStorageFiles();
    }
    
    await generateReport();
    
    log('Migration completed successfully!');
    
  } catch (error) {
    logError('Migration failed', error);
    process.exit(1);
  } finally {
    if (pool) {
      await pool.end();
    }
  }
}

main();

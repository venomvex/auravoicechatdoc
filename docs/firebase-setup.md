# Firebase Setup

Complete guide for Firebase configuration, Crashlytics, Analytics, database setup, security rules, and environment management for Aura Voice Chat.

**Developer:** Hawkaye Visions LTD — Pakistan

## Overview

Aura Voice Chat uses Firebase for:
- Authentication (Firebase Auth)
- Crash Reporting (Crashlytics)
- Analytics (Google Analytics for Firebase)
- Remote Configuration (Remote Config)
- Real-time features (Firestore/Realtime Database)
- Push Notifications (FCM)

---

## Project Setup

### Creating Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click "Create Project"
3. Enter project name: `aura-voice-chat-{env}`
4. Enable Google Analytics
5. Configure Analytics account
6. Create project

### Environment Projects

| Environment | Project ID | Purpose |
|-------------|------------|---------|
| Development | `aura-voice-chat-dev` | Development testing |
| Staging | `aura-voice-chat-staging` | QA testing |
| Production | `aura-voice-chat-prod` | Live users |

### Adding Android App

1. In Firebase Console, click "Add app" → Android
2. Package name: `com.aura.voicechat`
3. App nickname: "Aura Voice Chat"
4. Debug signing certificate SHA-1
5. Download `google-services.json`
6. Place in `app/` directory

---

## Firebase Authentication

### Enabled Providers

| Provider | Configuration |
|----------|---------------|
| Phone | Enabled with test phone numbers |
| Google | OAuth client ID configured |
| Facebook | App ID and secret configured |

### Phone Authentication Setup

```groovy
// build.gradle (app)
implementation 'com.google.firebase:firebase-auth-ktx:22.3.0'
```

**Test Phone Numbers (Development):**
```
+1 650-555-1234 → 123456
+1 650-555-5678 → 654321
```

### OAuth Configuration

**Google Sign-In:**
1. Enable in Firebase Console
2. Download updated `google-services.json`
3. Configure Web Client ID

**Facebook Login:**
1. Create Facebook App
2. Add Facebook App ID to Firebase
3. Add OAuth redirect URI to Facebook App

---

## Crashlytics

### Setup

```groovy
// build.gradle (project)
classpath 'com.google.firebase:firebase-crashlytics-gradle:2.9.9'

// build.gradle (app)
apply plugin: 'com.google.firebase.crashlytics'

dependencies {
    implementation 'com.google.firebase:firebase-crashlytics-ktx:18.6.0'
}
```

### Configuration

```kotlin
// Application class
Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

// Custom keys for debugging
Firebase.crashlytics.setCustomKey("user_id", userId)
Firebase.crashlytics.setCustomKey("vip_tier", vipTier)
Firebase.crashlytics.setCustomKey("room_id", currentRoomId)
```

### Non-Fatal Errors

```kotlin
try {
    // risky operation
} catch (e: Exception) {
    Firebase.crashlytics.recordException(e)
}
```

### Best Practices

- Disable in debug builds
- Add meaningful custom keys
- Log non-fatal errors
- Set user identifiers (hashed)
- Configure alerts in Firebase Console

---

## Analytics

### Setup

```groovy
// build.gradle (app)
implementation 'com.google.firebase:firebase-analytics-ktx:21.5.0'
```

### Standard Events

| Event | Parameters | When |
|-------|------------|------|
| `login` | method | User authenticates |
| `sign_up` | method | New registration |
| `purchase` | currency, value | Coin purchase |
| `spend_virtual_currency` | item_name, value | Gift/item purchase |
| `earn_virtual_currency` | currency_name, value | Reward earned |

### Custom Events

```kotlin
// Daily reward claim
Firebase.analytics.logEvent("daily_reward_claim") {
    param("day", currentDay)
    param("coins", coinsAwarded)
    param("vip_multiplier", multiplier)
}

// Gift sent
Firebase.analytics.logEvent("gift_send") {
    param("gift_id", giftId)
    param("quantity", quantity)
    param("total_coins", totalCoins)
    param("room_id", roomId)
}

// CP formation
Firebase.analytics.logEvent("cp_form") {
    param("partner_id", partnerId)
    param("payment_model", paymentModel)
}
```

### User Properties

```kotlin
Firebase.analytics.setUserProperty("vip_tier", vipTier)
Firebase.analytics.setUserProperty("user_level", level.toString())
Firebase.analytics.setUserProperty("account_age_days", accountAgeDays.toString())
```

### Conversion Events

Mark key events as conversions in Firebase Console:
- `purchase`
- `cp_form`
- `vip_upgrade`

---

## Remote Config

### Setup

```groovy
// build.gradle (app)
implementation 'com.google.firebase:firebase-config-ktx:21.6.0'
```

### Feature Flags

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enable_gift_animations` | Boolean | true | Gift animation toggle |
| `enable_video_mode` | Boolean | true | YouTube mode toggle |
| `enable_super_mic` | Boolean | true | Super Mic feature |
| `enable_lucky_bag` | Boolean | true | Lucky Bag event |
| `enable_cash_payouts` | Boolean | true | Cash withdrawal |
| `enable_16_seats` | Boolean | true | 16-seat rooms |
| `maintenance_mode` | Boolean | false | Maintenance toggle |

### Configuration Parameters

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `large_gift_threshold` | Number | 1000000 | Warning threshold |
| `animation_concurrency_cap` | Number | 10 | Max animations |
| `gift_cooldown_seconds` | Number | 3 | Send cooldown |
| `minimum_withdrawal_coins` | Number | 100 | Min withdrawal |

### Fetching Config

```kotlin
val remoteConfig = Firebase.remoteConfig
val configSettings = remoteConfigSettings {
    minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
}
remoteConfig.setConfigSettingsAsync(configSettings)
remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val enableGiftAnimations = remoteConfig.getBoolean("enable_gift_animations")
        // Apply configuration
    }
}
```

---

## Database Decision

### Option 1: Firestore (Recommended)

**Pros:**
- Better querying capabilities
- Offline persistence
- Real-time listeners
- Scalable structure

**Cons:**
- Higher cost for high-frequency updates
- More complex pricing model

**Use For:**
- User profiles
- Gifts catalog
- Family data
- Room metadata

### Option 2: Realtime Database

**Pros:**
- Lower latency for frequent updates
- Simpler pricing
- Better for presence systems

**Cons:**
- Limited querying
- Hierarchical structure only

**Use For:**
- Real-time room presence
- Chat messages
- Live activity feeds
- Typing indicators

### Hybrid Approach (Recommended)

```
Firestore: User profiles, gifts, families, rankings
Realtime DB: Room presence, live chat, activity feeds
```

---

## Firestore Setup

### Collections Structure

```
/users/{userId}
  - profile: { name, avatar, level, vipTier, ... }
  - wallet: { coins, diamonds }
  - settings: { privacy, notifications, ... }
  
/rooms/{roomId}
  - metadata: { name, owner, type, capacity }
  - settings: { visibility, permissions }
  
/gifts/{giftId}
  - catalog entry
  
/families/{familyId}
  - metadata, members subcollection
  
/cp/{cpId}
  - partner1, partner2, level, exp
```

### Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // User profiles
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
      
      // Wallet can only be modified by server
      match /wallet {
        allow read: if request.auth.uid == userId;
        allow write: if false; // Server only via Admin SDK
      }
    }
    
    // Rooms
    match /rooms/{roomId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null 
        && request.auth.uid == request.resource.data.ownerId;
      allow update: if request.auth.uid == resource.data.ownerId;
      allow delete: if request.auth.uid == resource.data.ownerId;
    }
    
    // Gifts catalog (read-only for clients)
    match /gifts/{giftId} {
      allow read: if true;
      allow write: if false; // Admin only
    }
    
    // Families
    match /families/{familyId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if isFamilyAdmin(familyId);
      allow delete: if isFamilyOwner(familyId);
      
      function isFamilyAdmin(familyId) {
        return get(/databases/$(database)/documents/families/$(familyId)/members/$(request.auth.uid)).data.role in ['owner', 'admin'];
      }
      
      function isFamilyOwner(familyId) {
        return get(/databases/$(database)/documents/families/$(familyId)/members/$(request.auth.uid)).data.role == 'owner';
      }
    }
    
    // CP partnerships
    match /cp/{cpId} {
      allow read: if request.auth.uid in [resource.data.partner1, resource.data.partner2];
      allow write: if false; // Server only
    }
  }
}
```

---

## Realtime Database Setup

### Data Structure

```json
{
  "presence": {
    "rooms": {
      "{roomId}": {
        "users": {
          "{userId}": {
            "status": "online",
            "joinedAt": 1701234567890,
            "seat": 3
          }
        }
      }
    }
  },
  "chat": {
    "{roomId}": {
      "{messageId}": {
        "senderId": "user_123",
        "text": "Hello!",
        "timestamp": 1701234567890
      }
    }
  },
  "typing": {
    "{roomId}": {
      "{userId}": true
    }
  }
}
```

### Security Rules

```json
{
  "rules": {
    "presence": {
      "rooms": {
        "$roomId": {
          "users": {
            "$userId": {
              ".read": "auth != null",
              ".write": "auth.uid == $userId"
            }
          }
        }
      }
    },
    "chat": {
      "$roomId": {
        ".read": "auth != null",
        "$messageId": {
          ".write": "auth != null && newData.child('senderId').val() == auth.uid",
          ".validate": "newData.hasChildren(['senderId', 'text', 'timestamp'])"
        }
      }
    },
    "typing": {
      "$roomId": {
        "$userId": {
          ".read": "auth != null",
          ".write": "auth.uid == $userId"
        }
      }
    }
  }
}
```

---

## Cloud Functions

### Setup

```bash
npm install -g firebase-tools
firebase login
firebase init functions
```

### Key Functions

```typescript
// functions/src/index.ts

import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

// Gift transaction processing
export const processGiftTransaction = functions.firestore
  .document('transactions/{transactionId}')
  .onCreate(async (snap, context) => {
    const transaction = snap.data();
    // Deduct coins from sender
    // Add diamonds to recipient
    // Record in history
  });

// Daily reward reset
export const resetDailyRewards = functions.pubsub
  .schedule('0 0 * * *')
  .timeZone('UTC')
  .onRun(async (context) => {
    // Reset daily counters
  });

// Ranking calculation
export const calculateRankings = functions.pubsub
  .schedule('0 * * * *')
  .onRun(async (context) => {
    // Calculate and update rankings
  });
```

---

## Environment Configuration

### Firebase Config Files

| Environment | File | Location |
|-------------|------|----------|
| Development | `google-services.json` | `app/src/dev/` |
| Staging | `google-services.json` | `app/src/staging/` |
| Production | `google-services.json` | `app/src/prod/` |

### Build Variants

```groovy
// build.gradle (app)
android {
    flavorDimensions "environment"
    productFlavors {
        dev {
            dimension "environment"
            applicationIdSuffix ".dev"
        }
        staging {
            dimension "environment"
            applicationIdSuffix ".staging"
        }
        prod {
            dimension "environment"
        }
    }
}
```

### CI/CD Configuration

**GitHub Secrets:**
```
FIREBASE_TOKEN
GOOGLE_SERVICES_JSON_DEV
GOOGLE_SERVICES_JSON_STAGING
GOOGLE_SERVICES_JSON_PROD
```

**Deployment:**
```yaml
# .github/workflows/deploy.yml
- name: Decode google-services.json
  run: echo ${{ secrets.GOOGLE_SERVICES_JSON_PROD }} | base64 -d > app/google-services.json
```

---

## Monitoring & Alerts

### Firebase Console Alerts

| Alert | Threshold | Channel |
|-------|-----------|---------|
| Crash spike | 1% increase | Email, Slack |
| Error rate | > 5% | PagerDuty |
| Performance | p95 > 3s | Email |

### Custom Metrics

```kotlin
// Performance monitoring
val trace = Firebase.performance.newTrace("gift_send")
trace.start()
// ... operation
trace.stop()

// Add custom metrics
trace.putMetric("gift_value", giftValue.toLong())
trace.putAttribute("gift_category", category)
```

---

## Related Documentation

- [Build & Gradle](./build-and-gradle.md)
- [Configuration](../configuration.md)
- [Architecture](../architecture.md)
- [Security](../security.md)

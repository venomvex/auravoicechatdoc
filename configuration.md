# Configuration

## Environment Variables

| Name                | Default    | Required | Description                    |
|---------------------|------------|----------|--------------------------------|
| API_BASE_URL        | —          | Yes      | Backend API endpoint           |
| FIREBASE_PROJECT_ID | —          | Yes      | Firebase project identifier    |
| FIREBASE_API_KEY    | —          | Yes      | Firebase API key               |
| ANALYTICS_ENABLED   | true       | No       | Enable analytics collection    |
| DEBUG_MODE          | false      | No       | Enable debug logging           |
| LOG_LEVEL           | INFO       | No       | Logging level (DEBUG/INFO/WARN/ERROR) |
| CACHE_TTL_SECONDS   | 300        | No       | Default cache time-to-live     |
| MAX_RETRY_ATTEMPTS  | 3          | No       | API retry attempts             |
| TIMEOUT_MS          | 30000      | No       | Request timeout in milliseconds|

---

## Configuration Files

### config.yaml Schema
```yaml
api:
  baseUrl: https://api.auravoice.chat
  timeout: 30000
  retries: 3

firebase:
  projectId: aura-voice-chat
  # API key injected at build time

analytics:
  enabled: true
  sampleRate: 1.0

cache:
  defaultTtl: 300
  walletTtl: 60
  catalogTtl: 3600

logging:
  level: INFO
  enableCrashReporting: true
```

### Build Variants
- **debug:** Full logging, mock services available
- **release:** Optimized, crash reporting, no debug logs

---

## Feature Flags

| Flag                    | Default | Safety Notes                        |
|-------------------------|---------|-------------------------------------|
| ENABLE_GIFT_ANIMATIONS  | true    | Disable if performance issues       |
| ENABLE_VIDEO_MODE       | true    | Disable if YouTube issues           |
| ENABLE_SUPER_MIC        | true    | Level/VIP gated                     |
| ENABLE_LUCKY_BAG        | true    | Event-based                         |
| ENABLE_CASH_PAYOUTS     | true    | Disable for compliance issues       |
| ENABLE_16_SEATS         | true    | Level gated (Level ≥20)            |
| MAINTENANCE_MODE        | false   | Blocks all features except notice   |

### Flag Management
- Controlled via Firebase Remote Config
- Changes take effect on next app launch
- Critical flags require forced refresh

---

## Platform-Specific Configuration

### Android (build.gradle)
```groovy
android {
    defaultConfig {
        minSdkVersion 28        // Android 9
        targetSdkVersion 34     // Android 14
    }
    
    buildTypes {
        debug {
            buildConfigField "boolean", "DEBUG_MODE", "true"
        }
        release {
            buildConfigField "boolean", "DEBUG_MODE", "false"
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt')
        }
    }
}
```

### ProGuard Rules
```proguard
# Keep models for JSON parsing
-keep class com.aura.voicechat.models.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
```

---

## Secrets Configuration

### Local Development (.env)
```
FIREBASE_API_KEY=your_key_here
GOOGLE_OAUTH_CLIENT_ID=your_client_id
FACEBOOK_APP_ID=your_app_id
```

### CI/CD (GitHub Secrets)
```
FIREBASE_API_KEY_RELEASE
GOOGLE_OAUTH_CLIENT_ID_RELEASE
KEYSTORE_PASSWORD
KEY_ALIAS
KEY_PASSWORD
```

---

## Related Documentation

- [Deployment](deployment.md)
- [Architecture](architecture.md)
- [Security](security.md)
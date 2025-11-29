# APK Build Guide

## Aura Voice Chat - Android Build Instructions

**Developer: Hawkaye Visions LTD — Pakistan**

This guide covers building signed APK and AAB files for the Aura Voice Chat Android app.

**Note: This app is 100% AWS-based. No Firebase is used.**

---

## Prerequisites

### Development Environment

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or later
- **Gradle**: 8.4+
- **Kotlin**: 1.9.21+
- **Android SDK**: API 34 (Android 14)

### Required Files

1. `awsconfiguration.json` - AWS configuration
2. `keystore.jks` or `keystore.properties` - Release signing
3. Environment variables or `.env` file for build config

---

## Step 1: Project Setup

### 1.1 Clone Repository

```bash
git clone https://github.com/your-repo/aura-voice-chat.git
cd aura-voice-chat/android
```

### 1.2 Add AWS Configuration

Create `awsconfiguration.json` in `android/app/src/main/res/raw/`:

```
android/
├── app/
│   ├── src/
│   │   └── main/
│   │       └── res/
│   │           └── raw/
│   │               └── awsconfiguration.json  ← Place here
│   └── build.gradle
└── build.gradle
```

**awsconfiguration.json content:**
```json
{
    "Version": "1.0",
    "CredentialsProvider": {
        "CognitoIdentity": {
            "Default": {
                "PoolId": "YOUR_IDENTITY_POOL_ID",
                "Region": "us-east-1"
            }
        }
    },
    "CognitoUserPool": {
        "Default": {
            "PoolId": "YOUR_USER_POOL_ID",
            "AppClientId": "YOUR_APP_CLIENT_ID",
            "Region": "us-east-1"
        }
    },
    "S3TransferUtility": {
        "Default": {
            "Bucket": "YOUR_S3_BUCKET",
            "Region": "us-east-1"
        }
    },
    "PinpointAnalytics": {
        "Default": {
            "AppId": "YOUR_PINPOINT_APP_ID",
            "Region": "us-east-1"
        }
    }
}
```

### 1.3 Configure Signing

Create `keystore.properties` in project root:

```properties
storeFile=../release.keystore
storePassword=your-store-password
keyAlias=aura-release-key
keyPassword=your-key-password
```

Or use environment variables:
```bash
export KEYSTORE_PATH=/path/to/release.keystore
export KEYSTORE_PASSWORD=your-store-password
export KEY_ALIAS=aura-release-key
export KEY_PASSWORD=your-key-password
```

---

## Step 2: Generate Release Keystore

### 2.1 Create New Keystore

```bash
keytool -genkey -v -keystore release.keystore \
  -alias aura-release-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass your-store-password \
  -keypass your-key-password \
  -dname "CN=Aura Voice Chat, OU=Mobile, O=Hawkaye Visions LTD, L=Lahore, ST=Punjab, C=PK"
```

### 2.2 Backup Keystore

⚠️ **CRITICAL**: Keep your keystore safe!

- Store in secure location
- Back up to multiple locations
- Never commit to version control
- You CANNOT update app without this keystore

---

## Step 3: Build Configuration

### 3.1 Build Types

The app supports these build types:

| Type | Description | Signing | Minification |
|------|-------------|---------|--------------|
| debug | Development | Debug key | No |
| release | Production | Release key | Yes |

### 3.2 Product Flavors

| Flavor | API URL | Use Case |
|--------|---------|----------|
| dev | api-dev.auravoice.chat | Development |
| staging | api-staging.auravoice.chat | Testing |
| prod | api.auravoice.chat | Production |

### 3.3 Build Variants

Combine build type + flavor:
- `devDebug`
- `devRelease`
- `stagingDebug`
- `stagingRelease`
- `prodDebug`
- `prodRelease` ← For Play Store

---

## Step 4: Build Commands

### 4.1 Clean Build

```bash
# Clean previous builds
./gradlew clean
```

### 4.2 Build Debug APK

```bash
# Build debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/dev/debug/app-dev-debug.apk
```

### 4.3 Build Release APK

```bash
# Build release APK
./gradlew assembleProdRelease

# Output: app/build/outputs/apk/prod/release/app-prod-release.apk
```

### 4.4 Build AAB (Android App Bundle)

Recommended for Play Store:

```bash
# Build release AAB
./gradlew bundleProdRelease

# Output: app/build/outputs/bundle/prodRelease/app-prod-release.aab
```

### 4.5 Build All Variants

```bash
# Build all APKs
./gradlew assemble

# Build all AABs
./gradlew bundle
```

---

## Step 5: Signing Verification

### 5.1 Verify APK Signature

```bash
# Check APK signature
apksigner verify --verbose app-prod-release.apk

# Check signature details
keytool -printcert -jarfile app-prod-release.apk
```

### 5.2 Verify AAB

```bash
# Use bundletool to check
bundletool validate --bundle=app-prod-release.aab
```

---

## Step 6: ProGuard/R8 Configuration

The `proguard-rules.pro` file includes rules for:

- AWS SDK
- Retrofit/OkHttp
- Gson serialization
- Hilt/Dagger
- WebRTC
- Compose
- Coil
- ML Kit

### 6.1 Test ProGuard

```bash
# Build release and check for issues
./gradlew assembleProdRelease

# Check mapping file
cat app/build/outputs/mapping/prodRelease/mapping.txt
```

### 6.2 Keep Mapping File

Save `mapping.txt` for crash report deobfuscation:

```bash
cp app/build/outputs/mapping/prodRelease/mapping.txt \
   mappings/mapping-v1.0.0.txt
```

---

## Step 7: Version Management

### 7.1 Update Version

Edit `app/build.gradle`:

```groovy
android {
    defaultConfig {
        versionCode 1        // Increment for each release
        versionName "1.0.0"  // Semantic versioning
    }
}
```

### 7.2 Version Code Strategy

```
Version Code = Major * 10000 + Minor * 100 + Patch

Example:
v1.0.0 → 10000
v1.0.1 → 10001
v1.1.0 → 10100
v2.0.0 → 20000
```

---

## Build Script

### scripts/build-apk.sh

```bash
#!/bin/bash

# Aura Voice Chat - Build Script
# Developer: Hawkaye Visions LTD — Pakistan

set -e

echo "======================================"
echo "Aura Voice Chat - Build Script"
echo "======================================"

# Configuration
BUILD_TYPE=${1:-release}
FLAVOR=${2:-prod}
OUTPUT_DIR="./build-output"

# Clean
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build
echo "🔨 Building $FLAVOR$BUILD_TYPE..."

if [ "$BUILD_TYPE" == "release" ]; then
    ./gradlew assemble${FLAVOR^}Release bundle${FLAVOR^}Release
else
    ./gradlew assemble${FLAVOR^}Debug
fi

# Copy outputs
echo "📦 Copying build outputs..."
mkdir -p $OUTPUT_DIR

if [ "$BUILD_TYPE" == "release" ]; then
    cp app/build/outputs/apk/$FLAVOR/release/*.apk $OUTPUT_DIR/
    cp app/build/outputs/bundle/${FLAVOR}Release/*.aab $OUTPUT_DIR/
    cp app/build/outputs/mapping/${FLAVOR}Release/mapping.txt $OUTPUT_DIR/
fi

if [ "$BUILD_TYPE" == "debug" ]; then
    cp app/build/outputs/apk/$FLAVOR/debug/*.apk $OUTPUT_DIR/
fi

# Print results
echo ""
echo "✅ Build complete!"
echo "📁 Output directory: $OUTPUT_DIR"
ls -la $OUTPUT_DIR

# Calculate APK size
APK_SIZE=$(ls -lh $OUTPUT_DIR/*.apk 2>/dev/null | awk '{print $5}')
echo ""
echo "📊 APK Size: $APK_SIZE"
```

Make executable:
```bash
chmod +x scripts/build-apk.sh
```

Usage:
```bash
# Release build for production
./scripts/build-apk.sh release prod

# Debug build for development
./scripts/build-apk.sh debug dev
```

---

## Optimization

### 8.1 APK Size Optimization

```groovy
android {
    buildTypes {
        release {
            shrinkResources true
            minifyEnabled true
        }
    }
    
    // Split by ABI
    splits {
        abi {
            enable true
            reset()
            include 'arm64-v8a', 'armeabi-v7a', 'x86', 'x86_64'
            universalApk true
        }
    }
}
```

### 8.2 Bundle Size Analysis

```bash
# Analyze APK contents
apkanalyzer apk summary app-prod-release.apk

# Detailed size breakdown
apkanalyzer dex references app-prod-release.apk
```

### 8.3 Lint Checks

```bash
# Run lint
./gradlew lint

# View report
open app/build/reports/lint-results.html
```

---

## Troubleshooting

### Common Issues

**1. Keystore not found**
```
Error: Keystore file not found
Solution: Check path in keystore.properties or environment variables
```

**2. Version code already used**
```
Error: Version code 10000 has already been used
Solution: Increment versionCode in build.gradle
```

**3. 64-bit requirement**
```
Error: App doesn't support 64-bit
Solution: Ensure ndk.abiFilters includes 'arm64-v8a'
```

**4. Firebase configuration missing**
```
Error: awsconfiguration.json not found
Solution: Download from AWS Console and place in app/
```

**5. Memory issues during build**
```
Error: Java heap space
Solution: Add to gradle.properties:
org.gradle.jvmargs=-Xmx4g
```

---

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build APK

on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Setup Android SDK
      uses: android-actions/setup-android@v2
    
    - name: Decode Keystore
      run: |
        echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > release.keystore
    
    - name: Build Release
      env:
        KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
        KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
        KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      run: |
        cd android
        ./gradlew bundleProdRelease
    
    - name: Upload AAB
      uses: actions/upload-artifact@v3
      with:
        name: app-bundle
        path: android/app/build/outputs/bundle/prodRelease/*.aab
```

---

## Output Files Summary

After a successful release build:

```
app/build/outputs/
├── apk/
│   └── prod/
│       └── release/
│           ├── app-prod-release.apk
│           └── output-metadata.json
├── bundle/
│   └── prodRelease/
│       └── app-prod-release.aab
└── mapping/
    └── prodRelease/
        └── mapping.txt
```

---

*Always test the signed APK on real devices before submission to Play Store.*

# Build & Gradle Configuration

Comprehensive guide for Android build configuration, Gradle settings, SDK targets, dependencies, and CI/CD setup for Aura Voice Chat.

**Developer:** Hawkaye Visions LTD — Pakistan

## Overview

Aura Voice Chat is built for Android using Kotlin with Gradle as the build system. This document covers all build-related configuration.

---

## Gradle Versions

### Recommended Versions

| Component | Version | Notes |
|-----------|---------|-------|
| Gradle | 8.4 | Wrapper version |
| Android Gradle Plugin | 8.2.0 | Latest stable |
| Kotlin | 1.9.21 | Language version |
| JDK | 17 | Compile/runtime |

### gradle-wrapper.properties

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
networkTimeout=10000
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

---

## Android SDK Targets

### Version Requirements

| Setting | Value | Android Version |
|---------|-------|-----------------|
| minSdkVersion | 28 | Android 9 (Pie) |
| targetSdkVersion | 34 | Android 14 |
| compileSdkVersion | 34 | Android 14 |

### Rationale

- **Min SDK 28:** Covers 95%+ of active devices, ensures modern API availability
- **Target SDK 34:** Required for Play Store compliance, enables latest features
- **Compile SDK 34:** Access to latest Android APIs

---

## Project-Level build.gradle

```groovy
// build.gradle (Project: aura-voice-chat)
buildscript {
    ext {
        kotlin_version = '1.9.21'
        compose_version = '1.5.6'
        hilt_version = '2.48.1'
    }
    
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    dependencies {
        classpath 'com.android.tools.build:gradle:8.2.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath 'com.google.gms:google-services:4.4.0'
        classpath 'com.google.firebase:firebase-crashlytics-gradle:2.9.9'
        classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"
    }
}

plugins {
    id 'com.android.application' version '8.2.0' apply false
    id 'com.android.library' version '8.2.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.21' apply false
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

---

## App-Level build.gradle

```groovy
// build.gradle (Module: app)
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
    id 'com.google.gms.google-services'
    id 'com.google.firebase.crashlytics'
    id 'dagger.hilt.android.plugin'
}

android {
    namespace 'com.aura.voicechat'
    compileSdk 34

    defaultConfig {
        applicationId "com.aura.voicechat"
        minSdk 28
        targetSdk 34
        versionCode 1
        versionName "1.0.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        
        vectorDrawables {
            useSupportLibrary true
        }
    }

    signingConfigs {
        debug {
            storeFile file('debug.keystore')
        }
        release {
            storeFile file(System.getenv("KEYSTORE_PATH") ?: "release.keystore")
            storePassword System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias System.getenv("KEY_ALIAS") ?: ""
            keyPassword System.getenv("KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix ".debug"
            versionNameSuffix "-debug"
            debuggable true
            minifyEnabled false
            buildConfigField "boolean", "DEBUG_MODE", "true"
            buildConfigField "String", "API_BASE_URL", '"https://api-dev.auravoice.chat"'
        }
        release {
            debuggable false
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
            buildConfigField "boolean", "DEBUG_MODE", "false"
            buildConfigField "String", "API_BASE_URL", '"https://api.auravoice.chat"'
        }
    }

    flavorDimensions "environment"
    productFlavors {
        dev {
            dimension "environment"
            applicationIdSuffix ".dev"
            versionNameSuffix "-dev"
            buildConfigField "String", "ENVIRONMENT", '"development"'
        }
        staging {
            dimension "environment"
            applicationIdSuffix ".staging"
            versionNameSuffix "-staging"
            buildConfigField "String", "ENVIRONMENT", '"staging"'
        }
        prod {
            dimension "environment"
            buildConfigField "String", "ENVIRONMENT", '"production"'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = '17'
        freeCompilerArgs += [
            '-opt-in=kotlin.RequiresOptIn',
            '-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi'
        ]
    }

    buildFeatures {
        compose true
        buildConfig true
        viewBinding true
    }

    composeOptions {
        kotlinCompilerExtensionVersion compose_version
    }

    packaging {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
        }
    }
}

dependencies {
    // See Dependencies section below
}
```

---

## Dependencies

### Core Android

```groovy
dependencies {
    // AndroidX Core
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.activity:activity-ktx:1.8.2'
    implementation 'androidx.fragment:fragment-ktx:1.6.2'
    
    // Lifecycle
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.2'
    
    // UI
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
}
```

### Jetpack Compose

```groovy
dependencies {
    // Compose BOM
    implementation platform('androidx.compose:compose-bom:2023.10.01')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.compose.material:material-icons-extended'
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation 'androidx.navigation:navigation-compose:2.7.6'
    
    debugImplementation 'androidx.compose.ui:ui-tooling'
    debugImplementation 'androidx.compose.ui:ui-test-manifest'
}
```

### Firebase

```groovy
dependencies {
    // Firebase BOM
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-database-ktx'
    implementation 'com.google.firebase:firebase-analytics-ktx'
    implementation 'com.google.firebase:firebase-crashlytics-ktx'
    implementation 'com.google.firebase:firebase-config-ktx'
    implementation 'com.google.firebase:firebase-messaging-ktx'
    implementation 'com.google.firebase:firebase-perf-ktx'
}
```

### Networking

```groovy
dependencies {
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
    
    // Serialization
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

### Dependency Injection

```groovy
dependencies {
    // Hilt
    implementation "com.google.dagger:hilt-android:$hilt_version"
    kapt "com.google.dagger:hilt-compiler:$hilt_version"
    implementation 'androidx.hilt:hilt-navigation-compose:1.1.0'
}
```

### Image Loading

```groovy
dependencies {
    // Coil
    implementation 'io.coil-kt:coil-compose:2.5.0'
    implementation 'io.coil-kt:coil-gif:2.5.0'
}
```

### Media & Real-time

```groovy
dependencies {
    // WebRTC (for voice/video)
    implementation 'org.webrtc:google-webrtc:1.0.32006'
    
    // ExoPlayer (for video playback)
    implementation 'androidx.media3:media3-exoplayer:1.2.0'
    implementation 'androidx.media3:media3-ui:1.2.0'
    
    // Lottie (for animations)
    implementation 'com.airbnb.android:lottie-compose:6.2.0'
}
```

### Testing

```groovy
dependencies {
    // Unit Testing
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:5.7.0'
    testImplementation 'org.mockito.kotlin:mockito-kotlin:5.2.1'
    testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3'
    
    // Android Testing
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation platform('androidx.compose:compose-bom:2023.10.01')
    androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
}
```

---

## ProGuard Rules

```proguard
# proguard-rules.pro

# Keep model classes for JSON parsing
-keep class com.aura.voicechat.data.model.** { *; }
-keep class com.aura.voicechat.domain.model.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# WebRTC
-keep class org.webrtc.** { *; }

# Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
```

---

## CI/CD Configuration

### GitHub Actions Workflow

```yaml
# .github/workflows/android.yml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Decode google-services.json
      env:
        GOOGLE_SERVICES_JSON: ${{ secrets.GOOGLE_SERVICES_JSON }}
      run: echo $GOOGLE_SERVICES_JSON | base64 -d > app/google-services.json
    
    - name: Build Debug APK
      run: ./gradlew assembleDevDebug
    
    - name: Run Unit Tests
      run: ./gradlew testDevDebugUnitTest
    
    - name: Build Release APK
      if: github.ref == 'refs/heads/main'
      env:
        KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
        KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
        KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      run: ./gradlew assembleProdRelease
    
    - name: Upload APK
      uses: actions/upload-artifact@v4
      with:
        name: app-release
        path: app/build/outputs/apk/prod/release/*.apk

  test:
    runs-on: ubuntu-latest
    needs: build
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle
    
    - name: Run All Tests
      run: ./gradlew test
    
    - name: Generate Coverage Report
      run: ./gradlew jacocoTestReport
    
    - name: Upload Coverage
      uses: codecov/codecov-action@v3
```

### Fastlane Configuration

```ruby
# Fastfile
default_platform(:android)

platform :android do
  desc "Build debug APK"
  lane :build_debug do
    gradle(task: "assembleDevDebug")
  end

  desc "Build release APK"
  lane :build_release do
    gradle(
      task: "assembleProdRelease",
      properties: {
        "android.injected.signing.store.file" => ENV["KEYSTORE_PATH"],
        "android.injected.signing.store.password" => ENV["KEYSTORE_PASSWORD"],
        "android.injected.signing.key.alias" => ENV["KEY_ALIAS"],
        "android.injected.signing.key.password" => ENV["KEY_PASSWORD"]
      }
    )
  end

  desc "Deploy to Play Store internal track"
  lane :deploy_internal do
    build_release
    upload_to_play_store(track: 'internal')
  end

  desc "Promote to production"
  lane :promote_production do
    upload_to_play_store(
      track: 'internal',
      track_promote_to: 'production',
      skip_upload_apk: true
    )
  end
end
```

---

## Common Issues & Solutions

### Issue: Gradle Sync Failed

**Symptoms:** Android Studio shows sync errors

**Solutions:**
1. File → Invalidate Caches / Restart
2. Delete `.gradle` and `.idea` folders
3. Check `gradle-wrapper.properties` version
4. Verify JDK 17 is configured

### Issue: Firebase Configuration Error

**Symptoms:** "Default FirebaseApp is not initialized"

**Solutions:**
1. Verify `google-services.json` in correct location
2. Check `google-services` plugin applied
3. Ensure package name matches Firebase config

### Issue: Build Variant Not Found

**Symptoms:** Cannot find build variant

**Solutions:**
1. Sync project with Gradle files
2. Check `productFlavors` configuration
3. Verify `flavorDimensions` set correctly

### Issue: ProGuard Errors in Release

**Symptoms:** Release build crashes, debug works

**Solutions:**
1. Check ProGuard rules for missing keeps
2. Add `-keep` rules for reflection-accessed classes
3. Test with `minifyEnabled false` to isolate

### Issue: Dependency Conflicts

**Symptoms:** Duplicate class errors

**Solutions:**
1. Use BOM for Firebase/Compose
2. Check for transitive dependency conflicts
3. Use `./gradlew dependencies` to analyze

---

## Build Performance Tips

### Gradle Configuration

```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4g -XX:+UseParallelGC
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
kotlin.incremental=true
android.useAndroidX=true
android.enableJetifier=false
```

### Build Cache

```groovy
// settings.gradle
buildCache {
    local {
        enabled = true
        directory = new File(rootDir, 'build-cache')
    }
}
```

---

## Related Documentation

- [Firebase Setup](./firebase-setup.md)
- [Configuration](../configuration.md)
- [Deployment](../deployment.md)
- [Troubleshooting](../troubleshooting.md)

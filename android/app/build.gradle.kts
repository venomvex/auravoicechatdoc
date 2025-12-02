// Module: app - Aura Voice Chat Android Application
// Developer: Hawkaye Visions LTD — Pakistan
//
// Updated: November 2025 - Uses version catalog (gradle/libs.versions.toml)
// Now uses Kotlin 2.0+, KSP instead of kapt, latest Compose compiler
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.aura.voicechat"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.aura.voicechat"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        // Multi-dex for large app
        multiDexEnabled = true

        // Build config fields
        // TODO: For production, update to domain with HTTPS (e.g., https://api.auravoice.chat)
        buildConfigField("String", "API_BASE_URL", "\"http://43.204.130.237\"")
        buildConfigField("String", "AGORA_APP_ID", "\"YOUR_AGORA_APP_ID\"")
        buildConfigField("String", "AWS_REGION", "\"ap-south-1\"")
    }

    signingConfigs {
        // The 'debug' signingConfig is created automatically by AGP.
        // Explicitly defining it causes a conflict, so it has been removed.
        create("release") {
            // Release signing - use environment variables or keystore.properties
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                // Use the classes directly since they are already imported
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            } else {
                // Fallback to environment variables for CI/CD
                storeFile = file(System.getenv("KEYSTORE_PATH") ?: "release.keystore")
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("KEY_ALIAS") ?: ""
                keyPassword = System.getenv("KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            isMinifyEnabled = false
            buildConfigField("boolean", "DEBUG_MODE", "true")
            // TODO: For production, update to domain with HTTPS (e.g., https://api-dev.auravoice.chat)
            buildConfigField("String", "API_BASE_URL", "\"http://43.204.130.237\"")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "DEBUG_MODE", "false")
            // TODO: For production, update to domain with HTTPS (e.g., https://api.auravoice.chat)
            buildConfigField("String", "API_BASE_URL", "\"http://43.204.130.237\"")
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "ENVIRONMENT", "\"development\"")
            // TODO: For production, update to domain with HTTPS (e.g., https://api-dev.auravoice.chat)
            buildConfigField("String", "API_BASE_URL", "\"http://43.204.130.237\"")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "ENVIRONMENT", "\"staging\"")
            // TODO: For production, update to domain with HTTPS (e.g., https://api-staging.auravoice.chat)
            buildConfigField("String", "API_BASE_URL", "\"http://43.204.130.237\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT", "\"production\"")
            // TODO: For production, update to domain with HTTPS (e.g., https://api.auravoice.chat)
            buildConfigField("String", "API_BASE_URL", "\"http://43.204.130.237\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
        }
    }

    // Lint options
    lint {
        abortOnError = false
        checkReleaseBuilds = true
        disable += listOf("MissingTranslation", "ExtraTranslation")


    }

    // Test options
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    // Enable 64-bit for Play Store compliance
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
}

configurations.all {
    resolutionStrategy {
        force("androidx.test.ext:junit:1.2.1")
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.multidex)

    // Updates
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // UI - Material Design
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.viewpager2)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.constraintlayout.compose)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // AWS Amplify
    implementation(libs.amplify.core)
    implementation(libs.amplify.auth.cognito)
    implementation(libs.amplify.storage.s3)
    implementation(libs.amplify.api)
    implementation(libs.amplify.push.notifications.pinpoint)

    // AWS SDK (for direct S3 operations if needed)
    implementation(libs.aws.sdk.kotlin.s3)

    // AWS SDK v2 (for Cognito, S3, SNS operations)
    implementation("software.amazon.awssdk:cognitoidentityprovider:2.25.0")
    implementation("software.amazon.awssdk:s3:2.25.0")
    implementation("software.amazon.awssdk:sns:2.25.0")

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Facebook Login
    implementation(libs.facebook.login)

    // Networking - Retrofit
    implementation(libs.bundles.networking)

    // Socket.io for real-time
    implementation(libs.socket.io.client)

    // Serialization
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit.converter.moshi)

    // Dependency Injection - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Image Loading - Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.network.okhttp)

    // WebRTC for voice/video
    implementation(libs.webrtc.dafruits)

    // ExoPlayer for video playback
    implementation(libs.bundles.media3)

    // Lottie animations
    implementation(libs.lottie.compose)

    // CameraX for KYC selfie
    implementation(libs.bundles.camerax)

    // ML Kit for face detection (liveness)
    implementation(libs.mlkit.face.detection)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore.preferences)

    // Work Manager for background tasks
    implementation(libs.work.runtime.ktx)

    // Paging
    implementation(libs.paging.runtime.ktx)
    implementation(libs.paging.compose)

    // Security
    implementation(libs.security.crypto)

    // Biometric
    implementation(libs.biometric)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)

    // Android Testing
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}


package com.aura.voicechat.di

import android.content.Context
import com.aura.voicechat.BuildConfig
import com.aura.voicechat.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.sns.SnsClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Network Module for Hilt DI
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * AWS-based implementation (No Firebase)
 * - Authentication: AWS Cognito
 * - Storage: AWS S3
 * - Push Notifications: AWS SNS
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG_MODE) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCognitoClient(@ApplicationContext context: Context): CognitoIdentityProviderClient {
        return CognitoIdentityProviderClient.builder()
            .region(Region.of(BuildConfig.AWS_REGION))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideS3Client(@ApplicationContext context: Context): S3Client {
        return S3Client.builder()
            .region(Region.of(BuildConfig.AWS_REGION))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideSnsClient(@ApplicationContext context: Context): SnsClient {
        return SnsClient.builder()
            .region(Region.of(BuildConfig.AWS_REGION))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }
}

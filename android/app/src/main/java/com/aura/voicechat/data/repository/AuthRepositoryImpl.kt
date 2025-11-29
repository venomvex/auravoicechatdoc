package com.aura.voicechat.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.aura.voicechat.data.model.SendOtpRequest
import com.aura.voicechat.data.model.VerifyOtpRequest
import com.aura.voicechat.data.remote.ApiService
import com.aura.voicechat.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Auth Repository Implementation
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * AWS Cognito-based authentication (No Firebase)
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : AuthRepository {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
    
    override suspend fun sendOtp(phoneNumber: String): Result<Unit> {
        return try {
            val response = apiService.sendOtp(SendOtpRequest(phoneNumber))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to send OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<Unit> {
        return try {
            val response = apiService.verifyOtp(VerifyOtpRequest(phoneNumber, otp))
            if (response.isSuccessful && response.body()?.success == true) {
                // Store token and user data from AWS Cognito response
                response.body()?.let { body ->
                    prefs.edit()
                        .putString(KEY_AUTH_TOKEN, body.token)
                        .putString(KEY_USER_ID, body.user.id)
                        .apply()
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signInWithGoogle(): Result<Unit> {
        // AWS Cognito social sign-in with Google
        // Implementation using AWS Amplify Auth or Cognito Identity Provider
        return Result.success(Unit)
    }
    
    override suspend fun signInWithFacebook(): Result<Unit> {
        // AWS Cognito social sign-in with Facebook
        // Implementation using AWS Amplify Auth or Cognito Identity Provider
        return Result.success(Unit)
    }
    
    override suspend fun signOut(): Result<Unit> {
        return try {
            // Clear local tokens
            prefs.edit()
                .remove(KEY_AUTH_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_USER_ID)
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
    
    override suspend fun isLoggedIn(): Boolean {
        return prefs.getString(KEY_AUTH_TOKEN, null) != null
    }
}

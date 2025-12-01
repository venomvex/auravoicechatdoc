package com.aura.voicechat.data.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.core.Amplify
import com.aura.voicechat.data.model.FacebookSignInRequest
import com.aura.voicechat.data.model.GoogleSignInRequest
import com.aura.voicechat.data.model.RefreshTokenRequest
import com.aura.voicechat.data.model.SendOtpRequest
import com.aura.voicechat.data.model.VerifyOtpRequest
import com.aura.voicechat.data.remote.ApiService
import com.aura.voicechat.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Auth Repository Implementation
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Implements authentication using:
 * - Phone OTP via backend API
 * - Google Sign-In via AWS Amplify/Cognito
 * - Facebook Sign-In via AWS Amplify/Cognito
 * 
 * Tokens are stored persistently using SharedPreferences.
 * 
 * Note: For social sign-in with Amplify, an Activity reference must be set
 * using setActivity() before calling signInWithGoogle() or signInWithFacebook().
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : AuthRepository {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    // Weak reference to the current activity for Amplify social sign-in
    private var activityRef: WeakReference<Activity>? = null
    
    companion object {
        private const val TAG = "AuthRepositoryImpl"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_AUTH_PROVIDER = "auth_provider"
    }
    
    /**
     * Set the current activity for Amplify social sign-in.
     * Should be called from the activity before initiating social sign-in.
     */
    fun setActivity(activity: Activity) {
        activityRef = WeakReference(activity)
    }
    
    /**
     * Get the current activity, or null if not set or activity was garbage collected.
     */
    private fun getActivity(): Activity? {
        return activityRef?.get()
    }
    
    /**
     * Send OTP to the provided phone number via backend API.
     * The backend handles rate limiting and SMS delivery.
     */
    override suspend fun sendOtp(phoneNumber: String): Result<Unit> {
        return try {
            Log.d(TAG, "Sending OTP to: ${phoneNumber.takeLast(4)}")
            val response = apiService.sendOtp(SendOtpRequest(phoneNumber))
            if (response.isSuccessful && response.body()?.success == true) {
                Log.d(TAG, "OTP sent successfully. Cooldown: ${response.body()?.cooldownSeconds}s")
                Result.success(Unit)
            } else {
                val errorMsg = "Failed to send OTP: ${response.code()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending OTP", e)
            Result.failure(e)
        }
    }
    
    /**
     * Verify OTP and authenticate user via backend API.
     * On success, stores auth tokens and user info.
     */
    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<Unit> {
        return try {
            Log.d(TAG, "Verifying OTP for: ${phoneNumber.takeLast(4)}")
            val response = apiService.verifyOtp(VerifyOtpRequest(phoneNumber, otp))
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.let { body ->
                    saveAuthData(
                        userId = body.user.id,
                        userName = body.user.name,
                        authToken = body.token,
                        refreshToken = body.refreshToken,
                        provider = "phone"
                    )
                    Log.i(TAG, "OTP verification successful. User: ${body.user.id}")
                }
                Result.success(Unit)
            } else {
                Log.e(TAG, "Invalid OTP: ${response.code()}")
                Result.failure(Exception("Invalid OTP"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying OTP", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sign in with Google using AWS Amplify/Cognito.
     * Uses federated sign-in with Google as the identity provider.
     * 
     * Note: setActivity() must be called before this method for Amplify to work.
     * If no activity is available, falls back to backend API.
     */
    override suspend fun signInWithGoogle(): Result<Unit> {
        val activity = getActivity()
        if (activity == null) {
            Log.w(TAG, "No activity available for Amplify sign-in, trying backend fallback")
            return tryBackendGoogleSignIn(Exception("No activity context available for social sign-in"))
        }
        
        return try {
            Log.d(TAG, "Starting Google Sign-In via Amplify")
            
            val result = suspendCancellableCoroutine<Unit> { continuation ->
                Amplify.Auth.signInWithSocialWebUI(
                    AuthProvider.google(),
                    activity,
                    { signInResult ->
                        Log.i(TAG, "Google Sign-In successful: ${signInResult.isSignedIn}")
                        continuation.resume(Unit)
                    },
                    { error ->
                        Log.e(TAG, "Google Sign-In failed", error)
                        continuation.resumeWithException(error)
                    }
                )
            }
            
            // After Amplify sign-in, fetch user attributes and store locally
            fetchAndStoreAmplifyUserData("google")
            
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In error", e)
            // Fallback: Try to use backend endpoint with a mock token for testing
            // In production, this would use the actual Google ID token
            tryBackendGoogleSignIn(e)
        }
    }
    
    /**
     * Sign in with Facebook using AWS Amplify/Cognito.
     * Uses federated sign-in with Facebook as the identity provider.
     * 
     * Note: setActivity() must be called before this method for Amplify to work.
     * If no activity is available, falls back to backend API.
     */
    override suspend fun signInWithFacebook(): Result<Unit> {
        val activity = getActivity()
        if (activity == null) {
            Log.w(TAG, "No activity available for Amplify sign-in, trying backend fallback")
            return tryBackendFacebookSignIn(Exception("No activity context available for social sign-in"))
        }
        
        return try {
            Log.d(TAG, "Starting Facebook Sign-In via Amplify")
            
            val result = suspendCancellableCoroutine<Unit> { continuation ->
                Amplify.Auth.signInWithSocialWebUI(
                    AuthProvider.facebook(),
                    activity,
                    { signInResult ->
                        Log.i(TAG, "Facebook Sign-In successful: ${signInResult.isSignedIn}")
                        continuation.resume(Unit)
                    },
                    { error ->
                        Log.e(TAG, "Facebook Sign-In failed", error)
                        continuation.resumeWithException(error)
                    }
                )
            }
            
            // After Amplify sign-in, fetch user attributes and store locally
            fetchAndStoreAmplifyUserData("facebook")
            
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Facebook Sign-In error", e)
            // Fallback: Try to use backend endpoint with a mock token for testing
            tryBackendFacebookSignIn(e)
        }
    }
    
    /**
     * Sign out from all authentication providers and clear local tokens.
     */
    override suspend fun signOut(): Result<Unit> {
        return try {
            Log.d(TAG, "Signing out user")
            
            // Try to sign out from Amplify
            try {
                suspendCancellableCoroutine<Unit> { continuation ->
                    Amplify.Auth.signOut { 
                        Log.d(TAG, "Amplify sign out completed")
                        continuation.resume(Unit)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Amplify sign out failed (may not have been signed in via Amplify)", e)
            }
            
            // Try to call backend logout
            try {
                apiService.logout()
            } catch (e: Exception) {
                Log.w(TAG, "Backend logout call failed", e)
            }
            
            // Clear all local auth data
            clearAuthData()
            
            Log.i(TAG, "Sign out successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error", e)
            // Even on error, clear local data
            clearAuthData()
            Result.failure(e)
        }
    }
    
    /**
     * Get the currently authenticated user's ID.
     */
    override suspend fun getCurrentUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
    
    /**
     * Check if user is currently logged in with a valid token.
     */
    override suspend fun isLoggedIn(): Boolean {
        val token = prefs.getString(KEY_AUTH_TOKEN, null)
        val userId = prefs.getString(KEY_USER_ID, null)
        
        if (token == null || userId == null) {
            // Also check Amplify session
            return try {
                suspendCancellableCoroutine { continuation ->
                    Amplify.Auth.fetchAuthSession(
                        { session ->
                            continuation.resume(session.isSignedIn)
                        },
                        { 
                            continuation.resume(false)
                        }
                    )
                }
            } catch (e: Exception) {
                false
            }
        }
        
        return true
    }
    
    /**
     * Get the current auth token for API requests.
     */
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    /**
     * Refresh the authentication token using the refresh token.
     */
    suspend fun refreshAuthToken(): Result<String> {
        val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null)
            ?: return Result.failure(Exception("No refresh token available"))
        
        return try {
            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    prefs.edit()
                        .putString(KEY_AUTH_TOKEN, body.token)
                        .apply()
                    Log.d(TAG, "Token refreshed successfully")
                    return Result.success(body.token)
                }
            }
            Log.e(TAG, "Failed to refresh token: ${response.code()}")
            Result.failure(Exception("Failed to refresh token"))
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing token", e)
            Result.failure(e)
        }
    }
    
    // ================== Private Helper Methods ==================
    
    /**
     * Save authentication data to SharedPreferences.
     */
    private fun saveAuthData(
        userId: String,
        userName: String,
        authToken: String,
        refreshToken: String?,
        provider: String
    ) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_AUTH_TOKEN, authToken)
            refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            putString(KEY_AUTH_PROVIDER, provider)
            putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000) // 7 days
            apply()
        }
    }
    
    /**
     * Clear all authentication data from SharedPreferences.
     */
    private fun clearAuthData() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_NAME)
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_TOKEN_EXPIRY)
            .remove(KEY_AUTH_PROVIDER)
            .apply()
    }
    
    /**
     * Fetch user data from Amplify and store it locally.
     */
    private suspend fun fetchAndStoreAmplifyUserData(provider: String) {
        try {
            suspendCancellableCoroutine<Unit> { continuation ->
                Amplify.Auth.fetchUserAttributes(
                    { attributes ->
                        val userId = attributes.find { it.key.keyString == "sub" }?.value ?: ""
                        val email = attributes.find { it.key.keyString == "email" }?.value ?: ""
                        val name = attributes.find { it.key.keyString == "name" }?.value 
                            ?: attributes.find { it.key.keyString == "given_name" }?.value
                            ?: email.substringBefore("@")
                        
                        // Get the access token from current session
                        Amplify.Auth.fetchAuthSession(
                            { session ->
                                // For Cognito, we can get the access token
                                val accessToken = try {
                                    // Access the tokens from the session
                                    val cognitoSession = session as? com.amplifyframework.auth.cognito.AWSCognitoAuthSession
                                    cognitoSession?.userPoolTokensResult?.value?.accessToken ?: ""
                                } catch (e: Exception) {
                                    Log.w(TAG, "Could not get access token from session", e)
                                    ""
                                }
                                
                                saveAuthData(
                                    userId = userId,
                                    userName = name,
                                    authToken = accessToken,
                                    refreshToken = null,
                                    provider = provider
                                )
                                continuation.resume(Unit)
                            },
                            { error ->
                                Log.w(TAG, "Could not fetch auth session", error)
                                continuation.resume(Unit)
                            }
                        )
                    },
                    { error ->
                        Log.e(TAG, "Failed to fetch user attributes", error)
                        continuation.resume(Unit)
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Amplify user data", e)
        }
    }
    
    /**
     * Fallback: Return the original error when Amplify sign-in fails.
     * 
     * Note: In a production implementation, you would integrate with Google Sign-In SDK
     * to get a valid ID token and then call the backend. For now, we simply return
     * the original error as the backend requires valid tokens.
     */
    private suspend fun tryBackendGoogleSignIn(originalError: Exception): Result<Unit> {
        // The backend requires a valid Google ID token for verification.
        // Without the Google Sign-In SDK integration to get a real token,
        // we cannot make a valid backend request.
        Log.w(TAG, "Google Sign-In requires proper SDK integration. Returning original error.")
        return Result.failure(originalError)
    }
    
    /**
     * Fallback: Return the original error when Amplify sign-in fails.
     * 
     * Note: In a production implementation, you would integrate with Facebook Login SDK
     * to get a valid access token and then call the backend. For now, we simply return
     * the original error as the backend requires valid tokens.
     */
    private suspend fun tryBackendFacebookSignIn(originalError: Exception): Result<Unit> {
        // The backend requires a valid Facebook access token for verification.
        // Without the Facebook Login SDK integration to get a real token,
        // we cannot make a valid backend request.
        Log.w(TAG, "Facebook Sign-In requires proper SDK integration. Returning original error.")
        return Result.failure(originalError)
    }
}

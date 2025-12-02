package com.aura.voicechat.domain.repository

/**
 * Authentication Repository interface
 * Developer: Hawkaye Visions LTD — Pakistan
 */
interface AuthRepository {
    suspend fun sendOtp(phoneNumber: String): Result<Unit>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<Unit>
    suspend fun signInWithGoogle(): Result<Unit>
    suspend fun signInWithFacebook(): Result<Unit>
    
    /**
     * Sign in with Google using an ID token obtained from Google Sign-In SDK.
     * This method sends the token to the backend for verification and user creation.
     */
    suspend fun loginWithGoogleToken(idToken: String, email: String?, displayName: String?): Result<Unit>
    
    /**
     * Sign in with Facebook using an access token obtained from Facebook Login SDK.
     * This method sends the token to the backend for verification and user creation.
     */
    suspend fun loginWithFacebookToken(accessToken: String, userId: String?, email: String?, displayName: String?): Result<Unit>
    
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUserId(): String?
    suspend fun isLoggedIn(): Boolean
}

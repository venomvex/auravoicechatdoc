package com.aura.voicechat.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.remote.HealthApi
import com.aura.voicechat.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Login ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Handles authentication flows (Phone OTP, Google, Facebook) and 
 * backend health checks via HealthApi.
 * 
 * Uses AuthRepository which implements:
 * - Phone OTP authentication via backend API
 * - Google Sign-In via Google Sign-In SDK + backend API
 * - Facebook Sign-In via Facebook Login SDK + backend API
 * 
 * Social sign-in flow:
 * 1. LoginScreen launches Google/Facebook SDK via ActivityResultLauncher
 * 2. SDK returns token (Google ID token or Facebook access token)
 * 3. LoginScreen calls onGoogleTokenReceived/onFacebookTokenReceived
 * 4. ViewModel calls authRepository.loginWithGoogleToken/loginWithFacebookToken
 * 5. Repository sends token to backend for verification and user creation
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val healthApi: HealthApi
) : ViewModel() {
    
    companion object {
        private const val TAG = "LoginViewModel"
    }
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    init {
        // Check if user is already logged in
        checkLoginStatus()
    }
    
    /**
     * Check if user is already logged in and update UI state.
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                val isLoggedIn = authRepository.isLoggedIn()
                if (isLoggedIn) {
                    _uiState.value = _uiState.value.copy(isLoggedIn = true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking login status", e)
            }
        }
    }
    
    /**
     * Ping the backend to verify connectivity.
     * Called once when the login screen appears (via LaunchedEffect).
     */
    fun pingBackend() {
        viewModelScope.launch {
            try {
                val response = healthApi.checkHealth()
                if (response.isSuccessful) {
                    val healthResponse = response.body()
                    if (healthResponse != null) {
                        Log.d(TAG, "Backend health check passed: ${healthResponse.status}")
                        _uiState.value = _uiState.value.copy(
                            isBackendReachable = true,
                            backendStatus = healthResponse.status
                        )
                    } else {
                        // HTTP 200 but null body - unexpected response format
                        Log.w(TAG, "Backend health check returned null body")
                        _uiState.value = _uiState.value.copy(
                            isBackendReachable = true,
                            backendStatus = "connected (no status)"
                        )
                    }
                } else {
                    Log.w(TAG, "Backend health check failed: ${response.code()}")
                    _uiState.value = _uiState.value.copy(
                        isBackendReachable = false,
                        backendStatus = "error: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Backend health check error: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isBackendReachable = false,
                    backendStatus = "error: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Send OTP to the provided phone number.
     * Uses the backend API at /api/v1/auth/otp/send
     */
    fun sendOtp(phoneNumber: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.sendOtp(phoneNumber)
            
            result.fold(
                onSuccess = {
                    Log.d(TAG, "OTP sent successfully to ${phoneNumber.takeLast(4)}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        otpSent = true
                    )
                },
                onFailure = { e ->
                    Log.e(TAG, "Failed to send OTP", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to send OTP. Please try again."
                    )
                }
            )
        }
    }
    
    /**
     * Handle Google Sign-In token received from the SDK.
     * This should be called after the Google Sign-In ActivityResultLauncher returns.
     * 
     * @param idToken The Google ID token from GoogleSignInAccount.idToken
     * @param email Optional email from the Google account
     * @param displayName Optional display name from the Google account
     */
    fun onGoogleTokenReceived(idToken: String, email: String?, displayName: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.loginWithGoogleToken(idToken, email, displayName)
            
            result.fold(
                onSuccess = {
                    Log.i(TAG, "Google Sign-In successful")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                },
                onFailure = { e ->
                    Log.e(TAG, "Google Sign-In failed", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Google Sign-In failed. Please try again."
                    )
                }
            )
        }
    }
    
    /**
     * Handle Facebook access token received from the SDK.
     * This should be called after the Facebook Login SDK callback.
     * 
     * @param accessToken The Facebook access token from AccessToken.currentAccessToken
     * @param userId Optional Facebook user ID
     * @param email Optional email from the Facebook account
     * @param displayName Optional display name from the Facebook account
     */
    fun onFacebookTokenReceived(
        accessToken: String,
        userId: String?,
        email: String?,
        displayName: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.loginWithFacebookToken(accessToken, userId, email, displayName)
            
            result.fold(
                onSuccess = {
                    Log.i(TAG, "Facebook Sign-In successful")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                },
                onFailure = { e ->
                    Log.e(TAG, "Facebook Sign-In failed", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Facebook Sign-In failed. Please try again."
                    )
                }
            )
        }
    }
    
    /**
     * Sign in with Google using legacy Amplify method.
     * Note: This requires Activity context and may fail. 
     * Prefer using onGoogleTokenReceived with Google Sign-In SDK.
     */
    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.signInWithGoogle()
            
            result.fold(
                onSuccess = {
                    Log.i(TAG, "Google Sign-In successful")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                },
                onFailure = { e ->
                    Log.e(TAG, "Google Sign-In failed", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Google Sign-In failed. Please try again."
                    )
                }
            )
        }
    }
    
    /**
     * Sign in with Facebook using legacy Amplify method.
     * Note: This requires Activity context and may fail.
     * Prefer using onFacebookTokenReceived with Facebook Login SDK.
     */
    fun signInWithFacebook() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.signInWithFacebook()
            
            result.fold(
                onSuccess = {
                    Log.i(TAG, "Facebook Sign-In successful")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                },
                onFailure = { e ->
                    Log.e(TAG, "Facebook Sign-In failed", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Facebook Sign-In failed. Please try again."
                    )
                }
            )
        }
    }
    
    /**
     * Clear the current error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Reset the OTP sent state (used when navigating back from OTP screen).
     */
    fun resetOtpSent() {
        _uiState.value = _uiState.value.copy(otpSent = false)
    }
}

/**
 * UI State for the Login screen.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val otpSent: Boolean = false,
    val error: String? = null,
    // Backend connectivity state
    val isBackendReachable: Boolean? = null,
    val backendStatus: String? = null
)

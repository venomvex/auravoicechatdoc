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
    
    /**
     * Ping the backend to verify connectivity.
     * Called once when the login screen appears (via LaunchedEffect).
     * 
     * TODO: Consider showing connection status to user or handling offline mode.
     */
    fun pingBackend() {
        viewModelScope.launch {
            try {
                val response = healthApi.checkHealth()
                if (response.isSuccessful) {
                    val healthResponse = response.body()
                    Log.d(TAG, "Backend health check passed: ${healthResponse?.status}")
                    _uiState.value = _uiState.value.copy(
                        isBackendReachable = true,
                        backendStatus = healthResponse?.status ?: "ok"
                    )
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
    
    fun sendOtp(phoneNumber: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                authRepository.sendOtp(phoneNumber)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    otpSent = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                authRepository.signInWithGoogle()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun signInWithFacebook() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                authRepository.signInWithFacebook()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val otpSent: Boolean = false,
    val error: String? = null,
    // Backend connectivity state
    val isBackendReachable: Boolean? = null,
    val backendStatus: String? = null
)

package com.aura.voicechat.ui.wallet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.model.ExchangeRequest
import com.aura.voicechat.data.remote.ApiService
import com.aura.voicechat.domain.model.Currency
import com.aura.voicechat.domain.model.Transaction
import com.aura.voicechat.domain.model.TransactionType
import com.aura.voicechat.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Wallet ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Fetches wallet balances and transactions from the backend API.
 */
@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "WalletViewModel"
    }
    
    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()
    
    init {
        loadWallet()
        loadTransactions()
    }
    
    private fun loadWallet() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = apiService.getWalletBalances()
                if (response.isSuccessful) {
                    val data = response.body()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        coins = data?.coins ?: 0,
                        diamonds = data?.diamonds ?: 0
                    )
                    Log.d(TAG, "Loaded wallet: coins=${data?.coins}, diamonds=${data?.diamonds}")
                } else {
                    Log.e(TAG, "Failed to load wallet: ${response.code()}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load wallet balances"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading wallet", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                // TODO: Add transactions endpoint to ApiService when backend implements it
                // For now, transactions will be empty until backend provides the data
                _uiState.value = _uiState.value.copy(transactions = emptyList())
            } catch (e: Exception) {
                Log.e(TAG, "Error loading transactions", e)
            }
        }
    }
    
    fun exchangeDiamondsToCoins(diamonds: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = apiService.exchangeDiamondsToCoins(
                    ExchangeRequest(diamonds = diamonds)
                )
                
                if (response.isSuccessful) {
                    val data = response.body()
                    val coinsReceived = data?.coinsReceived ?: (diamonds * 0.30).toLong()
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        diamonds = data?.newBalance?.diamonds ?: (_uiState.value.diamonds - diamonds),
                        coins = data?.newBalance?.coins ?: (_uiState.value.coins + coinsReceived),
                        message = "Successfully exchanged $diamonds diamonds for $coinsReceived coins!"
                    )
                    
                    // Reload wallet to get fresh data
                    loadWallet()
                    Log.d(TAG, "Exchange successful: $diamonds diamonds -> $coinsReceived coins")
                } else {
                    Log.e(TAG, "Exchange failed: ${response.code()}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Exchange failed. Please try again."
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error exchanging", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun refresh() {
        loadWallet()
        loadTransactions()
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class WalletUiState(
    val isLoading: Boolean = false,
    val coins: Long = 0,
    val diamonds: Long = 0,
    val transactions: List<Transaction> = emptyList(),
    val message: String? = null,
    val error: String? = null
)

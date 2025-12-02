package com.aura.voicechat.ui.gifts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.model.SendGiftRequest
import com.aura.voicechat.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Gift Panel ViewModel - Manages gift catalog and sending (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Gift data is synced from backend database via API.
 * Owner can add/remove/modify gifts via CMS panel.
 */
@HiltViewModel
class GiftPanelViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "GiftPanelViewModel"
    }
    
    private val _uiState = MutableStateFlow(GiftPanelUiState())
    val uiState: StateFlow<GiftPanelUiState> = _uiState.asStateFlow()
    
    private var allGifts: List<Gift> = emptyList()
    
    fun loadGifts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Fetch gifts from backend API
                val response = apiService.getGifts()
                if (response.isSuccessful) {
                    val data = response.body()
                    allGifts = data?.gifts?.map { dto ->
                        Gift(
                            id = dto.id,
                            name = dto.name,
                            category = dto.category,
                            price = dto.priceCoins,
                            diamondValue = dto.diamondValue,
                            rarity = when {
                                dto.priceCoins >= 50000000 -> "legendary"
                                dto.priceCoins >= 10000 -> "epic"
                                dto.priceCoins >= 1000 -> "rare"
                                else -> "common"
                            },
                            iconUrl = dto.thumbnailUrl,
                            animationFile = dto.animationUrl,
                            isAnimated = dto.animationUrl != null,
                            isFullScreen = dto.priceCoins >= 50000,
                            isCustom = dto.category == "custom",
                            isLegendary = dto.priceCoins >= 50000000,
                            isPremium = dto.isPremium
                        )
                    } ?: emptyList()
                    
                    Log.d(TAG, "Loaded ${allGifts.size} gifts from API")
                } else {
                    Log.e(TAG, "Failed to load gifts: ${response.code()}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load gifts"
                    )
                    return@launch
                }
                
                // Also fetch wallet balance
                try {
                    val walletResponse = apiService.getWalletBalances()
                    val coins = if (walletResponse.isSuccessful) {
                        walletResponse.body()?.coins ?: 0
                    } else 0
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        gifts = allGifts,
                        coins = coins
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        gifts = allGifts,
                        coins = 0
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading gifts", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun filterByCategory(category: String) {
        val filtered = if (category == "all") {
            allGifts
        } else {
            allGifts.filter { it.category == category }
        }
        _uiState.value = _uiState.value.copy(gifts = filtered)
    }
    
    fun sendGift(recipientId: String, giftId: String, quantity: Int, roomId: String? = null) {
        viewModelScope.launch {
            val gift = allGifts.find { it.id == giftId } ?: return@launch
            val totalCost = (gift.price ?: gift.priceCoins ?: 0L) * quantity
            val currentCoins = _uiState.value.coins
            
            if (currentCoins >= totalCost) {
                try {
                    val response = apiService.sendGiftSimple(
                        SendGiftRequest(
                            giftId = giftId,
                            receiverId = recipientId,
                            roomId = roomId,
                            quantity = quantity
                        )
                    )
                    
                    if (response.isSuccessful) {
                        val data = response.body()
                        _uiState.value = _uiState.value.copy(
                            coins = currentCoins - totalCost,
                            lastSentGift = gift,
                            lastSentQuantity = quantity
                        )
                        Log.d(TAG, "Gift sent successfully: $giftId x $quantity")
                    } else {
                        Log.e(TAG, "Failed to send gift: ${response.code()}")
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to send gift. Please try again."
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending gift", e)
                    _uiState.value = _uiState.value.copy(
                        error = "Network error. Please check your connection."
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "Insufficient coins"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun refresh() {
        loadGifts()
    }
}

data class GiftPanelUiState(
    val isLoading: Boolean = false,
    val gifts: List<Gift> = emptyList(),
    val coins: Long = 0,
    val lastSentGift: Gift? = null,
    val lastSentQuantity: Int = 0,
    val error: String? = null
)

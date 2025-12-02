package com.aura.voicechat.ui.store

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.model.PurchaseItemRequest
import com.aura.voicechat.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Store ViewModel (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class StoreViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "StoreViewModel"
    }
    
    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()
    
    private var allItems: List<StoreItem> = emptyList()
    
    init {
        loadStoreItems()
        loadWallet()
    }
    
    private fun loadStoreItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load store catalog
                val response = apiService.getStoreCatalog()
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    allItems = data.items.map { dto ->
                        StoreItem(
                            id = dto.id,
                            name = dto.name,
                            description = dto.description ?: "",
                            category = dto.category,
                            price = dto.price,
                            rarity = dto.rarity,
                            iconUrl = dto.iconUrl,
                            duration = dto.duration
                        )
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = allItems,
                        featuredItem = allItems.firstOrNull { it.rarity == "legendary" }
                    )
                    Log.d(TAG, "Loaded ${allItems.size} store items")
                }
                
                // Load featured items
                val featuredResponse = apiService.getFeaturedItems()
                if (featuredResponse.isSuccessful && featuredResponse.body() != null) {
                    val featured = featuredResponse.body()!!.items.firstOrNull()
                    if (featured != null) {
                        _uiState.value = _uiState.value.copy(
                            featuredItem = StoreItem(
                                id = featured.id,
                                name = featured.name,
                                description = featured.description ?: "",
                                category = featured.category,
                                price = featured.price,
                                rarity = featured.rarity,
                                iconUrl = featured.iconUrl,
                                duration = featured.duration
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading store items", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    purchaseError = e.message
                )
            }
        }
    }
    
    private fun loadWallet() {
        viewModelScope.launch {
            try {
                val response = apiService.getWalletBalances()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        coins = response.body()!!.coins
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading wallet", e)
            }
        }
    }
    
    fun filterByCategory(category: String) {
        val filtered = if (category == "all") {
            allItems
        } else {
            allItems.filter { it.category == category }
        }
        _uiState.value = _uiState.value.copy(items = filtered)
    }
    
    fun purchaseItem(itemId: String) {
        viewModelScope.launch {
            val item = allItems.find { it.id == itemId } ?: return@launch
            val currentCoins = _uiState.value.coins
            
            if (currentCoins < item.price) {
                _uiState.value = _uiState.value.copy(
                    purchaseError = "Insufficient coins"
                )
                return@launch
            }
            
            try {
                val response = apiService.purchaseItem(PurchaseItemRequest(itemId, 1))
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        coins = result.newBalance,
                        purchaseSuccess = true,
                        purchasedItemId = itemId,
                        purchaseMessage = "Successfully purchased ${item.name}!"
                    )
                    Log.d(TAG, "Purchased item: $itemId")
                } else {
                    _uiState.value = _uiState.value.copy(
                        purchaseError = "Failed to purchase item"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error purchasing item", e)
                _uiState.value = _uiState.value.copy(
                    purchaseError = e.message
                )
            }
        }
    }
    
    fun clearPurchaseState() {
        _uiState.value = _uiState.value.copy(
            purchaseSuccess = false,
            purchaseError = null,
            purchasedItemId = null,
            purchaseMessage = null
        )
    }
    
    fun refresh() {
        loadStoreItems()
        loadWallet()
    }
}

data class StoreUiState(
    val isLoading: Boolean = false,
    val items: List<StoreItem> = emptyList(),
    val featuredItem: StoreItem? = null,
    val coins: Long = 0,
    val purchaseSuccess: Boolean = false,
    val purchaseError: String? = null,
    val purchasedItemId: String? = null,
    val purchaseMessage: String? = null
)

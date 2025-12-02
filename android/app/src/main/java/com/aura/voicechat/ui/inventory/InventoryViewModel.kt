package com.aura.voicechat.ui.inventory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Inventory ViewModel (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "InventoryViewModel"
        private const val SOURCE_GIFT_RECEIVED = "gift_received"
        private const val SOURCE_BAGGAGE = "baggage"
        private const val HOURS_IN_DAY = 24L
        private const val MILLIS_IN_HOUR = 60 * 60 * 1000L
        private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L
    }
    
    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()
    
    private var allItems: List<InventoryItem> = emptyList()
    
    init {
        loadInventory()
    }
    
    private fun loadInventory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = apiService.getInventory()
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    allItems = data.items.map { dto ->
                        // Calculate expiration info
                        val expiresAt = dto.expiresAt
                        val expiresIn = if (expiresAt != null) {
                            val now = System.currentTimeMillis()
                            val diff = expiresAt - now
                            when {
                                diff <= 0 -> "Expired"
                                diff < MILLIS_IN_DAY -> "${diff / MILLIS_IN_HOUR}h"
                                else -> "${diff / MILLIS_IN_DAY}d"
                            }
                        } else null
                        val isExpiringSoon = expiresAt != null && (expiresAt - System.currentTimeMillis()) < MILLIS_IN_DAY
                        
                        InventoryItem(
                            id = dto.id,
                            name = dto.name,
                            description = dto.description ?: "",
                            category = dto.category,
                            rarity = dto.rarity,
                            iconUrl = dto.iconUrl,
                            expiresIn = expiresIn,
                            isExpiringSoon = isExpiringSoon,
                            isBaggage = dto.source == SOURCE_GIFT_RECEIVED || dto.source == SOURCE_BAGGAGE
                        )
                    }
                    
                    // Load equipped items separately
                    val equipped = loadEquippedItems()
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = allItems,
                        equippedItems = equipped
                    )
                    Log.d(TAG, "Loaded ${allItems.size} inventory items")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = emptyList()
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading inventory", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private suspend fun loadEquippedItems(): List<InventoryItem> {
        return try {
            val equippedResponse = apiService.getEquippedItems()
            if (equippedResponse.isSuccessful && equippedResponse.body() != null) {
                val equippedData = equippedResponse.body()!!
                listOfNotNull(
                    equippedData.frame, equippedData.vehicle, equippedData.theme,
                    equippedData.micSkin, equippedData.seatEffect, equippedData.chatBubble,
                    equippedData.entranceStyle, equippedData.roomCard, equippedData.cover
                ).map { dto ->
                    InventoryItem(
                        id = dto.id,
                        name = dto.name,
                        description = dto.description ?: "",
                        category = dto.category,
                        rarity = dto.rarity,
                        iconUrl = dto.iconUrl,
                        expiresIn = null
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading equipped items", e)
            emptyList()
        }
    }
    
    fun filterByCategory(category: String) {
        val filtered = when (category) {
            "all" -> allItems
            "baggage" -> allItems.filter { it.isBaggage }
            else -> allItems.filter { it.category == category }
        }
        _uiState.value = _uiState.value.copy(items = filtered)
    }
    
    fun selectItem(itemId: String) {
        val item = allItems.find { it.id == itemId }
        _uiState.value = _uiState.value.copy(selectedItem = item)
    }
    
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedItem = null)
    }
    
    fun equipItem(itemId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.equipItem(com.aura.voicechat.data.model.EquipItemRequest(itemId))
                if (response.isSuccessful) {
                    val item = allItems.find { it.id == itemId } ?: return@launch
                    val currentEquipped = _uiState.value.equippedItems.toMutableList()
                    
                    // Remove any existing item in same category
                    currentEquipped.removeAll { it.category == item.category }
                    currentEquipped.add(item)
                    
                    _uiState.value = _uiState.value.copy(
                        equippedItems = currentEquipped,
                        selectedItem = null,
                        message = "Equipped ${item.name}!"
                    )
                    Log.d(TAG, "Equipped item: $itemId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error equipping item", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun unequipItem(itemId: String) {
        viewModelScope.launch {
            try {
                // Find the item's category
                val item = _uiState.value.equippedItems.find { it.id == itemId } ?: return@launch
                val response = apiService.unequipItem(com.aura.voicechat.data.model.UnequipItemRequest(item.category))
                if (response.isSuccessful) {
                    val currentEquipped = _uiState.value.equippedItems.toMutableList()
                    currentEquipped.removeAll { it.id == itemId }
                    
                    _uiState.value = _uiState.value.copy(
                        equippedItems = currentEquipped,
                        selectedItem = null,
                        message = "Item unequipped"
                    )
                    Log.d(TAG, "Unequipped item: $itemId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error unequipping item", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun refresh() {
        loadInventory()
    }
    
    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class InventoryUiState(
    val isLoading: Boolean = false,
    val items: List<InventoryItem> = emptyList(),
    val equippedItems: List<InventoryItem> = emptyList(),
    val selectedItem: InventoryItem? = null,
    val message: String? = null,
    val error: String? = null
)

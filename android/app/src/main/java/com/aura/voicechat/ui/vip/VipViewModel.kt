package com.aura.voicechat.ui.vip

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
 * VIP System ViewModel (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class VipViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "VipViewModel"
    }
    
    private val _uiState = MutableStateFlow(VipUiState())
    val uiState: StateFlow<VipUiState> = _uiState.asStateFlow()
    
    init {
        loadVipData()
    }
    
    private fun loadVipData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load VIP status
                val statusResponse = apiService.getVipStatus()
                if (statusResponse.isSuccessful && statusResponse.body() != null) {
                    val data = statusResponse.body()!!
                    _uiState.value = _uiState.value.copy(
                        currentTier = data.tier,
                        daysRemaining = data.daysRemaining,
                        totalDiamondsSpent = data.totalSpent,
                        nextTierProgress = data.progress,
                        allTiers = (1..10).toList()
                    )
                    Log.d(TAG, "Loaded VIP status: tier=${data.tier}")
                }
                
                // Load VIP packages
                val packagesResponse = apiService.getVipPackages()
                if (packagesResponse.isSuccessful && packagesResponse.body() != null) {
                    val packages = packagesResponse.body()!!.packages.map { pkg ->
                        VipPackage(
                            id = pkg.id,
                            name = pkg.name,
                            description = pkg.description,
                            price = pkg.price,
                            originalPrice = pkg.originalPrice,
                            bonusDiamonds = pkg.bonusDiamonds,
                            days = pkg.days,
                            isBestValue = pkg.isBestValue
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        purchasePackages = packages,
                        isLoading = false
                    )
                    Log.d(TAG, "Loaded ${packages.size} VIP packages")
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading VIP data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun getBenefitsForTier(tier: Int): List<VipBenefit> {
        val baseBenefits = mutableListOf(
            VipBenefit("Daily Bonus", "${100 + tier * 20}% daily reward multiplier"),
            VipBenefit("VIP Badge", "Exclusive V$tier badge on profile"),
            VipBenefit("Priority Support", "24/7 dedicated support")
        )
        
        if (tier >= 2) baseBenefits.add(VipBenefit("VIP Frame", "Exclusive frame for V$tier"))
        if (tier >= 3) baseBenefits.add(VipBenefit("Gift Discount", "${tier * 2}% discount on gifts"))
        if (tier >= 4) baseBenefits.add(VipBenefit("VIP Vehicle", "Exclusive entrance vehicle"))
        if (tier >= 5) baseBenefits.add(VipBenefit("Room Priority", "Featured in room listings"))
        if (tier >= 6) baseBenefits.add(VipBenefit("Custom Theme", "Exclusive VIP theme"))
        if (tier >= 7) baseBenefits.add(VipBenefit("VIP Emojis", "Exclusive animated emojis"))
        if (tier >= 8) baseBenefits.add(VipBenefit("Withdrawal Bonus", "10% extra on withdrawals"))
        if (tier >= 9) baseBenefits.add(VipBenefit("VIP Events", "Access to exclusive events"))
        if (tier >= 10) baseBenefits.add(VipBenefit("Legend Status", "Permanent legend recognition"))
        
        return baseBenefits
    }
    
    fun getExclusiveItems(tier: Int): List<VipExclusiveItemData> {
        return listOf(
            VipExclusiveItemData("frame_v$tier", "V$tier Frame", "frame"),
            VipExclusiveItemData("badge_v$tier", "V$tier Badge", "badge"),
            VipExclusiveItemData("vehicle_v$tier", "V$tier Vehicle", "vehicle"),
            VipExclusiveItemData("theme_v$tier", "V$tier Theme", "theme")
        )
    }
    
    fun purchaseVip(pkg: VipPackage) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isPurchasing = true)
                val response = apiService.purchaseVip(
                    com.aura.voicechat.data.model.PurchaseVipRequest(packageId = pkg.id)
                )
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isPurchasing = false,
                        currentTier = result.newTier,
                        daysRemaining = result.daysRemaining,
                        message = "VIP ${pkg.name} activated!"
                    )
                    loadVipData() // Refresh
                    Log.d(TAG, "VIP purchased successfully")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isPurchasing = false,
                        error = "Failed to purchase VIP"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error purchasing VIP", e)
                _uiState.value = _uiState.value.copy(
                    isPurchasing = false,
                    error = e.message
                )
            }
        }
    }
    
    fun refresh() {
        loadVipData()
    }
    
    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class VipUiState(
    val isLoading: Boolean = false,
    val isPurchasing: Boolean = false,
    val currentTier: Int = 0,
    val daysRemaining: Int = 0,
    val totalDiamondsSpent: Long = 0,
    val nextTierProgress: Float = 0f,
    val allTiers: List<Int> = emptyList(),
    val purchasePackages: List<VipPackage> = emptyList(),
    val message: String? = null,
    val error: String? = null
)

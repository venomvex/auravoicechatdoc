package com.aura.voicechat.ui.games

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.model.GameActionRequest
import com.aura.voicechat.data.remote.ApiService
import com.aura.voicechat.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * Gift Wheel ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Uses live backend API for game data, balance, and spins.
 */
@HiltViewModel
class GiftWheelViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GiftWheelUiState())
    val uiState: StateFlow<GiftWheelUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load wallet balance
                val walletResponse = apiService.getWalletBalances()
                val coins = if (walletResponse.isSuccessful) {
                    walletResponse.body()?.coins ?: 0L
                } else 0L
                
                // Load game stats for free spins
                val statsResponse = apiService.getGameStats()
                val freeSpins = if (statsResponse.isSuccessful) {
                    statsResponse.body()?.giftWheelFreeSpins ?: 0
                } else 0
                
                _uiState.value = GiftWheelUiState(
                    isLoading = false,
                    coins = coins,
                    freeSpins = freeSpins,
                    prizes = listOf(
                        WheelPrize("1", "1x", 1000, AccentMagenta, 1f),
                        WheelPrize("2", "2x", 2000, VipGold, 2f),
                        WheelPrize("3", "5x", 5000, AccentCyan, 5f),
                        WheelPrize("4", "10x", 10000, SuccessGreen, 10f),
                        WheelPrize("5", "20x", 20000, DiamondBlue, 20f),
                        WheelPrize("6", "50x", 50000, Purple80, 50f),
                        WheelPrize("7", "100x", 100000, WarningOrange, 100f),
                        WheelPrize("8", "JACKPOT", 1000000, ErrorRed, 1000f)
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun setBetAmount(amount: Long) {
        _uiState.value = _uiState.value.copy(betAmount = amount)
    }
    
    fun spin() {
        if (_uiState.value.isSpinning) return
        if (_uiState.value.coins < _uiState.value.betAmount && _uiState.value.freeSpins <= 0) return
        
        viewModelScope.launch {
            val useFree = _uiState.value.freeSpins > 0
            val newCoins = if (useFree) {
                _uiState.value.coins
            } else {
                _uiState.value.coins - _uiState.value.betAmount
            }
            val newFreeSpins = if (useFree) _uiState.value.freeSpins - 1 else _uiState.value.freeSpins
            
            _uiState.value = _uiState.value.copy(
                isSpinning = true,
                coins = newCoins,
                freeSpins = newFreeSpins
            )
            
            try {
                // Call backend to perform game action
                val response = apiService.gameAction(
                    "giftwheel",
                    GameActionRequest(
                        action = "spin",
                        bet = if (useFree) 0 else _uiState.value.betAmount,
                        useFreeSpins = useFree
                    )
                )
                
                if (response.isSuccessful) {
                    val result = response.body()
                    val prizeIndex = result?.prizeIndex?.toInt() ?: Random.nextInt(_uiState.value.prizes.size)
                    val baseRotation = _uiState.value.wheelRotation
                    val targetRotation = baseRotation + 360f * 5 + (prizeIndex * 45f) // 8 segments = 45° each
                    
                    _uiState.value = _uiState.value.copy(
                        wheelRotation = targetRotation,
                        selectedPrizeIndex = prizeIndex
                    )
                } else {
                    // Revert on error
                    _uiState.value = _uiState.value.copy(
                        isSpinning = false,
                        coins = if (useFree) _uiState.value.coins else _uiState.value.coins + _uiState.value.betAmount,
                        freeSpins = if (useFree) _uiState.value.freeSpins + 1 else _uiState.value.freeSpins,
                        error = "Failed to spin"
                    )
                }
            } catch (e: Exception) {
                // Revert on error
                _uiState.value = _uiState.value.copy(
                    isSpinning = false,
                    coins = if (useFree) _uiState.value.coins else _uiState.value.coins + _uiState.value.betAmount,
                    freeSpins = if (useFree) _uiState.value.freeSpins + 1 else _uiState.value.freeSpins,
                    error = e.message
                )
            }
        }
    }
    
    fun onSpinComplete() {
        val prize = _uiState.value.prizes[_uiState.value.selectedPrizeIndex]
        val winAmount = (_uiState.value.betAmount * prize.multiplier).toLong()
        
        _uiState.value = _uiState.value.copy(
            isSpinning = false,
            coins = _uiState.value.coins + winAmount,
            lastPrize = prize.copy(value = winAmount),
            showWinDialog = true
        )
    }
    
    fun dismissWinDialog() {
        _uiState.value = _uiState.value.copy(showWinDialog = false)
    }
}

data class GiftWheelUiState(
    val isLoading: Boolean = false,
    val coins: Long = 0,
    val freeSpins: Int = 0,
    val betAmount: Long = 1000,
    val prizes: List<WheelPrize> = emptyList(),
    val isSpinning: Boolean = false,
    val wheelRotation: Float = 0f,
    val selectedPrizeIndex: Int = 0,
    val lastPrize: WheelPrize? = null,
    val showWinDialog: Boolean = false,
    val error: String? = null
)

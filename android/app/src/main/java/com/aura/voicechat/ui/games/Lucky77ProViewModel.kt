package com.aura.voicechat.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * ViewModel for Lucky 77 Pro Game
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Uses live backend API for game data, balance, and history.
 */
@HiltViewModel
class Lucky77ProViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(Lucky77ProUiState())
    val uiState: StateFlow<Lucky77ProUiState> = _uiState.asStateFlow()

    private val proSymbols = listOf("💎", "👑", "🔥", "⭐", "💰", "🎰", "77")

    init {
        loadGameData()
    }

    private fun loadGameData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Load wallet balance
                val walletResponse = apiService.getWalletBalances()
                val userDiamonds = if (walletResponse.isSuccessful) {
                    walletResponse.body()?.diamonds ?: 0
                } else 0
                
                // Load jackpot
                val jackpotResponse = apiService.getJackpot("lucky77pro")
                val jackpotAmount = if (jackpotResponse.isSuccessful) {
                    jackpotResponse.body()?.amount ?: 10000000L
                } else 10000000L
                
                // Load recent wins from game history
                val historyResponse = apiService.getGameHistory("lucky77pro", 1, 10)
                val recentWins = if (historyResponse.isSuccessful) {
                    historyResponse.body()?.records?.map { record ->
                        ProWinRecord(
                            username = record.userName ?: "Player",
                            amount = record.winAmount?.toInt() ?: 0,
                            timestamp = record.timestamp ?: System.currentTimeMillis()
                        )
                    } ?: emptyList()
                } else emptyList()
                
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        userDiamonds = userDiamonds.toInt(),
                        jackpotAmount = jackpotAmount,
                        slots = listOf("💎", "💎", "💎"),
                        recentWins = recentWins
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectMultiplier(multiplier: Int) {
        _uiState.update { state ->
            state.copy(selectedMultiplier = multiplier)
        }
    }

    fun selectBet(bet: Int) {
        _uiState.update { state ->
            state.copy(selectedBet = bet)
        }
    }

    fun spin() {
        val currentState = _uiState.value
        val cost = currentState.totalCost

        if (currentState.userDiamonds < cost) {
            return
        }

        viewModelScope.launch {
            // Start spinning
            _uiState.update { state ->
                state.copy(
                    isSpinning = true,
                    userDiamonds = state.userDiamonds - cost
                )
            }
            
            try {
                // Call backend to perform game action
                val response = apiService.gameAction(
                    "lucky77pro",
                    com.aura.voicechat.data.model.GameActionRequest(
                        action = "spin",
                        bet = currentState.selectedBet.toLong(),
                        multiplier = currentState.selectedMultiplier
                    )
                )
                
                if (response.isSuccessful) {
                    val result = response.body()
                    
                    // Animate slots
                    repeat(20) {
                        _uiState.update { state ->
                            state.copy(
                                slots = List(3) { proSymbols.random() }
                            )
                        }
                        delay(100)
                    }
                    
                    // Set final result from backend
                    val symbols = result?.symbols ?: determineProResult().symbols
                    val winAmount = result?.winAmount?.toInt() ?: 0
                    val isJackpot = symbols.all { it == "77" }
                    
                    _uiState.update { state ->
                        state.copy(
                            slots = symbols,
                            isSpinning = false
                        )
                    }

                    delay(300)

                    // Update balance from backend response if available
                    val newBalance = result?.newBalance?.toInt()
                    if (winAmount > 0 || newBalance != null) {
                        _uiState.update { state ->
                            state.copy(
                                lastWinAmount = winAmount,
                                isJackpot = isJackpot,
                                showWinDialog = winAmount > 0,
                                userDiamonds = newBalance ?: (state.userDiamonds + winAmount)
                            )
                        }
                    }
                } else {
                    // Revert on error
                    _uiState.update { state ->
                        state.copy(
                            isSpinning = false,
                            userDiamonds = state.userDiamonds + cost,
                            error = "Failed to spin"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isSpinning = false,
                        userDiamonds = state.userDiamonds + cost,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun determineProResult(): SpinResult {
        val random = Random.nextFloat()
        
        return when {
            random < 0.001f -> { // Jackpot 0.1%
                SpinResult(listOf("77", "77", "77"))
            }
            random < 0.01f -> { // Triple match 1%
                val symbol = proSymbols.filter { it != "77" }.random()
                SpinResult(listOf(symbol, symbol, symbol))
            }
            random < 0.1f -> { // Double match 10%
                val symbol = proSymbols.random()
                val other = proSymbols.filter { it != symbol }.random()
                SpinResult(listOf(symbol, symbol, other).shuffled())
            }
            else -> { // Random
                SpinResult(List(3) { proSymbols.random() })
            }
        }
    }

    private fun calculateProWin(symbols: List<String>, bet: Int, multiplier: Int): Int {
        val baseBet = bet * multiplier
        
        return when {
            symbols.all { it == "77" } -> baseBet * 777 // Jackpot
            symbols.all { it == "💎" } -> baseBet * 100
            symbols.all { it == "👑" } -> baseBet * 77
            symbols.all { it == "🔥" } -> baseBet * 50
            symbols.all { it == "⭐" } -> baseBet * 40
            symbols.all { it == "💰" } -> baseBet * 30
            symbols.all { it == "🎰" } -> baseBet * 25
            symbols.distinct().size == 2 -> baseBet * 3 // Two of a kind
            else -> 0
        }
    }

    fun dismissWinDialog() {
        _uiState.update { state ->
            state.copy(
                showWinDialog = false,
                isJackpot = false
            )
        }
    }
}

data class Lucky77ProUiState(
    val isLoading: Boolean = false,
    val userDiamonds: Int = 0,
    val jackpotAmount: Long = 0,
    val slots: List<String> = listOf("💎", "💎", "💎"),
    val isSpinning: Boolean = false,
    val selectedBet: Int = 100,
    val selectedMultiplier: Int = 1,
    val lastWinAmount: Int = 0,
    val isJackpot: Boolean = false,
    val showWinDialog: Boolean = false,
    val recentWins: List<ProWinRecord> = emptyList(),
    val error: String? = null
) {
    val totalCost: Int
        get() = selectedBet * selectedMultiplier

    val canSpin: Boolean
        get() = userDiamonds >= totalCost && !isSpinning
}

private data class SpinResult(
    val symbols: List<String>
)

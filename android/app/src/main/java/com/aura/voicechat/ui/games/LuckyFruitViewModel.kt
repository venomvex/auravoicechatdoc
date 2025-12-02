package com.aura.voicechat.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.model.GameActionRequest
import com.aura.voicechat.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * Lucky Fruit ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Uses live backend API for game data, balance, and history.
 * Betting game with:
 * - 8 fruits with different multipliers
 * - Lucky and Super Lucky special bets
 * - Timer-based betting rounds
 * - Real-time chip betting
 */
@HiltViewModel
class LuckyFruitViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LuckyFruitUiState())
    val uiState: StateFlow<LuckyFruitUiState> = _uiState.asStateFlow()
    
    // Fruit items with weights (lower = rarer)
    private val fruitWeights = mapOf(
        "orange" to 20,      // x5, common
        "lemon" to 20,       // x5, common
        "grape" to 18,       // x5, common
        "cherry" to 18,      // x5, common
        "apple" to 12,       // x10
        "watermelon" to 7,   // x15
        "mango" to 4,        // x25
        "strawberry" to 1    // x45, rarest
    )
    
    init {
        loadGameData()
    }
    
    private fun loadGameData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load wallet balance
                val walletResponse = apiService.getWalletBalances()
                val balance = if (walletResponse.isSuccessful) {
                    walletResponse.body()?.coins ?: 0L
                } else 0L
                
                // Load game history for recent results
                val historyResponse = apiService.getGameHistory("luckyfruit", 1, 10)
                val recentResults = if (historyResponse.isSuccessful) {
                    historyResponse.body()?.records?.mapNotNull { it.result } ?: emptyList()
                } else emptyList()
                
                _uiState.value = LuckyFruitUiState(
                    isLoading = false,
                    balance = balance,
                    selectedChip = 5000,
                    todaysWin = 0,
                    recentResults = recentResults.ifEmpty { 
                        listOf("orange", "cherry", "grape", "watermelon", "mango", "lemon", "apple", "grape") 
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun selectChip(value: Long) {
        if (!_uiState.value.isSpinning) {
            _uiState.value = _uiState.value.copy(selectedChip = value)
        }
    }
    
    fun placeBet(itemId: String) {
        val state = _uiState.value
        if (state.isSpinning) return
        if (state.balance < state.selectedChip) return
        
        val currentBets = state.bets.toMutableMap()
        val currentAmount = currentBets[itemId] ?: 0
        currentBets[itemId] = currentAmount + state.selectedChip
        
        _uiState.value = state.copy(
            bets = currentBets,
            balance = state.balance - state.selectedChip
        )
        
        // Auto-spin after placing bet (with delay for more bets)
        startSpinCountdown()
    }
    
    fun placeLuckyBet() {
        // Lucky = bet on all 5x fruits
        val state = _uiState.value
        if (state.isSpinning) return
        
        val betPerFruit = state.selectedChip / 4
        if (state.balance < state.selectedChip) return
        
        val currentBets = state.bets.toMutableMap()
        listOf("orange", "lemon", "grape", "cherry").forEach { fruitId ->
            val currentAmount = currentBets[fruitId] ?: 0
            currentBets[fruitId] = currentAmount + betPerFruit
        }
        
        _uiState.value = state.copy(
            bets = currentBets,
            balance = state.balance - state.selectedChip
        )
        
        startSpinCountdown()
    }
    
    fun placeSuperLuckyBet() {
        // Super Lucky = bet on all high-value fruits (x10+)
        val state = _uiState.value
        if (state.isSpinning) return
        
        val betPerFruit = state.selectedChip / 4
        if (state.balance < state.selectedChip) return
        
        val currentBets = state.bets.toMutableMap()
        listOf("apple", "watermelon", "mango", "strawberry").forEach { fruitId ->
            val currentAmount = currentBets[fruitId] ?: 0
            currentBets[fruitId] = currentAmount + betPerFruit
        }
        
        _uiState.value = state.copy(
            bets = currentBets,
            balance = state.balance - state.selectedChip
        )
        
        startSpinCountdown()
    }
    
    private var spinCountdownJob: kotlinx.coroutines.Job? = null
    
    private fun startSpinCountdown() {
        // Cancel existing countdown
        spinCountdownJob?.cancel()
        
        // Start new countdown (5 seconds to place more bets)
        spinCountdownJob = viewModelScope.launch {
            delay(5000)
            spin()
        }
    }
    
    private fun spin() {
        val state = _uiState.value
        if (state.isSpinning) return
        if (state.bets.isEmpty()) return
        
        viewModelScope.launch {
            _uiState.value = state.copy(isSpinning = true, winningItem = null)
            
            try {
                // Call backend to perform game action
                val response = apiService.gameAction(
                    "luckyfruit",
                    GameActionRequest(
                        action = "spin",
                        bet = state.bets.values.sum(),
                        bets = state.bets
                    )
                )
                
                // Spin animation time
                delay(2000)
                
                if (response.isSuccessful) {
                    val gameResult = response.body()
                    val winner = gameResult?.result ?: getWeightedRandomFruit()
                    val winAmount = gameResult?.winAmount ?: 0L
                    
                    val newTodaysWin = state.todaysWin + winAmount
                    val newResults = listOf(winner) + state.recentResults.take(9)
                    
                    _uiState.value = state.copy(
                        isSpinning = false,
                        winningItem = winner,
                        lastWin = winAmount,
                        todaysWin = newTodaysWin,
                        balance = state.balance + winAmount,
                        bets = emptyMap(), // Clear bets
                        recentResults = newResults
                    )
                    
                    // Reset winning highlight after delay
                    delay(3000)
                    _uiState.value = _uiState.value.copy(winningItem = null)
                } else {
                    // Revert on error
                    val totalBet = state.bets.values.sum()
                    _uiState.value = state.copy(
                        isSpinning = false,
                        balance = state.balance + totalBet,
                        bets = emptyMap(),
                        error = "Failed to spin"
                    )
                }
            } catch (e: Exception) {
                val totalBet = state.bets.values.sum()
                _uiState.value = state.copy(
                    isSpinning = false,
                    balance = state.balance + totalBet,
                    bets = emptyMap(),
                    error = e.message
                )
            }
        }
    }
    
    private fun getWeightedRandomFruit(): String {
        val totalWeight = fruitWeights.values.sum()
        var random = Random.nextInt(totalWeight)
        
        for ((fruit, weight) in fruitWeights) {
            random -= weight
            if (random < 0) return fruit
        }
        return fruitWeights.keys.first()
    }
    
    fun showRankings() {
        // Navigate to rankings
    }
}

data class LuckyFruitUiState(
    val isLoading: Boolean = false,
    val isSpinning: Boolean = false,
    val balance: Long = 0,
    val selectedChip: Long = 5000,
    val bets: Map<String, Long> = emptyMap(),
    val winningItem: String? = null,
    val lastWin: Long = 0,
    val todaysWin: Long = 0,
    val recentResults: List<String> = emptyList(),
    val error: String? = null
)

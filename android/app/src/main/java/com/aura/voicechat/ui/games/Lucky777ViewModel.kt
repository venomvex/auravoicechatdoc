package com.aura.voicechat.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.model.GameActionRequest
import com.aura.voicechat.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * Lucky 777 Pro ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Uses live backend API for game data, balance, and history.
 * Classic slot machine with:
 * - 3x3 grid (9 symbols)
 * - 5 paylines
 * - Auto-spin feature
 * - Room integration
 */
@HiltViewModel
class Lucky777ViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(Lucky777UiState())
    val uiState: StateFlow<Lucky777UiState> = _uiState.asStateFlow()
    
    // Symbols with their weights (higher = more common)
    private val symbolWeights = listOf(
        "🍒" to 25,    // Most common
        "🥭" to 20,
        "🍇" to 18,
        "🍉" to 15,
        "💎" to 10,
        "🔔" to 8,
        "7️⃣" to 4      // Rarest
    )
    
    // Payout table (for 3 matching on a line)
    private val payouts = mapOf(
        "7️⃣" to 1_000_000_000L,
        "🔔" to 300_000_000L,
        "💎" to 100_000_000L,
        "🍉" to 50_000_000L,
        "🍇" to 30_000_000L,
        "🥭" to 15_000_000L,
        "🍒" to 5_000_000L
    )
    
    // Bet levels
    private val betLevels = listOf(1000L, 5000L, 10000L, 50000L, 100000L, 500000L)
    private var currentBetIndex = 0
    
    private var autoSpinJob: Job? = null
    
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
                
                // Load jackpot
                val jackpotResponse = apiService.getJackpot("lucky777")
                val jackpotAmount = if (jackpotResponse.isSuccessful) {
                    jackpotResponse.body()?.amount ?: 125000000L
                } else 125000000L
                
                // Load game history for top winners
                val historyResponse = apiService.getGameHistory("lucky777", 1, 10)
                val topWinners = if (historyResponse.isSuccessful) {
                    historyResponse.body()?.records?.mapIndexed { index, record ->
                        Lucky777Winner(
                            id = record.id ?: (index + 1).toString(),
                            name = record.userName ?: "Player",
                            avatarUrl = record.avatarUrl,
                            winAmount = record.winAmount ?: 0L
                        )
                    }?.take(3) ?: emptyList()
                } else emptyList()
                
                // Initial 3x3 grid of symbols
                val initialReels = List(9) { getWeightedRandomSymbol() }
                
                _uiState.value = Lucky777UiState(
                    isLoading = false,
                    balance = balance,
                    jackpotAmount = jackpotAmount,
                    currentBet = betLevels[currentBetIndex],
                    reels = initialReels,
                    todaysWin = 0,
                    topWinners = topWinners,
                    onlineCount = 536
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun increaseBet() {
        if (_uiState.value.isSpinning) return
        if (currentBetIndex < betLevels.size - 1) {
            currentBetIndex++
            _uiState.value = _uiState.value.copy(currentBet = betLevels[currentBetIndex])
        }
    }
    
    fun decreaseBet() {
        if (_uiState.value.isSpinning) return
        if (currentBetIndex > 0) {
            currentBetIndex--
            _uiState.value = _uiState.value.copy(currentBet = betLevels[currentBetIndex])
        }
    }
    
    fun setBet(bet: Long) {
        if (_uiState.value.isSpinning) return
        val index = betLevels.indexOf(bet)
        if (index >= 0) {
            currentBetIndex = index
            _uiState.value = _uiState.value.copy(currentBet = bet)
        }
    }
    
    fun toggleAutoSpin() {
        if (_uiState.value.isAutoSpin) {
            // Stop auto spin
            autoSpinJob?.cancel()
            _uiState.value = _uiState.value.copy(isAutoSpin = false)
        } else {
            // Start auto spin
            _uiState.value = _uiState.value.copy(isAutoSpin = true)
            autoSpinJob = viewModelScope.launch {
                while (_uiState.value.isAutoSpin && _uiState.value.balance >= _uiState.value.currentBet) {
                    if (!_uiState.value.isSpinning) {
                        spin()
                        delay(3000) // Wait between spins
                    }
                    delay(100)
                }
                _uiState.value = _uiState.value.copy(isAutoSpin = false)
            }
        }
    }
    
    fun spin() {
        if (_uiState.value.isSpinning) return
        if (_uiState.value.balance < _uiState.value.currentBet) return
        
        viewModelScope.launch {
            // Deduct bet
            _uiState.value = _uiState.value.copy(
                isSpinning = true,
                balance = _uiState.value.balance - _uiState.value.currentBet,
                lastWin = 0,
                winLines = emptyList()
            )
            
            try {
                // Call backend to perform game action
                val response = apiService.gameAction(
                    "lucky777",
                    GameActionRequest(
                        action = "spin",
                        bet = _uiState.value.currentBet
                    )
                )
                
                // Simulate spin animation time
                delay(2000)
                
                if (response.isSuccessful) {
                    val gameResult = response.body()
                    val result = gameResult?.symbols?.take(9) ?: List(9) { getWeightedRandomSymbol() }
                    val winAmount = gameResult?.winAmount ?: 0L
                    val winLines = gameResult?.winLines ?: emptyList()
                    
                    val newTodaysWin = _uiState.value.todaysWin + winAmount
                    
                    _uiState.value = _uiState.value.copy(
                        isSpinning = false,
                        reels = result,
                        lastWin = winAmount,
                        todaysWin = newTodaysWin,
                        balance = _uiState.value.balance + winAmount,
                        winLines = winLines.map { it.toInt() }
                    )
                } else {
                    // Revert on error
                    _uiState.value = _uiState.value.copy(
                        isSpinning = false,
                        balance = _uiState.value.balance + _uiState.value.currentBet,
                        error = "Failed to spin"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSpinning = false,
                    balance = _uiState.value.balance + _uiState.value.currentBet,
                    error = e.message
                )
            }
        }
    }
    
    fun showRankings() {
        // Navigate to rankings screen
    }
    
    private fun getWeightedRandomSymbol(): String {
        val totalWeight = symbolWeights.sumOf { it.second }
        var random = Random.nextInt(totalWeight)
        
        for ((symbol, weight) in symbolWeights) {
            random -= weight
            if (random < 0) return symbol
        }
        return symbolWeights.first().first
    }
    
    private fun calculateWinnings(grid: List<String>): Pair<Long, List<Int>> {
        val bet = _uiState.value.currentBet
        var totalWin = 0L
        val winningLines = mutableListOf<Int>()
        
        // 5 paylines in a 3x3 grid:
        // Line 0: Top row (0, 1, 2)
        // Line 1: Middle row (3, 4, 5)
        // Line 2: Bottom row (6, 7, 8)
        // Line 3: Diagonal top-left to bottom-right (0, 4, 8)
        // Line 4: Diagonal top-right to bottom-left (2, 4, 6)
        
        val lines = listOf(
            listOf(0, 1, 2),   // Top row
            listOf(3, 4, 5),   // Middle row
            listOf(6, 7, 8),   // Bottom row
            listOf(0, 4, 8),   // Diagonal \
            listOf(2, 4, 6)    // Diagonal /
        )
        
        lines.forEachIndexed { lineIndex, indices ->
            val symbols = indices.map { grid[it] }
            if (symbols[0] == symbols[1] && symbols[1] == symbols[2]) {
                // Three of a kind!
                val payout = payouts[symbols[0]] ?: 0L
                val lineWin = (payout * bet) / 1000 // Scale payout by bet
                totalWin += lineWin
                winningLines.add(lineIndex)
            }
        }
        
        return Pair(totalWin, winningLines)
    }
}

data class Lucky777UiState(
    val isLoading: Boolean = false,
    val isSpinning: Boolean = false,
    val isAutoSpin: Boolean = false,
    val balance: Long = 0,
    val jackpotAmount: Long = 0,
    val currentBet: Long = 1000,
    val reels: List<String> = List(9) { "❓" },
    val lastWin: Long = 0,
    val todaysWin: Long = 0,
    val winLines: List<Int> = emptyList(),
    val topWinners: List<Lucky777Winner> = emptyList(),
    val onlineCount: Int = 0,
    val error: String? = null
)

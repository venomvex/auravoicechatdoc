package com.aura.voicechat.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * Classic slot machine with:
 * - 3x3 grid (9 symbols)
 * - 5 paylines
 * - Auto-spin feature
 * - Room integration
 */
@HiltViewModel
class Lucky777ViewModel @Inject constructor() : ViewModel() {
    
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
        // Initial 3x3 grid of symbols
        val initialReels = List(9) { getWeightedRandomSymbol() }
        
        _uiState.value = Lucky777UiState(
            balance = 500000,
            jackpotAmount = 125000000,
            currentBet = betLevels[currentBetIndex],
            reels = initialReels,
            todaysWin = 0,
            topWinners = listOf(
                Lucky777Winner("1", "JOJO", null, 2842910),
                Lucky777Winner("2", "HUNTER", null, 1500000),
                Lucky777Winner("3", "GANGSTER", null, 900000)
            ),
            onlineCount = 536
        )
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
            
            // Simulate spin animation time
            delay(2000)
            
            // Generate 3x3 grid results
            val result = List(9) { getWeightedRandomSymbol() }
            
            // Check win lines (5 lines)
            val (winAmount, winLines) = calculateWinnings(result)
            
            val newTodaysWin = _uiState.value.todaysWin + winAmount
            
            _uiState.value = _uiState.value.copy(
                isSpinning = false,
                reels = result,
                lastWin = winAmount,
                todaysWin = newTodaysWin,
                balance = _uiState.value.balance + winAmount,
                winLines = winLines
            )
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
    val onlineCount: Int = 0
)

package com.aura.voicechat.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * Lucky 777 ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class Lucky777ViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(Lucky777UiState())
    val uiState: StateFlow<Lucky777UiState> = _uiState.asStateFlow()
    
    private val symbols = listOf("7️⃣", "🍒", "🍋", "🍊", "🍇", "💎", "⭐", "🔔")
    private val winMultipliers = mapOf(
        "7️⃣7️⃣7️⃣" to 100,
        "💎💎💎" to 50,
        "⭐⭐⭐" to 25,
        "🔔🔔🔔" to 15,
        "🍇🍇🍇" to 10,
        "🍊🍊🍊" to 8,
        "🍋🍋🍋" to 5,
        "🍒🍒🍒" to 3
    )
    
    init {
        loadGameData()
    }
    
    private fun loadGameData() {
        _uiState.value = Lucky777UiState(
            balance = 500000,
            jackpotAmount = 125000000,
            currentBet = 1000,
            availableBets = listOf(1000, 5000, 10000, 50000),
            reels = listOf("7️⃣", "🍒", "💎")
        )
    }
    
    fun setBet(bet: Long) {
        _uiState.value = _uiState.value.copy(currentBet = bet)
    }
    
    fun spin() {
        if (_uiState.value.isSpinning) return
        if (_uiState.value.balance < _uiState.value.currentBet) return
        
        viewModelScope.launch {
            // Deduct bet
            _uiState.value = _uiState.value.copy(
                isSpinning = true,
                balance = _uiState.value.balance - _uiState.value.currentBet,
                lastWin = 0
            )
            
            // Simulate spin
            delay(2000)
            
            // Generate results with weighted randomness
            val result = generateSpinResult()
            val resultKey = result.joinToString("")
            val multiplier = winMultipliers[resultKey] ?: 0
            val winAmount = _uiState.value.currentBet * multiplier
            
            _uiState.value = _uiState.value.copy(
                isSpinning = false,
                reels = result,
                lastWin = winAmount,
                balance = _uiState.value.balance + winAmount
            )
        }
    }
    
    private fun generateSpinResult(): List<String> {
        // 5% chance of triple 7
        // 10% chance of any triple
        // 85% chance of random
        val chance = Random.nextInt(100)
        return when {
            chance < 1 -> listOf("7️⃣", "7️⃣", "7️⃣") // Jackpot!
            chance < 3 -> listOf("💎", "💎", "💎")
            chance < 6 -> listOf("⭐", "⭐", "⭐")
            chance < 10 -> {
                val sym = symbols.filter { it != "7️⃣" && it != "💎" && it != "⭐" }.random()
                listOf(sym, sym, sym)
            }
            else -> List(3) { symbols.random() }
        }
    }
}

data class Lucky777UiState(
    val isLoading: Boolean = false,
    val isSpinning: Boolean = false,
    val balance: Long = 0,
    val jackpotAmount: Long = 0,
    val currentBet: Long = 1000,
    val availableBets: List<Long> = emptyList(),
    val reels: List<String> = listOf("❓", "❓", "❓"),
    val lastWin: Long = 0
)

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
 * Lucky Fruit ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class LuckyFruitViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(LuckyFruitUiState())
    val uiState: StateFlow<LuckyFruitUiState> = _uiState.asStateFlow()
    
    private val fruits = listOf(
        "🍇" to 5,
        "🍊" to 8,
        "🍋" to 10,
        "🍒" to 15,
        "⭐" to 25
    )
    
    init {
        generateNewGrid()
    }
    
    private fun generateNewGrid() {
        val tiles = mutableListOf<FruitTile>()
        repeat(25) { index ->
            val (fruit, value) = fruits.random()
            tiles.add(FruitTile(index, fruit, value))
        }
        
        _uiState.value = LuckyFruitUiState(
            balance = 500000,
            currentBet = 1000,
            tiles = tiles
        )
    }
    
    fun setBet(bet: Long) {
        if (!_uiState.value.isPlaying) {
            _uiState.value = _uiState.value.copy(currentBet = bet)
        }
    }
    
    fun newGame() {
        if (_uiState.value.isPlaying) return
        if (_uiState.value.balance < _uiState.value.currentBet) return
        
        viewModelScope.launch {
            // Deduct bet and reset
            val newTiles = mutableListOf<FruitTile>()
            repeat(25) { index ->
                val (fruit, value) = fruits.random()
                newTiles.add(FruitTile(index, fruit, value))
            }
            
            _uiState.value = _uiState.value.copy(
                isPlaying = true,
                balance = _uiState.value.balance - _uiState.value.currentBet,
                tiles = newTiles,
                selectedTiles = emptyList(),
                matchedTiles = emptySet(),
                lastWin = 0,
                comboCount = 0,
                currentMultiplier = 1
            )
        }
    }
    
    fun selectTile(index: Int) {
        val state = _uiState.value
        if (!state.isPlaying) return
        if (index in state.matchedTiles) return
        if (index in state.selectedTiles) return
        
        viewModelScope.launch {
            val newSelected = state.selectedTiles + index
            val revealedTiles = state.tiles.mapIndexed { i, tile ->
                if (i == index) tile.copy(isRevealed = true) else tile
            }
            
            _uiState.value = state.copy(
                tiles = revealedTiles,
                selectedTiles = newSelected
            )
            
            // Check for match when 3 are selected
            if (newSelected.size == 3) {
                delay(300)
                checkMatch(newSelected)
            }
        }
    }
    
    private fun checkMatch(selected: List<Int>) {
        val state = _uiState.value
        val selectedFruits = selected.map { state.tiles[it].fruit }
        
        if (selectedFruits.distinct().size == 1) {
            // Match!
            val matchedFruit = selectedFruits.first()
            val value = fruits.first { it.first == matchedFruit }.second
            val winAmount = state.currentBet * value * state.currentMultiplier
            val newCombo = state.comboCount + 1
            val newMultiplier = minOf(state.currentMultiplier + 1, 5)
            
            _uiState.value = state.copy(
                balance = state.balance + winAmount,
                matchedTiles = state.matchedTiles + selected.toSet(),
                selectedTiles = emptyList(),
                lastWin = winAmount,
                comboCount = newCombo,
                currentMultiplier = newMultiplier
            )
            
            // Check if game is over (all matched or no more moves)
            viewModelScope.launch {
                delay(500)
                if (_uiState.value.matchedTiles.size >= 24) {
                    // Game over - bonus!
                    _uiState.value = _uiState.value.copy(isPlaying = false)
                }
            }
        } else {
            // No match - reset selection
            viewModelScope.launch {
                delay(500)
                val hiddenTiles = state.tiles.mapIndexed { i, tile ->
                    if (i in selected && i !in state.matchedTiles) tile.copy(isRevealed = false)
                    else tile
                }
                _uiState.value = state.copy(
                    tiles = hiddenTiles,
                    selectedTiles = emptyList(),
                    currentMultiplier = 1,
                    comboCount = 0
                )
            }
        }
    }
}

data class LuckyFruitUiState(
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val balance: Long = 0,
    val currentBet: Long = 1000,
    val tiles: List<FruitTile> = emptyList(),
    val selectedTiles: List<Int> = emptyList(),
    val matchedTiles: Set<Int> = emptySet(),
    val lastWin: Long = 0,
    val comboCount: Int = 0,
    val currentMultiplier: Int = 1
)

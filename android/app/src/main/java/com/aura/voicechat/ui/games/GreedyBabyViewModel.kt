package com.aura.voicechat.ui.games

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * ViewModel for Greedy Baby Game (Live API Connected)
 * Circular betting wheel game with live multiplayer betting, timer-based rounds
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class GreedyBabyViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "GreedyBabyViewModel"
    }

    private val _uiState = MutableStateFlow(GreedyBabyUiState())
    val uiState: StateFlow<GreedyBabyUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var roundNumber = 0

    init {
        initializeGame()
        loadUserCoins()
        loadGameHistory()
        startGameLoop()
    }

    private fun initializeGame() {
        // Initialize wheel items (8 items arranged clockwise from top)
        val wheelItems = listOf(
            WheelItem("apple", "Apple", "🍎", 5, Color(0xFFFFD700), true),
            WheelItem("lemon", "Lemon", "🍋", 5, Color(0xFFFFD700), true),
            WheelItem("strawberry", "Strawberry", "🍓", 5, Color(0xFFFFD700), true),
            WheelItem("mango", "Mango", "🥭", 5, Color(0xFFFFD700), true),
            WheelItem("fish", "Fish", "🐟", 10, Color(0xFFFFD700), false),
            WheelItem("burger", "Burger", "🍔", 15, Color(0xFFFFD700), false),
            WheelItem("pizza", "Pizza", "🍕", 25, Color(0xFFFFD700), false),
            WheelItem("chicken", "Chicken", "🍗", 45, Color(0xFFFFD700), false)
        )

        // Initialize betting chips
        val chips = listOf(
            BettingChip(100L, "100", Color(0xFF4CAF50), Color(0xFF2E7D32)),
            BettingChip(1_000L, "1K", Color(0xFF2196F3), Color(0xFF1565C0)),
            BettingChip(5_000L, "5K", Color(0xFF9C27B0), Color(0xFF6A1B9A)),
            BettingChip(10_000L, "10K", Color(0xFFFF9800), Color(0xFFE65100)),
            BettingChip(50_000L, "50K", Color(0xFFE91E63), Color(0xFFC2185B)),
            BettingChip(100_000L, "100K", Color(0xFF00BCD4), Color(0xFF00838F)),
            BettingChip(1_000_000L, "1M", Color(0xFFFFD700), Color(0xFFFF8F00)),
            BettingChip(2_000_000L, "2M", Color(0xFFFF5722), Color(0xFFBF360C)),
            BettingChip(5_000_000L, "5M", Color(0xFF607D8B), Color(0xFF37474F)),
            BettingChip(10_000_000L, "10M", Color(0xFF9E9E9E), Color(0xFF616161)),
            BettingChip(50_000_000L, "50M", Color(0xFF795548), Color(0xFF4E342E))
        )

        _uiState.update { state ->
            state.copy(
                wheelItems = wheelItems,
                chips = chips,
                selectedChip = chips.first(),
                timerSeconds = 15,
                gamePhase = GamePhase.BETTING
            )
        }
    }
    
    private fun loadUserCoins() {
        viewModelScope.launch {
            try {
                val response = apiService.getWalletBalances()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { state ->
                        state.copy(userCoins = response.body()!!.coins)
                    }
                    Log.d(TAG, "Loaded user coins: ${response.body()!!.coins}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user coins", e)
            }
        }
    }
    
    private fun loadGameHistory() {
        viewModelScope.launch {
            try {
                val response = apiService.getGameHistory("greedy_baby", 1, 10)
                if (response.isSuccessful && response.body() != null) {
                    val records = response.body()!!.records.map { dto ->
                        WinningRecord(
                            roundId = dto.id ?: "",
                            item = _uiState.value.wheelItems.find { it.id == dto.result } 
                                ?: _uiState.value.wheelItems.first(),
                            totalBet = dto.totalValue ?: 0L,
                            won = (dto.winAmount ?: 0L) > 0L,
                            payout = dto.winAmount ?: 0L,
                            timestamp = dto.timestamp ?: System.currentTimeMillis()
                        )
                    }
                    _uiState.update { state ->
                        state.copy(winningRecords = records)
                    }
                    Log.d(TAG, "Loaded ${records.size} game history records")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading game history", e)
            }
        }
    }

    private fun startGameLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            // Game loop runs continuously while ViewModel is active
            // Loop is automatically cancelled when ViewModel is cleared (see onCleared())
            // In production, this would sync with server-side game state via WebSocket
            while (isActive) {
                // Betting phase
                _uiState.update { it.copy(
                    gamePhase = GamePhase.BETTING,
                    timerSeconds = 15,
                    winningItem = null,
                    isSpinning = false
                )}
                
                // Countdown betting time
                for (i in 15 downTo 0) {
                    if (!isActive) return@launch
                    _uiState.update { it.copy(timerSeconds = i) }
                    delay(1000)
                }
                
                // Show time phase (spinning)
                _uiState.update { it.copy(
                    gamePhase = GamePhase.SHOW_TIME,
                    timerSeconds = 7,
                    isSpinning = true
                )}
                
                // Spin animation time
                for (i in 7 downTo 0) {
                    if (!isActive) return@launch
                    _uiState.update { it.copy(timerSeconds = i) }
                    delay(1000)
                }
                
                // Determine winner
                val winner = determineWinner()
                val specialResult = determineSpecialResult()
                
                // Result phase
                _uiState.update { it.copy(
                    gamePhase = GamePhase.RESULT,
                    winningItem = winner,
                    isSpinning = false,
                    timerSeconds = 5
                )}
                
                // Calculate winnings
                processRoundResults(winner, specialResult)
                
                // Show result popup
                _uiState.update { it.copy(showResultPopup = true) }
                
                // Wait for result display
                delay(5000)
                
                // Update result history
                updateResultHistory(winner, specialResult)
                
                // Clear bets for next round
                clearBets()
                
                roundNumber++
            }
        }
    }

    private fun determineWinner(): WheelItem {
        val currentState = _uiState.value
        val items = currentState.wheelItems
        
        // Probability-based selection (house edge considered)
        // Fruits (5x): 17% each = 68% total
        // Fish (10x): 12%
        // Burger (15x): 8%
        // Pizza (25x): 5%
        // Chicken (45x): 2%
        // Note: Adjusted based on house edge configuration
        
        val probabilities = mapOf(
            "apple" to 17,
            "lemon" to 17,
            "strawberry" to 17,
            "mango" to 17,
            "fish" to 12,
            "burger" to 8,
            "pizza" to 5,
            "chicken" to 2
        )
        
        val random = Random.nextInt(100)
        var cumulative = 0
        
        for ((itemId, probability) in probabilities) {
            cumulative += probability
            if (random < cumulative) {
                return items.find { it.id == itemId } ?: items.first()
            }
        }
        
        return items.first()
    }

    private fun determineSpecialResult(): SpecialResult? {
        // 3% chance for Fruit Basket
        // 2% chance for Full Pizza
        val random = Random.nextInt(100)
        return when {
            random < 3 -> SpecialResult.FRUIT_BASKET
            random < 5 -> SpecialResult.FULL_PIZZA
            else -> null
        }
    }

    private fun processRoundResults(winner: WheelItem, specialResult: SpecialResult?) {
        val currentState = _uiState.value
        var totalWinnings = 0L
        val topWinners = mutableListOf<TopWinner>()
        
        // Process user's bets
        currentState.betsOnItems.forEach { (itemId, bets) ->
            bets.forEach { bet ->
                val item = currentState.wheelItems.find { it.id == itemId }
                if (item != null) {
                    val won = when {
                        specialResult == SpecialResult.FRUIT_BASKET && item.isFruit -> {
                            // Check if user bet on all 4 fruits
                            val fruitItems = currentState.wheelItems.filter { it.isFruit }
                            fruitItems.all { fruitItem -> 
                                currentState.betsOnItems[fruitItem.id]?.isNotEmpty() == true 
                            }
                        }
                        specialResult == SpecialResult.FULL_PIZZA && !item.isFruit -> {
                            // Check if user bet on all 4 non-fruits
                            val nonFruitItems = currentState.wheelItems.filter { !it.isFruit }
                            nonFruitItems.all { nonFruitItem -> 
                                currentState.betsOnItems[nonFruitItem.id]?.isNotEmpty() == true 
                            }
                        }
                        else -> itemId == winner.id
                    }
                    
                    if (won) {
                        totalWinnings += bet.totalBet * item.multiplier
                    }
                }
            }
        }
        
        // Update user coins
        _uiState.update { state ->
            state.copy(
                userCoins = state.userCoins + totalWinnings,
                roundWinnings = totalWinnings,
                todaysWin = state.todaysWin + totalWinnings,
                topWinners = generateMockTopWinners(totalWinnings) // In production, get from server
            )
        }
        
        // Add to winning records
        currentState.betsOnItems.forEach { (itemId, bets) ->
            val item = currentState.wheelItems.find { it.id == itemId } ?: return@forEach
            val totalBet = bets.sumOf { it.totalBet }
            val won = itemId == winner.id
            val payout = if (won) totalBet * item.multiplier else 0L
            
            if (totalBet > 0) {
                val record = WinningRecord(
                    roundId = "round_$roundNumber",
                    item = item,
                    totalBet = totalBet,
                    won = won,
                    payout = payout,
                    timestamp = System.currentTimeMillis()
                )
                
                _uiState.update { state ->
                    state.copy(
                        winningRecords = listOf(record) + state.winningRecords.take(99)
                    )
                }
            }
        }
    }

    /**
     * Generate top winners list for the result popup.
     * In a live multiplayer environment, this data would come from:
     * - WebSocket event: "greedy_baby:result" with topWinners array
     * - Or fetched from GET /games/greedy-baby/rankings/daily
     * 
     * This mock implementation is for offline/development testing.
     */
    private fun generateMockTopWinners(userWinnings: Long): List<TopWinner> {
        return listOf(
            TopWinner("user_1", "Player1", Random.nextLong(1_000_000, 5_000_000)),
            TopWinner("user_2", "Player2", Random.nextLong(500_000, 2_000_000)),
            TopWinner("user_3", "Player3", Random.nextLong(100_000, 1_000_000))
        ).sortedByDescending { it.winnings }
    }

    private fun updateResultHistory(winner: WheelItem, specialResult: SpecialResult?) {
        val result = GameResult(
            roundId = "round_$roundNumber",
            winningItem = winner,
            specialResult = specialResult,
            timestamp = System.currentTimeMillis()
        )
        
        _uiState.update { state ->
            state.copy(
                resultHistory = listOf(result) + state.resultHistory.take(9)
            )
        }
    }

    private fun clearBets() {
        _uiState.update { state ->
            state.copy(
                betsOnItems = emptyMap(),
                showResultPopup = false
            )
        }
    }

    fun selectChip(chip: BettingChip) {
        _uiState.update { state ->
            state.copy(selectedChip = chip)
        }
    }

    fun onItemClicked(item: WheelItem) {
        val currentState = _uiState.value
        
        // Only allow betting during betting phase
        if (currentState.gamePhase != GamePhase.BETTING) return
        
        // Check if user has enough coins
        val selectedChip = currentState.selectedChip ?: return
        if (currentState.userCoins < selectedChip.value) return
        
        // Place bet
        val existingBets = currentState.betsOnItems[item.id] ?: emptyList()
        val newBet = PlacedBet(
            itemId = item.id,
            chipValue = selectedChip.value,
            chipCount = 1,
            totalBet = selectedChip.value
        )
        
        // Combine with existing bets of same chip value
        val updatedBets = existingBets.toMutableList()
        val existingBetIndex = updatedBets.indexOfFirst { it.chipValue == selectedChip.value }
        if (existingBetIndex >= 0) {
            val existing = updatedBets[existingBetIndex]
            updatedBets[existingBetIndex] = existing.copy(
                chipCount = existing.chipCount + 1,
                totalBet = existing.totalBet + selectedChip.value
            )
        } else {
            updatedBets.add(newBet)
        }
        
        _uiState.update { state ->
            state.copy(
                betsOnItems = state.betsOnItems + (item.id to updatedBets),
                userCoins = state.userCoins - selectedChip.value,
                selectedItem = item
            )
        }
    }

    fun toggleSound() {
        _uiState.update { state ->
            state.copy(isSoundEnabled = !state.isSoundEnabled)
        }
    }

    fun showHelp() {
        _uiState.update { state ->
            state.copy(showHelpDialog = true)
        }
    }

    fun dismissHelp() {
        _uiState.update { state ->
            state.copy(showHelpDialog = false)
        }
    }

    fun showRankings() {
        _uiState.update { state ->
            state.copy(showRankingsDialog = true)
        }
    }

    fun dismissRankings() {
        _uiState.update { state ->
            state.copy(showRankingsDialog = false)
        }
    }

    fun showWinningRecords() {
        _uiState.update { state ->
            state.copy(showWinningRecordsDialog = true)
        }
    }

    fun dismissWinningRecords() {
        _uiState.update { state ->
            state.copy(showWinningRecordsDialog = false)
        }
    }

    fun dismissResultPopup() {
        _uiState.update { state ->
            state.copy(showResultPopup = false)
        }
    }

    fun toggleFruitBasket() {
        val currentState = _uiState.value
        if (currentState.gamePhase != GamePhase.BETTING) return
        
        val selectedChip = currentState.selectedChip ?: return
        val fruitItems = currentState.wheelItems.filter { it.isFruit }
        val totalCost = selectedChip.value * fruitItems.size
        
        if (currentState.userCoins < totalCost) return
        
        // Toggle - if all fruits already have bets with this chip, remove them; otherwise add
        val allFruitsHaveBets = fruitItems.all { fruit ->
            currentState.betsOnItems[fruit.id]?.any { it.chipValue == selectedChip.value } == true
        }
        
        if (allFruitsHaveBets) {
            _uiState.update { state ->
                state.copy(fruitBasketActive = false)
            }
        } else {
            // Place bet on all fruits
            fruitItems.forEach { fruit ->
                onItemClicked(fruit)
            }
            _uiState.update { state ->
                state.copy(fruitBasketActive = true)
            }
        }
    }

    fun toggleFullPizza() {
        val currentState = _uiState.value
        if (currentState.gamePhase != GamePhase.BETTING) return
        
        val selectedChip = currentState.selectedChip ?: return
        val nonFruitItems = currentState.wheelItems.filter { !it.isFruit }
        val totalCost = selectedChip.value * nonFruitItems.size
        
        if (currentState.userCoins < totalCost) return
        
        // Toggle - if all non-fruits already have bets with this chip, remove them; otherwise add
        val allNonFruitsHaveBets = nonFruitItems.all { item ->
            currentState.betsOnItems[item.id]?.any { it.chipValue == selectedChip.value } == true
        }
        
        if (allNonFruitsHaveBets) {
            _uiState.update { state ->
                state.copy(fullPizzaActive = false)
            }
        } else {
            // Place bet on all non-fruits
            nonFruitItems.forEach { item ->
                onItemClicked(item)
            }
            _uiState.update { state ->
                state.copy(fullPizzaActive = true)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

data class GreedyBabyUiState(
    val isLoading: Boolean = false,
    val userCoins: Long = 0L,
    val isSoundEnabled: Boolean = true,
    
    // Game state
    val wheelItems: List<WheelItem> = emptyList(),
    val chips: List<BettingChip> = emptyList(),
    val selectedChip: BettingChip? = null,
    val selectedItem: WheelItem? = null,
    
    // Betting state
    val betsOnItems: Map<String, List<PlacedBet>> = emptyMap(),
    val otherPlayersBets: Map<String, List<PlacedBet>> = emptyMap(),
    
    // Timer and phase
    val timerSeconds: Int = 15,
    val gamePhase: GamePhase = GamePhase.BETTING,
    val isSpinning: Boolean = false,
    val winningItem: WheelItem? = null,
    
    // Combo features
    val fruitBasketActive: Boolean = false,
    val fullPizzaActive: Boolean = false,
    
    // Results
    val todaysWin: Long = 0L,
    val roundWinnings: Long = 0L,
    val resultHistory: List<GameResult> = emptyList(),
    val topWinners: List<TopWinner> = emptyList(),
    
    // Rankings
    val dailyRankings: List<RankingEntry> = emptyList(),
    val weeklyRankings: List<RankingEntry> = emptyList(),
    
    // Winning records
    val winningRecords: List<WinningRecord> = emptyList(),
    
    // Dialog states
    val showResultPopup: Boolean = false,
    val showHelpDialog: Boolean = false,
    val showRankingsDialog: Boolean = false,
    val showWinningRecordsDialog: Boolean = false,
    
    val error: String? = null
)

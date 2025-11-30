package com.aura.voicechat.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.voicechat.ui.theme.*

/**
 * Lucky Fruit Betting Game
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * 3x4 grid betting game with:
 * - 8 fruits with different multipliers
 * - Lucky and Super Lucky special cells
 * - Real-time chip betting
 * - Room integration with live players
 * - Timer-based rounds
 */

// Fruit items for Lucky Fruit game
data class LuckyFruitItem(
    val id: String,
    val emoji: String,
    val name: String,
    val multiplier: Int,
    val color: Color
)

val LUCKY_FRUIT_ITEMS = listOf(
    LuckyFruitItem("orange", "🍊", "Orange", 5, Color(0xFFFF8C00)),
    LuckyFruitItem("lemon", "🍋", "Lemon", 5, Color(0xFFFFD700)),
    LuckyFruitItem("grape", "🍇", "Grapes", 5, Color(0xFF9370DB)),
    LuckyFruitItem("cherry", "🍒", "Cherry", 5, Color(0xFFDC143C)),
    LuckyFruitItem("strawberry", "🍓", "Strawberry", 45, Color(0xFFFF4500)),
    LuckyFruitItem("mango", "🥭", "Mango", 25, Color(0xFFFFD700)),
    LuckyFruitItem("watermelon", "🍉", "Watermelon", 15, Color(0xFF32CD32)),
    LuckyFruitItem("apple", "🍎", "Apple", 10, Color(0xFFFF0000))
)

// Chip values for betting
val LUCKY_FRUIT_CHIPS = listOf(
    5_000L to "5K",
    10_000L to "10K",
    50_000L to "50K",
    100_000L to "100K",
    500_000L to "500K"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckyFruitScreen(
    onNavigateBack: () -> Unit,
    viewModel: LuckyFruitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isMuted by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D2B3E), Color(0xFF0A1A2A))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Game Header
            LuckyFruitHeader(
                coins = uiState.balance,
                isMuted = isMuted,
                onMuteToggle = { isMuted = !isMuted },
                onShowHelp = { },
                onClose = onNavigateBack
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Main Game Frame
            LuckyFruitGameFrame(
                items = LUCKY_FRUIT_ITEMS,
                bets = uiState.bets,
                selectedChip = uiState.selectedChip,
                isSpinning = uiState.isSpinning,
                winningItem = uiState.winningItem,
                onItemClick = { viewModel.placeBet(it) },
                onLuckyClick = { viewModel.placeLuckyBet() },
                onSuperLuckyClick = { viewModel.placeSuperLuckyBet() },
                onShowRankings = { viewModel.showRankings() }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Today's Win
            LuckyFruitTodaysWin(todaysWin = uiState.todaysWin)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Chip Selection
            LuckyFruitChipSelection(
                selectedChip = uiState.selectedChip,
                chips = LUCKY_FRUIT_CHIPS,
                onChipSelected = { viewModel.selectChip(it) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Result History
            LuckyFruitResultHistory(results = uiState.recentResults)
        }
    }
}

@Composable
private fun LuckyFruitHeader(
    coins: Long,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onShowHelp: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A4A5E))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coins display
        Row(
            modifier = Modifier
                .background(Color(0xFF0D2B3E), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🪙", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatLuckyFruitNumber(coins),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF00AA00), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Control icons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Mute
            IconButton(
                onClick = onMuteToggle,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF0D2B3E), CircleShape)
            ) {
                Icon(
                    if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            // Help
            IconButton(
                onClick = onShowHelp,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF0D2B3E), CircleShape)
            ) {
                Icon(Icons.Default.Help, contentDescription = "Help", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            // Close
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF0D2B3E), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun LuckyFruitGameFrame(
    items: List<LuckyFruitItem>,
    bets: Map<String, Long>,
    selectedChip: Long,
    isSpinning: Boolean,
    winningItem: String?,
    onItemClick: (String) -> Unit,
    onLuckyClick: () -> Unit,
    onSuperLuckyClick: () -> Unit,
    onShowRankings: () -> Unit
) {
    // Main game frame with golden border
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B6914))
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Title Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A5A4A), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Trophy
                Column(
                    modifier = Modifier.clickable { onShowRankings() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏆", fontSize = 24.sp)
                    Text("99+", color = Color.White, fontSize = 10.sp)
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Lucky Fruit Title
                Text(
                    text = "Lucky Fruit",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Clock icon for history
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF00AA88), CircleShape)
                ) {
                    Icon(Icons.Default.History, contentDescription = "History", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 3x4 Grid of Fruits
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A5A4A), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: Orange (x5), Lemon (x5), Grapes (x5), Cherry (x5)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items.take(4).forEach { item ->
                        FruitBetCell(
                            item = item,
                            betAmount = bets[item.id] ?: 0,
                            isWinning = winningItem == item.id,
                            isSpinning = isSpinning,
                            onClick = { onItemClick(item.id) }
                        )
                    }
                }
                
                // Row 2: Lucky | Center Spinner | Super Lucky
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lucky Button
                    SpecialBetButton(
                        text = "Lucky",
                        color = Color(0xFF6A5ACD),
                        onClick = onLuckyClick
                    )
                    
                    // Center Spinner
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0A2A3A))
                            .border(3.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSpinning) {
                            CircularProgressIndicator(
                                color = Color(0xFFFFD700),
                                modifier = Modifier.size(40.dp)
                            )
                        } else if (winningItem != null) {
                            val winningFruit = items.find { it.id == winningItem }
                            Text(
                                text = winningFruit?.emoji ?: "?",
                                fontSize = 48.sp
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎰", fontSize = 32.sp)
                                Text(
                                    text = "SPIN",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    
                    // Super Lucky Button
                    SpecialBetButton(
                        text = "Super\nLucky",
                        color = Color(0xFFFF6B6B),
                        isRainbow = true,
                        onClick = onSuperLuckyClick
                    )
                }
                
                // Row 3: Strawberry (x45), Mango (x25), Watermelon (x15), Apple (x10)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items.drop(4).forEach { item ->
                        FruitBetCell(
                            item = item,
                            betAmount = bets[item.id] ?: 0,
                            isWinning = winningItem == item.id,
                            isSpinning = isSpinning,
                            onClick = { onItemClick(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FruitBetCell(
    item: LuckyFruitItem,
    betAmount: Long,
    isWinning: Boolean,
    isSpinning: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "win_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isWinning) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = Modifier
            .size(70.dp)
            .scale(if (isWinning) scale else 1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isWinning) Color(0xFFFFD700) else Color(0xFF2A6A5A)
            )
            .border(
                width = if (betAmount > 0) 3.dp else 1.dp,
                color = if (isWinning) Color(0xFFFFD700) else if (betAmount > 0) Color.Cyan else Color(0xFF4A8A7A),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isSpinning) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = item.emoji,
                fontSize = 28.sp
            )
            Text(
                text = "x${item.multiplier}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        
        // Show bet chips
        if (betAmount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .background(Color.Red, CircleShape)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = formatLuckyFruitNumber(betAmount),
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SpecialBetButton(
    text: String,
    color: Color,
    isRainbow: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isRainbow) {
                    Brush.linearGradient(
                        colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
                    )
                } else {
                    Brush.linearGradient(colors = listOf(color, color.copy(alpha = 0.7f)))
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LuckyFruitTodaysWin(todaysWin: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFF1A1A2E), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TODAY'S WIN",
            color = Color.Gray,
            fontSize = 12.sp
        )
        Text(
            text = formatLuckyFruitNumber(todaysWin),
            color = if (todaysWin > 0) Color(0xFF00FF00) else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun LuckyFruitChipSelection(
    selectedChip: Long,
    chips: List<Pair<Long, String>>,
    onChipSelected: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Select Amount -> Choose Fruit",
            color = Color(0xFFFFD700),
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3A2A1A), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
        
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2A1A0A), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chips) { (value, label) ->
                ChipButton(
                    label = label,
                    isSelected = selectedChip == value,
                    chipColor = getChipColor(value),
                    onClick = { onChipSelected(value) }
                )
            }
        }
    }
}

@Composable
private fun ChipButton(
    label: String,
    isSelected: Boolean,
    chipColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(chipColor, chipColor.copy(alpha = 0.7f))
                )
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color.White else Color.Gray,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

private fun getChipColor(value: Long): Color {
    return when {
        value >= 500_000 -> Color(0xFFFFD700) // Gold
        value >= 100_000 -> Color(0xFFFF69B4) // Pink
        value >= 50_000 -> Color(0xFFFF4500) // Orange-Red
        value >= 10_000 -> Color(0xFF32CD32) // Green
        else -> Color(0xFF1E90FF) // Blue
    }
}

@Composable
private fun LuckyFruitResultHistory(results: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(Color(0xFF1A1A2E), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Result:",
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(results.take(10)) { result ->
                val fruit = LUCKY_FRUIT_ITEMS.find { it.id == result }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF2A2A3E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fruit?.emoji ?: "?",
                        fontSize = 20.sp
                    )
                }
            }
        }
        
        if (results.isNotEmpty()) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "New",
                color = Color.Red,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Data class for UI state
data class FruitTile(
    val id: Int,
    val fruit: String,
    val value: Int,
    val isRevealed: Boolean = false
)

private fun formatLuckyFruitNumber(number: Long): String {
    return when {
        number >= 1_000_000_000 -> String.format("%.0fB", number / 1_000_000_000.0)
        number >= 1_000_000 -> String.format("%.0fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.0fK", number / 1_000.0)
        else -> number.toString()
    }
}

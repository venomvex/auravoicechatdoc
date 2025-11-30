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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.voicechat.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Lucky 777 Pro Slot Machine Game
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Classic 3-reel slot machine with:
 * - 5 paylines
 * - Multiple fruit symbols (777, Bells, Diamonds, Grapes, Watermelon, Cherries, Mangoes)
 * - Room integration with live players
 * - Trophy rankings
 * - Jar coin collection system
 * - Auto-spin feature
 */

// Symbol definitions with payouts (matching screenshot)
data class SlotSymbol(
    val id: String,
    val emoji: String,
    val name: String,
    val payout: Long // For 3 matching
)

val SLOT_SYMBOLS = listOf(
    SlotSymbol("777", "7️⃣", "777", 1_000_000_000),
    SlotSymbol("bell", "🔔", "Bells", 300_000_000),
    SlotSymbol("diamond", "💎", "Diamonds", 100_000_000),
    SlotSymbol("watermelon", "🍉", "Watermelon", 50_000_000),
    SlotSymbol("grape", "🍇", "Grapes", 30_000_000),
    SlotSymbol("mango", "🥭", "Mangoes", 15_000_000),
    SlotSymbol("cherry", "🍒", "Cherries", 5_000_000),
    SlotSymbol("cherry_any", "🍒", "Cherry + Any", 3_000_000)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Lucky777Screen(
    onNavigateBack: () -> Unit,
    viewModel: Lucky777ViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPayTable by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0A2E), Color(0xFF0D0D1A))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Game Header Bar
            Lucky777Header(
                coins = uiState.balance,
                isMuted = isMuted,
                onMuteToggle = { isMuted = !isMuted },
                onShowPayTable = { showPayTable = true },
                onClose = onNavigateBack
            )
            
            // Rankings & Top Winners Row
            Lucky777TopBar(
                topWinners = uiState.topWinners,
                onlineCount = uiState.onlineCount,
                onShowRankings = { viewModel.showRankings() }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Main Slot Machine Area
            Lucky777SlotMachine(
                reels = uiState.reels,
                isSpinning = uiState.isSpinning,
                winLines = uiState.winLines
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stats Row: Total Bet | Today's Win | Win
            Lucky777StatsRow(
                totalBet = uiState.currentBet,
                todaysWin = uiState.todaysWin,
                currentWin = uiState.lastWin
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Control Buttons: BET- | BET+ | AUTO | SPIN
            Lucky777Controls(
                currentBet = uiState.currentBet,
                isSpinning = uiState.isSpinning,
                isAutoSpin = uiState.isAutoSpin,
                onDecreaseBet = { viewModel.decreaseBet() },
                onIncreaseBet = { viewModel.increaseBet() },
                onToggleAuto = { viewModel.toggleAutoSpin() },
                onSpin = { viewModel.spin() },
                canSpin = uiState.balance >= uiState.currentBet
            )
        }
        
        // Pay Table Dialog
        if (showPayTable) {
            Lucky777PayTableDialog(
                onDismiss = { showPayTable = false }
            )
        }
    }
}

@Composable
private fun Lucky777Header(
    coins: Long,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onShowPayTable: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFCC0066))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coins display
        Row(
            modifier = Modifier
                .background(Color(0xFF331133), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🪙", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatNumber(coins),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Lucky 777 Title with decorative styling
        Text(
            text = "Lucky 777",
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Control icons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // History
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF331133), CircleShape)
            ) {
                Icon(Icons.Default.History, contentDescription = "History", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            // Mute
            IconButton(
                onClick = onMuteToggle,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF331133), CircleShape)
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
                onClick = onShowPayTable,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF331133), CircleShape)
            ) {
                Icon(Icons.Default.Help, contentDescription = "Help", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            // Close
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF331133), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun Lucky777TopBar(
    topWinners: List<Lucky777Winner>,
    onlineCount: Int,
    onShowRankings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF660033))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Trophy Rankings
        Column(
            modifier = Modifier
                .clickable { onShowRankings() }
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🏆", fontSize = 24.sp)
            Text("99+", color = Color.White, fontSize = 10.sp)
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Top Winners Avatars
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(topWinners.take(4)) { winner ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .border(2.dp, Color(0xFFFFD700), CircleShape)
                    ) {
                        Text(
                            text = winner.name.take(1).uppercase(),
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = winner.name.take(8),
                        color = Color.White,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
        }
        
        // Online count & total win
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "+${formatNumber(topWinners.sumOf { it.winAmount })}",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Online: $onlineCount",
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun Lucky777SlotMachine(
    reels: List<String>,
    isSpinning: Boolean,
    winLines: List<Int>
) {
    // Slot machine frame with golden border and lights
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        // Main slot frame
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF8B4513))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Inner slot area
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDD0))
                ) {
                    // 3x3 Reel Grid
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(3) { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                repeat(3) { col ->
                                    val index = row * 3 + col
                                    val symbol = if (index < reels.size) reels[index] else "🍒"
                                    val isWinning = winLines.contains(row)
                                    
                                    SlotReelCell(
                                        symbol = symbol,
                                        isSpinning = isSpinning,
                                        isWinning = isWinning,
                                        delay = col * 150
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Payline indicator on left (5 lines)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-4).dp)
                        .padding(vertical = 20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(5) { line ->
                        Text(
                            text = "${line + 1}",
                            color = Color(0xFFCC0066),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color(0xFFFFD700), CircleShape)
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
        
        // Decorative lights on frame
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(12) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (it % 2 == 0) Color.Yellow else Color.Red,
                            CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun SlotReelCell(
    symbol: String,
    isSpinning: Boolean,
    isWinning: Boolean,
    delay: Int
) {
    val symbols = listOf("7️⃣", "🔔", "💎", "🍇", "🍉", "🥭", "🍒")
    var displaySymbol by remember { mutableStateOf(symbol) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "win_flash")
    val flashAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash"
    )
    
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            delay(delay.toLong())
            repeat(20) {
                displaySymbol = symbols.random()
                delay(80)
            }
            displaySymbol = symbol
        }
    }
    
    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isWinning) Color(0xFFFFD700).copy(alpha = flashAlpha)
                else Color.White
            )
            .border(
                width = 2.dp,
                color = if (isWinning) Color(0xFFFFD700) else Color(0xFFDDDDDD),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displaySymbol,
            fontSize = 36.sp
        )
    }
}

@Composable
private fun Lucky777StatsRow(
    totalBet: Long,
    todaysWin: Long,
    currentWin: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A0A2E))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(label = "TOTAL BET", value = totalBet)
        StatItem(label = "TODAY'S WIN", value = todaysWin, highlight = true)
        StatItem(label = "WIN", value = currentWin)
    }
}

@Composable
private fun StatItem(label: String, value: Long, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 10.sp
        )
        Box(
            modifier = Modifier
                .background(
                    if (highlight) Color(0xFF006600) else Color(0xFF333333),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                text = formatNumber(value),
                color = if (highlight) Color(0xFF00FF00) else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun Lucky777Controls(
    currentBet: Long,
    isSpinning: Boolean,
    isAutoSpin: Boolean,
    onDecreaseBet: () -> Unit,
    onIncreaseBet: () -> Unit,
    onToggleAuto: () -> Unit,
    onSpin: () -> Unit,
    canSpin: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // BET- Button
        ControlButton(
            text = "BET\n-",
            onClick = onDecreaseBet,
            enabled = !isSpinning,
            modifier = Modifier.weight(1f)
        )
        
        // BET+ Button
        ControlButton(
            text = "BET\n+",
            onClick = onIncreaseBet,
            enabled = !isSpinning,
            modifier = Modifier.weight(1f)
        )
        
        // AUTO Button
        ControlButton(
            text = "AUTO",
            onClick = onToggleAuto,
            enabled = true,
            isActive = isAutoSpin,
            modifier = Modifier.weight(1f)
        )
        
        // SPIN Button (larger)
        Button(
            onClick = onSpin,
            enabled = canSpin && !isSpinning,
            modifier = Modifier
                .weight(1.5f)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD700),
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isSpinning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "SPIN",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ControlButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) Color(0xFF00AA00) else Color(0xFF444444),
            disabledContainerColor = Color(0xFF333333)
        ),
        shape = RoundedCornerShape(8.dp)
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
private fun Lucky777PayTableDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📊", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Classic Slot\n5 Lines", color = Color.White)
            }
        },
        text = {
            Column {
                SLOT_SYMBOLS.forEach { symbol ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color(0xFFCC0066), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${symbol.emoji}${symbol.emoji}${symbol.emoji}", fontSize = 20.sp)
                        Text(
                            text = " = ",
                            color = Color.White
                        )
                        Text(
                            text = formatNumber(symbol.payout),
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFFFFD700))
            }
        },
        containerColor = Color(0xFFCC0066),
        shape = RoundedCornerShape(16.dp)
    )
}

// Data class for top winners
data class Lucky777Winner(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val winAmount: Long
)



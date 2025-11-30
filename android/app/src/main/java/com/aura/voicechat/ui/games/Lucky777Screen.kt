package com.aura.voicechat.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * Lucky 777 Slot Machine Game
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - 3 spinning reels
 * - Multiple bet amounts
 * - Win multipliers
 * - Jackpot system
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Lucky777Screen(
    onNavigateBack: () -> Unit,
    viewModel: Lucky777ViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lucky 777") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkCanvas)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DarkCanvas, Color(0xFF1A0A2E))
                    )
                )
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Jackpot Display
            JackpotDisplay(amount = uiState.jackpotAmount)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Slot Machine
            SlotMachine(
                reels = uiState.reels,
                isSpinning = uiState.isSpinning
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Win Display
            if (uiState.lastWin > 0) {
                WinDisplay(amount = uiState.lastWin)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Bet Selector
            BetSelector(
                currentBet = uiState.currentBet,
                availableBets = uiState.availableBets,
                onBetChanged = { viewModel.setBet(it) },
                isEnabled = !uiState.isSpinning
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Spin Button
            Button(
                onClick = { viewModel.spin() },
                enabled = !uiState.isSpinning && uiState.balance >= uiState.currentBet,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentMagenta,
                    disabledContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (uiState.isSpinning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "SPIN",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Balance Display
            BalanceDisplay(balance = uiState.balance)
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Pay Table
            PayTable()
        }
    }
}

@Composable
private fun JackpotDisplay(amount: Long) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = VipGold.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 JACKPOT 🏆",
                style = MaterialTheme.typography.titleSmall,
                color = VipGold
            )
            Text(
                text = formatNumber(amount),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = VipGold
            )
        }
    }
}

@Composable
private fun SlotMachine(
    reels: List<String>,
    isSpinning: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(4.dp, VipGold, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            reels.forEachIndexed { index, symbol ->
                SlotReel(
                    symbol = symbol,
                    isSpinning = isSpinning,
                    delay = index * 200
                )
            }
        }
    }
}

@Composable
private fun SlotReel(
    symbol: String,
    isSpinning: Boolean,
    delay: Int
) {
    val symbols = listOf("7️⃣", "🍒", "🍋", "🍊", "🍇", "💎", "⭐", "🔔")
    
    var displaySymbol by remember { mutableStateOf(symbol) }
    
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            delay(delay.toLong())
            repeat(15) {
                displaySymbol = symbols.random()
                delay(100)
            }
            displaySymbol = symbol
        }
    }
    
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .border(2.dp, AccentMagenta.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displaySymbol,
            fontSize = 40.sp
        )
    }
}

@Composable
private fun WinDisplay(amount: Long) {
    val infiniteTransition = rememberInfiniteTransition(label = "win_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Card(
        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🎉", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "WIN: ${formatNumber(amount)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("🎉", fontSize = 24.sp)
        }
    }
}

@Composable
private fun BetSelector(
    currentBet: Long,
    availableBets: List<Long>,
    onBetChanged: (Long) -> Unit,
    isEnabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "BET AMOUNT",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableBets.forEach { bet ->
                FilterChip(
                    selected = currentBet == bet,
                    onClick = { if (isEnabled) onBetChanged(bet) },
                    label = {
                        Text(
                            text = formatNumber(bet),
                            fontWeight = if (currentBet == bet) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    enabled = isEnabled,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentMagenta,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun BalanceDisplay(balance: Long) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.MonetizationOn,
            contentDescription = null,
            tint = CoinGold,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Balance: ${formatNumber(balance)}",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}

@Composable
private fun PayTable() {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pay Table",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PayTableItem("7️⃣7️⃣7️⃣", "x100")
                PayTableItem("💎💎💎", "x50")
                PayTableItem("⭐⭐⭐", "x25")
                PayTableItem("🍒🍒🍒", "x10")
            }
        }
    }
}

@Composable
private fun PayTableItem(symbols: String, multiplier: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = symbols, fontSize = 12.sp)
        Text(
            text = multiplier,
            style = MaterialTheme.typography.labelSmall,
            color = VipGold,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

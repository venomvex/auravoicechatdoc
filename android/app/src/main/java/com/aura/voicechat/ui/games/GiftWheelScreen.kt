package com.aura.voicechat.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.voicechat.ui.theme.*

/**
 * Gift Wheel Game Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftWheelScreen(
    onNavigateBack: () -> Unit,
    viewModel: GiftWheelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Rotation animation
    val rotation by animateFloatAsState(
        targetValue = uiState.wheelRotation,
        animationSpec = tween(
            durationMillis = if (uiState.isSpinning) 5000 else 0,
            easing = FastOutSlowInEasing
        ),
        label = "wheel_rotation",
        finishedListener = {
            if (uiState.isSpinning) {
                viewModel.onSpinComplete()
            }
        }
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gift Wheel") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = CoinGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatNumber(uiState.coins),
                            style = MaterialTheme.typography.labelMedium,
                            color = CoinGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkCanvas)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkCanvas)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Prize indicator
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = VipGold,
                modifier = Modifier.size(48.dp)
            )
            
            // Wheel
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .rotate(rotation),
                contentAlignment = Alignment.Center
            ) {
                // Wheel segments
                WheelSegments(prizes = uiState.prizes)
                
                // Center button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(VipGold, VipGold.copy(alpha = 0.7f))
                            )
                        )
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SPIN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkCanvas
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bet amount selector
            Text(
                text = "Bet Amount",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1000L, 5000L, 10000L, 50000L).forEach { amount ->
                    FilterChip(
                        selected = uiState.betAmount == amount,
                        onClick = { viewModel.setBetAmount(amount) },
                        label = { Text(formatNumber(amount)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VipGold,
                            selectedLabelColor = DarkCanvas
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Spin button
            Button(
                onClick = { viewModel.spin() },
                enabled = !uiState.isSpinning && uiState.coins >= uiState.betAmount,
                colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (uiState.isSpinning) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SPIN FOR ${formatNumber(uiState.betAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Free spins info
            if (uiState.freeSpins > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = SuccessGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${uiState.freeSpins} Free Spins Available!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }
    }
    
    // Win dialog
    if (uiState.showWinDialog) {
        WinDialog(
            prize = uiState.lastPrize,
            onDismiss = { viewModel.dismissWinDialog() }
        )
    }
}

@Composable
private fun WheelSegments(prizes: List<WheelPrize>) {
    // Simplified wheel representation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        AccentMagenta,
                        VipGold,
                        AccentCyan,
                        SuccessGreen,
                        DiamondBlue,
                        Purple80,
                        WarningOrange,
                        ErrorRed
                    )
                )
            )
            .border(4.dp, VipGold, CircleShape)
    )
}

@Composable
private fun WinDialog(
    prize: WheelPrize?,
    onDismiss: () -> Unit
) {
    if (prize == null) return
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎉",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Congratulations!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = VipGold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You won",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = CoinGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatNumber(prize.value),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = CoinGold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Collect")
            }
        }
    )
}

data class WheelPrize(
    val id: String,
    val name: String,
    val value: Long,
    val color: Color,
    val multiplier: Float
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

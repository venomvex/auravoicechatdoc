package com.aura.voicechat.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Greedy Baby Game Screen
 * Circular betting wheel game with live multiplayer betting, timer-based rounds
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreedyBabyScreen(
    onNavigateBack: () -> Unit,
    viewModel: GreedyBabyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2D1B4E), Color(0xFF1A0F2E))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            GreedyBabyHeader(
                userCoins = uiState.userCoins,
                isSoundEnabled = uiState.isSoundEnabled,
                onSoundToggle = { viewModel.toggleSound() },
                onHelpClick = { viewModel.showHelp() },
                onCloseClick = onNavigateBack
            )

            // Action Icons Row (Trophy + Rankings / Clock + Records)
            ActionIconsRow(
                dailyRankings = uiState.dailyRankings,
                winningRecords = uiState.winningRecords,
                onTrophyClick = { viewModel.showRankings() },
                onClockClick = { viewModel.showWinningRecords() }
            )

            // Main Game Area - Circular Wheel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularBettingWheel(
                    wheelItems = uiState.wheelItems,
                    selectedItem = uiState.selectedItem,
                    betsOnItems = uiState.betsOnItems,
                    otherPlayersBets = uiState.otherPlayersBets,
                    timerSeconds = uiState.timerSeconds,
                    gamePhase = uiState.gamePhase,
                    winningItem = uiState.winningItem,
                    isSpinning = uiState.isSpinning,
                    onItemClick = { item -> viewModel.onItemClicked(item) }
                )
            }

            // Combo Features Row (Fruit Basket + Today's Win + Full Pizza)
            ComboFeaturesRow(
                todaysWin = uiState.todaysWin,
                fruitBasketActive = uiState.fruitBasketActive,
                fullPizzaActive = uiState.fullPizzaActive,
                onFruitBasketClick = { viewModel.toggleFruitBasket() },
                onFullPizzaClick = { viewModel.toggleFullPizza() }
            )

            // Chip Selection Box
            ChipSelectionBox(
                chips = uiState.chips,
                selectedChip = uiState.selectedChip,
                onChipSelected = { chip -> viewModel.selectChip(chip) }
            )

            // Last 10 Results
            ResultsHistoryRow(
                results = uiState.resultHistory
            )
        }

        // Dialogs
        if (uiState.showResultPopup) {
            ResultPopupDialog(
                roundWinnings = uiState.roundWinnings,
                topWinners = uiState.topWinners,
                onDismiss = { viewModel.dismissResultPopup() }
            )
        }

        if (uiState.showHelpDialog) {
            HelpDialog(
                onDismiss = { viewModel.dismissHelp() }
            )
        }

        if (uiState.showRankingsDialog) {
            RankingsDialog(
                dailyRankings = uiState.dailyRankings,
                weeklyRankings = uiState.weeklyRankings,
                onDismiss = { viewModel.dismissRankings() }
            )
        }

        if (uiState.showWinningRecordsDialog) {
            WinningRecordsDialog(
                records = uiState.winningRecords,
                onDismiss = { viewModel.dismissWinningRecords() }
            )
        }
    }
}

@Composable
private fun GreedyBabyHeader(
    userCoins: Long,
    isSoundEnabled: Boolean,
    onSoundToggle: () -> Unit,
    onHelpClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coins display with + button
        Row(
            modifier = Modifier
                .background(Color(0xFF3D2D5E), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = formatCoins(userCoins),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50))
                    .clickable { /* Add coins */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add coins",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Game Title
        Text(
            text = "Greedy Baby",
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Sound toggle
        IconButton(
            onClick = onSoundToggle,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF3D2D5E))
        ) {
            Icon(
                if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                contentDescription = "Toggle sound",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Help button
        IconButton(
            onClick = onHelpClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF3D2D5E))
        ) {
            Icon(
                Icons.Default.Help,
                contentDescription = "Help",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Close button
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF3D2D5E))
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ActionIconsRow(
    dailyRankings: List<RankingEntry>,
    winningRecords: List<WinningRecord>,
    onTrophyClick: () -> Unit,
    onClockClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Trophy Icon - Rankings
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                    )
                )
                .clickable(onClick = onTrophyClick),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = "Rankings",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "99+",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Clock Icon - Winning Records
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF3D2D5E))
                .clickable(onClick = onClockClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.History,
                contentDescription = "Winning Records",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun CircularBettingWheel(
    wheelItems: List<WheelItem>,
    selectedItem: WheelItem?,
    betsOnItems: Map<String, List<PlacedBet>>,
    otherPlayersBets: Map<String, List<PlacedBet>>,
    timerSeconds: Int,
    gamePhase: GamePhase,
    winningItem: WheelItem?,
    isSpinning: Boolean,
    onItemClick: (WheelItem) -> Unit
) {
    val wheelRadius = 140.dp
    val itemRadius = 45.dp
    val density = LocalDensity.current
    
    // Spinning animation
    val infiniteTransition = rememberInfiniteTransition(label = "wheel_spin")
    val spinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_rotation"
    )

    // Winning item highlight animation
    val highlightAlpha by animateFloatAsState(
        targetValue = if (winningItem != null) 1f else 0f,
        animationSpec = if (winningItem != null) {
            infiniteRepeatable(
                animation = tween(500),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            tween(0)
        },
        label = "highlight_alpha"
    )

    Box(
        modifier = Modifier.size(320.dp),
        contentAlignment = Alignment.Center
    ) {
        // Wheel items arranged in a circle (8 items, starting from top clockwise)
        wheelItems.forEachIndexed { index, item ->
            val angle = (index * 45f - 90f) * (PI.toFloat() / 180f) // Start from top (12 o'clock)
            val x = cos(angle) * with(density) { wheelRadius.toPx() }
            val y = sin(angle) * with(density) { wheelRadius.toPx() }
            
            val isWinning = winningItem?.id == item.id
            val itemBets = betsOnItems[item.id] ?: emptyList()
            val otherBets = otherPlayersBets[item.id] ?: emptyList()
            
            WheelItemButton(
                item = item,
                isSelected = selectedItem?.id == item.id,
                isWinning = isWinning,
                highlightAlpha = if (isWinning) highlightAlpha else 0f,
                userBets = itemBets,
                otherPlayersBets = otherBets,
                offsetX = with(density) { x.toDp() },
                offsetY = with(density) { y.toDp() },
                size = itemRadius * 2,
                onClick = { onItemClick(item) },
                enabled = gamePhase == GamePhase.BETTING
            )
        }

        // Center - Elephant mascot with timer
        CenterMascotWithTimer(
            timerSeconds = timerSeconds,
            gamePhase = gamePhase,
            isSpinning = isSpinning
        )

        // Connecting lines from items to center (visual effect)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            wheelItems.forEachIndexed { index, _ ->
                val angle = (index * 45f - 90f) * (PI.toFloat() / 180f)
                val endX = centerX + cos(angle) * wheelRadius.toPx() * 0.6f
                val endY = centerY + sin(angle) * wheelRadius.toPx() * 0.6f
                
                drawLine(
                    color = Color(0xFF8B7355),
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 4f
                )
            }
        }
    }
}

@Composable
private fun WheelItemButton(
    item: WheelItem,
    isSelected: Boolean,
    isWinning: Boolean,
    highlightAlpha: Float,
    userBets: List<PlacedBet>,
    otherPlayersBets: List<PlacedBet>,
    offsetX: Dp,
    offsetY: Dp,
    size: Dp,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isWinning -> 1.2f
            isSelected -> 1.1f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isWinning) {
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = highlightAlpha),
                            item.backgroundColor
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(item.backgroundColor, item.backgroundColor.copy(alpha = 0.8f))
                    )
                }
            )
            .border(
                width = if (isSelected || isWinning) 3.dp else 2.dp,
                color = if (isWinning) Color(0xFFFFD700) else if (isSelected) Color.White else Color(0xFF8B7355),
                shape = CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Food emoji
            Text(
                text = item.emoji,
                fontSize = 28.sp
            )
            // Multiplier
            Text(
                text = "x${item.multiplier}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Show bet chips on item
        if (userBets.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-5).dp, y = (-5).dp)
                    .background(Color(0xFFE91E63), RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Bet 🪙${formatCoins(userBets.sumOf { it.totalBet })}",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Show other players' bet count
        val totalOtherBets = otherPlayersBets.size
        if (totalOtherBets > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 5.dp, y = 5.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$totalOtherBets",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CenterMascotWithTimer(
    timerSeconds: Int,
    gamePhase: GamePhase,
    isSpinning: Boolean
) {
    val rotation by animateFloatAsState(
        targetValue = if (isSpinning) 360f else 0f,
        animationSpec = if (isSpinning) {
            infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        } else {
            tween(0)
        },
        label = "mascot_rotation"
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFFF6B6B), Color(0xFFEE5A24))
                )
            )
            .border(4.dp, Color(0xFFFFD700), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elephant mascot emoji (representing Greedy Baby)
            Text(
                text = "🐘",
                fontSize = 32.sp,
                modifier = Modifier.rotate(if (isSpinning) rotation else 0f)
            )
            
            // Timer/Phase text
            Text(
                text = when (gamePhase) {
                    GamePhase.BETTING -> "Bet Time"
                    GamePhase.SHOW_TIME -> "Show Time"
                    GamePhase.RESULT -> "Result"
                },
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            
            // Timer countdown
            Text(
                text = "${timerSeconds}s",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun ComboFeaturesRow(
    todaysWin: Long,
    fruitBasketActive: Boolean,
    fullPizzaActive: Boolean,
    onFruitBasketClick: () -> Unit,
    onFullPizzaClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Fruit Basket combo
        ComboButton(
            emoji = "🧺",
            label = "Fruit",
            isActive = fruitBasketActive,
            onClick = onFruitBasketClick
        )

        // Today's Win
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TODAY'S WIN",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = formatCoins(todaysWin),
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        // Full Pizza combo
        ComboButton(
            emoji = "🍕",
            label = "Pizza",
            isActive = fullPizzaActive,
            onClick = onFullPizzaClick
        )
    }
}

@Composable
private fun ComboButton(
    emoji: String,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isActive) Color(0xFFFFD700) else Color(0xFF3D2D5E)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 28.sp)
        Text(
            text = label,
            color = if (isActive) Color(0xFF1A1A2E) else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ChipSelectionBox(
    chips: List<BettingChip>,
    selectedChip: BettingChip?,
    onChipSelected: (BettingChip) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4A3B6E))
            .padding(12.dp)
    ) {
        Text(
            text = "Choose the amount wager → Choose Food",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chips) { chip ->
                ChipButton(
                    chip = chip,
                    isSelected = selectedChip?.value == chip.value,
                    onClick = { onChipSelected(chip) }
                )
            }
        }
    }
}

@Composable
private fun ChipButton(
    chip: BettingChip,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(chip.primaryColor, chip.secondaryColor)
                )
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .shadow(if (isSelected) 8.dp else 2.dp, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = chip.displayText,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (chip.displayText.length > 3) 10.sp else 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ResultsHistoryRow(
    results: List<GameResult>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3D2D5E))
            .padding(8.dp)
    ) {
        Text(
            text = "Result",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            results.take(10).forEachIndexed { index, result ->
                ResultItem(
                    result = result,
                    isLatest = index == 0
                )
            }
        }
    }
}

@Composable
private fun ResultItem(
    result: GameResult,
    isLatest: Boolean
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isLatest) Color(0xFFFFD700) else Color(0xFF5D4E7A)
            )
            .border(
                width = if (isLatest) 2.dp else 0.dp,
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isLatest) {
                Text(
                    text = "New",
                    color = Color(0xFF1A1A2E),
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = result.winningItem.emoji,
                fontSize = 20.sp
            )
        }
    }
}

// Dialog Components

@Composable
private fun ResultPopupDialog(
    roundWinnings: Long,
    topWinners: List<TopWinner>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (roundWinnings > 0) "🎉 You Won!" else "Better luck next time!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (roundWinnings > 0) {
                    Text(
                        text = "+${formatCoins(roundWinnings)}",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Top 3 Winners",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                topWinners.forEachIndexed { index, winner ->
                    TopWinnerRow(
                        position = index + 1,
                        winner = winner
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color(0xFF1A1A2E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TopWinnerRow(
    position: Int,
    winner: TopWinner
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFF3D3D54), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Position medal
        Text(
            text = when (position) {
                1 -> "🥇"
                2 -> "🥈"
                3 -> "🥉"
                else -> "$position"
            },
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF5D5D7A))
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Username
        Text(
            text = winner.username,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        // Winnings
        Text(
            text = "🪙${formatCoins(winner.winnings)}",
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun HelpDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "How to Play",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                val rules = listOf(
                    "1. Select a chip amount from the bottom",
                    "2. Tap on food items to place bets",
                    "3. Each tap places one chip",
                    "4. Wait for the timer to end",
                    "5. If your item wins, you get the multiplier!",
                    "",
                    "🧺 Fruit Basket: Bet on all 4 fruits to win when 'Full Basket' appears",
                    "🍕 Full Pizza: Bet on all 4 non-fruits to win when 'Full Pizza' appears"
                )

                rules.forEach { rule ->
                    Text(
                        text = rule,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color(0xFF1A1A2E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Got it!", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RankingsDialog(
    dailyRankings: List<RankingEntry>,
    weeklyRankings: List<RankingEntry>,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🏆 Rankings",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tab selector
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TabButton(
                        text = "Daily",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Weekly",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val rankings = if (selectedTab == 0) dailyRankings else weeklyRankings
                rankings.take(10).forEachIndexed { index, entry ->
                    RankingRow(position = index + 1, entry = entry)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFFD700) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF1A1A2E) else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun RankingRow(
    position: Int,
    entry: RankingEntry
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (position) {
                1 -> "🥇"
                2 -> "🥈"
                3 -> "🥉"
                else -> "#$position"
            },
            fontSize = if (position <= 3) 20.sp else 14.sp,
            modifier = Modifier.width(40.dp)
        )

        Text(
            text = entry.username,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "🪙${formatCoins(entry.totalWinnings)}",
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun WinningRecordsDialog(
    records: List<WinningRecord>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📜 Winning Records",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (records.isEmpty()) {
                    Text(
                        text = "No records yet",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                } else {
                    records.take(20).forEach { record ->
                        WinningRecordRow(record = record)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun WinningRecordRow(record: WinningRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFF3D3D54), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = record.item.emoji,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bet: ${formatCoins(record.totalBet)}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = if (record.won) "+${formatCoins(record.payout)}" else "-${formatCoins(record.totalBet)}",
                color = if (record.won) Color(0xFF4CAF50) else Color(0xFFFF5252),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

// Helper function to format coins
private fun formatCoins(amount: Long): String {
    return when {
        amount >= 1_000_000_000 -> "%.1fB".format(amount / 1_000_000_000.0)
        amount >= 1_000_000 -> "%.1fM".format(amount / 1_000_000.0)
        amount >= 1_000 -> "%.1fK".format(amount / 1_000.0)
        else -> amount.toString()
    }
}

// Data Classes

data class WheelItem(
    val id: String,
    val name: String,
    val emoji: String,
    val multiplier: Int,
    val backgroundColor: Color,
    val isFruit: Boolean
)

data class BettingChip(
    val value: Long,
    val displayText: String,
    val primaryColor: Color,
    val secondaryColor: Color
)

data class PlacedBet(
    val itemId: String,
    val chipValue: Long,
    val chipCount: Int,
    val totalBet: Long,
    val userId: String? = null
)

data class GameResult(
    val roundId: String,
    val winningItem: WheelItem,
    val specialResult: SpecialResult? = null,
    val timestamp: Long
)

data class RankingEntry(
    val userId: String,
    val username: String,
    val totalWinnings: Long,
    val avatarUrl: String? = null
)

data class TopWinner(
    val userId: String,
    val username: String,
    val winnings: Long,
    val avatarUrl: String? = null
)

data class WinningRecord(
    val roundId: String,
    val item: WheelItem,
    val totalBet: Long,
    val won: Boolean,
    val payout: Long,
    val timestamp: Long
)

enum class GamePhase {
    BETTING,
    SHOW_TIME,
    RESULT
}

enum class SpecialResult {
    FRUIT_BASKET,
    FULL_PIZZA
}

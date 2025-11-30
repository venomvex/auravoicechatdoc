package com.aura.voicechat.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
 * Lucky Fruit Match Game
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Match 3 fruits to win
 * - Different fruit values
 * - Combo multipliers
 * - Progressive jackpot
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckyFruitScreen(
    onNavigateBack: () -> Unit,
    viewModel: LuckyFruitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lucky Fruit") },
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
                        colors = listOf(DarkCanvas, Color(0xFF0A1A0A))
                    )
                )
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Balance and Multiplier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceCard(balance = uiState.balance)
                MultiplierCard(multiplier = uiState.currentMultiplier)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Game Grid
            FruitGrid(
                tiles = uiState.tiles,
                selectedTiles = uiState.selectedTiles,
                matchedTiles = uiState.matchedTiles,
                onTileClick = { viewModel.selectTile(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Win Display
            if (uiState.lastWin > 0) {
                WinBanner(amount = uiState.lastWin, combo = uiState.comboCount)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Bet Controls
            BetControls(
                currentBet = uiState.currentBet,
                onBetChange = { viewModel.setBet(it) },
                onNewGame = { viewModel.newGame() },
                isPlaying = uiState.isPlaying
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Prize Table
            PrizeTable()
        }
    }
}

@Composable
private fun BalanceCard(balance: Long) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = CoinGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatNumber(balance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CoinGold
            )
        }
    }
}

@Composable
private fun MultiplierCard(multiplier: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (multiplier > 1) AccentMagenta.copy(alpha = 0.2f) else DarkCard
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "x$multiplier",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (multiplier > 1) AccentMagenta else TextSecondary
            )
            if (multiplier > 1) {
                Spacer(modifier = Modifier.width(4.dp))
                Text("🔥", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun FruitGrid(
    tiles: List<FruitTile>,
    selectedTiles: List<Int>,
    matchedTiles: Set<Int>,
    onTileClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(tiles.size) { index ->
            val tile = tiles[index]
            val isSelected = index in selectedTiles
            val isMatched = index in matchedTiles
            
            FruitTileItem(
                tile = tile,
                isSelected = isSelected,
                isMatched = isMatched,
                onClick = { onTileClick(index) }
            )
        }
    }
}

@Composable
private fun FruitTileItem(
    tile: FruitTile,
    isSelected: Boolean,
    isMatched: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isMatched -> 0f
            isSelected -> 1.1f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tile_scale"
    )
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isSelected -> AccentMagenta.copy(alpha = 0.3f)
                    else -> DarkCard
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AccentMagenta else DarkSurface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !tile.isRevealed && !isMatched, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (tile.isRevealed || isSelected) {
            Text(
                text = tile.fruit,
                fontSize = 28.sp
            )
        } else {
            Text(
                text = "❓",
                fontSize = 28.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun WinBanner(amount: Long, combo: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "win_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Card(
        modifier = Modifier.scale(scale),
        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (combo > 1) {
                Text(
                    text = "🔥 COMBO x$combo 🔥",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentMagenta,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎉", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+${formatNumber(amount)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("🎉", fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun BetControls(
    currentBet: Long,
    onBetChange: (Long) -> Unit,
    onNewGame: () -> Unit,
    isPlaying: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bet selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(500L, 1000L, 5000L, 10000L).forEach { bet ->
                FilterChip(
                    selected = currentBet == bet,
                    onClick = { onBetChange(bet) },
                    label = { Text(formatNumber(bet)) },
                    enabled = !isPlaying,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentMagenta
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = onNewGame,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isPlaying) "Playing..." else "New Game (${formatNumber(currentBet)})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PrizeTable() {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Match 3 to Win",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PrizeItem("🍇", "x5")
                PrizeItem("🍊", "x8")
                PrizeItem("🍋", "x10")
                PrizeItem("🍒", "x15")
                PrizeItem("⭐", "x25")
            }
        }
    }
}

@Composable
private fun PrizeItem(fruit: String, multiplier: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(fruit, fontSize = 20.sp)
        Text(
            text = multiplier,
            style = MaterialTheme.typography.labelSmall,
            color = VipGold,
            fontWeight = FontWeight.Bold
        )
    }
}

// Data class
data class FruitTile(
    val id: Int,
    val fruit: String,
    val value: Int,
    val isRevealed: Boolean = false
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

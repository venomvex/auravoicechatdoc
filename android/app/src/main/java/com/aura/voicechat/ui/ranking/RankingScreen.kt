package com.aura.voicechat.ui.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * Ranking Screen - Multiple ranking types
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Rankings:
 * - Gift Senders (Daily/Weekly/Monthly)
 * - Gift Receivers (Daily/Weekly/Monthly)
 * - Family Rankings
 * - Weekly Party Star
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    rankingType: String, // "sender", "receiver", "family", "party"
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToFamily: (String) -> Unit,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPeriod by remember { mutableStateOf("daily") }
    
    LaunchedEffect(rankingType) {
        viewModel.loadRanking(rankingType, selectedPeriod)
    }
    
    val title = when (rankingType) {
        "sender" -> "Gift Senders"
        "receiver" -> "Gift Receivers"
        "family" -> "Family Rankings"
        "party" -> "Weekly Party Star"
        else -> "Rankings"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
                .background(DarkCanvas)
                .padding(paddingValues)
        ) {
            // Period Selector (not for family)
            if (rankingType != "family") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("daily" to "Daily", "weekly" to "Weekly", "monthly" to "Monthly").forEach { (id, label) ->
                        FilterChip(
                            selected = selectedPeriod == id,
                            onClick = {
                                selectedPeriod = id
                                viewModel.loadRanking(rankingType, id)
                            },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentMagenta,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
            
            // Top 3 Podium
            if (uiState.rankings.size >= 3) {
                TopThreePodium(
                    first = uiState.rankings[0],
                    second = uiState.rankings[1],
                    third = uiState.rankings[2],
                    isFamily = rankingType == "family",
                    onClick = { id ->
                        if (rankingType == "family") onNavigateToFamily(id)
                        else onNavigateToProfile(id)
                    }
                )
            }
            
            // Rankings List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(uiState.rankings.drop(3)) { index, item ->
                    RankingItem(
                        rank = index + 4,
                        item = item,
                        isFamily = rankingType == "family",
                        onClick = {
                            if (rankingType == "family") onNavigateToFamily(item.id)
                            else onNavigateToProfile(item.id)
                        }
                    )
                }
                
                // My Ranking (if not in top)
                if (uiState.myRank > 0 && uiState.myRank > 10) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = DarkSurface
                        )
                        Text(
                            text = "Your Ranking",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        uiState.myRankingItem?.let { myItem ->
                            RankingItem(
                                rank = uiState.myRank,
                                item = myItem,
                                isFamily = rankingType == "family",
                                isHighlighted = true,
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopThreePodium(
    first: RankingItemData,
    second: RankingItemData,
    third: RankingItemData,
    isFamily: Boolean,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // Second Place
        PodiumItem(
            rank = 2,
            item = second,
            height = 100.dp,
            isFamily = isFamily,
            onClick = { onClick(second.id) }
        )
        
        // First Place
        PodiumItem(
            rank = 1,
            item = first,
            height = 130.dp,
            isFamily = isFamily,
            onClick = { onClick(first.id) }
        )
        
        // Third Place
        PodiumItem(
            rank = 3,
            item = third,
            height = 80.dp,
            isFamily = isFamily,
            onClick = { onClick(third.id) }
        )
    }
}

@Composable
private fun PodiumItem(
    rank: Int,
    item: RankingItemData,
    height: androidx.compose.ui.unit.Dp,
    isFamily: Boolean,
    onClick: () -> Unit
) {
    val colors = when (rank) {
        1 -> listOf(VipGold, VipGold.copy(alpha = 0.7f))
        2 -> listOf(Color(0xFFC0C0C0), Color(0xFF808080))
        else -> listOf(Color(0xFFCD7F32), Color(0xFF8B4513))
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        // Crown for #1
        if (rank == 1) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = VipGold,
                modifier = Modifier.size(32.dp)
            )
        }
        
        // Avatar
        Card(
            onClick = onClick,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.verticalGradient(colors))
                    .border(3.dp, colors[0], CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (item.avatar != null) {
                    AsyncImage(
                        model = item.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        if (isFamily) Icons.Default.Groups else Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Name
        Text(
            text = item.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1
        )
        
        // Value
        Text(
            text = formatNumber(item.value),
            style = MaterialTheme.typography.labelSmall,
            color = colors[0]
        )
        
        // Podium
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(brush = Brush.verticalGradient(colors)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun RankingItem(
    rank: Int,
    item: RankingItemData,
    isFamily: Boolean,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) AccentMagenta.copy(alpha = 0.2f) else DarkCard
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        rank <= 10 -> VipGold
                        rank <= 50 -> AccentMagenta
                        else -> TextSecondary
                    }
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                if (item.avatar != null) {
                    AsyncImage(
                        model = item.avatar,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        if (isFamily) Icons.Default.Groups else Icons.Default.Person,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Name and Level
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (!isFamily && item.level > 0) {
                    Text(
                        text = "Lv.${item.level}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            
            // Value
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Diamond,
                        contentDescription = null,
                        tint = DiamondBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatNumber(item.value),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = DiamondBlue
                    )
                }
            }
        }
    }
}

// Data class
data class RankingItemData(
    val id: String,
    val name: String,
    val avatar: String?,
    val level: Int,
    val value: Long
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

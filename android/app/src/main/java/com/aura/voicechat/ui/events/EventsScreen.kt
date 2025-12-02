package com.aura.voicechat.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * Events Screen - Recharge Events, Weekly Party Star, etc.
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    eventType: String, // "recharge", "party_star", "room_support"
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(eventType) {
        viewModel.loadEvent(eventType)
    }
    
    val title = when (eventType) {
        "recharge" -> "Recharge Event"
        "party_star" -> "Weekly Party Star"
        "room_support" -> "Room Support"
        else -> "Events"
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkCanvas)
                .padding(paddingValues)
        ) {
            // Event Banner
            item {
                EventBanner(
                    eventName = uiState.eventName,
                    timeRemaining = uiState.timeRemaining,
                    eventType = eventType
                )
            }
            
            when (eventType) {
                "recharge" -> {
                    // Recharge Tiers
                    item {
                        Text(
                            text = "Recharge Rewards",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    
                    items(uiState.rechargeTiers) { tier ->
                        RechargeTierItem(
                            tier = tier,
                            onClaim = { viewModel.claimRechargeReward(tier.id) }
                        )
                    }
                }
                
                "party_star" -> {
                    // My Progress
                    item {
                        PartyStarProgress(
                            currentPoints = uiState.myPoints,
                            rank = uiState.myRank,
                            targetPoints = uiState.nextTierPoints
                        )
                    }
                    
                    // Top Stars
                    item {
                        Text(
                            text = "Top Party Stars",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    
                    items(uiState.topStars) { star ->
                        PartyStarItem(
                            star = star,
                            onClick = { onNavigateToProfile(star.userId) }
                        )
                    }
                    
                    // Rewards Info
                    item {
                        PartyStarRewardsInfo(rewards = uiState.partyRewards)
                    }
                }
                
                "room_support" -> {
                    // Support Options
                    item {
                        Text(
                            text = "Support This Room",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    
                    items(uiState.supportOptions) { option ->
                        SupportOptionItem(
                            option = option,
                            onSupport = { viewModel.supportRoom(option.id) }
                        )
                    }
                    
                    // Top Supporters
                    item {
                        Text(
                            text = "Top Supporters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    
                    items(uiState.topSupporters) { supporter ->
                        SupporterItem(
                            supporter = supporter,
                            onClick = { onNavigateToProfile(supporter.userId) }
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun EventBanner(
    eventName: String,
    timeRemaining: String,
    eventType: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = when (eventType) {
                            "recharge" -> listOf(VipGold.copy(alpha = 0.5f), DarkCard)
                            "party_star" -> listOf(AccentMagenta.copy(alpha = 0.5f), DarkCard)
                            else -> listOf(AccentCyan.copy(alpha = 0.5f), DarkCard)
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = eventName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ends in: $timeRemaining",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarningOrange
                    )
                }
            }
        }
    }
}

@Composable
private fun RechargeTierItem(
    tier: RechargeTier,
    onClaim: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (tier.isCompleted) DarkCard else DarkSurface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Target amount
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recharge ${formatNumber(tier.targetAmount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (tier.isCompleted) TextPrimary else TextTertiary
                )
                if (!tier.isCompleted) {
                    LinearProgressIndicator(
                        progress = { (tier.currentAmount.toFloat() / tier.targetAmount).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = VipGold,
                        trackColor = DarkCanvas
                    )
                    Text(
                        text = "${formatNumber(tier.currentAmount)}/${formatNumber(tier.targetAmount)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Reward
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = AccentMagenta,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tier.rewardName,
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentMagenta
                    )
                }
                if (tier.bonusCoins > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = CoinGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+${formatNumber(tier.bonusCoins)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CoinGold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Claim button
            when {
                tier.isClaimed -> {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                tier.isCompleted -> {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Claim", style = MaterialTheme.typography.labelMedium)
                    }
                }
                else -> {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PartyStarProgress(
    currentPoints: Long,
    rank: Int,
    targetPoints: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = VipGold
                    )
                    Text("Rank", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatNumber(currentPoints),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentMagenta
                    )
                    Text("Points", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { (currentPoints.toFloat() / targetPoints).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = AccentMagenta,
                trackColor = DarkSurface
            )
            
            Text(
                text = "${formatNumber(targetPoints - currentPoints)} more to next tier",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun PartyStarItem(
    star: PartyStar,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${star.rank}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (star.rank) {
                        1 -> VipGold
                        2 -> Color(0xFFC0C0C0)
                        3 -> Color(0xFFCD7F32)
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
                Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = star.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Lv.${star.level}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            
            // Points
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatNumber(star.points),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentMagenta
                )
                Text(
                    text = "points",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }
    }
}

@Composable
private fun PartyStarRewardsInfo(rewards: List<PartyReward>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Rewards",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            rewards.forEach { reward ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = reward.rankRange,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = reward.prize,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = VipGold
                    )
                }
            }
        }
    }
}

@Composable
private fun SupportOptionItem(
    option: SupportOption,
    onSupport: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = VipGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Button(
                onClick = onSupport,
                colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta)
            ) {
                Text("${formatNumber(option.cost)}")
            }
        }
    }
}

@Composable
private fun SupporterItem(
    supporter: Supporter,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${supporter.rank}",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                modifier = Modifier.width(32.dp)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = supporter.userName,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatNumber(supporter.amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = VipGold
            )
        }
    }
}

// Data classes
data class RechargeTier(
    val id: String,
    val targetAmount: Long,
    val currentAmount: Long,
    val rewardName: String,
    val bonusCoins: Long,
    val isCompleted: Boolean,
    val isClaimed: Boolean
)

data class PartyStar(
    val userId: String,
    val userName: String,
    val level: Int,
    val rank: Int,
    val points: Long
)

data class PartyReward(
    val rankRange: String,
    val prize: String
)

data class SupportOption(
    val id: String,
    val name: String,
    val description: String,
    val cost: Long
)

data class Supporter(
    val userId: String,
    val userName: String,
    val rank: Int,
    val amount: Long
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

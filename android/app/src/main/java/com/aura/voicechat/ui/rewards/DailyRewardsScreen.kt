package com.aura.voicechat.ui.rewards

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.voicechat.domain.model.DayReward
import com.aura.voicechat.domain.model.RewardStatus
import com.aura.voicechat.ui.theme.*

/**
 * Daily Rewards Screen - 7-day cycle (5K→50K coins), VIP multipliers
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRewardsScreen(
    onNavigateBack: () -> Unit,
    viewModel: DailyRewardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Rewards") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCanvas
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DarkCanvas, DarkSurface)
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Streak Card
            StreakCard(
                streak = uiState.streak,
                vipTier = uiState.vipTier,
                vipMultiplier = uiState.vipMultiplier
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 7-Day Cycle Grid
            Text(
                text = "7-Day Reward Cycle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Days 1-4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                uiState.cycle.take(4).forEach { dayReward ->
                    DayRewardItem(
                        dayReward = dayReward,
                        currentDay = uiState.currentDay,
                        vipMultiplier = uiState.vipMultiplier
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Days 5-7
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                uiState.cycle.drop(4).forEach { dayReward ->
                    DayRewardItem(
                        dayReward = dayReward,
                        currentDay = uiState.currentDay,
                        vipMultiplier = uiState.vipMultiplier,
                        isLarge = dayReward.day == 7
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Claim Button
            val canClaim = uiState.claimable
            Button(
                onClick = { viewModel.claimReward() },
                enabled = canClaim && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canClaim) AccentMagenta else DarkCard,
                    disabledContainerColor = DarkCard
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (canClaim) "Claim Today's Reward" else "Already Claimed Today",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // VIP Bonus Info
            if (uiState.vipTier > 0) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = VipGold.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "VIP ${uiState.vipTier} Bonus Active",
                                style = MaterialTheme.typography.labelLarge,
                                color = VipGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your rewards are multiplied by ${uiState.vipMultiplier}x",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
            
            // Reward claim result
            uiState.claimResult?.let { result ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreen.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reward Claimed!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = CoinGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${formatCoins(result.totalCoins)} Coins",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = CoinGold
                            )
                        }
                        if (result.vipMultiplier > 1.0) {
                            Text(
                                text = "(Base: ${formatCoins(result.baseCoins)} × ${result.vipMultiplier})",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakCard(
    streak: Int,
    vipTier: Int,
    vipMultiplier: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(AccentMagenta, Purple80)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$streak",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Day Streak",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            
            VerticalDivider(
                modifier = Modifier.height(60.dp),
                color = DarkSurface
            )
            
            // VIP Tier
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            if (vipTier > 0) VipGold.copy(alpha = 0.2f) else DarkSurface,
                            CircleShape
                        )
                        .border(2.dp, if (vipTier > 0) VipGold else Color.Transparent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (vipTier > 0) {
                        Text(
                            text = "V$vipTier",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = VipGold
                        )
                    } else {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (vipTier > 0) "VIP $vipTier" else "No VIP",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (vipTier > 0) VipGold else TextTertiary
                )
            }
            
            VerticalDivider(
                modifier = Modifier.height(60.dp),
                color = DarkSurface
            )
            
            // Multiplier
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(AccentCyan.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${vipMultiplier}x",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Multiplier",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun DayRewardItem(
    dayReward: DayReward,
    currentDay: Int,
    vipMultiplier: Double,
    isLarge: Boolean = false
) {
    val isClaimed = dayReward.status == RewardStatus.CLAIMED
    val isClaimable = dayReward.status == RewardStatus.CLAIMABLE
    val isLocked = dayReward.status == RewardStatus.LOCKED
    
    val totalCoins = dayReward.coins + (dayReward.bonus ?: 0)
    val multipliedCoins = (totalCoins * vipMultiplier).toLong()
    
    Column(
        modifier = Modifier
            .width(if (isLarge) 100.dp else 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isLarge) 72.dp else 56.dp)
                .clip(RoundedCornerShape(if (isLarge) 20.dp else 16.dp))
                .background(
                    brush = when {
                        isClaimed -> Brush.linearGradient(
                            colors = listOf(SuccessGreen.copy(alpha = 0.3f), SuccessGreen.copy(alpha = 0.3f))
                        )
                        isClaimable -> Brush.linearGradient(
                            colors = listOf(AccentMagenta, Purple80)
                        )
                        else -> Brush.linearGradient(
                            colors = listOf(DarkCard, DarkCard)
                        )
                    }
                )
                .then(
                    if (isClaimable) Modifier.border(
                        2.dp,
                        Brush.linearGradient(colors = listOf(AccentMagenta, Purple80)),
                        RoundedCornerShape(if (isLarge) 20.dp else 16.dp)
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isClaimed) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Claimed",
                    tint = SuccessGreen,
                    modifier = Modifier.size(if (isLarge) 36.dp else 28.dp)
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = if (isClaimable) CoinGold else TextTertiary,
                        modifier = Modifier.size(if (isLarge) 28.dp else 20.dp)
                    )
                    if (isLarge && dayReward.bonus != null) {
                        Text(
                            text = "+Bonus",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentMagenta
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = "Day ${dayReward.day}",
            style = MaterialTheme.typography.labelSmall,
            color = if (isClaimable) AccentMagenta else TextSecondary,
            fontWeight = if (isClaimable) FontWeight.Bold else FontWeight.Normal
        )
        
        Text(
            text = formatCoins(if (vipMultiplier > 1.0) multipliedCoins else totalCoins),
            style = MaterialTheme.typography.labelSmall,
            color = when {
                isClaimed -> SuccessGreen
                isClaimable -> CoinGold
                else -> TextTertiary
            },
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatCoins(coins: Long): String {
    return when {
        coins >= 1_000_000 -> "${coins / 1_000_000}M"
        coins >= 1_000 -> "${coins / 1_000}K"
        else -> coins.toString()
    }
}

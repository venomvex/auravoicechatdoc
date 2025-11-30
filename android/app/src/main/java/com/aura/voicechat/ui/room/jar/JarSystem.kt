package com.aura.voicechat.ui.room.jar

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aura.voicechat.ui.theme.*

/**
 * In-Room Jar System
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Jar fills up with coins as gifts are sent
 * - Multiple jar slots (unlock with gifts)
 * - Random rewards when jar is full
 * - Progress tracking
 */
@Composable
fun JarSystem(
    jarData: JarData,
    onContribute: (Long) -> Unit,
    onClaimJar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏺",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Room Jar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Send gifts to fill the jar and win rewards!",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Main Jar Display
            MainJarDisplay(
                currentAmount = jarData.currentAmount,
                targetAmount = jarData.targetAmount,
                isReady = jarData.isReady,
                onClaim = { onClaimJar(0) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Additional Jar Slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                jarData.slots.forEachIndexed { index, slot ->
                    JarSlot(
                        slot = slot,
                        slotIndex = index + 1,
                        onUnlock = { onContribute(slot.unlockCost) },
                        onClaim = { onClaimJar(index + 1) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Top Contributors
            if (jarData.topContributors.isNotEmpty()) {
                Text(
                    text = "Top Contributors",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    jarData.topContributors.take(3).forEachIndexed { index, contributor ->
                        ContributorBadge(
                            rank = index + 1,
                            name = contributor.name,
                            amount = contributor.amount
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainJarDisplay(
    currentAmount: Long,
    targetAmount: Long,
    isReady: Boolean,
    onClaim: () -> Unit
) {
    val progress = (currentAmount.toFloat() / targetAmount).coerceIn(0f, 1f)
    
    // Pulse animation when ready
    val infiniteTransition = rememberInfiniteTransition(label = "jar_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isReady) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            if (isReady) VipGold else DarkSurface,
                            if (isReady) VipGold.copy(alpha = 0.5f) else DarkCanvas
                        )
                    )
                )
                .then(
                    if (isReady) Modifier.clickable(onClick = onClaim)
                    else Modifier
                )
                .border(
                    width = 3.dp,
                    color = if (isReady) VipGold else AccentMagenta.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Jar icon with fill indicator
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Background fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(progress)
                            .align(Alignment.BottomCenter)
                            .background(
                                CoinGold.copy(alpha = 0.5f),
                                RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                            )
                    )
                    Text(
                        text = "🏺",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (isReady) {
                    Text(
                        text = "CLAIM!",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkCanvas
                    )
                } else {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isReady) DarkCanvas else AccentMagenta
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress text
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = CoinGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${formatNumber(currentAmount)} / ${formatNumber(targetAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun JarSlot(
    slot: JarSlot,
    slotIndex: Int,
    onUnlock: () -> Unit,
    onClaim: () -> Unit
) {
    val progress = if (slot.isUnlocked) {
        (slot.currentAmount.toFloat() / slot.targetAmount).coerceIn(0f, 1f)
    } else 0f
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (slot.isUnlocked) {
                        if (slot.isReady) VipGold.copy(alpha = 0.3f) else DarkSurface
                    } else DarkCanvas
                )
                .then(
                    when {
                        slot.isReady -> Modifier.clickable(onClick = onClaim)
                        !slot.isUnlocked -> Modifier.clickable(onClick = onUnlock)
                        else -> Modifier
                    }
                )
                .border(
                    width = 2.dp,
                    color = when {
                        slot.isReady -> VipGold
                        slot.isUnlocked -> AccentMagenta.copy(alpha = 0.5f)
                        else -> TextTertiary
                    },
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (slot.isUnlocked) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏺", style = MaterialTheme.typography.titleLarge)
                    if (slot.isReady) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessGreen
                        )
                    } else {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentMagenta
                        )
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "No.$slotIndex",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
        }
        
        if (!slot.isUnlocked) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatNumber(slot.unlockCost),
                style = MaterialTheme.typography.labelSmall,
                color = CoinGold
            )
        }
    }
}

@Composable
private fun ContributorBadge(
    rank: Int,
    name: String,
    amount: Long
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when (rank) {
                        1 -> VipGold
                        2 -> Color(0xFFC0C0C0)
                        else -> Color(0xFFCD7F32)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = DarkCanvas
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = name.take(6),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1
        )
        Text(
            text = formatNumber(amount),
            style = MaterialTheme.typography.labelSmall,
            color = CoinGold
        )
    }
}

// Data classes
data class JarData(
    val currentAmount: Long,
    val targetAmount: Long,
    val isReady: Boolean,
    val slots: List<JarSlot>,
    val topContributors: List<JarContributor>
)

data class JarSlot(
    val id: String,
    val isUnlocked: Boolean,
    val currentAmount: Long,
    val targetAmount: Long,
    val unlockCost: Long,
    val isReady: Boolean
)

data class JarContributor(
    val userId: String,
    val name: String,
    val amount: Long
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

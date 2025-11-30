package com.aura.voicechat.ui.room.rocket

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aura.voicechat.ui.theme.*

/**
 * In-Room Rocket System
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Rocket fuel meter fills with gifts
 * - Launch animation when full
 * - Random rewards on launch
 * - Visual effects
 */
@Composable
fun RocketSystem(
    rocketData: RocketData,
    onFuel: (Long) -> Unit,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLaunchAnimation by remember { mutableStateOf(false) }
    
    // Launch animation
    val rocketOffset by animateFloatAsState(
        targetValue = if (showLaunchAnimation) -500f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        finishedListener = { showLaunchAnimation = false },
        label = "rocket_launch"
    )
    
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
                    text = "🚀",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rocket Launch",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Fuel the rocket to launch for rewards!",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Rocket Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Launch pad
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(DarkSurface, DarkCanvas, DarkSurface)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                
                // Rocket
                Column(
                    modifier = Modifier.offset(y = rocketOffset.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RocketVisual(
                        fuelLevel = rocketData.fuelLevel,
                        isReady = rocketData.isReady,
                        isLaunching = showLaunchAnimation
                    )
                }
                
                // Exhaust flames when launching
                if (showLaunchAnimation) {
                    ExhaustFlames(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-20).dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Fuel Progress
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Fuel",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "${(rocketData.fuelLevel * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (rocketData.isReady) SuccessGreen else AccentMagenta
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LinearProgressIndicator(
                    progress = { rocketData.fuelLevel },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = when {
                        rocketData.fuelLevel >= 1f -> SuccessGreen
                        rocketData.fuelLevel >= 0.7f -> VipGold
                        rocketData.fuelLevel >= 0.4f -> AccentMagenta
                        else -> AccentCyan
                    },
                    trackColor = DarkSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = CoinGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${formatNumber(rocketData.currentFuel)} / ${formatNumber(rocketData.targetFuel)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Launch Button
            Button(
                onClick = {
                    if (rocketData.isReady && !showLaunchAnimation) {
                        showLaunchAnimation = true
                        onLaunch()
                    }
                },
                enabled = rocketData.isReady && !showLaunchAnimation,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (rocketData.isReady) AccentMagenta else DarkSurface,
                    disabledContainerColor = DarkSurface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (showLaunchAnimation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (rocketData.isReady) "LAUNCH!" else "Not Ready",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Rewards Preview
            Text(
                text = "Possible Rewards",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RewardPreview("🎁", "Gift")
                RewardPreview("💰", "Coins")
                RewardPreview("💎", "Diamonds")
                RewardPreview("🎟️", "Frame")
            }
        }
    }
}

@Composable
private fun RocketVisual(
    fuelLevel: Float,
    isReady: Boolean,
    isLaunching: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rocket_shake")
    
    val shake by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Box(
        modifier = Modifier
            .size(80.dp, 120.dp)
            .then(
                if (isReady || isLaunching) Modifier.offset(x = shake.dp)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect when ready
        if (isReady) {
            Box(
                modifier = Modifier
                    .size(100.dp, 140.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AccentMagenta.copy(alpha = glowAlpha * 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        
        // Rocket body
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Nose cone
            Text(
                text = "🚀",
                style = MaterialTheme.typography.displayMedium
            )
            
            // Fuel indicator
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(DarkSurface)
                    .border(1.dp, AccentMagenta.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fuelLevel)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    when {
                                        fuelLevel >= 1f -> SuccessGreen
                                        fuelLevel >= 0.7f -> VipGold
                                        fuelLevel >= 0.4f -> AccentMagenta
                                        else -> AccentCyan
                                    },
                                    when {
                                        fuelLevel >= 1f -> SuccessGreen.copy(alpha = 0.5f)
                                        fuelLevel >= 0.7f -> VipGold.copy(alpha = 0.5f)
                                        fuelLevel >= 0.4f -> AccentMagenta.copy(alpha = 0.5f)
                                        else -> AccentCyan.copy(alpha = 0.5f)
                                    }
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun ExhaustFlames(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "flames")
    
    val flameSize by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_size"
    )
    
    Box(modifier = modifier.size(60.dp, 80.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(size.width / 2, 0f)
                lineTo(size.width * 0.2f, size.height * flameSize)
                lineTo(size.width / 2, size.height * 0.7f)
                lineTo(size.width * 0.8f, size.height * flameSize)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFF00),
                        Color(0xFFFF8C00),
                        Color(0xFFFF4500)
                    )
                ),
                style = Fill
            )
        }
    }
}

@Composable
private fun RewardPreview(emoji: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, style = MaterialTheme.typography.titleLarge)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
    }
}

// Data class
data class RocketData(
    val currentFuel: Long,
    val targetFuel: Long,
    val fuelLevel: Float = (currentFuel.toFloat() / targetFuel).coerceIn(0f, 1f),
    val isReady: Boolean = fuelLevel >= 1f
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

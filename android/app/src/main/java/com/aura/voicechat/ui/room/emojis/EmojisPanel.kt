package com.aura.voicechat.ui.room.emojis

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.voicechat.ui.theme.*

/**
 * In-Room Emojis System
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Standard emojis
 * - Animated emojis (VIP exclusive)
 * - Recently used
 * - Category tabs
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojisPanel(
    onEmojiSelected: (RoomEmoji) -> Unit,
    onDismiss: () -> Unit,
    userVipLevel: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(EmojiCategory.RECENT) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 350.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emojis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
            
            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedCategory.ordinal,
                containerColor = DarkCard,
                contentColor = AccentMagenta,
                edgePadding = 8.dp,
                indicator = {},
                divider = {}
            ) {
                EmojiCategory.entries.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedCategory == category) AccentMagenta
                                    else DarkSurface
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedCategory == category) androidx.compose.ui.graphics.Color.White else TextSecondary
                            )
                        }
                    }
                }
            }
            
            HorizontalDivider(color = DarkSurface)
            
            // Emojis Grid
            val emojis = getEmojisForCategory(selectedCategory)
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(emojis) { emoji ->
                    EmojiItem(
                        emoji = emoji,
                        isLocked = emoji.isVipOnly && userVipLevel < emoji.requiredVipLevel,
                        onClick = {
                            if (!(emoji.isVipOnly && userVipLevel < emoji.requiredVipLevel)) {
                                onEmojiSelected(emoji)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmojiItem(
    emoji: RoomEmoji,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (emoji.isAnimated) AccentMagenta.copy(alpha = 0.1f) else DarkSurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLocked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = emoji.emoji,
                    fontSize = 20.sp,
                    modifier = Modifier.alpha(0.3f)
                )
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = VipGold,
                    modifier = Modifier.size(12.dp)
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = emoji.emoji,
                    fontSize = 24.sp
                )
                if (emoji.isAnimated) {
                    Box(
                        modifier = Modifier
                            .background(AccentMagenta, RoundedCornerShape(2.dp))
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.alpha(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer(alpha = alpha)
)

private fun getEmojisForCategory(category: EmojiCategory): List<RoomEmoji> {
    return when (category) {
        EmojiCategory.RECENT -> listOf(
            RoomEmoji("1", "😂", false, false, 0),
            RoomEmoji("2", "❤️", false, false, 0),
            RoomEmoji("3", "😍", false, false, 0),
            RoomEmoji("4", "🔥", false, false, 0),
            RoomEmoji("5", "👍", false, false, 0)
        )
        EmojiCategory.SMILEYS -> listOf(
            RoomEmoji("s1", "😀", false, false, 0),
            RoomEmoji("s2", "😃", false, false, 0),
            RoomEmoji("s3", "😄", false, false, 0),
            RoomEmoji("s4", "😁", false, false, 0),
            RoomEmoji("s5", "😆", false, false, 0),
            RoomEmoji("s6", "😅", false, false, 0),
            RoomEmoji("s7", "🤣", false, false, 0),
            RoomEmoji("s8", "😂", false, false, 0),
            RoomEmoji("s9", "🙂", false, false, 0),
            RoomEmoji("s10", "😉", false, false, 0),
            RoomEmoji("s11", "😊", false, false, 0),
            RoomEmoji("s12", "😇", false, false, 0),
            RoomEmoji("s13", "🥰", false, false, 0),
            RoomEmoji("s14", "😍", false, false, 0),
            RoomEmoji("s15", "🤩", false, false, 0),
            RoomEmoji("s16", "😘", false, false, 0),
            RoomEmoji("s17", "😗", false, false, 0),
            RoomEmoji("s18", "😚", false, false, 0),
            RoomEmoji("s19", "😋", false, false, 0),
            RoomEmoji("s20", "😛", false, false, 0),
            RoomEmoji("s21", "😜", false, false, 0)
        )
        EmojiCategory.LOVE -> listOf(
            RoomEmoji("l1", "❤️", false, false, 0),
            RoomEmoji("l2", "🧡", false, false, 0),
            RoomEmoji("l3", "💛", false, false, 0),
            RoomEmoji("l4", "💚", false, false, 0),
            RoomEmoji("l5", "💙", false, false, 0),
            RoomEmoji("l6", "💜", false, false, 0),
            RoomEmoji("l7", "🖤", false, false, 0),
            RoomEmoji("l8", "🤍", false, false, 0),
            RoomEmoji("l9", "💕", false, false, 0),
            RoomEmoji("l10", "💞", false, false, 0),
            RoomEmoji("l11", "💓", false, false, 0),
            RoomEmoji("l12", "💗", false, false, 0),
            RoomEmoji("l13", "💖", false, false, 0),
            RoomEmoji("l14", "💘", false, false, 0),
            RoomEmoji("l15", "💝", false, false, 0),
            RoomEmoji("l16", "😻", false, false, 0),
            RoomEmoji("l17", "💑", false, false, 0),
            RoomEmoji("l18", "💏", false, false, 0),
            RoomEmoji("l19", "🥰", false, false, 0),
            RoomEmoji("l20", "😍", false, false, 0),
            RoomEmoji("l21", "😘", false, false, 0)
        )
        EmojiCategory.GESTURES -> listOf(
            RoomEmoji("g1", "👍", false, false, 0),
            RoomEmoji("g2", "👎", false, false, 0),
            RoomEmoji("g3", "👏", false, false, 0),
            RoomEmoji("g4", "🙌", false, false, 0),
            RoomEmoji("g5", "🤝", false, false, 0),
            RoomEmoji("g6", "✌️", false, false, 0),
            RoomEmoji("g7", "🤞", false, false, 0),
            RoomEmoji("g8", "🤟", false, false, 0),
            RoomEmoji("g9", "🤘", false, false, 0),
            RoomEmoji("g10", "👌", false, false, 0),
            RoomEmoji("g11", "🤌", false, false, 0),
            RoomEmoji("g12", "👋", false, false, 0),
            RoomEmoji("g13", "🤙", false, false, 0),
            RoomEmoji("g14", "💪", false, false, 0)
        )
        EmojiCategory.ANIMATED -> listOf(
            RoomEmoji("a1", "🎉", true, true, 1),
            RoomEmoji("a2", "🎊", true, true, 1),
            RoomEmoji("a3", "🌟", true, true, 1),
            RoomEmoji("a4", "✨", true, true, 1),
            RoomEmoji("a5", "💫", true, true, 2),
            RoomEmoji("a6", "🔥", true, true, 2),
            RoomEmoji("a7", "💖", true, true, 2),
            RoomEmoji("a8", "💎", true, true, 3),
            RoomEmoji("a9", "👑", true, true, 3),
            RoomEmoji("a10", "🦋", true, true, 3),
            RoomEmoji("a11", "🌈", true, true, 4),
            RoomEmoji("a12", "🎆", true, true, 4),
            RoomEmoji("a13", "🎇", true, true, 5),
            RoomEmoji("a14", "🏆", true, true, 5)
        )
        EmojiCategory.VIP -> listOf(
            RoomEmoji("v1", "💎", true, true, 3),
            RoomEmoji("v2", "👑", true, true, 5),
            RoomEmoji("v3", "🌟", true, true, 5),
            RoomEmoji("v4", "🔱", true, true, 7),
            RoomEmoji("v5", "⚜️", true, true, 7),
            RoomEmoji("v6", "💫", true, true, 8),
            RoomEmoji("v7", "🌙", true, true, 8),
            RoomEmoji("v8", "☀️", true, true, 9),
            RoomEmoji("v9", "🌠", true, true, 10),
            RoomEmoji("v10", "💝", true, true, 10)
        )
    }
}

// Data classes
data class RoomEmoji(
    val id: String,
    val emoji: String,
    val isAnimated: Boolean,
    val isVipOnly: Boolean,
    val requiredVipLevel: Int
)

enum class EmojiCategory(val displayName: String) {
    RECENT("Recent"),
    SMILEYS("Smileys"),
    LOVE("Love"),
    GESTURES("Gestures"),
    ANIMATED("Animated"),
    VIP("VIP")
}

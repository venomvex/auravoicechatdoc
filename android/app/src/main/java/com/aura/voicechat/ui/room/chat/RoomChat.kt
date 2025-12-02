package com.aura.voicechat.ui.room.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * In-Room Chat UI
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Real-time chat messages
 * - System announcements (gift notifications, etc.)
 * - User badges and levels
 * - Message input with emoji support
 */
@Composable
fun RoomChat(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onOpenEmojis: () -> Unit,
    onMentionUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkCanvas.copy(alpha = 0.85f))
    ) {
        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                when (message.type) {
                    MessageType.USER -> UserMessage(message, onMentionUser)
                    MessageType.SYSTEM -> SystemMessage(message)
                    MessageType.GIFT -> GiftNotification(message)
                    MessageType.JOIN -> JoinNotification(message)
                    MessageType.LEAVE -> LeaveNotification(message)
                }
            }
        }
        
        // Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCard)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji Button
            IconButton(onClick = onOpenEmojis) {
                Icon(
                    Icons.Default.EmojiEmotions,
                    contentDescription = "Emojis",
                    tint = AccentMagenta
                )
            }
            
            // Text Input
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Type a message...", color = TextTertiary) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentMagenta,
                    unfocusedBorderColor = DarkSurface
                )
            )
            
            // Send Button
            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText)
                        messageText = ""
                    }
                }
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (messageText.isNotBlank()) AccentMagenta else TextTertiary
                )
            }
        }
    }
}

@Composable
private fun UserMessage(
    message: ChatMessage,
    onMentionUser: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // Avatar (small)
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(DarkSurface),
            contentAlignment = Alignment.Center
        ) {
            if (message.avatar != null) {
                AsyncImage(
                    model = message.avatar,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = message.userName.first().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Message Content
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // User badges
                message.badges.forEach { badge ->
                    UserBadge(badge)
                    Spacer(modifier = Modifier.width(2.dp))
                }
                
                // Level badge
                if (message.level > 0) {
                    Box(
                        modifier = Modifier
                            .background(getLevelColor(message.level), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Lv.${message.level}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                
                // Username
                Text(
                    text = message.userName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = getUsernameColor(message.role)
                )
            }
            
            // Message text
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun SystemMessage(message: ChatMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message.content,
            style = MaterialTheme.typography.labelSmall,
            color = AccentCyan
        )
    }
}

@Composable
private fun GiftNotification(message: ChatMessage) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = AccentMagenta.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = AccentMagenta,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = VipGold, fontWeight = FontWeight.Bold)) {
                        append(message.userName)
                    }
                    append(" sent ")
                    withStyle(SpanStyle(color = AccentMagenta, fontWeight = FontWeight.Bold)) {
                        append(message.giftName ?: "a gift")
                    }
                    append(" x${message.giftCount} to ")
                    withStyle(SpanStyle(color = VipGold, fontWeight = FontWeight.Bold)) {
                        append(message.receiverName ?: "someone")
                    }
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun JoinNotification(message: ChatMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = SuccessGreen)) {
                    append("🎉 ")
                }
                withStyle(SpanStyle(color = AccentCyan, fontWeight = FontWeight.Bold)) {
                    append(message.userName)
                }
                withStyle(SpanStyle(color = TextSecondary)) {
                    append(" joined the room")
                }
            },
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun LeaveNotification(message: ChatMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${message.userName} left the room",
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
    }
}

@Composable
private fun UserBadge(badge: UserBadge) {
    when (badge) {
        UserBadge.VIP -> Box(
            modifier = Modifier
                .background(VipGold, RoundedCornerShape(3.dp))
                .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
            Text("VIP", style = MaterialTheme.typography.labelSmall, color = DarkCanvas, fontWeight = FontWeight.Bold)
        }
        UserBadge.OWNER -> Box(
            modifier = Modifier
                .background(AccentMagenta, RoundedCornerShape(3.dp))
                .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
            Text("👑", style = MaterialTheme.typography.labelSmall)
        }
        UserBadge.ADMIN -> Box(
            modifier = Modifier
                .background(AccentCyan, RoundedCornerShape(3.dp))
                .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
            Text("⚡", style = MaterialTheme.typography.labelSmall)
        }
        UserBadge.MODERATOR -> Box(
            modifier = Modifier
                .background(SuccessGreen, RoundedCornerShape(3.dp))
                .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
            Text("🛡️", style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun getLevelColor(level: Int): Color {
    return when {
        level >= 60 -> Color(0xFFFF4500) // Legendary
        level >= 40 -> Color(0xFFFFD700) // Epic
        level >= 20 -> Color(0xFF9370DB) // Rare
        level >= 10 -> Color(0xFF00CED1) // Uncommon
        else -> Color(0xFF808080) // Common
    }
}

private fun getUsernameColor(role: UserRole): Color {
    return when (role) {
        UserRole.OWNER -> AccentMagenta
        UserRole.ADMIN -> AccentCyan
        UserRole.MODERATOR -> SuccessGreen
        UserRole.VIP -> VipGold
        UserRole.MEMBER -> TextPrimary
    }
}

// Data classes
data class ChatMessage(
    val id: String,
    val type: MessageType,
    val userId: String,
    val userName: String,
    val avatar: String?,
    val content: String,
    val level: Int = 0,
    val role: UserRole = UserRole.MEMBER,
    val badges: List<UserBadge> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    // For gift messages
    val giftName: String? = null,
    val giftCount: Int = 0,
    val receiverName: String? = null
)

enum class MessageType {
    USER, SYSTEM, GIFT, JOIN, LEAVE
}

enum class UserRole {
    OWNER, ADMIN, MODERATOR, VIP, MEMBER
}

enum class UserBadge {
    VIP, OWNER, ADMIN, MODERATOR
}

package com.aura.voicechat.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * Messages/Notifications Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: MessagesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    
    val tabs = listOf("Messages", "Notifications", "System")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Mark all as read
                    if (uiState.unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text("Read All", color = AccentMagenta)
                        }
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
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkCanvas,
                contentColor = AccentMagenta
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    title,
                                    color = if (selectedTab == index) AccentMagenta else TextSecondary
                                )
                                val count = when (index) {
                                    0 -> uiState.unreadMessages
                                    1 -> uiState.unreadNotifications
                                    2 -> uiState.unreadSystem
                                    else -> 0
                                }
                                if (count > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge { Text(count.toString()) }
                                }
                            }
                        }
                    )
                }
            }
            
            // Content
            when (selectedTab) {
                0 -> MessagesList(
                    conversations = uiState.conversations,
                    onConversationClick = onNavigateToChat
                )
                1 -> NotificationsList(
                    notifications = uiState.notifications,
                    onNotificationClick = { viewModel.handleNotification(it) }
                )
                2 -> SystemMessagesList(
                    messages = uiState.systemMessages
                )
            }
        }
    }
}

@Composable
private fun MessagesList(
    conversations: List<Conversation>,
    onConversationClick: (String) -> Unit
) {
    if (conversations.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Message,
            message = "No messages yet"
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(conversations) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    onClick = { onConversationClick(conversation.id) }
                )
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (conversation.unreadCount > 0) DarkCard else Color.Transparent
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(DarkSurface),
                    contentAlignment = Alignment.Center
                ) {
                    if (conversation.avatar != null) {
                        AsyncImage(
                            model = conversation.avatar,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
                if (conversation.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = conversation.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        color = TextPrimary
                    )
                    Text(
                        text = conversation.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (conversation.unreadCount > 0) TextPrimary else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (conversation.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = AccentMagenta
                        ) {
                            Text(conversation.unreadCount.toString())
                        }
                    }
                }
            }
        }
    }
    
    HorizontalDivider(color = DarkSurface, thickness = 0.5.dp)
}

@Composable
private fun NotificationsList(
    notifications: List<NotificationItem>,
    onNotificationClick: (String) -> Unit
) {
    if (notifications.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Notifications,
            message = "No notifications"
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(notifications) { notification ->
                NotificationItemCard(
                    notification = notification,
                    onClick = { onNotificationClick(notification.id) }
                )
            }
        }
    }
}

@Composable
private fun NotificationItemCard(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) DarkCard else Color.Transparent
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        when (notification.type) {
                            "gift" -> AccentMagenta.copy(alpha = 0.2f)
                            "follow" -> AccentCyan.copy(alpha = 0.2f)
                            "like" -> ErrorRed.copy(alpha = 0.2f)
                            "friend" -> SuccessGreen.copy(alpha = 0.2f)
                            else -> DarkSurface
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (notification.type) {
                        "gift" -> Icons.Default.CardGiftcard
                        "follow" -> Icons.Default.PersonAdd
                        "like" -> Icons.Default.Favorite
                        "friend" -> Icons.Default.People
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = when (notification.type) {
                        "gift" -> AccentMagenta
                        "follow" -> AccentCyan
                        "like" -> ErrorRed
                        "friend" -> SuccessGreen
                        else -> TextSecondary
                    },
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal,
                    color = TextPrimary
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = notification.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
            
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AccentMagenta, CircleShape)
                )
            }
        }
    }
    
    HorizontalDivider(color = DarkSurface, thickness = 0.5.dp)
}

@Composable
private fun SystemMessagesList(messages: List<SystemMessage>) {
    if (messages.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Announcement,
            message = "No system messages"
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = null,
                                tint = VipGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = message.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = TextSecondary)
        }
    }
}

// Data classes
data class Conversation(
    val id: String,
    val name: String,
    val avatar: String?,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isOnline: Boolean
)

data class NotificationItem(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean
)

data class SystemMessage(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: String
)

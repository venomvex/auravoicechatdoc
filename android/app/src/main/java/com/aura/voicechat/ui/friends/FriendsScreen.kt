package com.aura.voicechat.ui.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * Friends System Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Friends list
 * - Friend requests
 * - Friend search
 * - Friend intimacy level
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showAddFriendDialog by remember { mutableStateOf(false) }
    
    val tabs = listOf("Friends", "Requests", "Sent")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Friends (${uiState.friends.size})") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddFriendDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Friend")
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
                                if (index == 1 && uiState.requests.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge { Text(uiState.requests.size.toString()) }
                                }
                            }
                        }
                    )
                }
            }
            
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.search(it) },
                placeholder = { Text("Search friends...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            
            // Content
            when (selectedTab) {
                0 -> FriendsListTab(
                    friends = uiState.filteredFriends,
                    onFriendClick = onNavigateToProfile,
                    onRemoveFriend = { viewModel.removeFriend(it) }
                )
                1 -> RequestsTab(
                    requests = uiState.requests,
                    onAccept = { viewModel.acceptRequest(it) },
                    onReject = { viewModel.rejectRequest(it) },
                    onProfileClick = onNavigateToProfile
                )
                2 -> SentRequestsTab(
                    sentRequests = uiState.sentRequests,
                    onCancel = { viewModel.cancelRequest(it) },
                    onProfileClick = onNavigateToProfile
                )
            }
        }
    }
    
    // Add Friend Dialog
    if (showAddFriendDialog) {
        AddFriendDialog(
            onDismiss = { showAddFriendDialog = false },
            onSendRequest = { userId ->
                viewModel.sendFriendRequest(userId)
                showAddFriendDialog = false
            }
        )
    }
}

@Composable
private fun FriendsListTab(
    friends: List<Friend>,
    onFriendClick: (String) -> Unit,
    onRemoveFriend: (String) -> Unit
) {
    if (friends.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No friends yet", color = TextSecondary)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(friends) { friend ->
                FriendItem(
                    friend = friend,
                    onClick = { onFriendClick(friend.userId) },
                    onRemove = { onRemoveFriend(friend.userId) }
                )
            }
        }
    }
}

@Composable
private fun FriendItem(
    friend: Friend,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online status
            Box {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DarkSurface),
                    contentAlignment = Alignment.Center
                ) {
                    if (friend.avatar != null) {
                        AsyncImage(
                            model = friend.avatar,
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
                // Online indicator
                if (friend.isOnline) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = friend.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    if (friend.intimacyLevel > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IntimacyBadge(level = friend.intimacyLevel)
                    }
                }
                Text(
                    text = if (friend.isOnline) "Online" else "Last seen ${friend.lastSeen}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (friend.isOnline) SuccessGreen else TextTertiary
                )
            }
            
            // Actions
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.PersonRemove,
                    contentDescription = "Remove",
                    tint = TextTertiary
                )
            }
        }
    }
}

@Composable
private fun IntimacyBadge(level: Int) {
    val (color, text) = when {
        level >= 10 -> VipGold to "💕"
        level >= 7 -> AccentMagenta to "💗"
        level >= 4 -> AccentCyan to "💙"
        else -> TextTertiary to "🤍"
    }
    
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$text Lv.$level",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun RequestsTab(
    requests: List<FriendRequest>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onProfileClick: (String) -> Unit
) {
    if (requests.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No pending requests", color = TextSecondary)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(requests) { request ->
                RequestItem(
                    request = request,
                    onAccept = { onAccept(request.userId) },
                    onReject = { onReject(request.userId) },
                    onClick = { onProfileClick(request.userId) }
                )
            }
        }
    }
}

@Composable
private fun RequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                if (request.avatar != null) {
                    AsyncImage(
                        model = request.avatar,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary)
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = request.timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onAccept,
                    modifier = Modifier
                        .size(36.dp)
                        .background(SuccessGreen, CircleShape)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color.White)
                }
                IconButton(
                    onClick = onReject,
                    modifier = Modifier
                        .size(36.dp)
                        .background(ErrorRed, CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Reject", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SentRequestsTab(
    sentRequests: List<FriendRequest>,
    onCancel: (String) -> Unit,
    onProfileClick: (String) -> Unit
) {
    if (sentRequests.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No sent requests", color = TextSecondary)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sentRequests) { request ->
                Card(
                    onClick = { onProfileClick(request.userId) },
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(DarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary)
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = request.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Pending",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarningOrange
                            )
                        }
                        
                        TextButton(onClick = { onCancel(request.userId) }) {
                            Text("Cancel", color = ErrorRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddFriendDialog(
    onDismiss: () -> Unit,
    onSendRequest: (String) -> Unit
) {
    var userId by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Add Friend", color = TextPrimary) },
        text = {
            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = { Text("User ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSendRequest(userId) },
                enabled = userId.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta)
            ) {
                Text("Send Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// Data classes
data class Friend(
    val userId: String,
    val name: String,
    val avatar: String?,
    val isOnline: Boolean,
    val lastSeen: String,
    val intimacyLevel: Int
)

data class FriendRequest(
    val userId: String,
    val name: String,
    val avatar: String?,
    val timeAgo: String
)

package com.aura.voicechat.ui.search

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * Search Screen - Find users and rooms
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToRoom: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    
    val tabs = listOf("Users", "Rooms", "ID Search")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
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
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.search(it, selectedTab) },
                placeholder = { 
                    Text(
                        when (selectedTab) {
                            0 -> "Search users..."
                            1 -> "Search rooms..."
                            else -> "Enter ID..."
                        }
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            
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
                            Text(
                                title,
                                color = if (selectedTab == index) AccentMagenta else TextSecondary
                            )
                        }
                    )
                }
            }
            
            // Recent Searches (when empty)
            if (uiState.searchQuery.isEmpty() && uiState.recentSearches.isNotEmpty()) {
                RecentSearches(
                    searches = uiState.recentSearches,
                    onSearchClick = { viewModel.search(it, selectedTab) },
                    onClearAll = { viewModel.clearRecentSearches() }
                )
            }
            
            // Results
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentMagenta)
                }
            } else {
                when (selectedTab) {
                    0 -> UserResults(
                        users = uiState.userResults,
                        onUserClick = onNavigateToProfile
                    )
                    1 -> RoomResults(
                        rooms = uiState.roomResults,
                        onRoomClick = onNavigateToRoom
                    )
                    2 -> IdSearchResult(
                        result = uiState.idSearchResult,
                        onUserClick = onNavigateToProfile,
                        onRoomClick = onNavigateToRoom
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSearches(
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary
            )
            TextButton(onClick = onClearAll) {
                Text("Clear All", color = AccentMagenta)
            }
        }
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searches) { search ->
                AssistChip(
                    onClick = { onSearchClick(search) },
                    label = { Text(search) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun UserResults(
    users: List<SearchUser>,
    onUserClick: (String) -> Unit
) {
    if (users.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No users found", color = TextSecondary)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                Card(
                    onClick = { onUserClick(user.userId) },
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
                            if (user.avatar != null) {
                                AsyncImage(
                                    model = user.avatar,
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
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "ID: ${user.userId} • Lv.${user.level}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        
                        if (user.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(SuccessGreen, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomResults(
    rooms: List<SearchRoom>,
    onRoomClick: (String) -> Unit
) {
    if (rooms.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No rooms found", color = TextSecondary)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(rooms) { room ->
                Card(
                    onClick = { onRoomClick(room.roomId) },
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
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (room.cover != null) {
                                AsyncImage(
                                    model = room.cover,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    Icons.Default.MeetingRoom,
                                    contentDescription = null,
                                    tint = AccentMagenta,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = room.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = room.ownerName,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${room.memberCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary
                                )
                            }
                        }
                        
                        Button(
                            onClick = { onRoomClick(room.roomId) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Join", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdSearchResult(
    result: IdSearchResult?,
    onUserClick: (String) -> Unit,
    onRoomClick: (String) -> Unit
) {
    if (result == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Numbers,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Enter a user or room ID", color = TextSecondary)
            }
        }
    } else {
        // Show found result
        Card(
            onClick = {
                when (result.type) {
                    "user" -> onUserClick(result.id)
                    "room" -> onRoomClick(result.id)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DarkSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (result.type == "user") Icons.Default.Person else Icons.Default.MeetingRoom,
                        contentDescription = null,
                        tint = AccentMagenta,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "ID: ${result.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = if (result.type == "user") "User" else "Room",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentMagenta
                    )
                }
                
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextTertiary
                )
            }
        }
    }
}

// Data classes
data class SearchUser(
    val userId: String,
    val name: String,
    val avatar: String?,
    val level: Int,
    val isOnline: Boolean
)

data class SearchRoom(
    val roomId: String,
    val name: String,
    val cover: String?,
    val ownerName: String,
    val memberCount: Int
)

data class IdSearchResult(
    val id: String,
    val name: String,
    val type: String // "user" or "room"
)

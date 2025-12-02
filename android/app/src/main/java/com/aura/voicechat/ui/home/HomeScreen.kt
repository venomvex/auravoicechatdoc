package com.aura.voicechat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.domain.model.RoomCard
import com.aura.voicechat.domain.model.RoomType
import com.aura.voicechat.ui.theme.*

/**
 * Home Screen with Mine/Popular tabs, room cards, banner carousel, Rewards FAB
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Home icon in top-left that either shows "Create Your Own" for new users or joins their room
 * - Mine tab with Recent and Following sub-tabs
 * - Popular tab with Popular Rooms and Video/Music Rooms sub-tabs
 * - Full square room cards
 * - Bottom navigation on all screens except game rooms
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRoom: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToDailyRewards: () -> Unit,
    onNavigateToKyc: () -> Unit,
    onNavigateToStore: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToRanking: () -> Unit = {},
    onNavigateToGames: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedMainTab by remember { mutableStateOf(0) }
    var selectedMineSubTab by remember { mutableStateOf(0) } // 0: Recent, 1: Following
    var selectedPopularSubTab by remember { mutableStateOf(0) } // 0: Popular Rooms, 1: Video/Music
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    
    val mainTabs = listOf("Mine", "Popular")
    val mineSubTabs = listOf("Recent", "Following")
    val popularSubTabs = listOf("Popular Rooms", "Video/Music")
    
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    // Home icon - for new users shows "Create Your Own", for users with rooms joins their room
                    IconButton(
                        onClick = {
                            val action = viewModel.handleHomeIconClick()
                            when (action) {
                                is HomeAction.ShowCreateRoom -> showCreateRoomDialog = true
                                is HomeAction.JoinMyRoom -> if (action.roomId.isNotEmpty()) {
                                    onNavigateToRoom(action.roomId)
                                } else {
                                    showCreateRoomDialog = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = AccentMagenta
                        )
                    }
                },
                title = {
                    Text(
                        text = "Aura Voice Chat",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Messages */ }) {
                        Icon(Icons.Default.Email, contentDescription = "Messages")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCanvas
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToDailyRewards,
                containerColor = AccentMagenta,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.CardGiftcard, contentDescription = null) },
                text = { Text("Rewards") }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSearch,
                    icon = { Icon(Icons.Default.Explore, contentDescription = null) },
                    label = { Text("Explore") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showCreateRoomDialog = true },
                    icon = { 
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(AccentMagenta, Purple80)
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    label = { Text("Create") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToWallet,
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                    label = { Text("Wallet") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToProfile("me") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Me") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkCanvas)
        ) {
            // Banner Carousel
            BannerCarousel(
                banners = uiState.banners,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(16.dp)
            )
            
            // Quick Actions
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuickActionChip(
                        icon = Icons.Default.Star,
                        text = "VIP",
                        onClick = { }
                    )
                }
                item {
                    QuickActionChip(
                        icon = Icons.Default.Favorite,
                        text = "CP",
                        onClick = { }
                    )
                }
                item {
                    QuickActionChip(
                        icon = Icons.Default.Badge,
                        text = "Medals",
                        onClick = { }
                    )
                }
                item {
                    QuickActionChip(
                        icon = Icons.Default.VerifiedUser,
                        text = "KYC",
                        onClick = onNavigateToKyc
                    )
                }
                item {
                    QuickActionChip(
                        icon = Icons.Default.SportsEsports,
                        text = "Games",
                        onClick = onNavigateToGames
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Main Tab Row (Mine / Popular)
            TabRow(
                selectedTabIndex = selectedMainTab,
                containerColor = Color.Transparent,
                contentColor = Purple80
            ) {
                mainTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedMainTab == index,
                        onClick = { selectedMainTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedMainTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
            
            // Sub-tabs based on main tab selection
            when (selectedMainTab) {
                0 -> {
                    // Mine tab - Show user's room or "Create Your Own" + Recent/Following
                    MineTabContent(
                        uiState = uiState,
                        selectedSubTab = selectedMineSubTab,
                        onSubTabChange = { selectedMineSubTab = it },
                        subTabs = mineSubTabs,
                        onNavigateToRoom = onNavigateToRoom,
                        onCreateRoom = { showCreateRoomDialog = true }
                    )
                }
                1 -> {
                    // Popular tab - Popular Rooms / Video/Music Rooms
                    PopularTabContent(
                        uiState = uiState,
                        selectedSubTab = selectedPopularSubTab,
                        onSubTabChange = { selectedPopularSubTab = it },
                        subTabs = popularSubTabs,
                        onNavigateToRoom = onNavigateToRoom
                    )
                }
            }
        }
    }
    
    // Create Room Dialog
    if (showCreateRoomDialog) {
        CreateRoomDialog(
            onDismiss = { showCreateRoomDialog = false },
            onCreate = { name, picture ->
                // TODO: Create room via API
                showCreateRoomDialog = false
            }
        )
    }
}

@Composable
private fun MineTabContent(
    uiState: HomeUiState,
    selectedSubTab: Int,
    onSubTabChange: (Int) -> Unit,
    subTabs: List<String>,
    onNavigateToRoom: (String) -> Unit,
    onCreateRoom: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // My Room / Create Your Own section at top
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    if (uiState.isNewUser || uiState.myRooms.isEmpty()) {
                        onCreateRoom()
                    } else {
                        uiState.myRooms.firstOrNull()?.let { onNavigateToRoom(it.id) }
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isNewUser) AccentMagenta.copy(alpha = 0.2f) else DarkCard
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (uiState.isNewUser) AccentMagenta.copy(alpha = 0.3f)
                            else Purple40.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (uiState.isNewUser) Icons.Default.Add else Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (uiState.isNewUser) AccentMagenta else Purple80,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (uiState.isNewUser) "Create Your Own" else uiState.myRooms.firstOrNull()?.name ?: "My Room",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (uiState.isNewUser) "Set name & picture to get started" else "Tap to enter your room",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextTertiary
                )
            }
        }
        
        // Recent / Following sub-tabs
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = DarkCard,
            contentColor = AccentMagenta,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            subTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { onSubTabChange(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedSubTab == index) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Room grid (Recent or Following)
        val rooms = when (selectedSubTab) {
            0 -> uiState.recentRooms.ifEmpty { uiState.popularRooms.take(4) } // Recent rooms
            else -> uiState.followingRooms.ifEmpty { emptyList() } // People following who are in rooms
        }
        
        if (rooms.isEmpty()) {
            EmptyState(
                message = if (selectedSubTab == 0) "No recent rooms" else "No one you follow is in a room",
                icon = if (selectedSubTab == 0) Icons.Default.History else Icons.Default.People
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(rooms) { room ->
                    SquareRoomCard(
                        room = room,
                        onClick = { onNavigateToRoom(room.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PopularTabContent(
    uiState: HomeUiState,
    selectedSubTab: Int,
    onSubTabChange: (Int) -> Unit,
    subTabs: List<String>,
    onNavigateToRoom: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Popular Rooms / Video/Music sub-tabs
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = DarkCard,
            contentColor = AccentMagenta,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            subTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { onSubTabChange(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedSubTab == index) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Room grid
        val rooms = when (selectedSubTab) {
            0 -> uiState.popularRooms.sortedByDescending { it.userCount } // Popular by active users
            else -> uiState.popularRooms.filter { it.type == RoomType.VIDEO || it.type == RoomType.MUSIC } // Video/Music rooms
        }
        
        if (rooms.isEmpty()) {
            EmptyState(
                message = if (selectedSubTab == 0) "No popular rooms" else "No video/music rooms",
                icon = if (selectedSubTab == 0) Icons.Default.TrendingUp else Icons.Default.VideoLibrary
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(rooms) { room ->
                    SquareRoomCard(
                        room = room,
                        onClick = { onNavigateToRoom(room.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SquareRoomCard(
    room: RoomCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Room cover image or gradient background
            if (room.coverImage != null) {
                AsyncImage(
                    model = room.coverImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Purple40.copy(alpha = 0.6f),
                                    AccentMagenta.copy(alpha = 0.3f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when (room.type) {
                            RoomType.VOICE -> Icons.Default.Mic
                            RoomType.VIDEO -> Icons.Default.Videocam
                            RoomType.MUSIC -> Icons.Default.MusicNote
                        },
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            // Overlay gradient for text readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${room.userCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentCyan
                        )
                    }
                }
            }
            
            // Live indicator
            if (room.isLive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(ErrorRed, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Room type indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(DarkCard.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    when (room.type) {
                        RoomType.VOICE -> Icons.Default.Mic
                        RoomType.VIDEO -> Icons.Default.Videocam
                        RoomType.MUSIC -> Icons.Default.MusicNote
                    },
                    contentDescription = null,
                    tint = Purple80,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextTertiary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, picture: String?) -> Unit
) {
    var roomName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "Create Your Room",
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Set up your room with a name and picture",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Room picture placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .clickable { /* Pick image */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add Photo",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Room Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentMagenta,
                        focusedLabelColor = AccentMagenta
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(roomName, null) },
                enabled = roomName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun BannerCarousel(
    banners: List<String>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { maxOf(banners.size, 1) })
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Box {
            HorizontalPager(state = pagerState) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Purple40, AccentMagenta)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Welcome to Aura Voice Chat!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            // Indicators
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(maxOf(banners.size, 1)) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                            .background(
                                color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(text) },
        selected = false,
        leadingIcon = {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = DarkCard,
            labelColor = TextPrimary
        )
    )
}

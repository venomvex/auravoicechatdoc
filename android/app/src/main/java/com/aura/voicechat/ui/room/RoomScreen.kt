package com.aura.voicechat.ui.room

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.data.model.*
import com.aura.voicechat.domain.model.Seat
import com.aura.voicechat.ui.room.jar.JarData
import com.aura.voicechat.ui.room.jar.JarSlot
import com.aura.voicechat.ui.room.jar.JarContributor
import com.aura.voicechat.ui.room.jar.JarSystem
import com.aura.voicechat.ui.theme.*

/**
 * Voice/Video Room Screen with full features (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Room settings (live)
 * - Room jar (live)
 * - Room rankings with trophy icon (daily/weekly/monthly)
 * - Live chat messages
 * - Mute/Unmute
 * - Owner/Admin powers (seat lock/unlock, kick, ban, clear chat)
 * - Games slider (Lucky 77 Pro, Lucky 777, Lucky Fruit, Greedy Baby, Lucky Wheel, Rocket)
 * - Events slider (Recharge, CP ranking, VIP spin, etc.)
 * - Video player with Cinema mode
 * - Lucky bags
 * - Activity notifications toggle
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    roomId: String,
    onNavigateBack: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showGiftSheet by remember { mutableStateOf(false) }
    var showVideoDialog by remember { mutableStateOf(false) }
    var videoUrl by remember { mutableStateOf("") }
    
    LaunchedEffect(roomId) {
        viewModel.loadRoom(roomId)
    }
    
    // Show snackbar for messages/errors
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.message, uiState.error) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.dismissError()
        }
    }
    
    // Cinema mode layout
    if (uiState.isCinemaMode && uiState.videoPlayer?.isPlaying == true) {
        CinemaRoomLayout(
            uiState = uiState,
            onExitCinema = { viewModel.toggleCinemaMode() },
            onNavigateBack = onNavigateBack,
            viewModel = viewModel
        )
        return
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RoomTopBar(
                roomName = uiState.room?.name ?: "Room",
                userCount = uiState.room?.currentUsers ?: 0,
                rankings = uiState.rankings,
                onNavigateBack = onNavigateBack,
                onSettingsClick = { viewModel.openSettings() },
                onRankingClick = { type -> viewModel.selectRankingType(type) }
            )
        },
        bottomBar = {
            RoomBottomBar(
                isMuted = uiState.isMuted,
                onMuteToggle = { viewModel.toggleMute() },
                onGiftClick = { showGiftSheet = true },
                onMessageClick = { /* Focus chat input */ },
                onMoreClick = { viewModel.openMoreMenu() }
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
        ) {
            // Events Slider (Top)
            if (uiState.events.isNotEmpty()) {
                EventsSlider(
                    events = uiState.events,
                    onEventClick = { viewModel.openEvent(it) }
                )
            }
            
            // Room Owner Section
            uiState.room?.let { room ->
                RoomOwnerCard(
                    ownerName = room.ownerName,
                    ownerAvatar = room.ownerAvatar,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Room Jar (if available)
            uiState.jar?.let { jar ->
                JarSystem(
                    jarData = JarData(
                        currentAmount = jar.currentAmount,
                        targetAmount = jar.targetAmount,
                        isReady = jar.isReady,
                        slots = jar.slots.map { slot ->
                            JarSlot(
                                id = slot.id,
                                isUnlocked = slot.isUnlocked,
                                currentAmount = slot.currentAmount,
                                targetAmount = slot.targetAmount,
                                unlockCost = slot.unlockCost,
                                isReady = slot.isReady
                            )
                        },
                        topContributors = jar.topContributors.map { c ->
                            JarContributor(c.userId, c.name, c.amount)
                        }
                    ),
                    onContribute = { viewModel.contributeToJar(it) },
                    onClaimJar = { viewModel.claimJar(it) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Seats Grid (8 or 16 seats)
            val seats = uiState.room?.seats ?: emptyList()
            val columns = if (seats.size <= 8) 4 else 4
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(seats) { seat ->
                    SeatItem(
                        seat = seat,
                        onClick = { viewModel.onSeatClick(seat.position) }
                    )
                }
            }
            
            // Games Slider (Above bottom bar)
            GamesSlider(
                games = uiState.games,
                rocketLevel = 5, // TODO: Get from room state
                onGameClick = { viewModel.openGame(it) }
            )
            
            // Live Chat Preview
            LiveChatPreview(
                messages = uiState.messages,
                onSendMessage = { viewModel.sendMessage(it) }
            )
        }
    }
    
    // Gift Bottom Sheet
    if (showGiftSheet) {
        ModalBottomSheet(
            onDismissRequest = { showGiftSheet = false },
            containerColor = DarkSurface
        ) {
            GiftBottomSheet(
                onGiftSelect = { giftId ->
                    // Send to room owner by default
                    uiState.room?.ownerId?.let { ownerId ->
                        viewModel.sendGift(giftId, ownerId)
                    }
                    showGiftSheet = false
                }
            )
        }
    }
    
    // Settings Bottom Sheet
    if (uiState.showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeSettings() },
            containerColor = DarkSurface
        ) {
            RoomSettingsSheet(
                settings = uiState.settings,
                activities = uiState.activities,
                onUpdateSettings = { viewModel.updateSettings(it) },
                onUpdateActivities = { viewModel.updateActivities(it) },
                onClose = { viewModel.closeSettings() }
            )
        }
    }
    
    // More Options Bottom Sheet
    if (uiState.showMoreMenu) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeMoreMenu() },
            containerColor = DarkSurface
        ) {
            MoreOptionsSheet(
                isOwner = viewModel.isOwner(),
                isAdmin = viewModel.isAdmin(),
                onPlayVideo = { showVideoDialog = true },
                onClearChat = { viewModel.clearChat() },
                onClose = { viewModel.closeMoreMenu() }
            )
        }
    }
    
    // Video URL Dialog
    if (showVideoDialog) {
        AlertDialog(
            onDismissRequest = { showVideoDialog = false },
            title = { Text("Play Video", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("YouTube URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (videoUrl.isNotBlank()) {
                        viewModel.playVideo(videoUrl)
                        showVideoDialog = false
                        videoUrl = ""
                    }
                }) {
                    Text("Play")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVideoDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = DarkCard
        )
    }
    
    // Game Sheet
    if (uiState.showGameSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeGameSheet() },
            containerColor = DarkSurface
        ) {
            GameSheet(
                gameType = uiState.selectedGame ?: "",
                onClose = { viewModel.closeGameSheet() }
            )
        }
    }
    
    // Seat Action Sheet
    if (uiState.showSeatActionSheet && uiState.selectedSeatPosition != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeSeatActionSheet() },
            containerColor = DarkSurface
        ) {
            SeatActionSheet(
                seatPosition = uiState.selectedSeatPosition!!,
                userId = uiState.selectedUserId,
                isOwner = viewModel.isOwner(),
                isAdmin = viewModel.isAdmin(),
                onLock = { viewModel.lockSeat(uiState.selectedSeatPosition!!) },
                onUnlock = { viewModel.unlockSeat(uiState.selectedSeatPosition!!) },
                onKick = { viewModel.kickFromSeat(uiState.selectedSeatPosition!!) },
                onMute = { viewModel.muteUserOnSeat(uiState.selectedSeatPosition!!) },
                onUnmute = { viewModel.unmuteUserOnSeat(uiState.selectedSeatPosition!!) },
                onClose = { viewModel.closeSeatActionSheet() }
            )
        }
    }
}

@Composable
private fun RoomTopBar(
    roomName: String,
    userCount: Int,
    rankings: RoomRankingsDto?,
    onNavigateBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onRankingClick: (String) -> Unit
) {
    Surface(
        color = DarkCanvas,
        tonalElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Leave", tint = TextPrimary)
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = roomName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "$userCount users",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                // Trophy Icon (Rankings)
                IconButton(onClick = { onRankingClick("daily") }) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Rankings",
                        tint = VipGold
                    )
                }
                
                IconButton(onClick = { /* Share */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = TextPrimary)
                }
                
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                }
            }
            
            // Rankings Tabs
            if (rankings != null) {
                RankingsTabs(
                    rankings = rankings,
                    onTabClick = onRankingClick
                )
            }
        }
    }
}

@Composable
private fun RankingsTabs(
    rankings: RoomRankingsDto,
    onTabClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Daily" to rankings.daily, "Weekly" to rankings.weekly, "Monthly" to rankings.monthly)
    
    Column {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkCard,
            contentColor = AccentMagenta
        ) {
            tabs.forEachIndexed { index, (title, _) ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                        onTabClick(title.lowercase())
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTab == index) VipGold else TextTertiary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(title)
                        }
                    }
                )
            }
        }
        
        // Top 3 contributors preview
        val currentRankings = tabs[selectedTab].second.take(3)
        if (currentRankings.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                currentRankings.forEachIndexed { index, entry ->
                    RankingPreviewItem(rank = index + 1, entry = entry)
                }
            }
        }
    }
}

@Composable
private fun RankingPreviewItem(rank: Int, entry: RoomRankingEntryDto) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
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
        Text(
            text = entry.userName.take(6),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1
        )
        Text(
            text = formatNumber(entry.giftsValue),
            style = MaterialTheme.typography.labelSmall,
            color = CoinGold
        )
    }
}

@Composable
private fun EventsSlider(
    events: List<RoomEventDto>,
    onEventClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(events) { event ->
            Card(
                modifier = Modifier
                    .width(120.dp)
                    .height(60.dp)
                    .clickable { onEventClick(event.id) },
                colors = CardDefaults.cardColors(containerColor = AccentMagenta.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (event.bannerUrl != null) {
                        AsyncImage(
                            model = event.bannerUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GamesSlider(
    games: List<RoomGameDto>,
    rocketLevel: Int,
    onGameClick: (String) -> Unit
) {
    val defaultGames = listOf(
        "lucky_77_pro" to "Lucky 77 Pro",
        "lucky_777" to "Lucky 777",
        "lucky_fruit" to "Lucky Fruit",
        "greedy_baby" to "Greedy Baby",
        "lucky_wheel" to "Lucky Wheel",
        "rocket" to "Rocket"
    )
    
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(if (games.isEmpty()) defaultGames else games.map { it.type to it.name }) { (type, name) ->
            Column(
                modifier = Modifier
                    .clickable { onGameClick(type) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(AccentMagenta, Purple80)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (type) {
                            "rocket" -> Icons.Default.RocketLaunch
                            "lucky_wheel" -> Icons.Default.Refresh
                            else -> Icons.Default.Casino
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    if (type == "rocket") {
                        // Show rocket level badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .background(VipGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$rocketLevel",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.sp,
                                color = DarkCanvas
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun LiveChatPreview(
    messages: List<RoomMessageDto>,
    onSendMessage: (String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(12.dp)
        ) {
            Text(
                text = "Chat",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(messages) { message ->
                    ChatMessageItem(message)
                }
            }
            
            // Input field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
}

@Composable
private fun ChatMessageItem(message: RoomMessageDto) {
    when (message.type) {
        "gift" -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = AccentMagenta.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp)
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
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${message.userName} sent ${message.giftName} x${message.giftCount} to ${message.receiverName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }
        }
        "join" -> {
            Text(
                text = "🎉 ${message.userName} joined the room",
                style = MaterialTheme.typography.labelSmall,
                color = SuccessGreen
            )
        }
        "leave" -> {
            Text(
                text = "${message.userName} left the room",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
        else -> {
            Row {
                Text(
                    text = "${message.userName}: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun RoomOwnerCard(
    ownerName: String,
    ownerAvatar: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(AccentMagenta, Purple80)
                        )
                    )
                    .border(2.dp, VipGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (ownerAvatar != null) {
                    AsyncImage(
                        model = ownerAvatar,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ownerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(VipGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "OWNER",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkCanvas
                        )
                    }
                }
                Text(
                    text = "Room Host",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            OutlinedButton(
                onClick = { },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Follow")
            }
        }
    }
}

@Composable
private fun SeatItem(
    seat: Seat,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (seat.userId != null) {
                        Brush.linearGradient(
                            colors = listOf(AccentMagenta.copy(alpha = 0.5f), Purple80.copy(alpha = 0.5f))
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(DarkCard, DarkCard)
                        )
                    }
                )
                .border(
                    width = 2.dp,
                    brush = if (seat.userId != null && !seat.isMuted) {
                        Brush.linearGradient(colors = listOf(AccentCyan, AccentMagenta))
                    } else {
                        Brush.linearGradient(colors = listOf(DarkCard, DarkCard))
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (seat.userId != null) {
                if (seat.userAvatar != null) {
                    AsyncImage(
                        model = seat.userAvatar,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                if (seat.isMuted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .background(ErrorRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MicOff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            } else if (seat.isLocked) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = seat.userName ?: "Seat ${seat.position + 1}",
            style = MaterialTheme.typography.labelSmall,
            color = if (seat.userId != null) TextPrimary else TextTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        
        if (seat.userVip != null && seat.userVip > 0) {
            Box(
                modifier = Modifier
                    .background(VipGold, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "VIP${seat.userVip}",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkCanvas
                )
            }
        }
    }
}

@Composable
private fun RoomBottomBar(
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onGiftClick: () -> Unit,
    onMessageClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMuteToggle,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (isMuted) ErrorRed else SuccessGreen,
                        CircleShape
                    )
            ) {
                Icon(
                    if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    tint = Color.White
                )
            }
            
            IconButton(
                onClick = onGiftClick,
                modifier = Modifier
                    .size(56.dp)
                    .background(AccentMagenta, CircleShape)
            ) {
                Icon(
                    Icons.Default.CardGiftcard,
                    contentDescription = "Send Gift",
                    tint = Color.White
                )
            }
            
            IconButton(
                onClick = onMessageClick,
                modifier = Modifier
                    .size(56.dp)
                    .background(DarkCard, CircleShape)
            ) {
                Icon(
                    Icons.Default.Chat,
                    contentDescription = "Chat",
                    tint = TextPrimary
                )
            }
            
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .size(56.dp)
                    .background(DarkCard, CircleShape)
            ) {
                Icon(
                    Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun GiftBottomSheet(
    onGiftSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Send Gift",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp)
        ) {
            val gifts = listOf(
                "Rose" to 100,
                "Heart" to 500,
                "Star" to 1000,
                "Crown" to 5000,
                "Diamond" to 10000,
                "Car" to 50000,
                "Castle" to 100000,
                "Rocket" to 500000
            )
            
            items(gifts.size) { index ->
                val (name, price) = gifts[index]
                Card(
                    modifier = Modifier.clickable { onGiftSelect("gift_$index") },
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = AccentMagenta,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = CoinGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = price.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = CoinGold
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RoomSettingsSheet(
    settings: RoomSettingsDto?,
    activities: RoomActivitiesDto?,
    onUpdateSettings: (UpdateRoomSettingsRequest) -> Unit,
    onUpdateActivities: (UpdateActivitiesRequest) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Room Settings",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Activity Notifications
        Text(
            text = "Activity Notifications",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        activities?.let { act ->
            ActivityToggle("Game Broadcasts", act.gameNotifications) {
                onUpdateActivities(UpdateActivitiesRequest(gameNotifications = it))
            }
            ActivityToggle("CP Notifications", act.cpNotifications) {
                onUpdateActivities(UpdateActivitiesRequest(cpNotifications = it))
            }
            ActivityToggle("Friendship Notifications", act.friendshipNotifications) {
                onUpdateActivities(UpdateActivitiesRequest(friendshipNotifications = it))
            }
            ActivityToggle("Rocket Notifications", act.rocketNotifications) {
                onUpdateActivities(UpdateActivitiesRequest(rocketNotifications = it))
            }
            ActivityToggle("Lucky Bag Notifications", act.luckyBagNotifications) {
                onUpdateActivities(UpdateActivitiesRequest(luckyBagNotifications = it))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Close")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ActivityToggle(
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun MoreOptionsSheet(
    isOwner: Boolean,
    isAdmin: Boolean,
    onPlayVideo: () -> Unit,
    onClearChat: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "More Options",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Play Video (YouTube)
        OptionItem(
            icon = Icons.Default.PlayCircle,
            label = "Play Video (YouTube)",
            onClick = onPlayVideo
        )
        
        // Lucky Bag
        OptionItem(
            icon = Icons.Default.CardGiftcard,
            label = "Send Lucky Bag",
            onClick = { /* TODO */ }
        )
        
        // Clear Chat (Owner/Admin only)
        if (isOwner || isAdmin) {
            OptionItem(
                icon = Icons.Default.Delete,
                label = "Clear Chat",
                onClick = onClearChat
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = AccentMagenta,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = TextPrimary)
    }
}

@Composable
private fun SeatActionSheet(
    seatPosition: Int,
    userId: String?,
    isOwner: Boolean,
    isAdmin: Boolean,
    onLock: () -> Unit,
    onUnlock: () -> Unit,
    onKick: () -> Unit,
    onMute: () -> Unit,
    onUnmute: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Seat ${seatPosition + 1} Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isOwner || isAdmin) {
            OptionItem(Icons.Default.Lock, "Lock Seat", onLock)
            OptionItem(Icons.Default.LockOpen, "Unlock Seat", onUnlock)
            
            if (userId != null) {
                OptionItem(Icons.Default.ExitToApp, "Kick from Seat", onKick)
                OptionItem(Icons.Default.MicOff, "Mute User", onMute)
                OptionItem(Icons.Default.Mic, "Unmute User", onUnmute)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun GameSheet(
    gameType: String,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = gameType.replace("_", " ").uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Game integration coming soon!",
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Close")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CinemaRoomLayout(
    uiState: RoomUiState,
    onExitCinema: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RoomViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video Player Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(DarkCanvas),
            contentAlignment = Alignment.Center
        ) {
            // YouTube player placeholder
            Text(
                text = uiState.videoPlayer?.currentVideo?.title ?: "Playing Video",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            
            // Exit cinema mode button
            IconButton(
                onClick = onExitCinema,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Exit Cinema",
                    tint = Color.White
                )
            }
        }
        
        // Cinema seats (bottom)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .background(DarkSurface)
        ) {
            // Seats in cinema layout
            val seats = uiState.room?.seats ?: emptyList()
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(seats) { seat ->
                    CinemaSeatItem(seat)
                }
            }
        }
    }
}

@Composable
private fun CinemaSeatItem(seat: Seat) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (seat.userId != null) AccentMagenta.copy(alpha = 0.5f) else DarkCard
            ),
        contentAlignment = Alignment.Center
    ) {
        if (seat.userId != null) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                Icons.Default.EventSeat,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

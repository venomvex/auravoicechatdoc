package com.aura.voicechat.ui.room

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aura.voicechat.domain.model.Seat
import com.aura.voicechat.ui.theme.*

/**
 * Voice/Video Room Screen with 8/16 seat layouts
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * This screen displays the main voice/video room UI with:
 * - Room owner card at the top
 * - Seat grid for participants (8 or 16 seats)
 * - Chat preview section
 * - Bottom bar with mic, gift, chat, and more actions
 * - Gift bottom sheet for sending gifts
 * 
 * TODO: Future enhancements:
 * - Implement real-time WebRTC voice/video
 * - Add room settings menu
 * - Add events slider
 * - Add games slider
 * - Integrate with Socket.io for real-time updates
 * - Add animations for gift sending
 * - Add full chat panel with message history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    roomId: String,
    onNavigateBack: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isMuted by remember { mutableStateOf(false) }
    var showGiftSheet by remember { mutableStateOf(false) }
    
    LaunchedEffect(roomId) {
        viewModel.loadRoom(roomId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.room?.name ?: "Room",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.room?.currentUsers ?: 0} users",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Leave")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCanvas
                )
            )
        },
        bottomBar = {
            RoomBottomBar(
                isMuted = isMuted,
                onMuteToggle = { isMuted = !isMuted },
                onGiftClick = { showGiftSheet = true },
                onMessageClick = { /* Open chat */ },
                onMoreClick = { /* More options */ }
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
            // Room Owner Section
            uiState.room?.let { room ->
                RoomOwnerCard(
                    ownerName = room.ownerName,
                    ownerAvatar = room.ownerAvatar,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Seats Grid (8 or 16 seats)
            val seats = uiState.room?.seats ?: generateEmptySeats(8)
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
            
            // Chat Preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Chat",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Mock chat messages
                    ChatMessage("User123", "Hello everyone! 👋")
                    ChatMessage("Sarah", "Welcome to the room!")
                }
            }
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
                    viewModel.sendGift(giftId)
                    showGiftSheet = false
                }
            )
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
        colors = CardDefaults.cardColors(
            containerColor = DarkCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Owner Avatar
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
                    // Owner badge
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
            
            // Follow button
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
        // Seat circle
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
                
                // Muted indicator
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
        
        // User name or seat number
        Text(
            text = seat.userName ?: "Seat ${seat.position + 1}",
            style = MaterialTheme.typography.labelSmall,
            color = if (seat.userId != null) TextPrimary else TextTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        
        // VIP badge
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
            // Mute button
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
            
            // Gift button
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
            
            // Message button
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
            
            // More button
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
private fun ChatMessage(userName: String, message: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$userName: ",
            style = MaterialTheme.typography.bodySmall,
            color = AccentCyan,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary
        )
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
        
        // Gift categories
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

private fun generateEmptySeats(count: Int): List<Seat> {
    return (0 until count).map { position ->
        Seat(
            position = position,
            userId = null,
            userName = null,
            userAvatar = null,
            userLevel = null,
            userVip = null,
            isMuted = false,
            isLocked = false,
            effects = emptyList()
        )
    }
}

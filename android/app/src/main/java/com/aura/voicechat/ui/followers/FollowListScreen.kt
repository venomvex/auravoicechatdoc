package com.aura.voicechat.ui.followers

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * Followers/Following List Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListScreen(
    type: String, // "followers" or "following"
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: FollowListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(type, userId) {
        viewModel.loadList(type, userId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (type == "followers") "Followers (${uiState.totalCount})" 
                        else "Following (${uiState.totalCount})"
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkCanvas)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentMagenta)
            }
        } else if (uiState.users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkCanvas)
                    .padding(paddingValues),
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
                    Text(
                        text = if (type == "followers") "No followers yet" else "Not following anyone",
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkCanvas)
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.users) { user ->
                    FollowUserCard(
                        user = user,
                        onClick = { onNavigateToProfile(user.userId) },
                        onFollowToggle = { viewModel.toggleFollow(user.userId) },
                        showFollowButton = type == "followers" // Show follow back button for followers
                    )
                }
            }
        }
    }
}

@Composable
private fun FollowUserCard(
    user: FollowUser,
    onClick: () -> Unit,
    onFollowToggle: () -> Unit,
    showFollowButton: Boolean
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
            // Avatar
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    if (user.vipLevel > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(VipGold, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "V${user.vipLevel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkCanvas,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = "Lv.${user.level} • ID: ${user.userId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            if (showFollowButton) {
                Button(
                    onClick = onFollowToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.isFollowing) DarkSurface else AccentMagenta
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (user.isFollowing) "Following" else "Follow",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (user.isFollowing) TextSecondary else Color.White
                    )
                }
            }
        }
    }
}

// Data class
data class FollowUser(
    val userId: String,
    val name: String,
    val avatar: String?,
    val level: Int,
    val vipLevel: Int,
    val isFollowing: Boolean
)

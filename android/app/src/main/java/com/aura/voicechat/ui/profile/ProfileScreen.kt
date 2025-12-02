package com.aura.voicechat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.domain.model.Gender
import com.aura.voicechat.domain.model.Medal
import com.aura.voicechat.domain.model.MedalCategory
import com.aura.voicechat.ui.theme.*

/**
 * Profile/Me Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOwnProfile = userId == "me"
    
    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isOwnProfile) "My Profile" else "Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isOwnProfile) {
                        IconButton(onClick = { /* Settings */ }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    } else {
                        IconButton(onClick = { /* More options */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Purple40.copy(alpha = 0.3f), DarkCanvas)
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            ProfileHeader(
                name = uiState.user?.name ?: "Loading...",
                avatar = uiState.user?.avatar,
                level = uiState.user?.level ?: 0,
                vipTier = uiState.user?.vipTier ?: 0,
                userId = uiState.user?.id ?: "",
                isOnline = uiState.user?.isOnline ?: false,
                bio = uiState.user?.bio
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "${uiState.followersCount}", label = "Followers")
                StatItem(value = "${uiState.followingCount}", label = "Following")
                StatItem(value = "${uiState.giftsReceivedCount}", label = "Gifts")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Wallet Quick View (only for own profile)
            if (isOwnProfile) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WalletItem(
                            icon = Icons.Default.MonetizationOn,
                            value = formatNumber(uiState.user?.coins ?: 0),
                            label = "Coins",
                            color = CoinGold
                        )
                        VerticalDivider(
                            modifier = Modifier.height(40.dp),
                            color = DarkSurface
                        )
                        WalletItem(
                            icon = Icons.Default.Diamond,
                            value = formatNumber(uiState.user?.diamonds ?: 0),
                            label = "Diamonds",
                            color = DiamondBlue
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Medals Section
            Text(
                text = "Medals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.medals) { medal ->
                    MedalItem(medal = medal)
                }
                
                if (uiState.medals.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(DarkCard, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No medals",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action Buttons (for other profiles)
            if (!isOwnProfile) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.toggleFollow() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isFollowing) DarkCard else AccentMagenta
                        )
                    ) {
                        Icon(
                            if (uiState.isFollowing) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (uiState.isFollowing) "Following" else "Follow")
                    }
                    
                    OutlinedButton(
                        onClick = { /* Send message */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Message")
                    }
                    
                    OutlinedButton(
                        onClick = { /* Send gift */ }
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            // Quick Links (for own profile)
            if (isOwnProfile) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Earnings Section (prominent at top)
                    EarningsCard(
                        earnings = uiState.earnings,
                        activeTarget = uiState.activeTarget,
                        onNavigateToEarnings = { /* Navigate to earnings */ }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Guide System (for female users)
                    if (uiState.user?.gender == Gender.FEMALE && !uiState.isGuideApplied) {
                        GuideApplicationCard(
                            onApply = { /* Apply for guide */ }
                        )
                    }
                    
                    // If user is a guide, show guide stats
                    if (uiState.isGuide) {
                        ProfileMenuItem(
                            icon = Icons.Default.Stars,
                            title = "Guide Dashboard",
                            subtitle = "View your guide stats and earnings",
                            onClick = { }
                        )
                    }
                    
                    ProfileMenuItem(
                        icon = Icons.Default.CardGiftcard,
                        title = "Daily Rewards",
                        subtitle = "Claim your daily coins",
                        onClick = { }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Star,
                        title = "VIP Center",
                        subtitle = "Upgrade your VIP level",
                        onClick = { }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Favorite,
                        title = "CP Partner",
                        subtitle = if (uiState.user?.cpPartnerId != null) "View partnership" else "Find your partner",
                        onClick = { }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.People,
                        title = "Invite Friends",
                        subtitle = "Earn rewards for referrals",
                        onClick = { }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.VerifiedUser,
                        title = "KYC Verification",
                        subtitle = when (uiState.user?.kycStatus) {
                            com.aura.voicechat.domain.model.KycStatus.VERIFIED -> "Verified"
                            com.aura.voicechat.domain.model.KycStatus.PENDING_REVIEW -> "Under review"
                            else -> "Verify your identity"
                        },
                        onClick = { }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        subtitle = "Account, privacy, notifications",
                        onClick = { }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EarningsCard(
    earnings: EarningsData?,
    activeTarget: TargetData?,
    onNavigateToEarnings: () -> Unit
) {
    Card(
        onClick = onNavigateToEarnings,
        colors = CardDefaults.cardColors(
            containerColor = AccentMagenta.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = AccentMagenta,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Earnings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextTertiary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Earnings summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EarningStatItem(
                    value = formatCurrency(earnings?.availableBalance ?: 0),
                    label = "Available",
                    color = SuccessGreen
                )
                EarningStatItem(
                    value = formatCurrency(earnings?.pendingBalance ?: 0),
                    label = "Pending",
                    color = VipGold
                )
                EarningStatItem(
                    value = formatCurrency(earnings?.totalEarned ?: 0),
                    label = "Total Earned",
                    color = AccentMagenta
                )
            }
            
            // Active target progress
            if (activeTarget != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Weekly Target: ${activeTarget.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { (activeTarget.progress.toFloat() / activeTarget.target).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AccentMagenta,
                    trackColor = DarkSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${formatNumber(activeTarget.progress)}/${formatNumber(activeTarget.target)} diamonds received",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Text(
                        text = "${activeTarget.daysRemaining} days left",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun EarningStatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun GuideApplicationCard(
    onApply: () -> Unit
) {
    Card(
        onClick = onApply,
        colors = CardDefaults.cardColors(
            containerColor = VipGold.copy(alpha = 0.15f)
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
                    .size(48.dp)
                    .background(VipGold.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Stars,
                    contentDescription = null,
                    tint = VipGold,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Become a Guide",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Earn extra income by being a voice guide",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Button(
                onClick = onApply,
                colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Apply", color = DarkCanvas)
            }
        }
    }
}

// Data classes for earnings
data class EarningsData(
    val availableBalance: Long = 0,
    val pendingBalance: Long = 0,
    val totalEarned: Long = 0
)

data class TargetData(
    val id: String = "",
    val name: String = "",
    val progress: Long = 0,
    val target: Long = 0,
    val daysRemaining: Int = 0
)

private fun formatCurrency(amount: Long): String {
    return when {
        amount >= 1_000_000 -> String.format("$%.1fM", amount / 1_000_000.0)
        amount >= 1_000 -> String.format("$%.1fK", amount / 1_000.0)
        else -> "$$amount"
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    avatar: String?,
    level: Int,
    vipTier: Int,
    userId: String,
    isOnline: Boolean,
    bio: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with frame
        Box(
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(AccentMagenta, Purple80)
                        )
                    )
                    .border(
                        width = 3.dp,
                        brush = if (vipTier > 0) Brush.linearGradient(
                            colors = listOf(VipGold, VipGold.copy(alpha = 0.6f))
                        ) else Brush.linearGradient(
                            colors = listOf(Purple80, Purple60)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (avatar != null) {
                    AsyncImage(
                        model = avatar,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            // Online indicator
            if (isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .background(DarkCanvas, CircleShape)
                        .padding(4.dp)
                        .background(SuccessGreen, CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Name and badges
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            if (vipTier > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(VipGold, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "VIP$vipTier",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkCanvas,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Level and ID
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Purple80.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Lv.$level",
                    style = MaterialTheme.typography.labelSmall,
                    color = Purple80
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "ID: $userId",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
        
        // Bio
        if (!bio.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun WalletItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun MedalItem(medal: Medal) {
    Column(
        modifier = Modifier
            .width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    when (medal.category) {
                        MedalCategory.GIFT -> AccentMagenta.copy(alpha = 0.2f)
                        MedalCategory.ACHIEVEMENT -> VipGold.copy(alpha = 0.2f)
                        MedalCategory.ACTIVITY -> AccentCyan.copy(alpha = 0.2f)
                        MedalCategory.SPECIAL -> Purple80.copy(alpha = 0.2f)
                    },
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                when (medal.category) {
                    MedalCategory.GIFT -> Icons.Default.CardGiftcard
                    MedalCategory.ACHIEVEMENT -> Icons.Default.EmojiEvents
                    MedalCategory.ACTIVITY -> Icons.Default.LocalActivity
                    MedalCategory.SPECIAL -> Icons.Default.Stars
                },
                contentDescription = null,
                tint = when (medal.category) {
                    MedalCategory.GIFT -> AccentMagenta
                    MedalCategory.ACHIEVEMENT -> VipGold
                    MedalCategory.ACTIVITY -> AccentCyan
                    MedalCategory.SPECIAL -> Purple80
                },
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = medal.name,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = AccentMagenta,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
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

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

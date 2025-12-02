package com.aura.voicechat.ui.family

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * Family System Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Family creation and management
 * - Family members list
 * - Family level and perks
 * - Family rankings
 * - Family activities/gifts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMemberProfile: (String) -> Unit,
    onNavigateToFamilyRanking: () -> Unit,
    viewModel: FamilyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    
    val tabs = listOf("Info", "Members", "Activity", "Perks")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.familyName.ifEmpty { "Family" }) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isOwner || uiState.isAdmin) {
                        IconButton(onClick = { viewModel.showSettingsDialog() }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                    IconButton(onClick = onNavigateToFamilyRanking) {
                        Icon(Icons.Default.Leaderboard, contentDescription = "Rankings")
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
            if (!uiState.hasFamily) {
                // No family - show create/join options
                NoFamilyContent(
                    onCreateFamily = { viewModel.showCreateFamilyDialog() },
                    onJoinFamily = { viewModel.showJoinFamilyDialog() }
                )
            } else {
                // Family header
                FamilyHeader(
                    familyName = uiState.familyName,
                    familyBadge = uiState.familyBadge,
                    familyLevel = uiState.familyLevel,
                    membersCount = uiState.membersCount,
                    maxMembers = uiState.maxMembers,
                    weeklyGifts = uiState.weeklyGifts
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
                
                // Tab content
                when (selectedTab) {
                    0 -> FamilyInfoTab(uiState)
                    1 -> FamilyMembersTab(
                        members = uiState.members,
                        onMemberClick = onNavigateToMemberProfile,
                        isAdmin = uiState.isOwner || uiState.isAdmin,
                        onKick = { viewModel.kickMember(it) },
                        onPromote = { viewModel.promoteMember(it) }
                    )
                    2 -> FamilyActivityTab(activities = uiState.recentActivities)
                    3 -> FamilyPerksTab(perks = uiState.perks, currentLevel = uiState.familyLevel)
                }
            }
        }
    }
    
    // Create Family Dialog
    if (uiState.showCreateDialog) {
        CreateFamilyDialog(
            onDismiss = { viewModel.dismissCreateFamilyDialog() },
            onCreate = { name, badge -> viewModel.createFamily(name, badge) }
        )
    }
    
    // Join Family Dialog
    if (uiState.showJoinDialog) {
        JoinFamilyDialog(
            onDismiss = { viewModel.dismissJoinFamilyDialog() },
            onJoin = { familyId -> viewModel.joinFamily(familyId) }
        )
    }
}

@Composable
private fun NoFamilyContent(
    onCreateFamily: () -> Unit,
    onJoinFamily: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Groups,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Family Yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Join a family to enjoy exclusive perks and compete in rankings!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onCreateFamily,
            colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Family")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onJoinFamily,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Join Family")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Creation fee: 1,000,000 coins",
            style = MaterialTheme.typography.bodySmall,
            color = WarningOrange
        )
    }
}

@Composable
private fun FamilyHeader(
    familyName: String,
    familyBadge: String?,
    familyLevel: Int,
    membersCount: Int,
    maxMembers: Int,
    weeklyGifts: Long
) {
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Purple80.copy(alpha = 0.3f), DarkCard)
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Family Badge
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(DarkSurface)
                    .border(2.dp, Purple80, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (familyBadge != null) {
                    AsyncImage(
                        model = familyBadge,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = null,
                        tint = Purple80,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = familyName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                // Level
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Lv.$familyLevel",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VipGold
                    )
                    Text(
                        text = "Level",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                
                // Members
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$membersCount/$maxMembers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Members",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                
                // Weekly Gifts
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatNumber(weeklyGifts),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DiamondBlue
                    )
                    Text(
                        text = "Weekly",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun FamilyInfoTab(uiState: FamilyUiState) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Family Notice",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.familyNotice.ifEmpty { "No notice set" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
        
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Family Stats",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StatRow("Total Gifts (All Time)", formatNumber(uiState.totalGifts))
                    StatRow("Weekly Ranking", "#${uiState.weeklyRanking}")
                    StatRow("Created", uiState.createdDate)
                    StatRow("Owner", uiState.ownerName)
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
private fun FamilyMembersTab(
    members: List<FamilyMember>,
    onMemberClick: (String) -> Unit,
    isAdmin: Boolean,
    onKick: (String) -> Unit,
    onPromote: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(members) { member ->
            FamilyMemberItem(
                member = member,
                onClick = { onMemberClick(member.userId) },
                isAdmin = isAdmin,
                onKick = { onKick(member.userId) },
                onPromote = { onPromote(member.userId) }
            )
        }
    }
}

@Composable
private fun FamilyMemberItem(
    member: FamilyMember,
    onClick: () -> Unit,
    isAdmin: Boolean,
    onKick: () -> Unit,
    onPromote: () -> Unit
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
                if (member.avatar != null) {
                    AsyncImage(
                        model = member.avatar,
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
                        text = member.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    if (member.role != "member") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    when (member.role) {
                                        "owner" -> VipGold
                                        "admin" -> AccentMagenta
                                        else -> TextTertiary
                                    },
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = member.role.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
                Text(
                    text = "Weekly: ${formatNumber(member.weeklyContribution)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            if (isAdmin && member.role == "member") {
                IconButton(onClick = onPromote) {
                    Icon(Icons.Default.Star, contentDescription = "Promote", tint = VipGold)
                }
                IconButton(onClick = onKick) {
                    Icon(Icons.Default.RemoveCircle, contentDescription = "Kick", tint = ErrorRed)
                }
            }
        }
    }
}

@Composable
private fun FamilyActivityTab(activities: List<FamilyActivity>) {
    if (activities.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No recent activity", color = TextTertiary)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(activities) { activity ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            when (activity.type) {
                                "gift" -> Icons.Default.CardGiftcard
                                "join" -> Icons.Default.PersonAdd
                                "leave" -> Icons.Default.ExitToApp
                                "levelup" -> Icons.Default.TrendingUp
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = AccentMagenta,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activity.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = activity.timeAgo,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyPerksTab(perks: List<FamilyPerk>, currentLevel: Int) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(perks) { perk ->
            val isUnlocked = currentLevel >= perk.requiredLevel
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) DarkCard else DarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Stars,
                        contentDescription = null,
                        tint = if (isUnlocked) VipGold else TextTertiary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = perk.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUnlocked) TextPrimary else TextTertiary
                        )
                        Text(
                            text = perk.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = if (isUnlocked) "✓" else "Lv.${perk.requiredLevel}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isUnlocked) SuccessGreen else TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateFamilyDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var familyName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Create Family", color = TextPrimary) },
        text = {
            Column {
                OutlinedTextField(
                    value = familyName,
                    onValueChange = { familyName = it },
                    label = { Text("Family Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Creation fee: 1,000,000 coins",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarningOrange
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(familyName, null) },
                enabled = familyName.isNotEmpty(),
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
private fun JoinFamilyDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var familyId by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Join Family", color = TextPrimary) },
        text = {
            OutlinedTextField(
                value = familyId,
                onValueChange = { familyId = it },
                label = { Text("Family ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onJoin(familyId) },
                enabled = familyId.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta)
            ) {
                Text("Join")
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
data class FamilyMember(
    val userId: String,
    val name: String,
    val avatar: String?,
    val role: String,
    val weeklyContribution: Long
)

data class FamilyActivity(
    val id: String,
    val type: String,
    val message: String,
    val timeAgo: String
)

data class FamilyPerk(
    val id: String,
    val name: String,
    val description: String,
    val requiredLevel: Int
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

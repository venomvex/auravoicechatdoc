package com.aura.voicechat.ui.cp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * CP (Couple Partnership) System Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - View CP partner info
 * - CP level and progress
 * - CP tasks and rewards
 * - CP cosmetics unlocks
 * - Formation/dissolution
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CpScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPartnerProfile: (String) -> Unit,
    viewModel: CpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CP Partnership") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCanvas
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkCanvas)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CP Status Card
            item {
                if (uiState.hasPartner) {
                    CpPartnerCard(
                        partnerName = uiState.partnerName,
                        partnerAvatar = uiState.partnerAvatar,
                        cpLevel = uiState.cpLevel,
                        cpExp = uiState.cpExp,
                        cpExpRequired = uiState.cpExpRequired,
                        cpDays = uiState.cpDays,
                        onPartnerClick = { onNavigateToPartnerProfile(uiState.partnerId) }
                    )
                } else {
                    NoCpPartnerCard(
                        onFindPartner = { viewModel.showFindPartnerDialog() }
                    )
                }
            }
            
            // CP Level Benefits
            if (uiState.hasPartner) {
                item {
                    Text(
                        text = "Level ${uiState.cpLevel} Benefits",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                item {
                    CpBenefitsCard(benefits = uiState.currentBenefits)
                }
                
                // CP Daily Tasks
                item {
                    Text(
                        text = "Daily Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                items(uiState.dailyTasks) { task ->
                    CpTaskItem(
                        task = task,
                        onClaim = { viewModel.claimTaskReward(task.id) }
                    )
                }
                
                // CP Cosmetics
                item {
                    Text(
                        text = "CP Cosmetics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                items(uiState.cpCosmetics) { cosmetic ->
                    CpCosmeticItem(
                        cosmetic = cosmetic,
                        currentLevel = uiState.cpLevel
                    )
                }
            }
        }
    }
    
    // Find Partner Dialog
    if (uiState.showFindPartnerDialog) {
        FindPartnerDialog(
            onDismiss = { viewModel.dismissFindPartnerDialog() },
            onSendRequest = { userId -> viewModel.sendCpRequest(userId) }
        )
    }
}

@Composable
private fun CpPartnerCard(
    partnerName: String,
    partnerAvatar: String?,
    cpLevel: Int,
    cpExp: Long,
    cpExpRequired: Long,
    cpDays: Int,
    onPartnerClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = DarkCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AccentMagenta.copy(alpha = 0.3f),
                            DarkCard
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hearts decoration
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = AccentMagenta,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CP Level $cpLevel",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AccentMagenta
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = AccentMagenta,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Partner info
            Row(
                modifier = Modifier
                    .clickable(onClick = onPartnerClick)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Partner Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .border(2.dp, AccentMagenta, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (partnerAvatar != null) {
                        AsyncImage(
                            model = partnerAvatar,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = partnerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "$cpDays days together",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // CP Progress
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CP EXP",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "$cpExp / $cpExpRequired",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (cpExp.toFloat() / cpExpRequired).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AccentMagenta,
                    trackColor = DarkSurface
                )
            }
        }
    }
}

@Composable
private fun NoCpPartnerCard(onFindPartner: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No CP Partner Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Find your special someone and unlock exclusive rewards together!",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onFindPartner,
                colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Find Partner")
            }
        }
    }
}

@Composable
private fun CpBenefitsCard(benefits: List<String>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            benefits.forEach { benefit ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CpTaskItem(
    task: CpTask,
    onClaim: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "${task.progress}/${task.target}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                LinearProgressIndicator(
                    progress = { (task.progress.toFloat() / task.target).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = AccentMagenta,
                    trackColor = DarkSurface
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "+${task.cpExpReward}",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentMagenta,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CP EXP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
            
            if (task.isCompleted && !task.isClaimed) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Claim", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun CpCosmeticItem(
    cosmetic: CpCosmetic,
    currentLevel: Int
) {
    val isUnlocked = currentLevel >= cosmetic.requiredLevel
    
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isUnlocked) AccentMagenta.copy(alpha = 0.2f) else DarkCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (cosmetic.type) {
                        "frame" -> Icons.Default.CropSquare
                        "vehicle" -> Icons.Default.DirectionsCar
                        "theme" -> Icons.Default.Palette
                        "bubble" -> Icons.Default.ChatBubble
                        else -> Icons.Default.CardGiftcard
                    },
                    contentDescription = null,
                    tint = if (isUnlocked) AccentMagenta else TextTertiary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cosmetic.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked) TextPrimary else TextTertiary
                )
                Text(
                    text = if (isUnlocked) "Unlocked" else "Unlock at Level ${cosmetic.requiredLevel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) SuccessGreen else TextTertiary
                )
            }
            
            if (isUnlocked) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun FindPartnerDialog(
    onDismiss: () -> Unit,
    onSendRequest: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Find CP Partner", color = TextPrimary) },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Enter User ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Formation fee: 3,000,000 coins",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarningOrange
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSendRequest(searchQuery) },
                enabled = searchQuery.isNotEmpty(),
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
data class CpTask(
    val id: String,
    val name: String,
    val progress: Int,
    val target: Int,
    val cpExpReward: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean
)

data class CpCosmetic(
    val id: String,
    val name: String,
    val type: String,
    val requiredLevel: Int
)

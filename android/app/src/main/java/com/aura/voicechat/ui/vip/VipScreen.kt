package com.aura.voicechat.ui.vip

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * VIP/SVIP System Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - VIP tier display (V1-V10)
 * - VIP benefits per tier
 * - VIP purchase options
 * - Daily multiplier rewards
 * - Exclusive cosmetics preview
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPurchase: () -> Unit,
    viewModel: VipViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTier by remember { mutableStateOf(uiState.currentTier) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SVIP") },
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
                .padding(paddingValues)
        ) {
            // Current VIP Status Header
            item {
                VipStatusHeader(
                    currentTier = uiState.currentTier,
                    daysRemaining = uiState.daysRemaining,
                    totalDiamondsSpent = uiState.totalDiamondsSpent,
                    nextTierProgress = uiState.nextTierProgress
                )
            }
            
            // VIP Tier Selector
            item {
                VipTierSelector(
                    tiers = uiState.allTiers,
                    selectedTier = selectedTier,
                    currentTier = uiState.currentTier,
                    onTierSelected = { selectedTier = it }
                )
            }
            
            // Selected Tier Benefits
            item {
                VipBenefitsCard(
                    tier = selectedTier,
                    benefits = uiState.getBenefitsForTier(selectedTier)
                )
            }
            
            // VIP Exclusive Items Preview
            item {
                Text(
                    text = "V$selectedTier Exclusive Items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.getExclusiveItems(selectedTier)) { item ->
                        VipExclusiveItem(item = item)
                    }
                }
            }
            
            // Purchase Options (if not max VIP)
            if (uiState.currentTier < 10) {
                item {
                    PurchaseSection(
                        currentTier = uiState.currentTier,
                        packages = uiState.purchasePackages,
                        onPurchase = { viewModel.purchaseVip(it) }
                    )
                }
            }
            
            // Spacer at bottom
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun VipStatusHeader(
    currentTier: Int,
    daysRemaining: Int,
    totalDiamondsSpent: Long,
    nextTierProgress: Float
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
                        colors = listOf(
                            VipGold.copy(alpha = 0.3f),
                            DarkCard
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // VIP Badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(VipGold, VipGold.copy(alpha = 0.7f))
                        )
                    )
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Diamond,
                        contentDescription = null,
                        tint = DarkCanvas,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "V$currentTier",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkCanvas
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = if (currentTier > 0) "SVIP $currentTier" else "Not VIP",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = VipGold
            )
            
            if (daysRemaining > 0) {
                Text(
                    text = "$daysRemaining days remaining",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress to next tier
            if (currentTier < 10) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress to V${currentTier + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "${(nextTierProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = VipGold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { nextTierProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = VipGold,
                        trackColor = DarkSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatNumber(totalDiamondsSpent),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DiamondBlue
                    )
                    Text(
                        text = "Diamonds Spent",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${currentTier * 20 + 100}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Text(
                        text = "Daily Bonus",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun VipTierSelector(
    tiers: List<Int>,
    selectedTier: Int,
    currentTier: Int,
    onTierSelected: (Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tiers) { tier ->
            val isSelected = tier == selectedTier
            val isUnlocked = tier <= currentTier
            
            Surface(
                onClick = { onTierSelected(tier) },
                color = when {
                    isSelected -> VipGold
                    isUnlocked -> DarkCard
                    else -> DarkSurface
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isUnlocked) {
                            Icon(
                                Icons.Default.Diamond,
                                contentDescription = null,
                                tint = if (isSelected) DarkCanvas else VipGold,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "V$tier",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isSelected -> DarkCanvas
                                isUnlocked -> VipGold
                                else -> TextTertiary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VipBenefitsCard(
    tier: Int,
    benefits: List<VipBenefit>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "V$tier Benefits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = VipGold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            benefits.forEach { benefit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = VipGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = benefit.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = benefit.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VipExclusiveItem(item: VipExclusiveItemData) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(VipGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (item.type) {
                        "frame" -> Icons.Default.CropSquare
                        "vehicle" -> Icons.Default.DirectionsCar
                        "badge" -> Icons.Default.Stars
                        else -> Icons.Default.CardGiftcard
                    },
                    contentDescription = null,
                    tint = VipGold,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            Text(
                text = item.type.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = VipGold
            )
        }
    }
}

@Composable
private fun PurchaseSection(
    currentTier: Int,
    packages: List<VipPackage>,
    onPurchase: (VipPackage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Upgrade VIP",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        packages.forEach { pkg ->
            Card(
                onClick = { onPurchase(pkg) },
                colors = CardDefaults.cardColors(
                    containerColor = if (pkg.isBestValue) VipGold.copy(alpha = 0.2f) else DarkCard
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .then(
                        if (pkg.isBestValue) {
                            Modifier.border(2.dp, VipGold, RoundedCornerShape(12.dp))
                        } else Modifier
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = pkg.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (pkg.isBestValue) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(VipGold, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "BEST VALUE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkCanvas,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = pkg.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${pkg.price}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = VipGold
                        )
                        if (pkg.originalPrice > pkg.price) {
                            Text(
                                text = "$${pkg.originalPrice}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }
        }
    }
}

// Data classes
data class VipBenefit(
    val name: String,
    val description: String
)

data class VipExclusiveItemData(
    val id: String,
    val name: String,
    val type: String
)

data class VipPackage(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double,
    val diamonds: Long,
    val durationDays: Int,
    val isBestValue: Boolean = false
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

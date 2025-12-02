package com.aura.voicechat.ui.gifts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aura.voicechat.ui.theme.*

/**
 * Gift Panel - In-room gift sending interface
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Supports 120+ gifts across categories with animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftPanel(
    recipientId: String,
    recipientName: String,
    onDismiss: () -> Unit,
    onGiftSent: (String, Int) -> Unit,
    viewModel: GiftPanelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf("all") }
    var selectedGift by remember { mutableStateOf<Gift?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    
    val categories = listOf(
        "all" to "All",
        "love" to "❤️ Love",
        "celebration" to "🎉 Party",
        "luxury" to "💎 Luxury",
        "nature" to "🌸 Nature",
        "fantasy" to "✨ Fantasy",
        "special" to "⭐ Special",
        "custom" to "🎁 Custom",
        "legendary" to "👑 Legend"
    )
    
    LaunchedEffect(Unit) {
        viewModel.loadGifts()
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(TextTertiary, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .padding(bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Send Gift to",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = recipientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                
                // Wallet Display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(DarkSurface, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = CoinGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatNumber(uiState.coins),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CoinGold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Category Tabs
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { (id, name) ->
                    FilterChip(
                        selected = selectedCategory == id,
                        onClick = { 
                            selectedCategory = id
                            viewModel.filterByCategory(id)
                        },
                        label = { Text(name, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentMagenta,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurface
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Gifts Grid
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentMagenta)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(uiState.gifts) { gift ->
                        GiftItem(
                            gift = gift,
                            isSelected = selectedGift?.id == gift.id,
                            onClick = { selectedGift = gift }
                        )
                    }
                }
            }
            
            // Selected Gift Info & Send Button
            if (selectedGift != null) {
                HorizontalDivider(color = DarkSurface, modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selected Gift Preview
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedGift!!.iconUrl != null) {
                            AsyncImage(
                                model = selectedGift!!.iconUrl,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = AccentMagenta,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedGift!!.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = CoinGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${formatNumber(selectedGift!!.price)} × $quantity = ${formatNumber(selectedGift!!.price * quantity)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CoinGold
                            )
                        }
                    }
                    
                    // Quantity Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(DarkSurface, RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.widthIn(min = 24.dp),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { if (quantity < 999) quantity++ },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Quick quantities
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(1, 10, 99).forEach { q ->
                            Surface(
                                onClick = { quantity = q },
                                color = if (quantity == q) AccentMagenta else DarkSurface,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = q.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (quantity == q) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Send Button
                Button(
                    onClick = {
                        val totalCost = selectedGift!!.price * quantity
                        if (totalCost > uiState.coins) {
                            // Show insufficient funds
                        } else if (totalCost >= 1000000) {
                            // Show confirmation for large gifts
                            showConfirmDialog = true
                        } else {
                            viewModel.sendGift(recipientId, selectedGift!!.id, quantity)
                            onGiftSent(selectedGift!!.id, quantity)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta),
                    enabled = selectedGift != null && (selectedGift!!.price * quantity) <= uiState.coins
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Gift",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
    // Large Gift Confirmation Dialog
    if (showConfirmDialog && selectedGift != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = DarkCard,
            title = {
                Text(
                    "Confirm Large Gift",
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        "You are about to send:",
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${selectedGift!!.name} × $quantity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = CoinGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatNumber(selectedGift!!.price * quantity),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CoinGold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendGift(recipientId, selectedGift!!.id, quantity)
                        onGiftSent(selectedGift!!.id, quantity)
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta)
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun GiftItem(
    gift: Gift,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                if (isSelected) AccentMagenta.copy(alpha = 0.2f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, AccentMagenta, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gift Icon with rarity indicator
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when (gift.rarity) {
                        "legendary" -> VipGold.copy(alpha = 0.2f)
                        "epic" -> AccentMagenta.copy(alpha = 0.2f)
                        "rare" -> AccentCyan.copy(alpha = 0.2f)
                        else -> DarkSurface
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (gift.iconUrl != null) {
                AsyncImage(
                    model = gift.iconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                Icon(
                    Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = when (gift.rarity) {
                        "legendary" -> VipGold
                        "epic" -> AccentMagenta
                        "rare" -> AccentCyan
                        else -> TextSecondary
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Animated badge
            if (gift.isAnimated) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(12.dp)
                        .background(AccentMagenta, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(8.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        // Gift Name
        Text(
            text = gift.name,
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        
        // Price
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = CoinGold,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = formatCompact(gift.price),
                style = MaterialTheme.typography.labelSmall,
                color = CoinGold
            )
        }
    }
}

data class Gift(
    val id: String,
    val name: String,
    val category: String,
    val price: Long,
    val diamondValue: Long,
    val rarity: String,
    val iconUrl: String? = null,
    val animationFile: String? = null,
    val isAnimated: Boolean = false,
    val isFullScreen: Boolean = false,
    val isCustom: Boolean = false,
    val isLegendary: Boolean = false,
    val isPremium: Boolean = false
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

private fun formatCompact(number: Long): String {
    return when {
        number >= 1_000_000_000 -> "${number / 1_000_000_000}B"
        number >= 1_000_000 -> "${number / 1_000_000}M"
        number >= 1_000 -> "${number / 1_000}K"
        else -> number.toString()
    }
}

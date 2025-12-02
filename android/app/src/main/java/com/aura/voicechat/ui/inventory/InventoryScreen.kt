package com.aura.voicechat.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
 * My Items/Inventory Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToStore: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf("all") }
    
    val categories = listOf(
        "all" to "All",
        "frames" to "Frames",
        "vehicles" to "Vehicles", 
        "themes" to "Themes",
        "mic_skins" to "Mic Skins",
        "seat_effects" to "Seats",
        "chat_bubbles" to "Bubbles",
        "baggage" to "Baggage"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Items") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToStore) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Store")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCanvas
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkCanvas)
                .padding(paddingValues)
        ) {
            // Category Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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
                        label = { Text(name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentMagenta,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            
            // Currently Equipped Section
            if (uiState.equippedItems.isNotEmpty()) {
                Text(
                    text = "Currently Equipped",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.equippedItems) { item ->
                        EquippedItemCard(
                            item = item,
                            onUnequip = { viewModel.unequipItem(item.id) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = DarkSurface)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Items Grid
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentMagenta)
                }
            } else if (uiState.items.isEmpty()) {
                EmptyInventory(onNavigateToStore = onNavigateToStore)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.items) { item ->
                        InventoryItemCard(
                            item = item,
                            isEquipped = uiState.equippedItems.any { it.id == item.id },
                            onClick = { viewModel.selectItem(item.id) }
                        )
                    }
                }
            }
        }
    }
    
    // Item Detail Dialog
    uiState.selectedItem?.let { item ->
        ItemDetailDialog(
            item = item,
            isEquipped = uiState.equippedItems.any { it.id == item.id },
            onDismiss = { viewModel.clearSelection() },
            onEquip = { viewModel.equipItem(item.id) },
            onUnequip = { viewModel.unequipItem(item.id) }
        )
    }
}

@Composable
private fun EquippedItemCard(
    item: InventoryItem,
    onUnequip: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(AccentMagenta.copy(alpha = 0.2f))
                    .border(2.dp, AccentMagenta, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (item.iconUrl != null) {
                    AsyncImage(
                        model = item.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AccentMagenta,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = item.category.replace("_", " ").replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: InventoryItem,
    isEquipped: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) AccentMagenta.copy(alpha = 0.2f) else DarkCard
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = if (isEquipped) {
            Modifier.border(2.dp, AccentMagenta, RoundedCornerShape(12.dp))
        } else {
            Modifier
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                if (item.iconUrl != null) {
                    AsyncImage(
                        model = item.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AccentMagenta,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Equipped badge
                if (isEquipped) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                            .background(AccentMagenta, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            
            // Expiry countdown
            if (item.expiresIn != null) {
                Text(
                    text = item.expiresIn,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.isExpiringSoon) WarningOrange else TextTertiary
                )
            }
        }
    }
}

@Composable
private fun EmptyInventory(onNavigateToStore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Inventory2,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Items Yet",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Visit the store to get frames, vehicles, themes and more!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNavigateToStore,
            colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta)
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Go to Store")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemDetailDialog(
    item: InventoryItem,
    isEquipped: Boolean,
    onDismiss: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Item Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.iconUrl != null) {
                        AsyncImage(
                            model = item.iconUrl,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentMagenta,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Item Name
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Category & Rarity
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.category.replace("_", " ").replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text("•", color = TextTertiary)
                    Box(
                        modifier = Modifier
                            .background(
                                when (item.rarity) {
                                    "legendary" -> VipGold
                                    "epic" -> AccentMagenta
                                    "rare" -> AccentCyan
                                    else -> TextTertiary
                                },
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.rarity.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (item.rarity == "legendary") DarkCanvas else Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                // Expiry
                if (item.expiresIn != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (item.isExpiringSoon) WarningOrange else TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Expires in ${item.expiresIn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (item.isExpiringSoon) WarningOrange else TextTertiary
                        )
                    }
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        confirmButton = {
            Button(
                onClick = if (isEquipped) onUnequip else onEquip,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEquipped) DarkSurface else AccentMagenta
                )
            ) {
                Text(if (isEquipped) "Unequip" else "Equip")
            }
        }
    )
}

data class InventoryItem(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val rarity: String,
    val iconUrl: String? = null,
    val expiresIn: String? = null,
    val isExpiringSoon: Boolean = false,
    val isBaggage: Boolean = false
)

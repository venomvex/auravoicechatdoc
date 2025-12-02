package com.aura.voicechat.ui.store

import androidx.compose.foundation.background
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
 * Store Screen - Shop for cosmetics and items
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    onNavigateBack: () -> Unit,
    onNavigateToItem: (String) -> Unit,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf("all") }
    
    val categories = listOf(
        "all" to "All",
        "frames" to "Frames",
        "vehicles" to "Vehicles",
        "themes" to "Themes",
        "mic_skins" to "Mic Skins",
        "seat_effects" to "Seat Effects",
        "chat_bubbles" to "Bubbles",
        "entrance" to "Entrance"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Store") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Wallet display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = CoinGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatNumber(uiState.coins),
                            style = MaterialTheme.typography.labelMedium,
                            color = CoinGold
                        )
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
            
            // Featured Banner (if any)
            if (uiState.featuredItem != null) {
                FeaturedBanner(
                    item = uiState.featuredItem!!,
                    onClick = { onNavigateToItem(uiState.featuredItem!!.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Items Grid
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentMagenta)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.items) { item ->
                        StoreItemCard(
                            item = item,
                            onClick = { onNavigateToItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedBanner(
    item: StoreItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = AccentMagenta.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FEATURED",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentMagenta
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = CoinGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatNumber(item.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CoinGold
                    )
                }
            }
            
            // Item Preview
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard),
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
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StoreItemCard(
    item: StoreItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rarity badge
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
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
                        color = if (item.rarity == "legendary") DarkCanvas else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Item Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                if (item.iconUrl != null) {
                    AsyncImage(
                        model = item.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    Icon(
                        when (item.category) {
                            "frames" -> Icons.Default.CropSquare
                            "vehicles" -> Icons.Default.DirectionsCar
                            "themes" -> Icons.Default.Palette
                            "mic_skins" -> Icons.Default.Mic
                            "seat_effects" -> Icons.Default.EventSeat
                            "chat_bubbles" -> Icons.Default.ChatBubble
                            else -> Icons.Default.AutoAwesome
                        },
                        contentDescription = null,
                        tint = AccentMagenta,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Name
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            
            // Duration
            if (item.duration != null) {
                Text(
                    text = item.duration,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = CoinGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatNumber(item.price),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CoinGold
                )
            }
        }
    }
}

data class StoreItem(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Long,
    val rarity: String,
    val iconUrl: String? = null,
    val duration: String? = null,
    val isOwned: Boolean = false
)

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

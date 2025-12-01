package com.aura.voicechat.ui.messages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Messages ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Fetches conversations, notifications, and system messages from the backend API.
 */
@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "MessagesViewModel"
    }
    
    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()
    
    init {
        loadAllData()
    }
    
    /**
     * Load all messages data from the backend.
     */
    private fun loadAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Load all data in parallel
            loadConversations()
            loadNotifications()
            loadSystemMessages()
            
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    /**
     * Load conversations from the backend API.
     */
    private suspend fun loadConversations() {
        try {
            val response = apiService.getConversations()
            if (response.isSuccessful) {
                val data = response.body()
                val conversations = data?.conversations?.map { dto ->
                    Conversation(
                        id = dto.id,
                        name = dto.name,
                        avatar = dto.avatar,
                        lastMessage = dto.lastMessage,
                        timestamp = formatTimestamp(dto.lastMessageAt),
                        unreadCount = dto.unreadCount,
                        isOnline = dto.isOnline
                    )
                } ?: emptyList()
                
                val unreadMessages = conversations.sumOf { it.unreadCount }
                
                _uiState.value = _uiState.value.copy(
                    conversations = conversations,
                    unreadMessages = unreadMessages
                )
                Log.d(TAG, "Loaded ${conversations.size} conversations")
            } else {
                Log.e(TAG, "Failed to load conversations: ${response.code()}")
                // Keep existing placeholder data or show empty state
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading conversations", e)
            // Keep existing placeholder data or show empty state
        }
    }
    
    /**
     * Load notifications from the backend API.
     */
    private suspend fun loadNotifications() {
        try {
            val response = apiService.getNotifications()
            if (response.isSuccessful) {
                val data = response.body()
                val notifications = data?.notifications?.map { dto ->
                    NotificationItem(
                        id = dto.id,
                        type = dto.type,
                        title = dto.title,
                        message = dto.message,
                        timestamp = formatTimestamp(dto.createdAt),
                        isRead = dto.isRead
                    )
                } ?: emptyList()
                
                val unreadNotifications = data?.unreadCount ?: notifications.count { !it.isRead }
                
                _uiState.value = _uiState.value.copy(
                    notifications = notifications,
                    unreadNotifications = unreadNotifications
                )
                Log.d(TAG, "Loaded ${notifications.size} notifications")
            } else {
                Log.e(TAG, "Failed to load notifications: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading notifications", e)
        }
    }
    
    /**
     * Load system messages from the backend API.
     */
    private suspend fun loadSystemMessages() {
        try {
            val response = apiService.getSystemMessages()
            if (response.isSuccessful) {
                val data = response.body()
                val systemMessages = data?.messages?.map { dto ->
                    SystemMessage(
                        id = dto.id,
                        title = dto.title,
                        content = dto.content,
                        timestamp = formatTimestamp(dto.createdAt)
                    )
                } ?: emptyList()
                
                _uiState.value = _uiState.value.copy(
                    systemMessages = systemMessages,
                    unreadSystem = systemMessages.size // All system messages are considered "new"
                )
                Log.d(TAG, "Loaded ${systemMessages.size} system messages")
            } else {
                Log.e(TAG, "Failed to load system messages: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading system messages", e)
        }
    }
    
    /**
     * Mark all notifications as read.
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val response = apiService.markAllNotificationsAsRead()
                if (response.isSuccessful) {
                    val updatedConversations = _uiState.value.conversations.map { it.copy(unreadCount = 0) }
                    val updatedNotifications = _uiState.value.notifications.map { it.copy(isRead = true) }
                    
                    _uiState.value = _uiState.value.copy(
                        conversations = updatedConversations,
                        notifications = updatedNotifications,
                        unreadMessages = 0,
                        unreadNotifications = 0,
                        unreadSystem = 0,
                        unreadCount = 0
                    )
                    Log.d(TAG, "Marked all as read")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error marking all as read", e)
            }
        }
    }
    
    /**
     * Handle a notification click - mark it as read.
     */
    fun handleNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.markNotificationAsRead(notificationId)
                if (response.isSuccessful) {
                    val updatedNotifications = _uiState.value.notifications.map { notification ->
                        if (notification.id == notificationId) notification.copy(isRead = true) else notification
                    }
                    val unreadCount = updatedNotifications.count { !it.isRead }
                    
                    _uiState.value = _uiState.value.copy(
                        notifications = updatedNotifications,
                        unreadNotifications = unreadCount
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error marking notification as read", e)
            }
        }
    }
    
    /**
     * Refresh all data from the backend.
     */
    fun refresh() {
        loadAllData()
    }
    
    /**
     * Format an ISO timestamp to a human-readable relative time.
     */
    private fun formatTimestamp(isoTimestamp: String): String {
        return try {
            val instant = java.time.Instant.parse(isoTimestamp)
            val now = java.time.Instant.now()
            val duration = java.time.Duration.between(instant, now)
            
            when {
                duration.toMinutes() < 1 -> "Just now"
                duration.toMinutes() < 60 -> "${duration.toMinutes()} min ago"
                duration.toHours() < 24 -> "${duration.toHours()} hours ago"
                duration.toDays() < 2 -> "Yesterday"
                duration.toDays() < 7 -> "${duration.toDays()} days ago"
                else -> {
                    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d")
                    java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
                        .format(formatter)
                }
            }
        } catch (e: Exception) {
            isoTimestamp // Return as-is if parsing fails
        }
    }
}

data class MessagesUiState(
    val isLoading: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val systemMessages: List<SystemMessage> = emptyList(),
    val unreadMessages: Int = 0,
    val unreadNotifications: Int = 0,
    val unreadSystem: Int = 0,
    val unreadCount: Int = 0,
    val error: String? = null
)

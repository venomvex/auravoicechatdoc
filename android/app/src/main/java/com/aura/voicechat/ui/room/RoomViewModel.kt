package com.aura.voicechat.ui.room

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.model.*
import com.aura.voicechat.data.remote.ApiService
import com.aura.voicechat.data.repository.ModerationRepository
import com.aura.voicechat.domain.model.Room
import com.aura.voicechat.domain.model.RoomMode
import com.aura.voicechat.domain.model.RoomType
import com.aura.voicechat.domain.model.Seat
import com.aura.voicechat.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Room ViewModel for voice/video rooms (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Features:
 * - Room settings
 * - Room jar
 * - Room rankings (daily/weekly/monthly)
 * - Live chat messages with content moderation
 * - Seat management (lock/unlock/kick/invite/mute/unmute/drag)
 * - Video player (YouTube)
 * - Games slider
 * - Events slider
 * - Lucky bags
 * - Activity notifications toggle
 */
@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val apiService: ApiService,
    private val moderationRepository: ModerationRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "RoomViewModel"
    }
    
    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()
    
    private var currentRoomId: String? = null
    private var currentUserId: String? = null // Set from auth
    
    fun loadRoom(roomId: String) {
        currentRoomId = roomId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load room details from API
                val response = apiService.getRoomDetails(roomId)
                if (response.isSuccessful && response.body() != null) {
                    val roomDetails = response.body()!!
                    val room = mapRoomDetailsToRoom(roomDetails)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        room = room,
                        settings = roomDetails.settings,
                        jar = roomDetails.jar,
                        rankings = roomDetails.rankings,
                        videoPlayer = roomDetails.videoPlayer,
                        activities = roomDetails.activities,
                        isCinemaMode = roomDetails.videoPlayer?.isCinemaMode == true
                    )
                    
                    // Load additional data
                    loadRoomMessages(roomId)
                    loadRoomGames(roomId)
                    loadRoomEvents(roomId)
                    loadLuckyBags(roomId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load room"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun mapRoomDetailsToRoom(details: RoomDetailsResponse): Room {
        return Room(
            id = details.id,
            name = details.name,
            coverImage = details.coverImage,
            ownerId = details.ownerId,
            ownerName = details.ownerName,
            ownerAvatar = details.ownerAvatar,
            type = when (details.type.lowercase()) {
                "video" -> RoomType.VIDEO
                "music" -> RoomType.MUSIC
                else -> RoomType.VOICE
            },
            mode = when (details.mode.lowercase()) {
                "vip_only" -> RoomMode.VIP_ONLY
                "invite_only" -> RoomMode.INVITE_ONLY
                else -> RoomMode.FREE
            },
            capacity = details.capacity,
            currentUsers = details.currentUsers,
            isLocked = details.isLocked,
            tags = details.tags,
            seats = details.seats.map { seat ->
                Seat(
                    position = seat.position,
                    userId = seat.userId,
                    userName = seat.userName,
                    userAvatar = seat.userAvatar,
                    userLevel = seat.userLevel,
                    userVip = seat.userVip,
                    isMuted = seat.isMuted,
                    isLocked = seat.isLocked,
                    effects = emptyList()
                )
            },
            createdAt = details.createdAt
        )
    }
    
    // ==================== ROOM SETTINGS ====================
    
    fun openSettings() {
        _uiState.value = _uiState.value.copy(showSettingsSheet = true)
    }
    
    fun closeSettings() {
        _uiState.value = _uiState.value.copy(showSettingsSheet = false)
    }
    
    fun updateSettings(request: UpdateRoomSettingsRequest) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.updateRoomSettings(roomId, request)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        settings = response.body(),
                        message = "Settings updated"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(error = "Failed to update settings")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== ROOM JAR ====================
    
    fun loadJar() {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.getRoomJar(roomId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(jar = response.body())
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun contributeToJar(amount: Long) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.contributeToJar(roomId, ContributeToJarRequest(amount))
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        jar = response.body(),
                        message = "Contributed $amount to jar!"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun claimJar(slotIndex: Int) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.claimJarReward(roomId, ClaimJarRequest(slotIndex))
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        message = result.message
                    )
                    loadJar() // Refresh jar state
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== ROOM RANKINGS ====================
    
    fun loadRankings(type: String = "daily") {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.getRoomRankings(roomId, type)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(rankings = response.body())
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun selectRankingType(type: String) {
        _uiState.value = _uiState.value.copy(selectedRankingType = type)
        loadRankings(type)
    }
    
    // ==================== ROOM MESSAGES (LIVE CHAT) ====================
    
    private fun loadRoomMessages(roomId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRoomMessages(roomId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        messages = response.body()!!.messages
                    )
                }
            } catch (e: Exception) {
                // Silent fail for messages
            }
        }
    }
    
    fun sendMessage(content: String) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            
            // Check for content violations before sending
            val moderationResult = moderationRepository.checkContent(content, "chat")
            moderationResult.fold(
                onSuccess = { result ->
                    if (result.isViolation) {
                        // Content violates policy
                        Log.w(TAG, "Message blocked due to violation: ${result.action}")
                        _uiState.value = _uiState.value.copy(
                            error = result.message ?: "Your message contains inappropriate content.",
                            isBanned = result.action != "warn",
                            banMessage = if (result.action != "warn") result.message else null,
                            banExpiry = result.banExpiry
                        )
                        return@launch
                    }
                    
                    // Content is clean, send it
                    try {
                        val response = apiService.sendRoomMessage(roomId, SendRoomMessageRequest(content))
                        if (response.isSuccessful && response.body() != null) {
                            // Add message to list
                            val newMessage = response.body()!!
                            _uiState.value = _uiState.value.copy(
                                messages = _uiState.value.messages + newMessage
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(error = e.message)
                    }
                },
                onFailure = { e ->
                    // Moderation check failed - log for audit and block message for safety
                    Log.w(TAG, "MODERATION_BYPASS: Content moderation check failed, blocking message for safety. Error: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        error = "Unable to verify message. Please try again."
                    )
                }
            )
        }
    }
    
    /**
     * Send image in chat with moderation check
     */
    fun sendImage(imageUrl: String) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            
            // Check for image violations before sending
            val moderationResult = moderationRepository.checkImage(imageUrl, "chat")
            moderationResult.fold(
                onSuccess = { result ->
                    if (result.isViolation) {
                        Log.w(TAG, "Image blocked due to violation: ${result.action}")
                        _uiState.value = _uiState.value.copy(
                            error = result.message ?: "This image violates our content policy.",
                            isBanned = result.action != "warn",
                            banMessage = if (result.action != "warn") result.message else null,
                            banExpiry = result.banExpiry
                        )
                        return@launch
                    }
                    
                    // Image is clean, send it
                    try {
                        val response = apiService.sendRoomImage(roomId, SendRoomImageRequest(imageUrl))
                        if (response.isSuccessful && response.body() != null) {
                            val newMessage = response.body()!!
                            _uiState.value = _uiState.value.copy(
                                messages = _uiState.value.messages + newMessage
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(error = e.message)
                    }
                },
                onFailure = { e ->
                    // Image moderation check failed - log for audit and block for safety
                    Log.w(TAG, "MODERATION_BYPASS: Image moderation check failed, blocking for safety. Error: ${e.message}")
                    _uiState.value = _uiState.value.copy(error = "Unable to verify image. Please try again.")
                }
            )
        }
    }
    
    /**
     * Check user's ban status
     */
    fun checkBanStatus() {
        viewModelScope.launch {
            val banStatus = moderationRepository.getBanStatus()
            _uiState.value = _uiState.value.copy(
                isBanned = banStatus.isBanned,
                banMessage = if (banStatus.isBanned) banStatus.banReason else null,
                banExpiry = banStatus.banExpiry
            )
        }
    }
    
    fun clearChat() {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.clearRoomChat(roomId, ClearChatRequest())
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        messages = emptyList(),
                        message = "Chat cleared"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== SEAT MANAGEMENT ====================
    
    fun onSeatClick(position: Int) {
        viewModelScope.launch {
            val room = _uiState.value.room ?: return@launch
            val seat = room.seats.getOrNull(position) ?: return@launch
            
            if (seat.userId == null && !seat.isLocked) {
                // Request to join seat
                joinSeat(position)
            } else if (seat.userId != null) {
                // Show user profile or seat action menu
                _uiState.value = _uiState.value.copy(
                    selectedUserId = seat.userId,
                    selectedSeatPosition = position,
                    showSeatActionSheet = true
                )
            }
        }
    }
    
    fun joinSeat(position: Int) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.joinSeat(roomId, JoinSeatRequest(position))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        currentSeatPosition = position,
                        isMuted = true,
                        message = "Joined seat ${position + 1}"
                    )
                    loadRoom(roomId) // Refresh room state
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun leaveSeat() {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            val position = _uiState.value.currentSeatPosition ?: return@launch
            try {
                val response = apiService.leaveSeat(roomId, LeaveSeatRequest(position))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        currentSeatPosition = null,
                        message = "Left seat"
                    )
                    loadRoom(roomId) // Refresh room state
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun lockSeat(position: Int) {
        performSeatAction(position, "lock")
    }
    
    fun unlockSeat(position: Int) {
        performSeatAction(position, "unlock")
    }
    
    fun kickFromSeat(position: Int) {
        val seat = _uiState.value.room?.seats?.getOrNull(position) ?: return
        performSeatAction(position, "kick", seat.userId)
    }
    
    fun inviteToSeat(position: Int, userId: String) {
        performSeatAction(position, "invite", userId)
    }
    
    fun muteUserOnSeat(position: Int) {
        val seat = _uiState.value.room?.seats?.getOrNull(position) ?: return
        performSeatAction(position, "mute", seat.userId)
    }
    
    fun unmuteUserOnSeat(position: Int) {
        val seat = _uiState.value.room?.seats?.getOrNull(position) ?: return
        performSeatAction(position, "unmute", seat.userId)
    }
    
    fun dragUserToSeat(fromPosition: Int, toPosition: Int) {
        val seat = _uiState.value.room?.seats?.getOrNull(fromPosition) ?: return
        performSeatAction(fromPosition, "drag", seat.userId, toPosition)
    }
    
    private fun performSeatAction(position: Int, action: String, targetUserId: String? = null, targetPosition: Int? = null) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.seatAction(roomId, SeatActionRequest(
                    seatPosition = position,
                    action = action,
                    targetUserId = targetUserId,
                    targetSeatPosition = targetPosition
                ))
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(message = response.body()!!.message)
                    loadRoom(roomId) // Refresh room state
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== MUTE/UNMUTE SELF ====================
    
    fun toggleMute() {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.toggleMuteSelf(roomId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isMuted = !_uiState.value.isMuted
                    )
                    loadRoom(roomId) // Refresh to get updated seat state
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== ADMIN ACTIONS ====================
    
    fun kickUser(userId: String, reason: String? = null) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.kickUser(roomId, KickUserRequest(userId, reason))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(message = "User kicked")
                    loadRoom(roomId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun banUser(userId: String, reason: String? = null, duration: Int? = null) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.banUser(roomId, BanUserRequest(userId, reason, duration))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(message = "User banned")
                    loadRoom(roomId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== VIDEO PLAYER ====================
    
    fun playVideo(url: String) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.playVideo(roomId, PlayVideoRequest(url))
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        videoPlayer = response.body(),
                        isCinemaMode = response.body()!!.isCinemaMode,
                        message = "Video started"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun stopVideo() {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.stopVideo(roomId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        videoPlayer = _uiState.value.videoPlayer?.copy(isPlaying = false, currentVideo = null),
                        isCinemaMode = false,
                        message = "Video stopped"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun toggleCinemaMode() {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.toggleCinemaMode(roomId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        videoPlayer = response.body(),
                        isCinemaMode = response.body()!!.isCinemaMode
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== GAMES SLIDER ====================
    
    private fun loadRoomGames(roomId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRoomGames(roomId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(games = response.body()!!.games)
                }
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }
    
    fun openGame(gameType: String) {
        _uiState.value = _uiState.value.copy(
            selectedGame = gameType,
            showGameSheet = true
        )
    }
    
    fun closeGameSheet() {
        _uiState.value = _uiState.value.copy(showGameSheet = false, selectedGame = null)
    }
    
    // ==================== EVENTS SLIDER ====================
    
    private fun loadRoomEvents(roomId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRoomEvents(roomId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(events = response.body()!!.events)
                }
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }
    
    fun openEvent(eventId: String) {
        _uiState.value = _uiState.value.copy(
            selectedEvent = eventId,
            showEventSheet = true
        )
    }
    
    fun closeEventSheet() {
        _uiState.value = _uiState.value.copy(showEventSheet = false, selectedEvent = null)
    }
    
    // ==================== LUCKY BAGS ====================
    
    private fun loadLuckyBags(roomId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getLuckyBags(roomId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(luckyBags = response.body()!!.bags)
                }
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }
    
    fun grabLuckyBag(bagId: String) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.grabLuckyBag(roomId, bagId)
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        message = if (result.success) "You got ${result.amount} coins!" else result.message
                    )
                    loadLuckyBags(roomId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun sendLuckyBag(totalAmount: Long, slots: Int, message: String?) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.sendLuckyBag(roomId, SendLuckyBagRequest(totalAmount, slots, message))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(message = "Lucky bag sent!")
                    loadLuckyBags(roomId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== ACTIVITIES SETTINGS ====================
    
    fun updateActivities(request: UpdateActivitiesRequest) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.updateRoomActivities(roomId, request)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(activities = response.body())
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== GIFT SENDING ====================
    
    fun sendGift(giftId: String, receiverId: String, quantity: Int = 1) {
        viewModelScope.launch {
            val roomId = currentRoomId ?: return@launch
            try {
                val response = apiService.sendGiftSimple(SendGiftRequest(giftId, receiverId, roomId, quantity))
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        message = "Gift sent!"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // ==================== MORE OPTIONS MENU ====================
    
    fun openMoreMenu() {
        _uiState.value = _uiState.value.copy(showMoreMenu = true)
    }
    
    fun closeMoreMenu() {
        _uiState.value = _uiState.value.copy(showMoreMenu = false)
    }
    
    // ==================== UI STATE HELPERS ====================
    
    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun closeSeatActionSheet() {
        _uiState.value = _uiState.value.copy(showSeatActionSheet = false, selectedSeatPosition = null)
    }
    
    // Check if current user is owner
    fun isOwner(): Boolean {
        return _uiState.value.room?.ownerId == currentUserId
    }
    
    // Check if current user is admin (implement with API check)
    fun isAdmin(): Boolean {
        // TODO: Implement proper admin check
        return false
    }
    
    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }
}

data class RoomUiState(
    val isLoading: Boolean = false,
    val room: Room? = null,
    val currentSeatPosition: Int? = null,
    val isMuted: Boolean = true,
    val selectedUserId: String? = null,
    val selectedSeatPosition: Int? = null,
    val message: String? = null,
    val error: String? = null,
    
    // Settings
    val settings: RoomSettingsDto? = null,
    val showSettingsSheet: Boolean = false,
    
    // Jar
    val jar: RoomJarDto? = null,
    
    // Rankings
    val rankings: RoomRankingsDto? = null,
    val selectedRankingType: String = "daily",
    
    // Messages (Live Chat)
    val messages: List<RoomMessageDto> = emptyList(),
    
    // Video Player
    val videoPlayer: VideoPlayerDto? = null,
    val isCinemaMode: Boolean = false,
    
    // Activities
    val activities: RoomActivitiesDto? = null,
    
    // Games
    val games: List<RoomGameDto> = emptyList(),
    val selectedGame: String? = null,
    val showGameSheet: Boolean = false,
    
    // Events
    val events: List<RoomEventDto> = emptyList(),
    val selectedEvent: String? = null,
    val showEventSheet: Boolean = false,
    
    // Lucky Bags
    val luckyBags: List<LuckyBagDto> = emptyList(),
    
    // UI State
    val showMoreMenu: Boolean = false,
    val showSeatActionSheet: Boolean = false,
    
    // Ban status (from moderation)
    val isBanned: Boolean = false,
    val banMessage: String? = null,
    val banExpiry: String? = null
)

package com.aura.voicechat.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.domain.model.RoomCard
import com.aura.voicechat.domain.model.RoomType
import com.aura.voicechat.domain.repository.AuthRepository
import com.aura.voicechat.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Home ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Fetches rooms and user data from the backend API.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "HomeViewModel"
    }
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadUserInfo()
        loadRooms()
        loadBanners()
    }
    
    /**
     * Load current user information.
     */
    private fun loadUserInfo() {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUserId()
                val hasRoom = _uiState.value.myRooms.isNotEmpty()
                
                _uiState.value = _uiState.value.copy(
                    currentUserId = userId,
                    isNewUser = userId == null || !hasRoom
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user info", e)
            }
        }
    }
    
    /**
     * Load rooms from the backend API.
     */
    private fun loadRooms() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load popular rooms from backend
                val popularResult = roomRepository.getPopularRooms()
                popularResult.fold(
                    onSuccess = { rooms ->
                        Log.d(TAG, "Loaded ${rooms.size} popular rooms from backend")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            popularRooms = rooms
                        )
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to load popular rooms from backend", e)
                        // Use mock data as fallback
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            popularRooms = getMockPopularRooms()
                        )
                    }
                )
                
                // Load user's own rooms from backend
                val myRoomsResult = roomRepository.getMyRooms()
                myRoomsResult.fold(
                    onSuccess = { rooms ->
                        Log.d(TAG, "Loaded ${rooms.size} my rooms from backend")
                        _uiState.value = _uiState.value.copy(
                            myRooms = rooms,
                            isNewUser = rooms.isEmpty()
                        )
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to load my rooms from backend", e)
                        _uiState.value = _uiState.value.copy(
                            myRooms = emptyList(),
                            isNewUser = true
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading rooms", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                    popularRooms = getMockPopularRooms()
                )
            }
        }
    }
    
    /**
     * Load banners - for now uses mock data.
     */
    private fun loadBanners() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                banners = listOf("banner1", "banner2", "banner3")
            )
        }
    }
    
    /**
     * Refresh all data from the backend.
     */
    fun refresh() {
        loadRooms()
        loadUserInfo()
    }
    
    /**
     * Handle home icon click - either join user's room or show create dialog.
     */
    fun handleHomeIconClick(): HomeAction {
        return if (_uiState.value.isNewUser || _uiState.value.myRooms.isEmpty()) {
            HomeAction.ShowCreateRoom
        } else {
            HomeAction.JoinMyRoom(_uiState.value.myRooms.firstOrNull()?.id ?: "")
        }
    }
    
    /**
     * Get mock popular rooms as fallback when backend is unavailable.
     */
    private fun getMockPopularRooms(): List<RoomCard> {
        return listOf(
            RoomCard(
                id = "room_1",
                name = "Music Lounge 🎵",
                coverImage = null,
                ownerName = "DJ Mike",
                ownerAvatar = null,
                type = RoomType.MUSIC,
                userCount = 45,
                capacity = 100,
                isLive = true,
                tags = listOf("Music", "Chill", "English")
            ),
            RoomCard(
                id = "room_2",
                name = "Late Night Talk",
                coverImage = null,
                ownerName = "Sarah",
                ownerAvatar = null,
                type = RoomType.VOICE,
                userCount = 23,
                capacity = 50,
                isLive = true,
                tags = listOf("Talk", "Dating")
            ),
            RoomCard(
                id = "room_3",
                name = "Gaming Zone 🎮",
                coverImage = null,
                ownerName = "GamerPro",
                ownerAvatar = null,
                type = RoomType.VIDEO,
                userCount = 67,
                capacity = 100,
                isLive = true,
                tags = listOf("Gaming", "Fun")
            )
        )
    }
}

/**
 * Sealed class for Home screen actions.
 */
sealed class HomeAction {
    object ShowCreateRoom : HomeAction()
    data class JoinMyRoom(val roomId: String) : HomeAction()
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val currentUserId: String? = null,
    val isNewUser: Boolean = true,
    val myRooms: List<RoomCard> = emptyList(),
    val popularRooms: List<RoomCard> = emptyList(),
    val recentRooms: List<RoomCard> = emptyList(),
    val followingRooms: List<RoomCard> = emptyList(),
    val banners: List<String> = emptyList(),
    val error: String? = null
)

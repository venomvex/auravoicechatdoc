package com.aura.voicechat.ui.followers

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
 * Follow List ViewModel (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "FollowListViewModel"
    }
    
    private val _uiState = MutableStateFlow(FollowListUiState())
    val uiState: StateFlow<FollowListUiState> = _uiState.asStateFlow()
    
    private var currentUserId: String? = null
    private var listType: String = "followers"
    
    fun loadList(type: String, userId: String) {
        currentUserId = userId
        listType = type
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = if (type == "followers") {
                    apiService.getUserFollowers(userId)
                } else {
                    apiService.getUserFollowing(userId)
                }
                
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    val users = data.users.map { dto ->
                        FollowUser(
                            userId = dto.id,
                            name = dto.name,
                            avatar = dto.avatar,
                            level = dto.level,
                            vipLevel = dto.vipTier,
                            isFollowing = dto.isFollowing
                        )
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        users = users,
                        totalCount = data.total
                    )
                    Log.d(TAG, "Loaded ${users.size} $type")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        users = emptyList()
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading $type", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun toggleFollow(userId: String) {
        viewModelScope.launch {
            try {
                val user = _uiState.value.users.find { it.userId == userId } ?: return@launch
                
                val response = if (user.isFollowing) {
                    apiService.unfollowUser(userId)
                } else {
                    apiService.followUser(userId)
                }
                
                if (response.isSuccessful) {
                    val updatedUsers = _uiState.value.users.map { u ->
                        if (u.userId == userId) {
                            u.copy(isFollowing = !u.isFollowing)
                        } else {
                            u
                        }
                    }
                    _uiState.value = _uiState.value.copy(users = updatedUsers)
                    Log.d(TAG, "Toggled follow for $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling follow", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class FollowListUiState(
    val isLoading: Boolean = false,
    val users: List<FollowUser> = emptyList(),
    val totalCount: Int = 0,
    val error: String? = null
)

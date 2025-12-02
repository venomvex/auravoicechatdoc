package com.aura.voicechat.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.remote.ApiService
import com.aura.voicechat.domain.model.RoomCard
import com.aura.voicechat.domain.model.RoomType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Search ViewModel (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "SearchViewModel"
    }
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    fun search(query: String, tabIndex: Int) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            isLoading = query.isNotEmpty()
        )
        
        if (query.isEmpty()) {
            clearSearch()
            return
        }
        
        viewModelScope.launch {
            when (tabIndex) {
                0 -> searchUsers(query)
                1 -> searchRooms(query)
                2 -> searchById(query)
            }
        }
    }
    
    private suspend fun searchUsers(query: String) {
        try {
            val response = apiService.searchUsers(query)
            if (response.isSuccessful && response.body() != null) {
                val results = response.body()!!.users.map { user ->
                    SearchUser(
                        userId = user.id,
                        name = user.name,
                        avatar = user.avatar,
                        level = user.level,
                        isOnline = user.isOnline
                    )
                }
                addToRecentSearches(query)
                _uiState.value = _uiState.value.copy(
                    userResults = results,
                    isLoading = false
                )
                Log.d(TAG, "Found ${results.size} users")
            } else {
                _uiState.value = _uiState.value.copy(
                    userResults = emptyList(),
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching users", e)
            _uiState.value = _uiState.value.copy(
                userResults = emptyList(),
                isLoading = false,
                error = e.message
            )
        }
    }
    
    private suspend fun searchRooms(query: String) {
        try {
            val response = apiService.searchRooms(query)
            if (response.isSuccessful && response.body() != null) {
                val results = response.body()!!.data.map { room ->
                    SearchRoom(
                        roomId = room.id,
                        name = room.name,
                        cover = room.coverImage,
                        ownerName = room.ownerName,
                        memberCount = room.userCount
                    )
                }
                addToRecentSearches(query)
                _uiState.value = _uiState.value.copy(
                    roomResults = results,
                    isLoading = false
                )
                Log.d(TAG, "Found ${results.size} rooms")
            } else {
                _uiState.value = _uiState.value.copy(
                    roomResults = emptyList(),
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching rooms", e)
            _uiState.value = _uiState.value.copy(
                roomResults = emptyList(),
                isLoading = false,
                error = e.message
            )
        }
    }
    
    private suspend fun searchById(query: String) {
        try {
            // First try to find user by ID
            val userResponse = apiService.getUser(query)
            if (userResponse.isSuccessful && userResponse.body() != null) {
                val user = userResponse.body()!!
                _uiState.value = _uiState.value.copy(
                    idSearchResult = IdSearchResult(user.id, user.name, "user"),
                    isLoading = false
                )
                return
            }
            
            // Then try to find room by ID
            val roomResponse = apiService.getRoom(query)
            if (roomResponse.isSuccessful && roomResponse.body() != null) {
                val room = roomResponse.body()!!
                _uiState.value = _uiState.value.copy(
                    idSearchResult = IdSearchResult(room.id, room.name, "room"),
                    isLoading = false
                )
                return
            }
            
            // Not found
            _uiState.value = _uiState.value.copy(
                idSearchResult = null,
                isLoading = false
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error searching by ID", e)
            _uiState.value = _uiState.value.copy(
                idSearchResult = null,
                isLoading = false
            )
        }
    }
    
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            userResults = emptyList(),
            roomResults = emptyList(),
            idSearchResult = null
        )
    }
    
    fun clearRecentSearches() {
        _uiState.value = _uiState.value.copy(recentSearches = emptyList())
    }
    
    private fun addToRecentSearches(query: String) {
        if (query.isBlank()) return
        val current = _uiState.value.recentSearches.toMutableList()
        current.remove(query) // Remove if exists to avoid duplicates
        current.add(0, query) // Add at the beginning
        _uiState.value = _uiState.value.copy(
            recentSearches = current.take(10) // Keep only last 10 searches
        )
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val userResults: List<SearchUser> = emptyList(),
    val roomResults: List<SearchRoom> = emptyList(),
    val idSearchResult: IdSearchResult? = null,
    val recentSearches: List<String> = emptyList(),
    val error: String? = null
)

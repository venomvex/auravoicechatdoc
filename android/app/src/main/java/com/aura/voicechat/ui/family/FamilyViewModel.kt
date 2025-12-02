package com.aura.voicechat.ui.family

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
 * Family System ViewModel (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class FamilyViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "FamilyViewModel"
    }
    
    private val _uiState = MutableStateFlow(FamilyUiState())
    val uiState: StateFlow<FamilyUiState> = _uiState.asStateFlow()
    
    init {
        loadFamilyData()
    }
    
    private fun loadFamilyData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load user's family from backend
                val response = apiService.getMyFamily()
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasFamily = data.hasFamily,
                        familyId = data.family?.id ?: "",
                        familyName = data.family?.name ?: "",
                        familyBadge = data.family?.badge,
                        familyLevel = data.family?.level ?: 0,
                        membersCount = data.family?.memberCount ?: 0,
                        maxMembers = data.family?.maxMembers ?: 50,
                        weeklyGifts = data.family?.weeklyGifts ?: 0,
                        totalGifts = data.family?.totalGifts ?: 0,
                        weeklyRanking = data.family?.weeklyRanking ?: 0,
                        createdDate = data.family?.createdAt ?: "",
                        ownerName = data.family?.ownerName ?: "",
                        familyNotice = data.family?.notice ?: "",
                        isOwner = data.family?.isOwner ?: false,
                        isAdmin = data.family?.isAdmin ?: false,
                        members = data.family?.members?.map { m ->
                            FamilyMember(
                                userId = m.userId,
                                name = m.userName,
                                avatar = m.userAvatar,
                                role = m.role,
                                weeklyContribution = m.contribution
                            )
                        } ?: emptyList(),
                        perks = data.family?.perks?.map { p ->
                            FamilyPerk(
                                id = p.id,
                                name = p.name,
                                description = p.description,
                                requiredLevel = p.requiredLevel
                            )
                        } ?: emptyList()
                    )
                    
                    // Load activity if user has a family
                    if (data.hasFamily && data.family != null) {
                        loadFamilyActivity(data.family.id)
                    }
                    Log.d(TAG, "Loaded family data: hasFamily=${data.hasFamily}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasFamily = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading family data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private suspend fun loadFamilyActivity(familyId: String) {
        try {
            val response = apiService.getFamilyActivity(familyId)
            if (response.isSuccessful && response.body() != null) {
                val activities = response.body()!!.activities.map { a ->
                    FamilyActivity(
                        id = a.id,
                        type = a.type,
                        message = a.message,
                        timeAgo = a.timeAgo
                    )
                }
                _uiState.value = _uiState.value.copy(recentActivities = activities)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading family activity", e)
        }
    }
    
    fun showCreateFamilyDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }
    
    fun dismissCreateFamilyDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }
    
    fun showJoinFamilyDialog() {
        _uiState.value = _uiState.value.copy(showJoinDialog = true)
    }
    
    fun dismissJoinFamilyDialog() {
        _uiState.value = _uiState.value.copy(showJoinDialog = false)
    }
    
    fun showSettingsDialog() {
        _uiState.value = _uiState.value.copy(showSettingsDialog = true)
    }
    
    fun dismissSettingsDialog() {
        _uiState.value = _uiState.value.copy(showSettingsDialog = false)
    }
    
    fun createFamily(name: String, badge: String?) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isCreating = true)
                val response = apiService.createFamily(
                    com.aura.voicechat.data.model.CreateFamilyRequest(
                        name = name,
                        badge = badge
                    )
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    dismissCreateFamilyDialog()
                    loadFamilyData() // Refresh
                    Log.d(TAG, "Family created successfully")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = response.body()?.message ?: "Failed to create family"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating family", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isCreating = false)
            }
        }
    }
    
    fun joinFamily(familyId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.joinFamily(familyId)
                if (response.isSuccessful) {
                    dismissJoinFamilyDialog()
                    loadFamilyData() // Refresh
                    Log.d(TAG, "Joined family successfully")
                } else {
                    _uiState.value = _uiState.value.copy(error = "Failed to join family")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error joining family", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun leaveFamily() {
        viewModelScope.launch {
            val familyId = _uiState.value.familyId
            if (familyId.isEmpty()) return@launch
            
            try {
                val response = apiService.leaveFamily(familyId)
                if (response.isSuccessful) {
                    loadFamilyData() // Refresh
                    Log.d(TAG, "Left family successfully")
                } else {
                    _uiState.value = _uiState.value.copy(error = "Failed to leave family")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error leaving family", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun kickMember(userId: String) {
        viewModelScope.launch {
            val familyId = _uiState.value.familyId
            if (familyId.isEmpty()) return@launch
            
            try {
                val response = apiService.kickFamilyMember(familyId, userId)
                if (response.isSuccessful) {
                    loadFamilyData() // Refresh
                    Log.d(TAG, "Kicked member successfully")
                } else {
                    _uiState.value = _uiState.value.copy(error = "Failed to kick member")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error kicking member", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun promoteMember(userId: String, newRole: String) {
        viewModelScope.launch {
            val familyId = _uiState.value.familyId
            if (familyId.isEmpty()) return@launch
            
            try {
                val response = apiService.updateFamilyMemberRole(
                    familyId, 
                    userId, 
                    com.aura.voicechat.data.model.UpdateFamilyRoleRequest(role = newRole)
                )
                if (response.isSuccessful) {
                    loadFamilyData() // Refresh
                    Log.d(TAG, "Updated member role successfully")
                } else {
                    _uiState.value = _uiState.value.copy(error = "Failed to update role")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating member role", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun searchFamilies(query: String) {
        viewModelScope.launch {
            try {
                val response = apiService.searchFamilies(query)
                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!.families.map { f ->
                        FamilySearchResult(
                            id = f.id,
                            name = f.name,
                            badge = f.badge,
                            level = f.level,
                            memberCount = f.memberCount,
                            isOpen = f.isOpen
                        )
                    }
                    _uiState.value = _uiState.value.copy(searchResults = results)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error searching families", e)
            }
        }
    }
    
    fun refresh() {
        loadFamilyData()
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class FamilyUiState(
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val hasFamily: Boolean = false,
    val familyId: String = "",
    val familyName: String = "",
    val familyBadge: String? = null,
    val familyLevel: Int = 0,
    val membersCount: Int = 0,
    val maxMembers: Int = 50,
    val weeklyGifts: Long = 0,
    val totalGifts: Long = 0,
    val weeklyRanking: Int = 0,
    val createdDate: String = "",
    val ownerName: String = "",
    val familyNotice: String = "",
    val isOwner: Boolean = false,
    val isAdmin: Boolean = false,
    val members: List<FamilyMember> = emptyList(),
    val recentActivities: List<FamilyActivity> = emptyList(),
    val perks: List<FamilyPerk> = emptyList(),
    val searchResults: List<FamilySearchResult> = emptyList(),
    val showCreateDialog: Boolean = false,
    val showJoinDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val error: String? = null
)

data class FamilySearchResult(
    val id: String,
    val name: String,
    val badge: String?,
    val level: Int,
    val memberCount: Int,
    val isOpen: Boolean
)

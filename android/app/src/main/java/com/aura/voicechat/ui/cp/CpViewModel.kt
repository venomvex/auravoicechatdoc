package com.aura.voicechat.ui.cp

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
 * CP System ViewModel (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@HiltViewModel
class CpViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "CpViewModel"
    }
    
    private val _uiState = MutableStateFlow(CpUiState())
    val uiState: StateFlow<CpUiState> = _uiState.asStateFlow()
    
    init {
        loadCpData()
    }
    
    private fun loadCpData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load CP status from backend
                val response = apiService.getCpStatus()
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasPartner = data.hasPartner,
                        partnerId = data.partner?.userId ?: "",
                        partnerName = data.partner?.name ?: "",
                        partnerAvatar = data.partner?.avatar,
                        cpLevel = data.level,
                        cpDays = calculateCpDays(data.anniversaryDate),
                        pendingRequests = data.pendingRequests?.map { req ->
                            CpRequest(
                                id = req.id,
                                fromUserId = req.fromUserId,
                                fromUserName = req.fromUserName,
                                fromUserAvatar = req.fromUserAvatar,
                                message = req.message
                            )
                        } ?: emptyList()
                    )
                    
                    // Load CP progress if has partner
                    if (data.hasPartner) {
                        loadCpProgress()
                    }
                    Log.d(TAG, "Loaded CP data: hasPartner=${data.hasPartner}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasPartner = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading CP data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private suspend fun loadCpProgress() {
        try {
            val response = apiService.getCpProgress()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                _uiState.value = _uiState.value.copy(
                    cpExp = data.currentPoints,
                    cpExpRequired = data.nextLevelPoints,
                    currentBenefits = data.perks.filter { it.isUnlocked }.map { it.description },
                    cpCosmetics = data.perks.map { perk ->
                        CpCosmetic(
                            id = perk.id,
                            name = perk.name,
                            type = "cosmetic",
                            requiredLevel = perk.requiredLevel
                        )
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading CP progress", e)
        }
    }
    
    private fun calculateCpDays(anniversaryDate: String?): Int {
        if (anniversaryDate == null) return 0
        return try {
            val anniversary = java.time.Instant.parse(anniversaryDate)
            val now = java.time.Instant.now()
            java.time.Duration.between(anniversary, now).toDays().toInt()
        } catch (e: Exception) {
            0
        }
    }
    
    fun showFindPartnerDialog() {
        _uiState.value = _uiState.value.copy(showFindPartnerDialog = true)
    }
    
    fun dismissFindPartnerDialog() {
        _uiState.value = _uiState.value.copy(showFindPartnerDialog = false)
    }
    
    fun sendCpRequest(userId: String, message: String? = null) {
        viewModelScope.launch {
            try {
                val response = apiService.sendCpRequest(
                    com.aura.voicechat.data.model.CpRequestDto(
                        id = "",
                        fromUserId = "",
                        fromUserName = "",
                        fromUserAvatar = null,
                        toUserId = userId,
                        message = message,
                        createdAt = ""
                    )
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    dismissFindPartnerDialog()
                    _uiState.value = _uiState.value.copy(message = "CP request sent!")
                    Log.d(TAG, "CP request sent successfully")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = response.body()?.message ?: "Failed to send CP request"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending CP request", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun acceptCpRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.respondToCpRequest(
                    requestId,
                    com.aura.voicechat.data.model.CpRespondRequest(accept = true)
                )
                if (response.isSuccessful) {
                    loadCpData() // Refresh
                    _uiState.value = _uiState.value.copy(message = "CP request accepted!")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error accepting CP request", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun rejectCpRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.respondToCpRequest(
                    requestId,
                    com.aura.voicechat.data.model.CpRespondRequest(accept = false)
                )
                if (response.isSuccessful) {
                    val requests = _uiState.value.pendingRequests.filter { it.id != requestId }
                    _uiState.value = _uiState.value.copy(pendingRequests = requests)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error rejecting CP request", e)
            }
        }
    }
    
    fun dissolveCp() {
        viewModelScope.launch {
            try {
                val response = apiService.dissolveCp()
                if (response.isSuccessful) {
                    loadCpData() // Refresh
                    _uiState.value = _uiState.value.copy(message = "CP partnership dissolved")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error dissolving CP", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun loadRankings() {
        viewModelScope.launch {
            try {
                val response = apiService.getCpRankings()
                if (response.isSuccessful && response.body() != null) {
                    val rankings = response.body()!!.rankings.map { r ->
                        CpRankingItem(
                            rank = r.rank,
                            user1Name = r.user1.name,
                            user1Avatar = r.user1.avatar,
                            user2Name = r.user2.name,
                            user2Avatar = r.user2.avatar,
                            level = r.level,
                            points = r.points
                        )
                    }
                    _uiState.value = _uiState.value.copy(rankings = rankings)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading CP rankings", e)
            }
        }
    }
    
    fun claimTaskReward(taskId: String) {
        viewModelScope.launch {
            try {
                // In a real implementation, this would call an API to claim the task reward
                // For now, we update the UI state to reflect the claimed task
                val updatedTasks = _uiState.value.dailyTasks.map { task ->
                    if (task.id == taskId && task.isCompleted) {
                        task.copy(isClaimed = true)
                    } else {
                        task
                    }
                }
                _uiState.value = _uiState.value.copy(
                    dailyTasks = updatedTasks,
                    message = "Task reward claimed!"
                )
                Log.d(TAG, "Claimed task reward: $taskId")
            } catch (e: Exception) {
                Log.e(TAG, "Error claiming task reward", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun refresh() {
        loadCpData()
    }
    
    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CpUiState(
    val isLoading: Boolean = false,
    val hasPartner: Boolean = false,
    val partnerId: String = "",
    val partnerName: String = "",
    val partnerAvatar: String? = null,
    val cpLevel: Int = 0,
    val cpExp: Long = 0,
    val cpExpRequired: Long = 0,
    val cpDays: Int = 0,
    val currentBenefits: List<String> = emptyList(),
    val dailyTasks: List<CpTask> = emptyList(),
    val cpCosmetics: List<CpCosmetic> = emptyList(),
    val pendingRequests: List<CpRequest> = emptyList(),
    val rankings: List<CpRankingItem> = emptyList(),
    val showFindPartnerDialog: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

data class CpRequest(
    val id: String,
    val fromUserId: String,
    val fromUserName: String,
    val fromUserAvatar: String?,
    val message: String?
)

data class CpRankingItem(
    val rank: Int,
    val user1Name: String,
    val user1Avatar: String?,
    val user2Name: String,
    val user2Avatar: String?,
    val level: Int,
    val points: Long
)

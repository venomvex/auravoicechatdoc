package com.aura.voicechat.ui.events

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
 * Events ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Uses live backend API for events data.
 */
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()
    
    fun loadEvent(eventType: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Load events from backend
                val eventsResponse = apiService.getEvents()
                if (eventsResponse.isSuccessful) {
                    val events = eventsResponse.body()?.events ?: emptyList()
                    val targetEvent = events.find { it.type == eventType }
                    
                    if (targetEvent != null) {
                        // Load event details
                        val detailsResponse = apiService.getEventDetails(targetEvent.id)
                        if (detailsResponse.isSuccessful) {
                            val details = detailsResponse.body()
                            
                            when (eventType) {
                                "recharge" -> {
                                    _uiState.value = EventsUiState(
                                        isLoading = false,
                                        eventName = details?.name ?: "Monthly Recharge Rewards",
                                        timeRemaining = details?.timeRemaining ?: "Loading...",
                                        rechargeTiers = details?.rechargeTiers?.map { tier ->
                                            RechargeTier(
                                                id = tier.id,
                                                amount = tier.amount,
                                                currentAmount = tier.currentAmount ?: 0,
                                                reward = tier.reward,
                                                bonus = tier.bonus ?: 0,
                                                isCompleted = tier.isCompleted ?: false,
                                                isClaimed = tier.isClaimed ?: false
                                            )
                                        } ?: emptyList()
                                    )
                                }
                                "party_star" -> {
                                    _uiState.value = EventsUiState(
                                        isLoading = false,
                                        eventName = details?.name ?: "Weekly Party Star",
                                        timeRemaining = details?.timeRemaining ?: "Loading...",
                                        myPoints = details?.myPoints ?: 0,
                                        myRank = details?.myRank ?: 0,
                                        nextTierPoints = details?.nextTierPoints ?: 0,
                                        topStars = details?.topStars?.map { star ->
                                            PartyStar(
                                                id = star.id,
                                                name = star.name,
                                                level = star.level,
                                                rank = star.rank,
                                                points = star.points
                                            )
                                        } ?: emptyList(),
                                        partyRewards = details?.partyRewards?.map { reward ->
                                            PartyReward(
                                                rankRange = reward.rankRange,
                                                rewards = reward.rewards
                                            )
                                        } ?: emptyList()
                                    )
                                }
                                "room_support" -> {
                                    _uiState.value = EventsUiState(
                                        isLoading = false,
                                        eventName = details?.name ?: "Support Room",
                                        timeRemaining = details?.timeRemaining ?: "Permanent",
                                        supportOptions = details?.supportOptions?.map { option ->
                                            SupportOption(
                                                id = option.id,
                                                name = option.name,
                                                description = option.description,
                                                cost = option.cost
                                            )
                                        } ?: emptyList(),
                                        topSupporters = details?.topSupporters?.map { supporter ->
                                            Supporter(
                                                id = supporter.id,
                                                name = supporter.name,
                                                rank = supporter.rank,
                                                amount = supporter.amount
                                            )
                                        } ?: emptyList()
                                    )
                                }
                                else -> {
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        error = "Unknown event type"
                                    )
                                }
                            }
                        } else {
                            // Fallback to basic data
                            loadFallbackData(eventType)
                        }
                    } else {
                        // Event not found, load fallback
                        loadFallbackData(eventType)
                    }
                } else {
                    loadFallbackData(eventType)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun loadFallbackData(eventType: String) {
        // Fallback to loading from individual event endpoints if available
        when (eventType) {
            "recharge" -> loadRechargeEvent()
            "party_star" -> loadPartyStarEvent()
            "room_support" -> loadRoomSupportEvent()
        }
    }
    
    private fun loadRechargeEvent() {
        viewModelScope.launch {
            try {
                val progressResponse = apiService.getEventProgress("recharge")
                if (progressResponse.isSuccessful) {
                    val progress = progressResponse.body()
                    _uiState.value = EventsUiState(
                        isLoading = false,
                        eventName = "Monthly Recharge Rewards",
                        timeRemaining = progress?.timeRemaining ?: "5 days",
                        rechargeTiers = progress?.tiers?.map { tier ->
                            RechargeTier(
                                tier.id, 
                                tier.targetAmount ?: 0,
                                tier.currentAmount ?: 0, 
                                tier.reward ?: "",
                                tier.bonus ?: 0,
                                tier.isCompleted ?: false,
                                tier.isClaimed ?: false
                            )
                        } ?: emptyList()
                    )
                } else {
                    _uiState.value = EventsUiState(
                        isLoading = false,
                        eventName = "Monthly Recharge Rewards",
                        timeRemaining = "Loading...",
                        error = "Failed to load event data"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = EventsUiState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun loadPartyStarEvent() {
        viewModelScope.launch {
            try {
                val progressResponse = apiService.getEventProgress("party_star")
                if (progressResponse.isSuccessful) {
                    val progress = progressResponse.body()
                    _uiState.value = EventsUiState(
                        isLoading = false,
                        eventName = "Weekly Party Star",
                        timeRemaining = progress?.timeRemaining ?: "3 days",
                        myPoints = progress?.myPoints ?: 0,
                        myRank = progress?.myRank ?: 0,
                        nextTierPoints = progress?.nextTierPoints ?: 0
                    )
                } else {
                    _uiState.value = EventsUiState(
                        isLoading = false,
                        eventName = "Weekly Party Star",
                        timeRemaining = "Loading...",
                        error = "Failed to load event data"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = EventsUiState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun loadRoomSupportEvent() {
        viewModelScope.launch {
            try {
                val progressResponse = apiService.getEventProgress("room_support")
                if (progressResponse.isSuccessful) {
                    val progress = progressResponse.body()
                    _uiState.value = EventsUiState(
                        isLoading = false,
                        eventName = "Support Room",
                        timeRemaining = "Permanent"
                    )
                } else {
                    _uiState.value = EventsUiState(
                        isLoading = false,
                        eventName = "Support Room",
                        timeRemaining = "Permanent",
                        error = "Failed to load event data"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = EventsUiState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun claimRechargeReward(tierId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.participateInEvent(tierId)
                if (response.isSuccessful) {
                    val updatedTiers = _uiState.value.rechargeTiers.map { tier ->
                        if (tier.id == tierId && tier.isCompleted) {
                            tier.copy(isClaimed = true)
                        } else {
                            tier
                        }
                    }
                    _uiState.value = _uiState.value.copy(rechargeTiers = updatedTiers)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun supportRoom(optionId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.participateInEvent(optionId)
                if (response.isSuccessful) {
                    // Reload room support event to get updated data
                    loadRoomSupportEvent()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}

data class EventsUiState(
    val isLoading: Boolean = false,
    val eventName: String = "",
    val timeRemaining: String = "",
    val rechargeTiers: List<RechargeTier> = emptyList(),
    val myPoints: Long = 0,
    val myRank: Int = 0,
    val nextTierPoints: Long = 0,
    val topStars: List<PartyStar> = emptyList(),
    val partyRewards: List<PartyReward> = emptyList(),
    val supportOptions: List<SupportOption> = emptyList(),
    val topSupporters: List<Supporter> = emptyList(),
    val error: String? = null
)

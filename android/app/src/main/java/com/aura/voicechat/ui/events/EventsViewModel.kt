package com.aura.voicechat.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.model.EventRewardDto
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
 * Properly maps from EventDto, EventDetailsResponse, and EventProgressResponse.
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
                        val progressResponse = apiService.getEventProgress(targetEvent.id)
                        
                        val eventDetails = detailsResponse.body()
                        val eventProgress = progressResponse.body()
                        val eventData = eventDetails?.event ?: targetEvent
                        
                        // Calculate time remaining
                        val timeRemaining = calculateTimeRemaining(eventData.endAt)
                        
                        when (eventType) {
                            "recharge" -> {
                                // Map rewards to recharge tiers
                                val tiers = eventData.rewards?.mapIndexed { index, reward ->
                                    val rewardProgress = eventProgress?.rewards?.find { it.rewardId == reward.id }
                                    RechargeTier(
                                        id = reward.id,
                                        targetAmount = reward.requirement,
                                        currentAmount = eventProgress?.progress ?: 0,
                                        rewardName = reward.name,
                                        bonusCoins = reward.value,
                                        isCompleted = (eventProgress?.progress ?: 0) >= reward.requirement,
                                        isClaimed = rewardProgress?.isClaimed ?: false
                                    )
                                } ?: emptyList()
                                
                                _uiState.value = EventsUiState(
                                    isLoading = false,
                                    eventName = eventData.name,
                                    timeRemaining = timeRemaining,
                                    rechargeTiers = tiers
                                )
                            }
                            "party_star" -> {
                                // Map rankings to party stars
                                val stars = eventDetails?.rankings?.mapIndexed { index, ranking ->
                                    PartyStar(
                                        userId = ranking.userId,
                                        userName = ranking.userName,
                                        level = ranking.userLevel,
                                        rank = ranking.rank,
                                        points = ranking.value
                                    )
                                } ?: emptyList()
                                
                                // Map rewards to party rewards
                                val rewards = eventData.rewards?.map { reward ->
                                    PartyReward(
                                        rankRange = "Top ${reward.requirement.toInt()}",
                                        prize = "${reward.value} ${reward.type}"
                                    )
                                } ?: emptyList()
                                
                                _uiState.value = EventsUiState(
                                    isLoading = false,
                                    eventName = eventData.name,
                                    timeRemaining = timeRemaining,
                                    myPoints = eventProgress?.progress ?: 0,
                                    myRank = eventProgress?.rank ?: 0,
                                    nextTierPoints = getNextTierPoints(eventData.rewards, eventProgress?.progress ?: 0),
                                    topStars = stars,
                                    partyRewards = rewards
                                )
                            }
                            "room_support" -> {
                                // Map rewards to support options
                                val options = eventData.rewards?.map { reward ->
                                    SupportOption(
                                        id = reward.id,
                                        name = reward.name,
                                        description = "${reward.value} support points",
                                        cost = reward.requirement
                                    )
                                } ?: emptyList()
                                
                                // Map rankings to supporters
                                val supporters = eventDetails?.rankings?.mapIndexed { index, ranking ->
                                    Supporter(
                                        userId = ranking.userId,
                                        userName = ranking.userName,
                                        rank = ranking.rank,
                                        amount = ranking.value
                                    )
                                } ?: emptyList()
                                
                                _uiState.value = EventsUiState(
                                    isLoading = false,
                                    eventName = eventData.name,
                                    timeRemaining = if (eventData.isActive) "Active" else timeRemaining,
                                    supportOptions = options,
                                    topSupporters = supporters
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
                        // Event not found, show fallback data
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
    
    private fun calculateTimeRemaining(endAt: Long): String {
        val now = System.currentTimeMillis()
        val diff = endAt - now
        if (diff <= 0) return "Ended"
        
        val days = diff / (24 * 60 * 60 * 1000)
        val hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
        
        return when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h"
            else -> "< 1h"
        }
    }
    
    private fun getNextTierPoints(rewards: List<EventRewardDto>?, currentPoints: Long): Long {
        if (rewards.isNullOrEmpty()) return 0
        val nextTier = rewards.sortedBy { it.requirement }.find { it.requirement > currentPoints }
        return nextTier?.requirement ?: rewards.maxOfOrNull { it.requirement } ?: 0
    }
    
    private fun loadFallbackData(eventType: String) {
        // Fallback with placeholder data when API fails
        when (eventType) {
            "recharge" -> {
                _uiState.value = EventsUiState(
                    isLoading = false,
                    eventName = "Monthly Recharge Rewards",
                    timeRemaining = "Loading...",
                    error = "Event not available"
                )
            }
            "party_star" -> {
                _uiState.value = EventsUiState(
                    isLoading = false,
                    eventName = "Weekly Party Star",
                    timeRemaining = "Loading...",
                    error = "Event not available"
                )
            }
            "room_support" -> {
                _uiState.value = EventsUiState(
                    isLoading = false,
                    eventName = "Support Room",
                    timeRemaining = "Active",
                    error = "Event not available"
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
                    loadEvent("room_support")
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

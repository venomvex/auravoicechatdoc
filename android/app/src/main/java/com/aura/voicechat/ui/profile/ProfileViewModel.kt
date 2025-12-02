package com.aura.voicechat.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.remote.ApiService
import com.aura.voicechat.domain.model.*
import com.aura.voicechat.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Profile ViewModel (Live API Connected - No Mock Data)
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Fetches user profile, medals, earnings, and guide status from the backend API.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "ProfileViewModel"
    }
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                if (userId == "me") {
                    loadCurrentUserProfile()
                    loadEarnings()
                    loadGuideStatus()
                    loadMedals()
                } else {
                    loadOtherUserProfile(userId)
                    loadOtherUserMedals(userId)
                }
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private suspend fun loadCurrentUserProfile() {
        try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val user = User(
                    id = data.id,
                    name = data.name,
                    avatar = data.avatar,
                    level = data.level,
                    exp = data.exp,
                    vipTier = data.vipTier,
                    vipExpiry = data.vipExpiry,
                    coins = data.coins,
                    diamonds = data.diamonds,
                    gender = when (data.gender.lowercase()) {
                        "female" -> Gender.FEMALE
                        "male" -> Gender.MALE
                        else -> Gender.OTHER
                    },
                    country = data.country,
                    bio = data.bio,
                    isOnline = data.isOnline,
                    lastActiveAt = data.lastActiveAt,
                    kycStatus = when (data.kycStatus.lowercase()) {
                        "verified", "approved" -> KycStatus.VERIFIED
                        "pending", "pending_review" -> KycStatus.PENDING_REVIEW
                        "rejected" -> KycStatus.REJECTED
                        else -> KycStatus.NOT_STARTED
                    },
                    cpPartnerId = data.cpPartnerId,
                    familyId = data.familyId,
                    createdAt = data.createdAt
                )
                
                _uiState.value = _uiState.value.copy(
                    user = user,
                    followersCount = data.followersCount ?: 0,
                    followingCount = data.followingCount ?: 0,
                    giftsReceivedCount = data.giftsReceivedCount ?: 0,
                    isFollowing = false // Can't follow yourself
                )
                Log.d(TAG, "Loaded current user profile: ${user.name}")
            } else {
                throw Exception("Failed to load profile: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading current user profile", e)
            throw e
        }
    }
    
    private suspend fun loadOtherUserProfile(userId: String) {
        try {
            val response = apiService.getUser(userId)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val user = User(
                    id = data.id,
                    name = data.name,
                    avatar = data.avatar,
                    level = data.level,
                    exp = data.exp,
                    vipTier = data.vipTier,
                    vipExpiry = data.vipExpiry,
                    coins = 0, // Don't expose other user's coins
                    diamonds = 0, // Don't expose other user's diamonds
                    gender = when (data.gender.lowercase()) {
                        "female" -> Gender.FEMALE
                        "male" -> Gender.MALE
                        else -> Gender.OTHER
                    },
                    country = data.country,
                    bio = data.bio,
                    isOnline = data.isOnline,
                    lastActiveAt = data.lastActiveAt,
                    kycStatus = KycStatus.NOT_STARTED, // Don't expose KYC status
                    cpPartnerId = data.cpPartnerId,
                    familyId = data.familyId,
                    createdAt = data.createdAt
                )
                
                _uiState.value = _uiState.value.copy(
                    user = user,
                    followersCount = data.followersCount ?: 0,
                    followingCount = data.followingCount ?: 0,
                    giftsReceivedCount = data.giftsReceivedCount ?: 0,
                    isFollowing = data.isFollowing ?: false
                )
                Log.d(TAG, "Loaded user profile: ${user.name}")
            } else {
                throw Exception("Failed to load user profile: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading other user profile", e)
            throw e
        }
    }
    
    private suspend fun loadMedals() {
        try {
            val response = apiService.getUserMedals()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val medals = data.medals.map { dto ->
                    Medal(
                        id = dto.id,
                        name = dto.name,
                        category = when (dto.category.lowercase()) {
                            "gift" -> MedalCategory.GIFT
                            "activity" -> MedalCategory.ACTIVITY
                            "achievement" -> MedalCategory.ACHIEVEMENT
                            else -> MedalCategory.SPECIAL
                        },
                        description = dto.description,
                        icon = dto.icon,
                        milestone = 0,
                        milestoneType = "",
                        rewards = MedalReward(coins = 0, cosmetic = null),
                        duration = "permanent",
                        earnedAt = dto.earnedAt,
                        isDisplayed = dto.isDisplayed
                    )
                }
                _uiState.value = _uiState.value.copy(medals = medals)
                Log.d(TAG, "Loaded ${medals.size} medals")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading medals", e)
        }
    }
    
    private suspend fun loadOtherUserMedals(userId: String) {
        try {
            val response = apiService.getOtherUserMedals(userId)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val medals = data.medals.map { dto ->
                    Medal(
                        id = dto.id,
                        name = dto.name,
                        category = when (dto.category.lowercase()) {
                            "gift" -> MedalCategory.GIFT
                            "activity" -> MedalCategory.ACTIVITY
                            "achievement" -> MedalCategory.ACHIEVEMENT
                            else -> MedalCategory.SPECIAL
                        },
                        description = dto.description,
                        icon = dto.icon,
                        milestone = 0,
                        milestoneType = "",
                        rewards = MedalReward(coins = 0, cosmetic = null),
                        duration = "permanent",
                        earnedAt = dto.earnedAt,
                        isDisplayed = dto.isDisplayed
                    )
                }
                _uiState.value = _uiState.value.copy(medals = medals)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading other user medals", e)
        }
    }
    
    private suspend fun loadEarnings() {
        try {
            // Load earning wallet
            val walletResponse = apiService.getEarningWallet()
            if (walletResponse.isSuccessful && walletResponse.body() != null) {
                val wallet = walletResponse.body()!!
                _uiState.value = _uiState.value.copy(
                    earnings = EarningsData(
                        availableBalance = wallet.availableBalance,
                        pendingBalance = wallet.pendingBalance,
                        totalEarned = wallet.totalEarned
                    )
                )
            }
            
            // Load active target
            val targetsResponse = apiService.getActiveTargets()
            if (targetsResponse.isSuccessful && targetsResponse.body() != null) {
                val data = targetsResponse.body()!!
                data.activeTarget?.let { target ->
                    // Calculate days remaining
                    val daysRemaining = if (target.endDate != null) {
                        try {
                            val endInstant = java.time.Instant.parse(target.endDate)
                            val now = java.time.Instant.now()
                            java.time.Duration.between(now, endInstant).toDays().toInt().coerceAtLeast(0)
                        } catch (e: Exception) {
                            7
                        }
                    } else 7
                    
                    _uiState.value = _uiState.value.copy(
                        activeTarget = TargetData(
                            id = target.id,
                            name = target.name,
                            progress = target.progress ?: 0,
                            target = target.targetAmount,
                            daysRemaining = daysRemaining
                        )
                    )
                }
            }
            Log.d(TAG, "Loaded earnings data")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading earnings", e)
        }
    }
    
    private suspend fun loadGuideStatus() {
        try {
            val response = apiService.getGuideStatus()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                _uiState.value = _uiState.value.copy(
                    isGuide = data.isGuide,
                    guideLevel = data.guide?.level
                )
            }
            
            // Check if user has pending application
            val eligibilityResponse = apiService.checkGuideEligibility()
            if (eligibilityResponse.isSuccessful && eligibilityResponse.body() != null) {
                val eligibility = eligibilityResponse.body()!!
                _uiState.value = _uiState.value.copy(
                    canApplyForGuide = eligibility.isEligible && !_uiState.value.isGuide
                )
            }
            Log.d(TAG, "Loaded guide status: isGuide=${_uiState.value.isGuide}")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading guide status", e)
        }
    }
    
    fun toggleFollow() {
        viewModelScope.launch {
            val userId = _uiState.value.user?.id ?: return@launch
            val currentState = _uiState.value.isFollowing
            
            try {
                val response = if (currentState) {
                    apiService.unfollowUser(userId)
                } else {
                    apiService.followUser(userId)
                }
                
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isFollowing = !currentState,
                        followersCount = if (currentState)
                            _uiState.value.followersCount - 1
                        else
                            _uiState.value.followersCount + 1
                    )
                    Log.d(TAG, "Toggled follow: isFollowing=${!currentState}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling follow", e)
            }
        }
    }
    
    fun applyForGuide() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isApplyingForGuide = true)
                val response = apiService.applyForGuide(
                    com.aura.voicechat.data.model.GuideApplicationRequest(documents = null)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = _uiState.value.copy(
                        isGuideApplied = true,
                        canApplyForGuide = false,
                        message = "Guide application submitted successfully!"
                    )
                    Log.d(TAG, "Guide application submitted")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = response.body()?.message ?: "Failed to apply for guide"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error applying for guide", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isApplyingForGuide = false)
            }
        }
    }
    
    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isApplyingForGuide: Boolean = false,
    val user: User? = null,
    val medals: List<Medal> = emptyList(),
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val giftsReceivedCount: Int = 0,
    val isFollowing: Boolean = false,
    val earnings: EarningsData? = null,
    val activeTarget: TargetData? = null,
    val isGuide: Boolean = false,
    val guideLevel: String? = null,
    val isGuideApplied: Boolean = false,
    val canApplyForGuide: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

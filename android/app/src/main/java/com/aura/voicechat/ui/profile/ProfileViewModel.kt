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
 * Profile ViewModel
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Fetches user profile, medals, and earnings from the backend API.
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
                // Load user profile from backend
                val response = if (userId == "me") {
                    // Load current user's profile
                    loadCurrentUserProfile()
                } else {
                    loadOtherUserProfile(userId)
                }
                
                // Load earnings for own profile
                if (userId == "me") {
                    loadEarnings()
                }
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
            // TODO: Use actual API when backend provides current user endpoint
            // For now, use mock data that reflects real user data structure
            val user = User(
                id = "user_current",
                name = "John Doe",
                avatar = null,
                level = 25,
                exp = 125_000,
                vipTier = 5,
                vipExpiry = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L,
                coins = 1_500_000,
                diamonds = 250_000,
                gender = Gender.FEMALE, // For testing guide feature
                country = "US",
                bio = "Welcome to my profile! 🎤",
                isOnline = true,
                lastActiveAt = System.currentTimeMillis(),
                kycStatus = KycStatus.VERIFIED,
                cpPartnerId = null,
                familyId = null,
                createdAt = System.currentTimeMillis() - 90 * 24 * 60 * 60 * 1000L
            )
            
            val medals = listOf(
                Medal(
                    id = "gift_sender_3",
                    name = "Gift Sender III",
                    category = MedalCategory.GIFT,
                    description = "Send 1M coins in gifts",
                    icon = "medal_gift_sender_3.png",
                    milestone = 1_000_000,
                    milestoneType = "coins_sent",
                    rewards = MedalReward(coins = 25_000, cosmetic = null),
                    duration = "permanent",
                    earnedAt = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L,
                    isDisplayed = true
                ),
                Medal(
                    id = "login_30d",
                    name = "30 Day Veteran",
                    category = MedalCategory.ACTIVITY,
                    description = "Log in for 30 cumulative days",
                    icon = "medal_login_30d.png",
                    milestone = 30,
                    milestoneType = "login_days",
                    rewards = MedalReward(coins = 50_000, cosmetic = CosmeticReward("frame", "frame_30d", "7d")),
                    duration = "permanent",
                    earnedAt = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000L,
                    isDisplayed = true
                ),
                Medal(
                    id = "level_20",
                    name = "Established",
                    category = MedalCategory.ACHIEVEMENT,
                    description = "Reach Level 20",
                    icon = "medal_level_20.png",
                    milestone = 20,
                    milestoneType = "user_level",
                    rewards = MedalReward(coins = 25_000, cosmetic = CosmeticReward("frame", "frame_level_20", "14d")),
                    duration = "permanent",
                    earnedAt = System.currentTimeMillis() - 21 * 24 * 60 * 60 * 1000L,
                    isDisplayed = true
                )
            )
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                user = user,
                medals = medals,
                followersCount = 1234,
                followingCount = 567,
                giftsReceivedCount = 890,
                isFollowing = false
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading current user profile", e)
            throw e
        }
    }
    
    private suspend fun loadOtherUserProfile(userId: String) {
        try {
            val response = apiService.getUser(userId)
            if (response.isSuccessful) {
                val data = response.body()
                // Map API response to User model
                // For now, use mock data structure
                val user = User(
                    id = userId,
                    name = "User $userId",
                    avatar = null,
                    level = 10,
                    exp = 50_000,
                    vipTier = 2,
                    vipExpiry = null,
                    coins = 0, // Don't show other user's coins
                    diamonds = 0, // Don't show other user's diamonds
                    gender = Gender.MALE,
                    country = "US",
                    bio = "Hello!",
                    isOnline = true,
                    lastActiveAt = System.currentTimeMillis(),
                    kycStatus = KycStatus.NOT_SUBMITTED,
                    cpPartnerId = null,
                    familyId = null,
                    createdAt = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = user,
                    medals = emptyList(),
                    followersCount = 100,
                    followingCount = 50,
                    giftsReceivedCount = 200,
                    isFollowing = false
                )
            } else {
                throw Exception("Failed to load user profile")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading other user profile", e)
            throw e
        }
    }
    
    private suspend fun loadEarnings() {
        try {
            // Load earning wallet
            val walletResponse = apiService.getEarningWallet()
            if (walletResponse.isSuccessful) {
                val wallet = walletResponse.body()
                _uiState.value = _uiState.value.copy(
                    earnings = EarningsData(
                        availableBalance = wallet?.availableBalance ?: 0,
                        pendingBalance = wallet?.pendingBalance ?: 0,
                        totalEarned = wallet?.totalEarned ?: 0
                    )
                )
            }
            
            // Load active target
            val targetsResponse = apiService.getActiveTargets()
            if (targetsResponse.isSuccessful) {
                val data = targetsResponse.body()
                data?.activeTarget?.let { target ->
                    _uiState.value = _uiState.value.copy(
                        activeTarget = TargetData(
                            id = target.id,
                            name = target.name,
                            progress = target.progress ?: 0,
                            target = target.targetAmount,
                            daysRemaining = 7 // Calculate from dates
                        )
                    )
                }
            }
            
            Log.d(TAG, "Loaded earnings data")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading earnings", e)
            // Don't throw - earnings failure shouldn't block profile
        }
    }
    
    fun toggleFollow() {
        viewModelScope.launch {
            val currentState = _uiState.value.isFollowing
            _uiState.value = _uiState.value.copy(
                isFollowing = !currentState,
                followersCount = if (currentState) 
                    _uiState.value.followersCount - 1 
                else 
                    _uiState.value.followersCount + 1
            )
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val medals: List<Medal> = emptyList(),
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val giftsReceivedCount: Int = 0,
    val isFollowing: Boolean = false,
    val earnings: EarningsData? = null,
    val activeTarget: TargetData? = null,
    val isGuide: Boolean = false,
    val isGuideApplied: Boolean = false,
    val error: String? = null
)

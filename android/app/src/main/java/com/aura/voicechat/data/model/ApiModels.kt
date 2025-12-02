package com.aura.voicechat.data.model

import com.google.gson.annotations.SerializedName

/**
 * API Request/Response Models
 * Developer: Hawkaye Visions LTD — Pakistan
 */

// Authentication
data class SendOtpRequest(
    @SerializedName("phone") val phone: String
)

data class SendOtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("cooldownSeconds") val cooldownSeconds: Int,
    @SerializedName("attemptsRemaining") val attemptsRemaining: Int
)

data class VerifyOtpRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("otp") val otp: String
)

data class VerifyOtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String? = null,
    @SerializedName("user") val user: UserDto
)

// Token refresh
data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class RefreshTokenResponse(
    @SerializedName("token") val token: String
)

// Logout
data class LogoutResponse(
    @SerializedName("success") val success: Boolean
)

// Social Sign-In - Google
data class GoogleSignInRequest(
    @SerializedName("idToken") val idToken: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("displayName") val displayName: String? = null
)

// Social Sign-In - Facebook
data class FacebookSignInRequest(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("displayName") val displayName: String? = null
)

// Social Sign-In Response (used for both Google and Facebook)
data class SocialSignInResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String? = null,
    @SerializedName("user") val user: UserDto,
    @SerializedName("isNewUser") val isNewUser: Boolean = false
)

// Daily Rewards
data class DailyRewardStatusResponse(
    @SerializedName("currentDay") val currentDay: Int,
    @SerializedName("claimable") val claimable: Boolean,
    @SerializedName("canClaim") val canClaim: Boolean,
    @SerializedName("cycle") val cycle: List<DayRewardDto>,
    @SerializedName("streak") val streak: Int,
    @SerializedName("nextResetUtc") val nextResetUtc: String,
    @SerializedName("vipTier") val vipTier: Int,
    @SerializedName("vipMultiplier") val vipMultiplier: Double
)

data class DayRewardDto(
    @SerializedName("day") val day: Int,
    @SerializedName("coins") val coins: Long,
    @SerializedName("bonus") val bonus: Long?,
    @SerializedName("status") val status: String
)

data class ClaimRewardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("day") val day: Int,
    @SerializedName("baseCoins") val baseCoins: Long,
    @SerializedName("vipMultiplier") val vipMultiplier: Double,
    @SerializedName("totalCoins") val totalCoins: Long
)

// VIP
data class VipTierResponse(
    @SerializedName("tier") val tier: String,
    @SerializedName("multiplier") val multiplier: Double,
    @SerializedName("expBoost") val expBoost: Double,
    @SerializedName("expiry") val expiry: String,
    @SerializedName("benefits") val benefits: List<String>
)

data class PurchaseVipRequest(
    @SerializedName("packageId") val packageId: String
)

data class PurchaseVipResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("newTier") val newTier: Int,
    @SerializedName("daysRemaining") val daysRemaining: Int,
    @SerializedName("message") val message: String?
)

data class VipStatusResponse(
    @SerializedName("tier") val tier: Int,
    @SerializedName("daysRemaining") val daysRemaining: Int,
    @SerializedName("totalSpent") val totalSpent: Long,
    @SerializedName("progress") val progress: Float,
    @SerializedName("expiry") val expiry: String?
)

data class VipPackagesResponse(
    @SerializedName("packages") val packages: List<VipPackageDto>
)

data class VipPackageDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("originalPrice") val originalPrice: Double,
    @SerializedName("bonusDiamonds") val bonusDiamonds: Long,
    @SerializedName("days") val days: Int,
    @SerializedName("isBestValue") val isBestValue: Boolean
)

// Medals
data class MedalsResponse(
    @SerializedName("medals") val medals: List<MedalDto>,
    @SerializedName("displaySettings") val displaySettings: DisplaySettingsDto
)

data class MedalDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("earnedAt") val earnedAt: Long?,
    @SerializedName("isDisplayed") val isDisplayed: Boolean
)

data class DisplaySettingsDto(
    @SerializedName("displayedMedals") val displayedMedals: List<String>,
    @SerializedName("hiddenMedals") val hiddenMedals: List<String>,
    @SerializedName("maxDisplayed") val maxDisplayed: Int
)

data class UpdateMedalDisplayRequest(
    @SerializedName("displayedMedals") val displayedMedals: List<String>,
    @SerializedName("hiddenMedals") val hiddenMedals: List<String>
)

// Wallet
data class WalletBalancesResponse(
    @SerializedName("coins") val coins: Long,
    @SerializedName("diamonds") val diamonds: Long,
    @SerializedName("lastUpdated") val lastUpdated: String
)

data class ExchangeRequest(
    @SerializedName("diamonds") val diamonds: Long
)

data class ExchangeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("diamondsUsed") val diamondsUsed: Long,
    @SerializedName("coinsReceived") val coinsReceived: Long,
    @SerializedName("newBalance") val newBalance: NewBalanceDto
)

data class NewBalanceDto(
    @SerializedName("coins") val coins: Long,
    @SerializedName("diamonds") val diamonds: Long
)

// Referrals
data class BindReferralRequest(
    @SerializedName("code") val code: String
)

data class ReferralCoinsSummaryResponse(
    @SerializedName("invitationsCount") val invitationsCount: Int,
    @SerializedName("totalCoinsRewarded") val totalCoinsRewarded: Long,
    @SerializedName("withdrawableCoins") val withdrawableCoins: Long,
    @SerializedName("withdrawMin") val withdrawMin: Long,
    @SerializedName("cooldownSeconds") val cooldownSeconds: Int
)

data class WithdrawCoinsRequest(
    @SerializedName("amount") val amount: Long
)

data class ReferralRecordsResponse(
    @SerializedName("data") val data: List<ReferralRecordDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class ReferralRecordDto(
    @SerializedName("inviteeId") val inviteeId: String,
    @SerializedName("inviteeName") val inviteeName: String,
    @SerializedName("inviteeAvatar") val inviteeAvatar: String?,
    @SerializedName("joinedAt") val joinedAt: Long,
    @SerializedName("coinsRewarded") val coinsRewarded: Long,
    @SerializedName("status") val status: String
)

data class ReferralCashSummaryResponse(
    @SerializedName("balanceUsd") val balanceUsd: Double,
    @SerializedName("minWithdrawalUsd") val minWithdrawalUsd: Double,
    @SerializedName("walletCooldownSeconds") val walletCooldownSeconds: Int,
    @SerializedName("externalAllowedMinUsd") val externalAllowedMinUsd: Double,
    @SerializedName("externalClearanceDays") val externalClearanceDays: Int
)

data class WithdrawCashRequest(
    @SerializedName("destination") val destination: String
)

data class InviteRecordsResponse(
    @SerializedName("data") val data: List<InviteRecordDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class InviteRecordDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("date") val date: String,
    @SerializedName("amount") val amount: Double
)

data class RankingResponse(
    @SerializedName("data") val data: List<RankingEntryDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class RankingEntryDto(
    @SerializedName("rank") val rank: Int,
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("amount") val amount: Double
)

// Rooms
data class RoomsResponse(
    @SerializedName("data") val data: List<RoomCardDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class RoomCardDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("coverImage") val coverImage: String?,
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("ownerAvatar") val ownerAvatar: String?,
    @SerializedName("type") val type: String,
    @SerializedName("userCount") val userCount: Int,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("isLive") val isLive: Boolean,
    @SerializedName("tags") val tags: List<String>
)

data class RoomResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("coverImage") val coverImage: String?,
    @SerializedName("ownerId") val ownerId: String,
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("ownerAvatar") val ownerAvatar: String?,
    @SerializedName("type") val type: String,
    @SerializedName("mode") val mode: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("currentUsers") val currentUsers: Int,
    @SerializedName("isLocked") val isLocked: Boolean,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("seats") val seats: List<SeatDto>,
    @SerializedName("createdAt") val createdAt: Long
)

data class SeatDto(
    @SerializedName("position") val position: Int,
    @SerializedName("userId") val userId: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("userLevel") val userLevel: Int?,
    @SerializedName("userVip") val userVip: Int?,
    @SerializedName("isMuted") val isMuted: Boolean,
    @SerializedName("isLocked") val isLocked: Boolean
)

data class AddToPlaylistRequest(
    @SerializedName("url") val url: String
)

// Banners
data class BannersResponse(
    @SerializedName("banners") val banners: List<BannerDto>
)

data class BannerDto(
    @SerializedName("id") val id: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("targetUrl") val targetUrl: String?,
    @SerializedName("type") val type: String,
    @SerializedName("startAt") val startAt: Long?,
    @SerializedName("endAt") val endAt: Long?,
    @SerializedName("isActive") val isActive: Boolean
)

// Followers
data class FollowersResponse(
    @SerializedName("users") val users: List<FollowerDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class FollowerDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("level") val level: Int,
    @SerializedName("vipTier") val vipTier: Int,
    @SerializedName("isOnline") val isOnline: Boolean,
    @SerializedName("isFollowing") val isFollowing: Boolean,
    @SerializedName("followedAt") val followedAt: Long
)

// User
data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("level") val level: Int,
    @SerializedName("vipTier") val vipTier: Int,
    @SerializedName("isNewUser") val isNewUser: Boolean?
)

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("level") val level: Int,
    @SerializedName("exp") val exp: Long,
    @SerializedName("vipTier") val vipTier: Int,
    @SerializedName("vipExpiry") val vipExpiry: Long?,
    @SerializedName("coins") val coins: Long,
    @SerializedName("diamonds") val diamonds: Long,
    @SerializedName("gender") val gender: String,
    @SerializedName("country") val country: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("isOnline") val isOnline: Boolean,
    @SerializedName("lastActiveAt") val lastActiveAt: Long?,
    @SerializedName("kycStatus") val kycStatus: String,
    @SerializedName("cpPartnerId") val cpPartnerId: String?,
    @SerializedName("familyId") val familyId: String?,
    @SerializedName("followersCount") val followersCount: Int?,
    @SerializedName("followingCount") val followingCount: Int?,
    @SerializedName("giftsReceivedCount") val giftsReceivedCount: Int?,
    @SerializedName("isFollowing") val isFollowing: Boolean?,
    @SerializedName("createdAt") val createdAt: Long
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("avatar") val avatar: String?
)

// KYC
data class KycStatusResponse(
    @SerializedName("userId") val userId: String,
    @SerializedName("status") val status: String,
    @SerializedName("idCardFront") val idCardFront: String?,
    @SerializedName("idCardBack") val idCardBack: String?,
    @SerializedName("selfie") val selfie: String?,
    @SerializedName("livenessScore") val livenessScore: Float?,
    @SerializedName("submittedAt") val submittedAt: Long?,
    @SerializedName("reviewedAt") val reviewedAt: Long?,
    @SerializedName("rejectionReason") val rejectionReason: String?
)

data class SubmitKycRequest(
    @SerializedName("idCardFrontUri") val idCardFrontUri: String,
    @SerializedName("idCardBackUri") val idCardBackUri: String,
    @SerializedName("selfieUri") val selfieUri: String,
    @SerializedName("livenessCheckPassed") val livenessCheckPassed: Boolean
)

// Common
data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("totalPages") val totalPages: Int
)

// ============================================
// Games - New Game Types
// ============================================

// Game info response
data class GameInfoDto(
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("minBet") val minBet: Long?,
    @SerializedName("maxBet") val maxBet: Long?,
    @SerializedName("betOptions") val betOptions: List<Long>?,
    @SerializedName("spinCost") val spinCost: Long?,
    @SerializedName("advancedSpinCost") val advancedSpinCost: Long?
)

data class GamesListResponse(
    @SerializedName("games") val games: List<GameInfoDto>
)

// Game session
data class StartGameRequest(
    @SerializedName("betAmount") val betAmount: Long,
    @SerializedName("roomId") val roomId: String? = null
)

data class GameSessionResponse(
    @SerializedName("session") val session: GameSessionDto
)

data class GameSessionDto(
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("gameType") val gameType: String,
    @SerializedName("betAmount") val betAmount: Long,
    @SerializedName("data") val data: Any?
)

// Game action request/response
data class GameActionRequest(
    @SerializedName("sessionId") val sessionId: String? = null,
    @SerializedName("action") val action: String,
    @SerializedName("bet") val bet: Long? = null,
    @SerializedName("multiplier") val multiplier: Int? = null,
    @SerializedName("bets") val bets: Map<String, Long>? = null,
    @SerializedName("useFreeSpins") val useFreeSpins: Boolean? = null,
    @SerializedName("data") val data: GameActionDataDto? = null
)

data class GameActionDataDto(
    @SerializedName("selectedItem") val selectedItem: String? = null,
    @SerializedName("selectedFruit") val selectedFruit: String? = null,
    @SerializedName("drawType") val drawType: String? = null,
    @SerializedName("drawCount") val drawCount: Int? = null
)

data class GameResultResponse(
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("symbols") val symbols: List<String>? = null,
    @SerializedName("winAmount") val winAmount: Long? = null,
    @SerializedName("winLines") val winLines: List<Long>? = null,
    @SerializedName("result") val result: String? = null,
    @SerializedName("prizeIndex") val prizeIndex: Long? = null,
    @SerializedName("isJackpot") val isJackpot: Boolean? = null,
    @SerializedName("newBalance") val newBalance: Long? = null,
    @SerializedName("expEarned") val expEarned: Int? = null,
    @SerializedName("resultData") val resultData: GameResultDto? = null
)

// Lucky 777 Pro result
data class Lucky777ProResultDto(
    @SerializedName("reels") val reels: List<List<String>>,
    @SerializedName("middleRow") val middleRow: List<String>,
    @SerializedName("winningSymbol") val winningSymbol: String?,
    @SerializedName("matchCount") val matchCount: Int,
    @SerializedName("isJackpot") val isJackpot: Boolean,
    @SerializedName("winAmount") val winAmount: Long,
    @SerializedName("jackpot") val jackpot: Long,
    @SerializedName("expEarned") val expEarned: Int
)

// Lucky 77 Pro result
data class Lucky77ProResultDto(
    @SerializedName("reels") val reels: List<String>,
    @SerializedName("multiplier") val multiplier: Int,
    @SerializedName("isJackpot") val isJackpot: Boolean,
    @SerializedName("winAmount") val winAmount: Long,
    @SerializedName("jackpot") val jackpot: Long,
    @SerializedName("expEarned") val expEarned: Int
)

// Greedy Baby result
data class GreedyBabyResultDto(
    @SerializedName("selectedItem") val selectedItem: String,
    @SerializedName("winningItem") val winningItem: String,
    @SerializedName("winningItemName") val winningItemName: String,
    @SerializedName("won") val won: Boolean,
    @SerializedName("multiplier") val multiplier: Int,
    @SerializedName("winAmount") val winAmount: Long,
    @SerializedName("todaysWin") val todaysWin: Long,
    @SerializedName("expEarned") val expEarned: Int
)

// Lucky Fruit result
data class LuckyFruitResultDto(
    @SerializedName("selectedFruit") val selectedFruit: String,
    @SerializedName("winningFruit") val winningFruit: String,
    @SerializedName("winningFruitName") val winningFruitName: String,
    @SerializedName("won") val won: Boolean,
    @SerializedName("specialBonus") val specialBonus: String?,
    @SerializedName("multiplier") val multiplier: Int,
    @SerializedName("winAmount") val winAmount: Long,
    @SerializedName("resultHistory") val resultHistory: List<FruitResultHistoryDto>,
    @SerializedName("expEarned") val expEarned: Int
)

data class FruitResultHistoryDto(
    @SerializedName("fruit") val fruit: String,
    @SerializedName("timestamp") val timestamp: String
)

// Gift Wheel result
data class GiftWheelResultDto(
    @SerializedName("drawType") val drawType: String,
    @SerializedName("drawCount") val drawCount: Int,
    @SerializedName("wonItems") val wonItems: List<GiftWheelItemDto>,
    @SerializedName("totalValue") val totalValue: Long,
    @SerializedName("expEarned") val expEarned: Int
)

data class GiftWheelItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("value") val value: Long
)

// Gift Wheel draw records
data class GiftWheelRecordsResponse(
    @SerializedName("records") val records: List<GiftWheelRecordDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class GiftWheelRecordDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("drawType") val drawType: String? = null,
    @SerializedName("drawCount") val drawCount: Int? = null,
    @SerializedName("items") val items: List<GiftWheelItemDto>? = null,
    @SerializedName("totalValue") val totalValue: Long? = null,
    @SerializedName("timestamp") val timestamp: Long? = null,
    @SerializedName("userName") val userName: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("winAmount") val winAmount: Long? = null,
    @SerializedName("result") val result: String? = null
)

// Generic game result wrapper
data class GameResultDto(
    // Lucky 777 Pro fields
    @SerializedName("reels") val reels: Any? = null,
    @SerializedName("middleRow") val middleRow: List<String>? = null,
    @SerializedName("winningSymbol") val winningSymbol: String? = null,
    @SerializedName("matchCount") val matchCount: Int? = null,
    
    // Common fields
    @SerializedName("isJackpot") val isJackpot: Boolean? = null,
    @SerializedName("multiplier") val multiplier: Int? = null,
    @SerializedName("winAmount") val winAmount: Long? = null,
    @SerializedName("jackpot") val jackpot: Long? = null,
    @SerializedName("expEarned") val expEarned: Int? = null,
    
    // Greedy Baby / Lucky Fruit fields
    @SerializedName("selectedItem") val selectedItem: String? = null,
    @SerializedName("winningItem") val winningItem: String? = null,
    @SerializedName("winningItemName") val winningItemName: String? = null,
    @SerializedName("selectedFruit") val selectedFruit: String? = null,
    @SerializedName("winningFruit") val winningFruit: String? = null,
    @SerializedName("winningFruitName") val winningFruitName: String? = null,
    @SerializedName("won") val won: Boolean? = null,
    @SerializedName("specialBonus") val specialBonus: String? = null,
    @SerializedName("todaysWin") val todaysWin: Long? = null,
    @SerializedName("resultHistory") val resultHistory: List<FruitResultHistoryDto>? = null,
    
    // Gift Wheel fields
    @SerializedName("drawType") val drawType: String? = null,
    @SerializedName("drawCount") val drawCount: Int? = null,
    @SerializedName("wonItems") val wonItems: List<GiftWheelItemDto>? = null,
    @SerializedName("totalValue") val totalValue: Long? = null
)

// Jackpots
data class JackpotsResponse(
    @SerializedName("jackpots") val jackpots: List<JackpotDto>
)

data class JackpotDto(
    @SerializedName("game") val game: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("lastWinner") val lastWinner: String?,
    @SerializedName("lastWinDate") val lastWinDate: String?
)

// Game stats
data class GameStatsResponse(
    @SerializedName("stats") val stats: GameStatsDto? = null,
    @SerializedName("giftWheelFreeSpins") val giftWheelFreeSpins: Int = 0,
    @SerializedName("dailySpinsRemaining") val dailySpinsRemaining: Int = 0
)

data class GameStatsDto(
    @SerializedName("totalPlayed") val totalPlayed: Int,
    @SerializedName("totalWon") val totalWon: Long,
    @SerializedName("totalLost") val totalLost: Long,
    @SerializedName("biggestWin") val biggestWin: Long,
    @SerializedName("favoriteGame") val favoriteGame: String?
)

// ============================================
// Gifts - Gift Catalog and Sending
// ============================================

data class GiftCatalogResponse(
    @SerializedName("gifts") val gifts: List<GiftDto>,
    @SerializedName("categories") val categories: List<GiftCategoryDto>
)

data class GiftDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("price") val price: Long,
    @SerializedName("diamondValue") val diamondValue: Long,
    @SerializedName("rarity") val rarity: String,
    @SerializedName("iconUrl") val iconUrl: String?,
    @SerializedName("animationFile") val animationFile: String?,
    @SerializedName("soundFile") val soundFile: String?,
    @SerializedName("isAnimated") val isAnimated: Boolean,
    @SerializedName("isFullScreen") val isFullScreen: Boolean,
    @SerializedName("isCustom") val isCustom: Boolean,
    @SerializedName("isLegendary") val isLegendary: Boolean,
    @SerializedName("duration") val duration: Int,
    @SerializedName("enabled") val enabled: Boolean
)

data class GiftCategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String?,
    @SerializedName("order") val order: Int
)

data class GiftSendRequestDto(
    @SerializedName("giftId") val giftId: String,
    @SerializedName("recipients") val recipients: List<String>,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("roomId") val roomId: String?
)

data class BaggageSendRequestDto(
    @SerializedName("baggageItemId") val baggageItemId: String,
    @SerializedName("recipient") val recipient: String,
    @SerializedName("roomId") val roomId: String?
)

data class GiftSendResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("giftId") val giftId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("totalCoinsSpent") val totalCoinsSpent: Long,
    @SerializedName("animationUrl") val animationUrl: String?,
    @SerializedName("newBalance") val newBalance: NewBalanceDto?
)

data class GiftHistoryResponse(
    @SerializedName("transactions") val transactions: List<GiftTransactionDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class GiftTransactionDto(
    @SerializedName("id") val id: String,
    @SerializedName("giftId") val giftId: String,
    @SerializedName("giftName") val giftName: String,
    @SerializedName("giftIcon") val giftIcon: String?,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("senderName") val senderName: String,
    @SerializedName("recipientId") val recipientId: String,
    @SerializedName("recipientName") val recipientName: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("coinsSpent") val coinsSpent: Long,
    @SerializedName("diamondsReceived") val diamondsReceived: Long,
    @SerializedName("roomId") val roomId: String?,
    @SerializedName("timestamp") val timestamp: Long
)

// ============================================
// Inventory - User's owned items
// ============================================

data class InventoryResponse(
    @SerializedName("items") val items: List<InventoryItemDto>,
    @SerializedName("totalItems") val totalItems: Int
)

data class InventoryItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("itemId") val itemId: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("rarity") val rarity: String,
    @SerializedName("iconUrl") val iconUrl: String?,
    @SerializedName("animationUrl") val animationUrl: String?,
    @SerializedName("acquiredAt") val acquiredAt: Long,
    @SerializedName("expiresAt") val expiresAt: Long?,
    @SerializedName("isEquipped") val isEquipped: Boolean,
    @SerializedName("source") val source: String
)

data class EquippedItemsResponse(
    @SerializedName("frame") val frame: InventoryItemDto?,
    @SerializedName("vehicle") val vehicle: InventoryItemDto?,
    @SerializedName("theme") val theme: InventoryItemDto?,
    @SerializedName("micSkin") val micSkin: InventoryItemDto?,
    @SerializedName("seatEffect") val seatEffect: InventoryItemDto?,
    @SerializedName("chatBubble") val chatBubble: InventoryItemDto?,
    @SerializedName("entranceStyle") val entranceStyle: InventoryItemDto?,
    @SerializedName("roomCard") val roomCard: InventoryItemDto?,
    @SerializedName("cover") val cover: InventoryItemDto?
)

data class EquipItemRequest(
    @SerializedName("itemId") val itemId: String
)

data class UnequipItemRequest(
    @SerializedName("category") val category: String
)

data class BaggageResponse(
    @SerializedName("items") val items: List<BaggageItemDto>,
    @SerializedName("totalValue") val totalValue: Long
)

data class BaggageItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("giftId") val giftId: String,
    @SerializedName("giftName") val giftName: String,
    @SerializedName("giftIconUrl") val giftIconUrl: String?,
    @SerializedName("diamondValue") val diamondValue: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("source") val source: String,
    @SerializedName("acquiredAt") val acquiredAt: Long,
    @SerializedName("expiresAt") val expiresAt: Long?
)

// ============================================
// Store - Items for purchase
// ============================================

data class StoreCatalogResponse(
    @SerializedName("items") val items: List<StoreItemDto>,
    @SerializedName("categories") val categories: List<StoreCategoryDto>?
)

data class StoreItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("rarity") val rarity: String,
    @SerializedName("price") val price: Long,
    @SerializedName("iconUrl") val iconUrl: String?,
    @SerializedName("animationUrl") val animationUrl: String?,
    @SerializedName("previewUrl") val previewUrl: String?,
    @SerializedName("duration") val duration: String,
    @SerializedName("vipRequired") val vipRequired: Int,
    @SerializedName("enabled") val enabled: Boolean,
    @SerializedName("isFeatured") val isFeatured: Boolean,
    @SerializedName("isNew") val isNew: Boolean,
    @SerializedName("discount") val discount: Int,
    @SerializedName("isOwned") val isOwned: Boolean?
)

data class StoreCategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String?,
    @SerializedName("order") val order: Int
)

data class PurchaseItemRequest(
    @SerializedName("itemId") val itemId: String,
    @SerializedName("quantity") val quantity: Int
)

data class PurchaseItemResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("itemId") val itemId: String,
    @SerializedName("coinsSpent") val coinsSpent: Long,
    @SerializedName("newBalance") val newBalance: Long,
    @SerializedName("inventoryItemId") val inventoryItemId: String?
)

// ============================================
// Messages & Notifications
// ============================================

// Conversations
data class ConversationsResponse(
    @SerializedName("conversations") val conversations: List<ConversationDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class ConversationDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("lastMessage") val lastMessage: String,
    @SerializedName("lastMessageAt") val lastMessageAt: String,
    @SerializedName("unreadCount") val unreadCount: Int,
    @SerializedName("isOnline") val isOnline: Boolean,
    @SerializedName("userId") val userId: String?
)

// Messages
data class MessagesListResponse(
    @SerializedName("messages") val messages: List<MessageDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class MessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("senderName") val senderName: String,
    @SerializedName("senderAvatar") val senderAvatar: String?,
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("isRead") val isRead: Boolean
)

data class SendMessageRequest(
    @SerializedName("recipientId") val recipientId: String,
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String = "text"
)

data class MessageResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: MessageDto?
)

// Notifications
data class NotificationsResponse(
    @SerializedName("notifications") val notifications: List<NotificationDto>,
    @SerializedName("unreadCount") val unreadCount: Int,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class NotificationDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Map<String, String>?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("isRead") val isRead: Boolean
)

// System Messages
data class SystemMessagesResponse(
    @SerializedName("messages") val messages: List<SystemMessageDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class SystemMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("priority") val priority: String?
)

// ============================================
// Earnings System Models
// ============================================

data class EarningTargetsResponse(
    @SerializedName("targets") val targets: List<EarningTargetDto>,
    @SerializedName("activeTarget") val activeTarget: EarningTargetDto?
)

data class EarningTargetDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("type") val type: String, // "receiver" - user receiving diamonds
    @SerializedName("targetAmount") val targetAmount: Long, // Diamonds to receive
    @SerializedName("rewardAmount") val rewardAmount: Long, // Coins/cash reward
    @SerializedName("rewardType") val rewardType: String, // "cash" or "coins"
    @SerializedName("duration") val duration: String, // "weekly", "monthly"
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("progress") val progress: Long?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("endDate") val endDate: String?
)

data class TargetProgressResponse(
    @SerializedName("targetId") val targetId: String,
    @SerializedName("progress") val progress: Long,
    @SerializedName("target") val target: Long,
    @SerializedName("percentComplete") val percentComplete: Float,
    @SerializedName("daysRemaining") val daysRemaining: Int,
    @SerializedName("estimatedReward") val estimatedReward: Long
)

data class EarningWalletResponse(
    @SerializedName("availableBalance") val availableBalance: Long,
    @SerializedName("pendingBalance") val pendingBalance: Long,
    @SerializedName("totalEarned") val totalEarned: Long,
    @SerializedName("totalWithdrawn") val totalWithdrawn: Long,
    @SerializedName("currency") val currency: String
)

data class EarningHistoryResponse(
    @SerializedName("earnings") val earnings: List<EarningRecordDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class EarningRecordDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("description") val description: String,
    @SerializedName("fromUserId") val fromUserId: String?,
    @SerializedName("fromUserName") val fromUserName: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("status") val status: String
)

data class PendingEarningsResponse(
    @SerializedName("pendingEarnings") val pendingEarnings: List<PendingEarningDto>,
    @SerializedName("totalPending") val totalPending: Long
)

data class PendingEarningDto(
    @SerializedName("id") val id: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("source") val source: String,
    @SerializedName("expectedDate") val expectedDate: String
)

data class WithdrawEarningsRequest(
    @SerializedName("amount") val amount: Long,
    @SerializedName("methodId") val methodId: String
)

data class WithdrawEarningsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("withdrawalId") val withdrawalId: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("estimatedArrival") val estimatedArrival: String
)

data class WithdrawalMethodsResponse(
    @SerializedName("methods") val methods: List<WithdrawalMethodDto>
)

data class WithdrawalMethodDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("minAmount") val minAmount: Long,
    @SerializedName("maxAmount") val maxAmount: Long,
    @SerializedName("fee") val fee: Float,
    @SerializedName("isAvailable") val isAvailable: Boolean
)

data class PaymentMethodsResponse(
    @SerializedName("methods") val methods: List<PaymentMethodDto>
)

data class PaymentMethodDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("isDefault") val isDefault: Boolean,
    @SerializedName("isVerified") val isVerified: Boolean
)

data class AddPaymentMethodRequest(
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("isDefault") val isDefault: Boolean
)

data class PaymentMethodResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("method") val method: PaymentMethodDto?
)

// ============================================
// Guide System Models
// ============================================

data class GuideStatusResponse(
    @SerializedName("isGuide") val isGuide: Boolean,
    @SerializedName("guide") val guide: GuideDto?
)

data class GuideDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("status") val status: String,
    @SerializedName("level") val level: String,
    @SerializedName("joined_at") val joinedAt: String,
    @SerializedName("months_active") val monthsActive: Int,
    @SerializedName("sheets_completed") val sheetsCompleted: Int,
    @SerializedName("total_earned") val totalEarned: Double,
    @SerializedName("current_frame") val currentFrame: String
)

data class GuideEligibilityResponse(
    @SerializedName("isEligible") val isEligible: Boolean,
    @SerializedName("eligibility") val eligibility: EligibilityDetailsDto,
    @SerializedName("message") val message: String
)

data class EligibilityDetailsDto(
    @SerializedName("level") val level: EligibilityItemDto,
    @SerializedName("accountAge") val accountAge: EligibilityItemDto,
    @SerializedName("roomOwnership") val roomOwnership: EligibilityItemDto,
    @SerializedName("kycVerified") val kycVerified: EligibilityItemDto
)

data class EligibilityItemDto(
    @SerializedName("required") val required: Any,
    @SerializedName("current") val current: Any,
    @SerializedName("met") val met: Boolean
)

data class GuideApplicationRequest(
    @SerializedName("documents") val documents: Map<String, String>?
)

data class GuideApplicationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("application") val application: Any?,
    @SerializedName("message") val message: String
)

data class GuideDashboardResponse(
    @SerializedName("guide") val guide: GuideDto,
    @SerializedName("currentMonth") val currentMonth: String,
    @SerializedName("monthlyTargets") val monthlyTargets: GuideMonthlyTargetsDto?,
    @SerializedName("monthlyStats") val monthlyStats: GuideMonthlyStatsDto,
    @SerializedName("earnings") val earnings: GuideEarningsDto,
    @SerializedName("daysRemaining") val daysRemaining: Int
)

data class GuideMonthlyTargetsDto(
    @SerializedName("year_month") val yearMonth: String,
    @SerializedName("targets") val targets: List<GuideTargetDto>,
    @SerializedName("earnings") val earnings: Double,
    @SerializedName("status") val status: String
)

data class GuideTargetDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("requirement") val requirement: String,
    @SerializedName("progress") val progress: Long,
    @SerializedName("required") val required: Long,
    @SerializedName("reward") val reward: Double,
    @SerializedName("status") val status: String
)

data class GuideMonthlyStatsDto(
    @SerializedName("days_completed") val daysCompleted: Int?,
    @SerializedName("total_room_hours") val totalRoomHours: Double?,
    @SerializedName("total_mic_hours") val totalMicHours: Double?,
    @SerializedName("total_coins_received") val totalCoinsReceived: Long?,
    @SerializedName("total_messages") val totalMessages: Int?,
    @SerializedName("total_visitors") val totalVisitors: Int?,
    @SerializedName("total_games") val totalGames: Int?
)

data class GuideEarningsDto(
    @SerializedName("pending_usd") val pendingUsd: Double,
    @SerializedName("available_usd") val availableUsd: Double,
    @SerializedName("total_earned") val totalEarned: Double,
    @SerializedName("total_withdrawn") val totalWithdrawn: Double?,
    @SerializedName("total_converted") val totalConverted: Double?
)

data class GuideTargetsResponse(
    @SerializedName("targets") val targets: GuideMonthlyTargetsDto
)

data class GuideDailyResponse(
    @SerializedName("daily") val daily: GuideDailyDto?,
    @SerializedName("message") val message: String?
)

data class GuideDailyDto(
    @SerializedName("date") val date: String,
    @SerializedName("jar_completed") val jarCompleted: Boolean,
    @SerializedName("jar_points") val jarPoints: Int,
    @SerializedName("room_hours") val roomHours: Double,
    @SerializedName("mic_hours") val micHours: Double,
    @SerializedName("coins_received") val coinsReceived: Long,
    @SerializedName("messages_received") val messagesReceived: Int,
    @SerializedName("unique_visitors") val uniqueVisitors: Int,
    @SerializedName("games_hosted") val gamesHosted: Int
)

data class GuideEarningsResponse(
    @SerializedName("earnings") val earnings: GuideEarningsDto
)

data class GuideWithdrawalRequest(
    @SerializedName("amount") val amount: Double,
    @SerializedName("method") val method: String,
    @SerializedName("account_details") val accountDetails: Map<String, String>
)

data class GuideWithdrawalResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("withdrawal") val withdrawal: Any?,
    @SerializedName("message") val message: String
)

data class GuideConvertRequest(
    @SerializedName("usdAmount") val usdAmount: Double
)

data class GuideConvertResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("usdConverted") val usdConverted: Double,
    @SerializedName("coinsReceived") val coinsReceived: Long,
    @SerializedName("bonusCoins") val bonusCoins: Long,
    @SerializedName("message") val message: String
)

data class GuideEarningsHistoryResponse(
    @SerializedName("history") val history: List<GuideMonthlyTargetsDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class GuideLeaderboardResponse(
    @SerializedName("leaderboard") val leaderboard: List<GuideLeaderboardEntryDto>
)

data class GuideLeaderboardEntryDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("level") val level: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("earnings") val earnings: Double?,
    @SerializedName("targets_completed") val targetsCompleted: Int?,
    @SerializedName("weekly_coins") val weeklyCoins: Long?,
    @SerializedName("weekly_hours") val weeklyHours: Double?,
    @SerializedName("days_completed") val daysCompleted: Int?
)

// ============================================
// Gifts Models
// ============================================

data class GiftsResponse(
    @SerializedName("gifts") val gifts: List<GiftDto>
)

data class GiftDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("price_coins") val priceCoins: Long,
    @SerializedName("diamond_value") val diamondValue: Long,
    @SerializedName("category") val category: String,
    @SerializedName("animation_url") val animationUrl: String?,
    @SerializedName("thumbnail_url") val thumbnailUrl: String?,
    @SerializedName("is_premium") val isPremium: Boolean,
    @SerializedName("is_active") val isActive: Boolean
)

data class SendGiftRequest(
    @SerializedName("giftId") val giftId: String,
    @SerializedName("receiverId") val receiverId: String,
    @SerializedName("roomId") val roomId: String?,
    @SerializedName("quantity") val quantity: Int
)

data class SendGiftResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("transaction") val transaction: GiftTransactionDto?,
    @SerializedName("message") val message: String?
)

data class GiftTransactionDto(
    @SerializedName("id") val id: String,
    @SerializedName("gift_id") val giftId: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("receiver_id") val receiverId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("coins_spent") val coinsSpent: Long,
    @SerializedName("diamonds_earned") val diamondsEarned: Long,
    @SerializedName("created_at") val createdAt: String
)

// ============================================
// Room System Models (Live API)
// ============================================

// Room details response (extended)
data class RoomDetailsResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("coverImage") val coverImage: String?,
    @SerializedName("ownerId") val ownerId: String,
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("ownerAvatar") val ownerAvatar: String?,
    @SerializedName("type") val type: String,
    @SerializedName("mode") val mode: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("currentUsers") val currentUsers: Int,
    @SerializedName("isLocked") val isLocked: Boolean,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("seats") val seats: List<SeatDto>,
    @SerializedName("settings") val settings: RoomSettingsDto?,
    @SerializedName("jar") val jar: RoomJarDto?,
    @SerializedName("rankings") val rankings: RoomRankingsDto?,
    @SerializedName("videoPlayer") val videoPlayer: VideoPlayerDto?,
    @SerializedName("activities") val activities: RoomActivitiesDto?,
    @SerializedName("createdAt") val createdAt: Long
)

// Room settings
data class RoomSettingsDto(
    @SerializedName("welcomeMessage") val welcomeMessage: String?,
    @SerializedName("announcement") val announcement: String?,
    @SerializedName("backgroundUrl") val backgroundUrl: String?,
    @SerializedName("theme") val theme: String?,
    @SerializedName("autoMuteOnJoin") val autoMuteOnJoin: Boolean,
    @SerializedName("allowGifts") val allowGifts: Boolean,
    @SerializedName("allowGames") val allowGames: Boolean,
    @SerializedName("minLevelToSpeak") val minLevelToSpeak: Int,
    @SerializedName("isPrivate") val isPrivate: Boolean,
    @SerializedName("password") val password: String?
)

data class UpdateRoomSettingsRequest(
    @SerializedName("welcomeMessage") val welcomeMessage: String? = null,
    @SerializedName("announcement") val announcement: String? = null,
    @SerializedName("backgroundUrl") val backgroundUrl: String? = null,
    @SerializedName("theme") val theme: String? = null,
    @SerializedName("autoMuteOnJoin") val autoMuteOnJoin: Boolean? = null,
    @SerializedName("allowGifts") val allowGifts: Boolean? = null,
    @SerializedName("allowGames") val allowGames: Boolean? = null,
    @SerializedName("minLevelToSpeak") val minLevelToSpeak: Int? = null,
    @SerializedName("isPrivate") val isPrivate: Boolean? = null,
    @SerializedName("password") val password: String? = null
)

// Room Jar
data class RoomJarDto(
    @SerializedName("id") val id: String,
    @SerializedName("currentAmount") val currentAmount: Long,
    @SerializedName("targetAmount") val targetAmount: Long,
    @SerializedName("isReady") val isReady: Boolean,
    @SerializedName("slots") val slots: List<JarSlotDto>,
    @SerializedName("topContributors") val topContributors: List<JarContributorDto>
)

data class JarSlotDto(
    @SerializedName("id") val id: String,
    @SerializedName("isUnlocked") val isUnlocked: Boolean,
    @SerializedName("currentAmount") val currentAmount: Long,
    @SerializedName("targetAmount") val targetAmount: Long,
    @SerializedName("unlockCost") val unlockCost: Long,
    @SerializedName("isReady") val isReady: Boolean
)

data class JarContributorDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("amount") val amount: Long
)

data class ContributeToJarRequest(
    @SerializedName("amount") val amount: Long
)

data class ClaimJarRequest(
    @SerializedName("slotIndex") val slotIndex: Int
)

data class JarClaimResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("reward") val reward: Long,
    @SerializedName("rewardType") val rewardType: String,
    @SerializedName("message") val message: String
)

// Room Rankings (Daily/Weekly/Monthly Trophy)
data class RoomRankingsDto(
    @SerializedName("daily") val daily: List<RoomRankingEntryDto>,
    @SerializedName("weekly") val weekly: List<RoomRankingEntryDto>,
    @SerializedName("monthly") val monthly: List<RoomRankingEntryDto>
)

data class RoomRankingEntryDto(
    @SerializedName("rank") val rank: Int,
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("giftsValue") val giftsValue: Long,
    @SerializedName("level") val level: Int?,
    @SerializedName("vipTier") val vipTier: Int?
)

// Video Player (YouTube)
data class VideoPlayerDto(
    @SerializedName("isPlaying") val isPlaying: Boolean,
    @SerializedName("currentVideo") val currentVideo: VideoDto?,
    @SerializedName("playlist") val playlist: List<VideoDto>,
    @SerializedName("isCinemaMode") val isCinemaMode: Boolean
)

data class VideoDto(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("addedBy") val addedBy: String?,
    @SerializedName("addedAt") val addedAt: Long?
)

data class PlayVideoRequest(
    @SerializedName("url") val url: String
)

// Room Activities (notifications toggle)
data class RoomActivitiesDto(
    @SerializedName("gameNotifications") val gameNotifications: Boolean,
    @SerializedName("cpNotifications") val cpNotifications: Boolean,
    @SerializedName("friendshipNotifications") val friendshipNotifications: Boolean,
    @SerializedName("rocketNotifications") val rocketNotifications: Boolean,
    @SerializedName("luckyBagNotifications") val luckyBagNotifications: Boolean
)

data class UpdateActivitiesRequest(
    @SerializedName("gameNotifications") val gameNotifications: Boolean? = null,
    @SerializedName("cpNotifications") val cpNotifications: Boolean? = null,
    @SerializedName("friendshipNotifications") val friendshipNotifications: Boolean? = null,
    @SerializedName("rocketNotifications") val rocketNotifications: Boolean? = null,
    @SerializedName("luckyBagNotifications") val luckyBagNotifications: Boolean? = null
)

// Room Messages (Live Chat)
data class RoomMessagesResponse(
    @SerializedName("messages") val messages: List<RoomMessageDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class RoomMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String, // "user", "system", "gift", "join", "leave"
    @SerializedName("userId") val userId: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("userLevel") val userLevel: Int?,
    @SerializedName("userVip") val userVip: Int?,
    @SerializedName("role") val role: String?, // "owner", "admin", "moderator", "member"
    @SerializedName("content") val content: String,
    @SerializedName("giftName") val giftName: String?,
    @SerializedName("giftCount") val giftCount: Int?,
    @SerializedName("receiverName") val receiverName: String?,
    @SerializedName("timestamp") val timestamp: Long
)

data class SendRoomMessageRequest(
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String = "text"
)

data class SendRoomImageRequest(
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("type") val type: String = "image"
)

// Seat Management
data class SeatActionRequest(
    @SerializedName("seatPosition") val seatPosition: Int,
    @SerializedName("action") val action: String, // "lock", "unlock", "kick", "invite", "mute", "unmute", "drag"
    @SerializedName("targetUserId") val targetUserId: String? = null,
    @SerializedName("targetSeatPosition") val targetSeatPosition: Int? = null
)

data class SeatActionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class JoinSeatRequest(
    @SerializedName("seatPosition") val seatPosition: Int
)

data class LeaveSeatRequest(
    @SerializedName("seatPosition") val seatPosition: Int
)

// Room Admin Actions
data class KickUserRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("reason") val reason: String? = null
)

data class BanUserRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("duration") val duration: Int? = null // Minutes, null = permanent
)

data class MuteUserRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("duration") val duration: Int? = null // Seconds, null = until unmuted
)

data class ClearChatRequest(
    @SerializedName("beforeTimestamp") val beforeTimestamp: Long? = null
)

// Room Events Slider
data class RoomEventsResponse(
    @SerializedName("events") val events: List<RoomEventDto>
)

data class RoomEventDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String, // "recharge", "offline_recharge", "cp_ranking", "custom_gift", "vip_spin", "room_support"
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("bannerUrl") val bannerUrl: String?,
    @SerializedName("startAt") val startAt: Long,
    @SerializedName("endAt") val endAt: Long,
    @SerializedName("isActive") val isActive: Boolean
)

// Room Games Slider
data class RoomGamesResponse(
    @SerializedName("games") val games: List<RoomGameDto>
)

data class RoomGameDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String, // "lucky_77_pro", "lucky_777", "lucky_fruit", "greedy_baby", "lucky_wheel", "rocket"
    @SerializedName("name") val name: String,
    @SerializedName("iconUrl") val iconUrl: String?,
    @SerializedName("isAvailable") val isAvailable: Boolean,
    @SerializedName("minBet") val minBet: Long?,
    @SerializedName("maxBet") val maxBet: Long?
)

// Lucky Bag
data class LuckyBagResponse(
    @SerializedName("bags") val bags: List<LuckyBagDto>
)

data class LuckyBagDto(
    @SerializedName("id") val id: String,
    @SerializedName("senderName") val senderName: String,
    @SerializedName("senderAvatar") val senderAvatar: String?,
    @SerializedName("totalAmount") val totalAmount: Long,
    @SerializedName("remainingAmount") val remainingAmount: Long,
    @SerializedName("totalSlots") val totalSlots: Int,
    @SerializedName("remainingSlots") val remainingSlots: Int,
    @SerializedName("expiresAt") val expiresAt: Long
)

data class GrabLuckyBagRequest(
    @SerializedName("bagId") val bagId: String
)

data class GrabLuckyBagResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("amount") val amount: Long?,
    @SerializedName("message") val message: String
)

data class SendLuckyBagRequest(
    @SerializedName("totalAmount") val totalAmount: Long,
    @SerializedName("slots") val slots: Int,
    @SerializedName("message") val message: String?
)

// Room Create
data class CreateRoomRequest(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("coverImage") val coverImage: String? = null,
    @SerializedName("tags") val tags: List<String>? = null
)

data class CreateRoomResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("room") val room: RoomDetailsResponse?,
    @SerializedName("message") val message: String?
)

// ============================================
// Family System Models
// ============================================

data class MyFamilyResponse(
    @SerializedName("hasFamily") val hasFamily: Boolean,
    @SerializedName("family") val family: FamilyDto?
)

data class FamilyDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("badge") val badge: String?,
    @SerializedName("level") val level: Int,
    @SerializedName("memberCount") val memberCount: Int,
    @SerializedName("maxMembers") val maxMembers: Int,
    @SerializedName("weeklyGifts") val weeklyGifts: Long,
    @SerializedName("totalGifts") val totalGifts: Long,
    @SerializedName("weeklyRanking") val weeklyRanking: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("notice") val notice: String?,
    @SerializedName("isOwner") val isOwner: Boolean,
    @SerializedName("isAdmin") val isAdmin: Boolean,
    @SerializedName("members") val members: List<FamilyMemberDto>?,
    @SerializedName("perks") val perks: List<FamilyPerkDto>?,
    @SerializedName("isOpen") val isOpen: Boolean
)

data class FamilyMemberDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("role") val role: String,
    @SerializedName("contribution") val contribution: Long,
    @SerializedName("joinedAt") val joinedAt: String?
)

data class FamilyPerkDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("requiredLevel") val requiredLevel: Int
)

data class FamilyDetailsResponse(
    @SerializedName("family") val family: FamilyDto
)

data class CreateFamilyRequest(
    @SerializedName("name") val name: String,
    @SerializedName("badge") val badge: String?
)

data class CreateFamilyResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("family") val family: FamilyDto?,
    @SerializedName("message") val message: String?
)

data class FamilySearchResponse(
    @SerializedName("families") val families: List<FamilyDto>
)

data class FamilyMembersResponse(
    @SerializedName("members") val members: List<FamilyMemberDto>
)

data class FamilyActivityResponse(
    @SerializedName("activities") val activities: List<FamilyActivityDto>
)

data class FamilyActivityDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("message") val message: String,
    @SerializedName("timeAgo") val timeAgo: String,
    @SerializedName("timestamp") val timestamp: Long?
)

data class UpdateFamilyRoleRequest(
    @SerializedName("role") val role: String
)

data class FamilyRankingsResponse(
    @SerializedName("rankings") val rankings: List<FamilyRankingDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class FamilyRankingDto(
    @SerializedName("rank") val rank: Int,
    @SerializedName("familyId") val familyId: String,
    @SerializedName("familyName") val familyName: String,
    @SerializedName("badge") val badge: String?,
    @SerializedName("level") val level: Int,
    @SerializedName("weeklyGifts") val weeklyGifts: Long,
    @SerializedName("memberCount") val memberCount: Int
)

// ============================================
// CP Partnership Models
// ============================================

data class CpStatusResponse(
    @SerializedName("hasPartner") val hasPartner: Boolean,
    @SerializedName("partner") val partner: CpPartnerDto?,
    @SerializedName("level") val level: Int,
    @SerializedName("points") val points: Long,
    @SerializedName("anniversaryDate") val anniversaryDate: String?,
    @SerializedName("pendingRequests") val pendingRequests: List<CpRequestDto>?
)

data class CpPartnerDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("level") val level: Int,
    @SerializedName("vipTier") val vipTier: Int,
    @SerializedName("isOnline") val isOnline: Boolean
)

data class CpRequestDto(
    @SerializedName("id") val id: String,
    @SerializedName("fromUserId") val fromUserId: String,
    @SerializedName("fromUserName") val fromUserName: String,
    @SerializedName("fromUserAvatar") val fromUserAvatar: String?,
    @SerializedName("toUserId") val toUserId: String,
    @SerializedName("message") val message: String?,
    @SerializedName("createdAt") val createdAt: String
)

data class CpRequestResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class CpRespondRequest(
    @SerializedName("accept") val accept: Boolean
)

data class CpRankingsResponse(
    @SerializedName("rankings") val rankings: List<CpRankingDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class CpRankingDto(
    @SerializedName("rank") val rank: Int,
    @SerializedName("user1") val user1: CpPartnerDto,
    @SerializedName("user2") val user2: CpPartnerDto,
    @SerializedName("level") val level: Int,
    @SerializedName("points") val points: Long
)

data class CpProgressResponse(
    @SerializedName("currentLevel") val currentLevel: Int,
    @SerializedName("currentPoints") val currentPoints: Long,
    @SerializedName("nextLevelPoints") val nextLevelPoints: Long,
    @SerializedName("perks") val perks: List<CpPerkDto>
)

data class CpPerkDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("requiredLevel") val requiredLevel: Int,
    @SerializedName("isUnlocked") val isUnlocked: Boolean
)

// ============================================
// Ranking System Models
// ============================================

data class GiftRankingsResponse(
    @SerializedName("rankings") val rankings: List<UserRankingDto>,
    @SerializedName("myRank") val myRank: Int?,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class UserRankingDto(
    @SerializedName("rank") val rank: Int,
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("userLevel") val userLevel: Int,
    @SerializedName("userVip") val userVip: Int,
    @SerializedName("value") val value: Long
)

data class LevelRankingsResponse(
    @SerializedName("rankings") val rankings: List<UserRankingDto>,
    @SerializedName("myRank") val myRank: Int?,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class WealthRankingsResponse(
    @SerializedName("rankings") val rankings: List<UserRankingDto>,
    @SerializedName("myRank") val myRank: Int?,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class CharmRankingsResponse(
    @SerializedName("rankings") val rankings: List<UserRankingDto>,
    @SerializedName("myRank") val myRank: Int?,
    @SerializedName("pagination") val pagination: PaginationDto
)

// ============================================
// Friends System Models
// ============================================

data class FriendsResponse(
    @SerializedName("friends") val friends: List<FriendDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class FriendDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("level") val level: Int,
    @SerializedName("vipTier") val vipTier: Int,
    @SerializedName("isOnline") val isOnline: Boolean,
    @SerializedName("intimacy") val intimacy: Int,
    @SerializedName("friendSince") val friendSince: String
)

data class FriendRequestsResponse(
    @SerializedName("requests") val requests: List<FriendRequestItem>
)

data class FriendRequestItem(
    @SerializedName("id") val id: String,
    @SerializedName("fromUserId") val fromUserId: String,
    @SerializedName("fromUserName") val fromUserName: String,
    @SerializedName("fromUserAvatar") val fromUserAvatar: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("createdAt") val createdAt: String
)

data class FriendRequestDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("message") val message: String?
)

// ============================================
// Events System Models
// ============================================

data class EventsListResponse(
    @SerializedName("events") val events: List<EventDto>
)

data class EventDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("type") val type: String,
    @SerializedName("bannerUrl") val bannerUrl: String?,
    @SerializedName("startAt") val startAt: Long,
    @SerializedName("endAt") val endAt: Long,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("rewards") val rewards: List<EventRewardDto>?
)

data class EventRewardDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("value") val value: Long,
    @SerializedName("requirement") val requirement: Long
)

data class EventDetailsResponse(
    @SerializedName("event") val event: EventDto,
    @SerializedName("rankings") val rankings: List<UserRankingDto>?,
    @SerializedName("myProgress") val myProgress: Long?
)

data class EventProgressResponse(
    @SerializedName("eventId") val eventId: String,
    @SerializedName("progress") val progress: Long,
    @SerializedName("rank") val rank: Int?,
    @SerializedName("rewards") val rewards: List<EventRewardProgressDto>
)

data class EventRewardProgressDto(
    @SerializedName("rewardId") val rewardId: String,
    @SerializedName("name") val name: String,
    @SerializedName("requirement") val requirement: Long,
    @SerializedName("isClaimed") val isClaimed: Boolean
)

// ============================================
// Search Models
// ============================================

data class SearchUsersResponse(
    @SerializedName("users") val users: List<SearchUserDto>
)

data class SearchUserDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("level") val level: Int,
    @SerializedName("vipTier") val vipTier: Int,
    @SerializedName("isOnline") val isOnline: Boolean
)

// ============================================
// Admin Panel Models
// ============================================

data class AdminDashboardResponse(
    @SerializedName("totalUsers") val totalUsers: Long,
    @SerializedName("activeUsers") val activeUsers: Long,
    @SerializedName("totalRooms") val totalRooms: Long,
    @SerializedName("activeRooms") val activeRooms: Long,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("todayRevenue") val todayRevenue: Double,
    @SerializedName("pendingReports") val pendingReports: Int,
    @SerializedName("pendingKyc") val pendingKyc: Int,
    @SerializedName("pendingGuides") val pendingGuides: Int
)

data class AdminUsersResponse(
    @SerializedName("users") val users: List<AdminUserDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class AdminUserDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("level") val level: Int,
    @SerializedName("vipTier") val vipTier: Int,
    @SerializedName("coins") val coins: Long,
    @SerializedName("diamonds") val diamonds: Long,
    @SerializedName("isBanned") val isBanned: Boolean,
    @SerializedName("createdAt") val createdAt: String
)

data class AdminBanRequest(
    @SerializedName("reason") val reason: String,
    @SerializedName("duration") val duration: Int? // Days, null = permanent
)

data class AdminReportsResponse(
    @SerializedName("reports") val reports: List<AdminReportDto>
)

data class AdminReportDto(
    @SerializedName("id") val id: String,
    @SerializedName("reporterId") val reporterId: String,
    @SerializedName("reporterName") val reporterName: String,
    @SerializedName("targetId") val targetId: String,
    @SerializedName("targetName") val targetName: String,
    @SerializedName("targetType") val targetType: String, // "user", "room", "message"
    @SerializedName("reason") val reason: String,
    @SerializedName("description") val description: String?,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String
)

data class ResolveReportRequest(
    @SerializedName("action") val action: String, // "dismiss", "warn", "ban"
    @SerializedName("notes") val notes: String?
)

data class KycApplicationsResponse(
    @SerializedName("applications") val applications: List<KycApplicationDto>
)

data class KycApplicationDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("status") val status: String,
    @SerializedName("idCardFront") val idCardFront: String?,
    @SerializedName("idCardBack") val idCardBack: String?,
    @SerializedName("selfie") val selfie: String?,
    @SerializedName("submittedAt") val submittedAt: String
)

data class RejectKycRequest(
    @SerializedName("reason") val reason: String
)

data class GuideApplicationsResponse(
    @SerializedName("applications") val applications: List<GuideApplicationDto>
)

data class GuideApplicationDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("userLevel") val userLevel: Int,
    @SerializedName("status") val status: String,
    @SerializedName("appliedAt") val appliedAt: String
)

// ============================================
// Content Moderation Models
// ============================================

data class CheckContentRequest(
    @SerializedName("content") val content: String,
    @SerializedName("context") val context: String = "chat" // chat, bio, room_name, room_announcement
)

data class CheckImageRequest(
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("context") val context: String = "chat" // profile, room_cover, chat
)

data class ModerationResult(
    @SerializedName("success") val success: Boolean,
    @SerializedName("isViolation") val isViolation: Boolean,
    @SerializedName("action") val action: String?, // warn, ban_5min, ban_10min, ban_permanent
    @SerializedName("message") val message: String?,
    @SerializedName("banExpiry") val banExpiry: String?
)

data class BanStatusResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("isBanned") val isBanned: Boolean,
    @SerializedName("banType") val banType: String?,
    @SerializedName("banReason") val banReason: String?,
    @SerializedName("banExpiry") val banExpiry: String?
)

data class ViolationsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("violations") val violations: List<ViolationDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class ViolationDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("user_avatar") val userAvatar: String?,
    @SerializedName("violation_type") val violationType: String,
    @SerializedName("severity") val severity: String,
    @SerializedName("content") val content: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("action_taken") val actionTaken: String,
    @SerializedName("ban_expiry") val banExpiry: String?,
    @SerializedName("created_at") val createdAt: String
)

data class ImageReviewsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("reviews") val reviews: List<ImageReviewDto>,
    @SerializedName("total") val total: Int
)

data class ImageReviewDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("user_avatar") val userAvatar: String?,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("context") val context: String,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String
)

data class ReviewImageRequest(
    @SerializedName("approved") val approved: Boolean
)

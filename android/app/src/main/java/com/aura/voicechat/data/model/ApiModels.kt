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
    @SerializedName("cycle") val cycle: List<DayRewardDto>,
    @SerializedName("streak") val streak: Int,
    @SerializedName("nextResetUtc") val nextResetUtc: String,
    @SerializedName("vipTier") val vipTier: String,
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
    @SerializedName("tier") val tier: String,
    @SerializedName("duration") val duration: String
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
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("action") val action: String,
    @SerializedName("data") val data: GameActionDataDto?
)

data class GameActionDataDto(
    @SerializedName("selectedItem") val selectedItem: String? = null,
    @SerializedName("selectedFruit") val selectedFruit: String? = null,
    @SerializedName("drawType") val drawType: String? = null,
    @SerializedName("drawCount") val drawCount: Int? = null
)

data class GameResultResponse(
    @SerializedName("result") val result: GameResultDto
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
    @SerializedName("id") val id: String,
    @SerializedName("drawType") val drawType: String,
    @SerializedName("drawCount") val drawCount: Int,
    @SerializedName("items") val items: List<GiftWheelItemDto>,
    @SerializedName("totalValue") val totalValue: Long,
    @SerializedName("timestamp") val timestamp: String
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
    @SerializedName("stats") val stats: GameStatsDto
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

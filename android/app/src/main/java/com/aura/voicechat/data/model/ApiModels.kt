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
    @SerializedName("user") val user: UserDto
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

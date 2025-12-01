package com.aura.voicechat.data.remote

import com.aura.voicechat.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service interface for Retrofit
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * All endpoints use the /api/v1/ prefix to match the backend routes.
 */
interface ApiService {
    
    // Authentication
    @POST("api/v1/auth/otp/send")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>
    
    @POST("api/v1/auth/otp/verify")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>
    
    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>
    
    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<LogoutResponse>
    
    @POST("api/v1/auth/google")
    suspend fun signInWithGoogle(@Body request: GoogleSignInRequest): Response<SocialSignInResponse>
    
    @POST("api/v1/auth/facebook")
    suspend fun signInWithFacebook(@Body request: FacebookSignInRequest): Response<SocialSignInResponse>
    
    // Daily Rewards
    @GET("api/v1/rewards/daily/status")
    suspend fun getDailyRewardStatus(): Response<DailyRewardStatusResponse>
    
    @POST("api/v1/rewards/daily/claim")
    suspend fun claimDailyReward(): Response<ClaimRewardResponse>
    
    // VIP
    @GET("api/v1/vip/tier")
    suspend fun getVipTier(): Response<VipTierResponse>
    
    @POST("api/v1/vip/purchase")
    suspend fun purchaseVip(@Body request: PurchaseVipRequest): Response<Unit>
    
    // Medals
    @GET("api/v1/profile/medals")
    suspend fun getUserMedals(): Response<MedalsResponse>
    
    @POST("api/v1/profile/medals/display")
    suspend fun updateMedalDisplay(@Body request: UpdateMedalDisplayRequest): Response<Unit>
    
    @GET("api/v1/users/{userId}/medals")
    suspend fun getOtherUserMedals(@Path("userId") userId: String): Response<MedalsResponse>
    
    // Wallet
    @GET("api/v1/wallet/balances")
    suspend fun getWalletBalances(): Response<WalletBalancesResponse>
    
    @POST("api/v1/wallet/exchange")
    suspend fun exchangeDiamondsToCoins(@Body request: ExchangeRequest): Response<ExchangeResponse>
    
    // Referrals - Get Coins
    @POST("api/v1/referrals/bind")
    suspend fun bindReferralCode(@Body request: BindReferralRequest): Response<Unit>
    
    @GET("api/v1/referrals/coins/summary")
    suspend fun getReferralCoinsSummary(): Response<ReferralCoinsSummaryResponse>
    
    @POST("api/v1/referrals/coins/withdraw")
    suspend fun withdrawReferralCoins(@Body request: WithdrawCoinsRequest): Response<Unit>
    
    @GET("api/v1/referrals/records")
    suspend fun getReferralRecords(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ReferralRecordsResponse>
    
    // Referrals - Get Cash
    @GET("api/v1/referrals/cash/summary")
    suspend fun getReferralCashSummary(): Response<ReferralCashSummaryResponse>
    
    @POST("api/v1/referrals/cash/withdraw")
    suspend fun withdrawReferralCash(@Body request: WithdrawCashRequest): Response<Unit>
    
    @GET("api/v1/referrals/cash/invite-record")
    suspend fun getCashInviteRecords(
        @Query("weekStart") weekStart: String,
        @Query("page") page: Int
    ): Response<InviteRecordsResponse>
    
    @GET("api/v1/referrals/cash/ranking")
    suspend fun getCashRanking(@Query("page") page: Int): Response<RankingResponse>
    
    // Rooms
    @GET("api/v1/rooms/popular")
    suspend fun getPopularRooms(): Response<RoomsResponse>
    
    @GET("api/v1/rooms/mine")
    suspend fun getMyRooms(): Response<RoomsResponse>
    
    @GET("api/v1/rooms/{roomId}")
    suspend fun getRoom(@Path("roomId") roomId: String): Response<RoomResponse>
    
    @POST("api/v1/rooms/{roomId}/video/playlist")
    suspend fun addToPlaylist(
        @Path("roomId") roomId: String,
        @Body request: AddToPlaylistRequest
    ): Response<Unit>
    
    @POST("api/v1/rooms/{roomId}/video/exit")
    suspend fun exitVideo(@Path("roomId") roomId: String): Response<Unit>
    
    // User Profile
    @GET("api/v1/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<UserResponse>
    
    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<Unit>
    
    // KYC
    @GET("api/v1/kyc/status")
    suspend fun getKycStatus(): Response<KycStatusResponse>
    
    @POST("api/v1/kyc/submit")
    suspend fun submitKyc(@Body request: SubmitKycRequest): Response<Unit>
    
    // ============================================
    // Games - New Game Types
    // ============================================
    
    // Get available games
    @GET("api/v1/games")
    suspend fun getAvailableGames(): Response<GamesListResponse>
    
    // Get game stats
    @GET("api/v1/games/stats")
    suspend fun getGameStats(): Response<GameStatsResponse>
    
    // Get jackpots
    @GET("api/v1/games/jackpots")
    suspend fun getJackpots(): Response<JackpotsResponse>
    
    @GET("api/v1/games/jackpots/{gameType}")
    suspend fun getJackpot(@Path("gameType") gameType: String): Response<JackpotDto>
    
    // Start game session
    @POST("api/v1/games/{gameType}/start")
    suspend fun startGame(
        @Path("gameType") gameType: String,
        @Body request: StartGameRequest
    ): Response<GameSessionResponse>
    
    // Perform game action
    @POST("api/v1/games/{gameType}/action")
    suspend fun gameAction(
        @Path("gameType") gameType: String,
        @Body request: GameActionRequest
    ): Response<GameResultResponse>
    
    // Get game history
    @GET("api/v1/games/{gameType}/history")
    suspend fun getGameHistory(
        @Path("gameType") gameType: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<GiftWheelRecordsResponse>
    
    // Get Gift Wheel draw records
    @GET("api/v1/games/gift-wheel/draw-records")
    suspend fun getGiftWheelRecords(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<GiftWheelRecordsResponse>
    
    // ============================================
    // Gifts - Gift Catalog and Sending
    // ============================================
    
    // Get gift catalog
    @GET("api/v1/gifts/catalog")
    suspend fun getGiftCatalog(
        @Query("region") region: String? = null,
        @Query("category") category: String? = null
    ): Response<GiftCatalogResponse>
    
    // Send gift
    @POST("api/v1/gifts/send")
    suspend fun sendGift(@Body request: GiftSendRequestDto): Response<GiftSendResponseDto>
    
    // Send baggage gift (free)
    @POST("api/v1/gifts/send/baggage")
    suspend fun sendBaggageGift(@Body request: BaggageSendRequestDto): Response<GiftSendResponseDto>
    
    // Get gift transaction history
    @GET("api/v1/gifts/history")
    suspend fun getGiftHistory(
        @Query("type") type: String, // "sent" or "received"
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<GiftHistoryResponse>
    
    // ============================================
    // Inventory - User's owned items
    // ============================================
    
    // Get user inventory
    @GET("api/v1/profile/inventory")
    suspend fun getInventory(
        @Query("category") category: String? = null
    ): Response<InventoryResponse>
    
    // Get equipped items
    @GET("api/v1/profile/inventory/equipped")
    suspend fun getEquippedItems(): Response<EquippedItemsResponse>
    
    // Equip item
    @POST("api/v1/profile/inventory/equip")
    suspend fun equipItem(@Body request: EquipItemRequest): Response<Unit>
    
    // Unequip item
    @POST("api/v1/profile/inventory/unequip")
    suspend fun unequipItem(@Body request: UnequipItemRequest): Response<Unit>
    
    // Get baggage (free gifts to send)
    @GET("api/v1/profile/baggage")
    suspend fun getBaggage(): Response<BaggageResponse>
    
    // ============================================
    // Store - Items for purchase
    // ============================================
    
    // Get store catalog
    @GET("api/v1/store/catalog")
    suspend fun getStoreCatalog(
        @Query("category") category: String? = null
    ): Response<StoreCatalogResponse>
    
    // Get featured items
    @GET("api/v1/store/featured")
    suspend fun getFeaturedItems(): Response<StoreCatalogResponse>
    
    // Purchase item
    @POST("api/v1/store/purchase")
    suspend fun purchaseItem(@Body request: PurchaseItemRequest): Response<PurchaseItemResponse>
    
    // Get item details
    @GET("api/v1/store/items/{itemId}")
    suspend fun getStoreItem(@Path("itemId") itemId: String): Response<StoreItemDto>
    
    // ============================================
    // Messages & Notifications
    // ============================================
    
    // Get conversations
    @GET("api/v1/messages/conversations")
    suspend fun getConversations(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ConversationsResponse>
    
    // Get messages in a conversation
    @GET("api/v1/messages/conversations/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<MessagesListResponse>
    
    // Send a message
    @POST("api/v1/messages/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<MessageResponse>
    
    // Get notifications
    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("type") type: String? = null
    ): Response<NotificationsResponse>
    
    // Mark notification as read
    @POST("api/v1/notifications/{notificationId}/read")
    suspend fun markNotificationAsRead(
        @Path("notificationId") notificationId: String
    ): Response<Unit>
    
    // Mark all notifications as read
    @POST("api/v1/notifications/read-all")
    suspend fun markAllNotificationsAsRead(): Response<Unit>
    
    // Get system messages
    @GET("api/v1/notifications/system")
    suspend fun getSystemMessages(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<SystemMessagesResponse>
    
    // ============================================
    // Earnings System
    // ============================================
    
    // Get earning targets (receiver-based targets)
    @GET("api/v1/earnings/targets")
    suspend fun getEarningTargets(): Response<EarningTargetsResponse>
    
    // Get active targets
    @GET("api/v1/earnings/targets/active")
    suspend fun getActiveTargets(): Response<EarningTargetsResponse>
    
    // Get target progress
    @GET("api/v1/earnings/targets/{targetId}/progress")
    suspend fun getTargetProgress(
        @Path("targetId") targetId: String
    ): Response<TargetProgressResponse>
    
    // Activate a target
    @POST("api/v1/earnings/targets/{targetId}/activate")
    suspend fun activateTarget(
        @Path("targetId") targetId: String
    ): Response<Unit>
    
    // Get earning wallet
    @GET("api/v1/earnings/wallet")
    suspend fun getEarningWallet(): Response<EarningWalletResponse>
    
    // Get earning history
    @GET("api/v1/earnings/history")
    suspend fun getEarningHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<EarningHistoryResponse>
    
    // Get pending earnings
    @GET("api/v1/earnings/pending")
    suspend fun getPendingEarnings(): Response<PendingEarningsResponse>
    
    // Request withdrawal
    @POST("api/v1/earnings/withdraw")
    suspend fun requestEarningWithdrawal(@Body request: WithdrawEarningsRequest): Response<WithdrawEarningsResponse>
    
    // Get withdrawal methods
    @GET("api/v1/earnings/withdraw/methods")
    suspend fun getEarningWithdrawalMethods(): Response<WithdrawalMethodsResponse>
    
    // Get payment methods
    @GET("api/v1/earnings/payment-methods")
    suspend fun getPaymentMethods(): Response<PaymentMethodsResponse>
    
    // Add payment method
    @POST("api/v1/earnings/payment-methods")
    suspend fun addPaymentMethod(@Body request: AddPaymentMethodRequest): Response<PaymentMethodResponse>
}

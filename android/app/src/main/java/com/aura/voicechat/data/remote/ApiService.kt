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
    @GET("api/v1/vip/status")
    suspend fun getVipStatus(): Response<VipStatusResponse>
    
    @GET("api/v1/vip/tier")
    suspend fun getVipTier(): Response<VipTierResponse>
    
    @GET("api/v1/vip/packages")
    suspend fun getVipPackages(): Response<VipPackagesResponse>
    
    @POST("api/v1/vip/purchase")
    suspend fun purchaseVip(@Body request: PurchaseVipRequest): Response<PurchaseVipResponse>
    
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
    
    @GET("api/v1/rooms/recent")
    suspend fun getRecentRooms(): Response<RoomsResponse>
    
    @GET("api/v1/rooms/following")
    suspend fun getFollowingRooms(): Response<RoomsResponse>
    
    @GET("api/v1/rooms/video-music")
    suspend fun getVideoMusicRooms(): Response<RoomsResponse>
    
    @GET("api/v1/rooms/{roomId}")
    suspend fun getRoom(@Path("roomId") roomId: String): Response<RoomResponse>
    
    @POST("api/v1/rooms/{roomId}/video/playlist")
    suspend fun addToPlaylist(
        @Path("roomId") roomId: String,
        @Body request: AddToPlaylistRequest
    ): Response<Unit>
    
    @POST("api/v1/rooms/{roomId}/video/exit")
    suspend fun exitVideo(@Path("roomId") roomId: String): Response<Unit>
    
    // Banners
    @GET("api/v1/banners")
    suspend fun getBanners(): Response<BannersResponse>
    
    // User Profile
    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<UserResponse>
    
    @GET("api/v1/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<UserResponse>
    
    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<Unit>
    
    @GET("api/v1/users/{userId}/followers")
    suspend fun getUserFollowers(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<FollowersResponse>
    
    @GET("api/v1/users/{userId}/following")
    suspend fun getUserFollowing(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<FollowersResponse>
    
    @POST("api/v1/users/{userId}/follow")
    suspend fun followUser(@Path("userId") userId: String): Response<Unit>
    
    @POST("api/v1/users/{userId}/unfollow")
    suspend fun unfollowUser(@Path("userId") userId: String): Response<Unit>
    
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
    
    // ============================================
    // Guide System
    // ============================================
    
    // Check guide status
    @GET("api/v1/guide/status")
    suspend fun getGuideStatus(): Response<GuideStatusResponse>
    
    // Check eligibility
    @GET("api/v1/guide/eligibility")
    suspend fun checkGuideEligibility(): Response<GuideEligibilityResponse>
    
    // Apply to become a guide
    @POST("api/v1/guide/apply")
    suspend fun applyForGuide(@Body request: GuideApplicationRequest): Response<GuideApplicationResponse>
    
    // Get guide dashboard
    @GET("api/v1/guide/dashboard")
    suspend fun getGuideDashboard(): Response<GuideDashboardResponse>
    
    // Get guide targets for a month
    @GET("api/v1/guide/targets/{yearMonth}")
    suspend fun getGuideTargets(@Path("yearMonth") yearMonth: String): Response<GuideTargetsResponse>
    
    // Get daily tracking
    @GET("api/v1/guide/daily/{date}")
    suspend fun getGuideDailyTracking(@Path("date") date: String): Response<GuideDailyResponse>
    
    // Get guide earnings
    @GET("api/v1/guide/earnings")
    suspend fun getGuideEarnings(): Response<GuideEarningsResponse>
    
    // Request guide withdrawal
    @POST("api/v1/guide/earnings/withdraw")
    suspend fun requestGuideWithdrawal(@Body request: GuideWithdrawalRequest): Response<GuideWithdrawalResponse>
    
    // Convert USD to coins
    @POST("api/v1/guide/earnings/convert")
    suspend fun convertGuideEarningsToCoins(@Body request: GuideConvertRequest): Response<GuideConvertResponse>
    
    // Get guide earnings history
    @GET("api/v1/guide/earnings/history")
    suspend fun getGuideEarningsHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<GuideEarningsHistoryResponse>
    
    // Get monthly leaderboard
    @GET("api/v1/guide/leaderboard/monthly")
    suspend fun getGuideMonthlyLeaderboard(): Response<GuideLeaderboardResponse>
    
    // Get weekly leaderboard
    @GET("api/v1/guide/leaderboard/weekly")
    suspend fun getGuideWeeklyLeaderboard(): Response<GuideLeaderboardResponse>
    
    // ============================================
    // Gifts (from backend)
    // ============================================
    
    // Get all gifts
    @GET("api/v1/gifts")
    suspend fun getGifts(
        @Query("category") category: String? = null
    ): Response<GiftsResponse>
    
    // Send a gift to single recipient (used by RoomViewModel)
    @POST("api/v1/gifts/send")
    suspend fun sendGiftSimple(@Body request: SendGiftRequest): Response<SendGiftResponse>
    
    // ============================================
    // Room Operations (Live API)
    // ============================================
    
    // Get room details with all data (settings, jar, rankings, video, activities)
    @GET("api/v1/rooms/{roomId}/details")
    suspend fun getRoomDetails(@Path("roomId") roomId: String): Response<RoomDetailsResponse>
    
    // Get room settings
    @GET("api/v1/rooms/{roomId}/settings")
    suspend fun getRoomSettings(@Path("roomId") roomId: String): Response<RoomSettingsDto>
    
    // Update room settings (owner only)
    @PUT("api/v1/rooms/{roomId}/settings")
    suspend fun updateRoomSettings(
        @Path("roomId") roomId: String,
        @Body request: UpdateRoomSettingsRequest
    ): Response<RoomSettingsDto>
    
    // Get room jar
    @GET("api/v1/rooms/{roomId}/jar")
    suspend fun getRoomJar(@Path("roomId") roomId: String): Response<RoomJarDto>
    
    // Contribute to room jar
    @POST("api/v1/rooms/{roomId}/jar/contribute")
    suspend fun contributeToJar(
        @Path("roomId") roomId: String,
        @Body request: ContributeToJarRequest
    ): Response<RoomJarDto>
    
    // Claim room jar reward
    @POST("api/v1/rooms/{roomId}/jar/claim")
    suspend fun claimJarReward(
        @Path("roomId") roomId: String,
        @Body request: ClaimJarRequest
    ): Response<JarClaimResponse>
    
    // Get room rankings (daily/weekly/monthly)
    @GET("api/v1/rooms/{roomId}/rankings")
    suspend fun getRoomRankings(
        @Path("roomId") roomId: String,
        @Query("type") type: String = "daily"
    ): Response<RoomRankingsDto>
    
    // Get room messages (live chat)
    @GET("api/v1/rooms/{roomId}/messages")
    suspend fun getRoomMessages(
        @Path("roomId") roomId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<RoomMessagesResponse>
    
    // Send room message
    @POST("api/v1/rooms/{roomId}/messages")
    suspend fun sendRoomMessage(
        @Path("roomId") roomId: String,
        @Body request: SendRoomMessageRequest
    ): Response<RoomMessageDto>
    
    // Send room image
    @POST("api/v1/rooms/{roomId}/messages/image")
    suspend fun sendRoomImage(
        @Path("roomId") roomId: String,
        @Body request: SendRoomImageRequest
    ): Response<RoomMessageDto>
    
    // Clear chat (owner/admin only)
    @POST("api/v1/rooms/{roomId}/messages/clear")
    suspend fun clearRoomChat(
        @Path("roomId") roomId: String,
        @Body request: ClearChatRequest
    ): Response<Unit>
    
    // Join a seat
    @POST("api/v1/rooms/{roomId}/seats/join")
    suspend fun joinSeat(
        @Path("roomId") roomId: String,
        @Body request: JoinSeatRequest
    ): Response<SeatActionResponse>
    
    // Leave seat
    @POST("api/v1/rooms/{roomId}/seats/leave")
    suspend fun leaveSeat(
        @Path("roomId") roomId: String,
        @Body request: LeaveSeatRequest
    ): Response<SeatActionResponse>
    
    // Seat action (lock/unlock/kick/invite/mute/unmute/drag)
    @POST("api/v1/rooms/{roomId}/seats/action")
    suspend fun seatAction(
        @Path("roomId") roomId: String,
        @Body request: SeatActionRequest
    ): Response<SeatActionResponse>
    
    // Toggle mute self
    @POST("api/v1/rooms/{roomId}/mute")
    suspend fun toggleMuteSelf(@Path("roomId") roomId: String): Response<SeatActionResponse>
    
    // Kick user from room
    @POST("api/v1/rooms/{roomId}/kick")
    suspend fun kickUser(
        @Path("roomId") roomId: String,
        @Body request: KickUserRequest
    ): Response<SeatActionResponse>
    
    // Ban user from room
    @POST("api/v1/rooms/{roomId}/ban")
    suspend fun banUser(
        @Path("roomId") roomId: String,
        @Body request: BanUserRequest
    ): Response<SeatActionResponse>
    
    // Mute user
    @POST("api/v1/rooms/{roomId}/mute-user")
    suspend fun muteUser(
        @Path("roomId") roomId: String,
        @Body request: MuteUserRequest
    ): Response<SeatActionResponse>
    
    // Get video player state
    @GET("api/v1/rooms/{roomId}/video")
    suspend fun getVideoPlayer(@Path("roomId") roomId: String): Response<VideoPlayerDto>
    
    // Play video (YouTube URL)
    @POST("api/v1/rooms/{roomId}/video/play")
    suspend fun playVideo(
        @Path("roomId") roomId: String,
        @Body request: PlayVideoRequest
    ): Response<VideoPlayerDto>
    
    // Stop video
    @POST("api/v1/rooms/{roomId}/video/stop")
    suspend fun stopVideo(@Path("roomId") roomId: String): Response<Unit>
    
    // Toggle cinema mode
    @POST("api/v1/rooms/{roomId}/video/cinema")
    suspend fun toggleCinemaMode(@Path("roomId") roomId: String): Response<VideoPlayerDto>
    
    // Get room activities settings
    @GET("api/v1/rooms/{roomId}/activities")
    suspend fun getRoomActivities(@Path("roomId") roomId: String): Response<RoomActivitiesDto>
    
    // Update room activities settings
    @PUT("api/v1/rooms/{roomId}/activities")
    suspend fun updateRoomActivities(
        @Path("roomId") roomId: String,
        @Body request: UpdateActivitiesRequest
    ): Response<RoomActivitiesDto>
    
    // Get room events slider
    @GET("api/v1/rooms/{roomId}/events")
    suspend fun getRoomEvents(@Path("roomId") roomId: String): Response<RoomEventsResponse>
    
    // Get room games slider
    @GET("api/v1/rooms/{roomId}/games")
    suspend fun getRoomGames(@Path("roomId") roomId: String): Response<RoomGamesResponse>
    
    // Get lucky bags in room
    @GET("api/v1/rooms/{roomId}/lucky-bags")
    suspend fun getLuckyBags(@Path("roomId") roomId: String): Response<LuckyBagResponse>
    
    // Send lucky bag
    @POST("api/v1/rooms/{roomId}/lucky-bags")
    suspend fun sendLuckyBag(
        @Path("roomId") roomId: String,
        @Body request: SendLuckyBagRequest
    ): Response<LuckyBagDto>
    
    // Grab lucky bag
    @POST("api/v1/rooms/{roomId}/lucky-bags/{bagId}/grab")
    suspend fun grabLuckyBag(
        @Path("roomId") roomId: String,
        @Path("bagId") bagId: String
    ): Response<GrabLuckyBagResponse>
    
    // Create room
    @POST("api/v1/rooms/create")
    suspend fun createRoom(@Body request: CreateRoomRequest): Response<CreateRoomResponse>
    
    // Join room
    @POST("api/v1/rooms/{roomId}/join")
    suspend fun joinRoom(@Path("roomId") roomId: String): Response<Unit>
    
    // Leave room
    @POST("api/v1/rooms/{roomId}/leave")
    suspend fun leaveRoom(@Path("roomId") roomId: String): Response<Unit>
    
    // ============================================
    // Family System
    // ============================================
    
    // Get my family
    @GET("api/v1/family/my")
    suspend fun getMyFamily(): Response<MyFamilyResponse>
    
    // Get family by ID
    @GET("api/v1/family/{familyId}")
    suspend fun getFamily(@Path("familyId") familyId: String): Response<FamilyDetailsResponse>
    
    // Create family
    @POST("api/v1/family/create")
    suspend fun createFamily(@Body request: CreateFamilyRequest): Response<CreateFamilyResponse>
    
    // Search families
    @GET("api/v1/family/search")
    suspend fun searchFamilies(@Query("q") query: String): Response<FamilySearchResponse>
    
    // Join family
    @POST("api/v1/family/{familyId}/join")
    suspend fun joinFamily(@Path("familyId") familyId: String): Response<Unit>
    
    // Leave family
    @POST("api/v1/family/{familyId}/leave")
    suspend fun leaveFamily(@Path("familyId") familyId: String): Response<Unit>
    
    // Get family members
    @GET("api/v1/family/{familyId}/members")
    suspend fun getFamilyMembers(@Path("familyId") familyId: String): Response<FamilyMembersResponse>
    
    // Get family activity
    @GET("api/v1/family/{familyId}/activity")
    suspend fun getFamilyActivity(@Path("familyId") familyId: String): Response<FamilyActivityResponse>
    
    // Kick member
    @POST("api/v1/family/{familyId}/kick/{userId}")
    suspend fun kickFamilyMember(
        @Path("familyId") familyId: String,
        @Path("userId") userId: String
    ): Response<Unit>
    
    // Update member role
    @POST("api/v1/family/{familyId}/role/{userId}")
    suspend fun updateFamilyMemberRole(
        @Path("familyId") familyId: String,
        @Path("userId") userId: String,
        @Body request: UpdateFamilyRoleRequest
    ): Response<Unit>
    
    // Get family rankings
    @GET("api/v1/family/rankings")
    suspend fun getFamilyRankings(
        @Query("type") type: String = "weekly",
        @Query("page") page: Int = 1
    ): Response<FamilyRankingsResponse>
    
    // ============================================
    // CP Partnership System
    // ============================================
    
    // Get CP status
    @GET("api/v1/cp/status")
    suspend fun getCpStatus(): Response<CpStatusResponse>
    
    // Send CP request
    @POST("api/v1/cp/request")
    suspend fun sendCpRequest(@Body request: CpRequestDto): Response<CpRequestResponse>
    
    // Accept/Reject CP request
    @POST("api/v1/cp/request/{requestId}/respond")
    suspend fun respondToCpRequest(
        @Path("requestId") requestId: String,
        @Body request: CpRespondRequest
    ): Response<Unit>
    
    // Dissolve CP
    @POST("api/v1/cp/dissolve")
    suspend fun dissolveCp(): Response<Unit>
    
    // Get CP rankings
    @GET("api/v1/cp/rankings")
    suspend fun getCpRankings(
        @Query("type") type: String = "weekly",
        @Query("page") page: Int = 1
    ): Response<CpRankingsResponse>
    
    // Get CP progress
    @GET("api/v1/cp/progress")
    suspend fun getCpProgress(): Response<CpProgressResponse>
    
    // ============================================
    // Ranking System
    // ============================================
    
    // Get gift rankings
    @GET("api/v1/rankings/gifts")
    suspend fun getGiftRankings(
        @Query("type") type: String = "daily",
        @Query("category") category: String = "sender",
        @Query("page") page: Int = 1
    ): Response<GiftRankingsResponse>
    
    // Get level rankings
    @GET("api/v1/rankings/level")
    suspend fun getLevelRankings(@Query("page") page: Int = 1): Response<LevelRankingsResponse>
    
    // Get wealth rankings
    @GET("api/v1/rankings/wealth")
    suspend fun getWealthRankings(@Query("page") page: Int = 1): Response<WealthRankingsResponse>
    
    // Get charm rankings
    @GET("api/v1/rankings/charm")
    suspend fun getCharmRankings(
        @Query("type") type: String = "daily",
        @Query("page") page: Int = 1
    ): Response<CharmRankingsResponse>
    
    // ============================================
    // Friends System
    // ============================================
    
    // Get friends list
    @GET("api/v1/friends")
    suspend fun getFriends(@Query("page") page: Int = 1): Response<FriendsResponse>
    
    // Get friend requests
    @GET("api/v1/friends/requests")
    suspend fun getFriendRequests(): Response<FriendRequestsResponse>
    
    // Send friend request
    @POST("api/v1/friends/request")
    suspend fun sendFriendRequest(@Body request: FriendRequestDto): Response<Unit>
    
    // Accept friend request
    @POST("api/v1/friends/accept/{userId}")
    suspend fun acceptFriendRequest(@Path("userId") userId: String): Response<Unit>
    
    // Reject friend request
    @POST("api/v1/friends/reject/{userId}")
    suspend fun rejectFriendRequest(@Path("userId") userId: String): Response<Unit>
    
    // Remove friend
    @DELETE("api/v1/friends/{userId}")
    suspend fun removeFriend(@Path("userId") userId: String): Response<Unit>
    
    // ============================================
    // Events System
    // ============================================
    
    // Get active events
    @GET("api/v1/events")
    suspend fun getEvents(): Response<EventsListResponse>
    
    // Get event details
    @GET("api/v1/events/{eventId}")
    suspend fun getEventDetails(@Path("eventId") eventId: String): Response<EventDetailsResponse>
    
    // Participate in event
    @POST("api/v1/events/{eventId}/participate")
    suspend fun participateInEvent(@Path("eventId") eventId: String): Response<Unit>
    
    // Get event progress
    @GET("api/v1/events/{eventId}/progress")
    suspend fun getEventProgress(@Path("eventId") eventId: String): Response<EventProgressResponse>
    
    // ============================================
    // Search
    // ============================================
    
    // Search users
    @GET("api/v1/search/users")
    suspend fun searchUsers(@Query("q") query: String): Response<SearchUsersResponse>
    
    // Search rooms
    @GET("api/v1/search/rooms")
    suspend fun searchRooms(@Query("q") query: String): Response<RoomsResponse>
    
    // ============================================
    // Admin Panel
    // ============================================
    
    // Get admin dashboard
    @GET("api/v1/admin/dashboard")
    suspend fun getAdminDashboard(): Response<AdminDashboardResponse>
    
    // Get users list (admin)
    @GET("api/v1/admin/users")
    suspend fun getAdminUsers(
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null
    ): Response<AdminUsersResponse>
    
    // Ban user (admin)
    @POST("api/v1/admin/users/{userId}/ban")
    suspend fun adminBanUser(
        @Path("userId") userId: String,
        @Body request: AdminBanRequest
    ): Response<Unit>
    
    // Unban user (admin)
    @POST("api/v1/admin/users/{userId}/unban")
    suspend fun adminUnbanUser(@Path("userId") userId: String): Response<Unit>
    
    // Get reports (admin)
    @GET("api/v1/admin/reports")
    suspend fun getAdminReports(@Query("status") status: String? = null): Response<AdminReportsResponse>
    
    // Resolve report (admin)
    @POST("api/v1/admin/reports/{reportId}/resolve")
    suspend fun resolveReport(
        @Path("reportId") reportId: String,
        @Body request: ResolveReportRequest
    ): Response<Unit>
    
    // Get KYC applications (admin)
    @GET("api/v1/admin/kyc")
    suspend fun getKycApplications(@Query("status") status: String? = null): Response<KycApplicationsResponse>
    
    // Approve KYC (admin)
    @POST("api/v1/admin/kyc/{userId}/approve")
    suspend fun approveKyc(@Path("userId") userId: String): Response<Unit>
    
    // Reject KYC (admin)
    @POST("api/v1/admin/kyc/{userId}/reject")
    suspend fun rejectKyc(
        @Path("userId") userId: String,
        @Body request: RejectKycRequest
    ): Response<Unit>
    
    // Get guide applications (admin)
    @GET("api/v1/admin/guides")
    suspend fun getGuideApplications(@Query("status") status: String? = null): Response<GuideApplicationsResponse>
    
    // Approve guide (admin)
    @POST("api/v1/admin/guides/{userId}/approve")
    suspend fun approveGuide(@Path("userId") userId: String): Response<Unit>
    
    // Reject guide (admin)
    @POST("api/v1/admin/guides/{userId}/reject")
    suspend fun rejectGuide(@Path("userId") userId: String): Response<Unit>
    
    // ============================================
    // Content Moderation
    // ============================================
    
    // Check text content for violations (before sending)
    @POST("api/v1/moderation/check-content")
    suspend fun checkContent(@Body request: CheckContentRequest): Response<ModerationResult>
    
    // Check image for violations (before uploading)
    @POST("api/v1/moderation/check-image")
    suspend fun checkImage(@Body request: CheckImageRequest): Response<ModerationResult>
    
    // Get current user's ban status
    @GET("api/v1/moderation/ban-status")
    suspend fun getBanStatus(): Response<BanStatusResponse>
    
    // Get violations (admin)
    @GET("api/v1/moderation/violations")
    suspend fun getViolations(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("status") status: String? = null
    ): Response<ViolationsResponse>
    
    // Get pending image reviews (admin)
    @GET("api/v1/moderation/image-reviews")
    suspend fun getPendingImageReviews(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ImageReviewsResponse>
    
    // Review an image (admin)
    @POST("api/v1/moderation/image-reviews/{reviewId}")
    suspend fun reviewImage(
        @Path("reviewId") reviewId: String,
        @Body request: ReviewImageRequest
    ): Response<Unit>
    
    // Unban a user (admin)
    @POST("api/v1/moderation/unban/{userId}")
    suspend fun unbanUser(@Path("userId") userId: String): Response<Unit>
}

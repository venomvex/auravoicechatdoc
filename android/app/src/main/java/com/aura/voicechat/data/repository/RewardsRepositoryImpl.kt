package com.aura.voicechat.data.repository

import com.aura.voicechat.data.remote.ApiService
import com.aura.voicechat.domain.model.*
import com.aura.voicechat.domain.repository.RewardsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rewards Repository Implementation
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@Singleton
class RewardsRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : RewardsRepository {
    
    override suspend fun getDailyRewardStatus(): Result<DailyRewardStatus> {
        return try {
            val response = apiService.getDailyRewardStatus()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val status = DailyRewardStatus(
                    currentDay = dto.currentDay,
                    claimable = dto.claimable,
                    cycle = dto.cycle.map { dayDto ->
                        DayReward(
                            day = dayDto.day,
                            coins = dayDto.coins,
                            bonus = dayDto.bonus,
                            status = try { RewardStatus.valueOf(dayDto.status.uppercase()) } catch (e: Exception) { RewardStatus.LOCKED }
                        )
                    },
                    streak = dto.streak,
                    nextResetUtc = dto.nextResetUtc,
                    vipTier = dto.vipTier,
                    vipMultiplier = dto.vipMultiplier
                )
                Result.success(status)
            } else {
                Result.failure(Exception("Failed to load reward status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun claimDailyReward(): Result<ClaimRewardResult> {
        return try {
            val response = apiService.claimDailyReward()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val result = ClaimRewardResult(
                    success = dto.success,
                    day = dto.day,
                    baseCoins = dto.baseCoins,
                    vipMultiplier = dto.vipMultiplier,
                    totalCoins = dto.totalCoins
                )
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to claim reward"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

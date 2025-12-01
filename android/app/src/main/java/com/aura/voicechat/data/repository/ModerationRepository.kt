package com.aura.voicechat.data.repository

import android.util.Log
import com.aura.voicechat.data.model.CheckContentRequest
import com.aura.voicechat.data.model.CheckImageRequest
import com.aura.voicechat.data.model.ModerationResult
import com.aura.voicechat.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Moderation Repository
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Handles content moderation - checking for abusive language and vulgar images.
 */
@Singleton
class ModerationRepository @Inject constructor(
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "ModerationRepository"
    }
    
    /**
     * Check text content for violations before sending.
     * Returns true if content is safe, false if it violates policies.
     */
    suspend fun checkContent(
        content: String,
        context: String = "chat"
    ): Result<ModerationResult> {
        return try {
            val response = apiService.checkContent(
                CheckContentRequest(content = content, context = context)
            )
            
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                
                if (result.isViolation) {
                    Log.w(TAG, "Content violation detected: ${result.action}")
                }
                
                Result.success(result)
            } else {
                // If API fails, allow content (fail-open for user experience)
                Log.e(TAG, "Moderation API failed: ${response.code()}")
                Result.success(ModerationResult(
                    success = true,
                    isViolation = false,
                    action = null,
                    message = null,
                    banExpiry = null
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking content", e)
            // Fail open - allow content if API is unavailable
            Result.success(ModerationResult(
                success = true,
                isViolation = false,
                action = null,
                message = null,
                banExpiry = null
            ))
        }
    }
    
    /**
     * Check image for violations before uploading/sending.
     */
    suspend fun checkImage(
        imageUrl: String,
        context: String = "chat"
    ): Result<ModerationResult> {
        return try {
            val response = apiService.checkImage(
                CheckImageRequest(imageUrl = imageUrl, context = context)
            )
            
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                
                if (result.isViolation) {
                    Log.w(TAG, "Image violation detected: ${result.action}")
                }
                
                Result.success(result)
            } else {
                Log.e(TAG, "Image moderation API failed: ${response.code()}")
                Result.success(ModerationResult(
                    success = true,
                    isViolation = false,
                    action = null,
                    message = null,
                    banExpiry = null
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking image", e)
            Result.success(ModerationResult(
                success = true,
                isViolation = false,
                action = null,
                message = null,
                banExpiry = null
            ))
        }
    }
    
    /**
     * Check if user is currently banned.
     */
    suspend fun getBanStatus(): BanStatus {
        return try {
            val response = apiService.getBanStatus()
            
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                BanStatus(
                    isBanned = data.isBanned,
                    banType = data.banType,
                    banReason = data.banReason,
                    banExpiry = data.banExpiry
                )
            } else {
                BanStatus(isBanned = false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ban status", e)
            BanStatus(isBanned = false)
        }
    }
}

data class BanStatus(
    val isBanned: Boolean,
    val banType: String? = null,
    val banReason: String? = null,
    val banExpiry: String? = null
)

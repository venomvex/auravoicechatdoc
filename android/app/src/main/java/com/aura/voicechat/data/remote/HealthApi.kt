package com.aura.voicechat.data.remote

import retrofit2.Response
import retrofit2.http.GET

/**
 * Health API interface for backend connectivity check
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Used to verify connectivity to the backend server.
 * The backend is currently running on EC2 at http://13.127.85.109
 * 
 * TODO: For production, update to use HTTPS with a proper domain name.
 */
interface HealthApi {
    
    /**
     * Check backend health status.
     * Returns a JSON response from the /health endpoint.
     */
    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>
}

/**
 * Response model for health check endpoint.
 * 
 * The backend should always return a status field, but we keep it nullable
 * to handle edge cases where the response format might be unexpected.
 */
data class HealthResponse(
    val status: String = "unknown",
    val message: String? = null
)

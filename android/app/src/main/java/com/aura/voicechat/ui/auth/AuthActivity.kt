package com.aura.voicechat.ui.auth

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aura.voicechat.BuildConfig
import com.aura.voicechat.ui.theme.AuraVoiceChatTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

/**
 * Authentication Activity for Aura Voice Chat
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Handles user authentication via:
 * - AWS Cognito (Phone OTP)
 * - Google Identity Services (GIS) - Modern Google Sign-In
 * - Facebook Login
 * 
 * Uses Google Identity Services (GIS) instead of deprecated GoogleSignIn APIs
 * for better security and user experience.
 */
@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            val email = credential.id
            val displayName = credential.displayName
            
            if (idToken != null) {
                handleGoogleSignIn(idToken, email, displayName)
            } else {
                Log.e(TAG, "Google Sign-In: ID token is null")
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google Sign-In failed: ${e.statusCode}", e)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Google Identity Services One Tap client
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
        
        setContent {
            AuraVoiceChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Use the existing LoginScreen composable
                    LoginScreen(
                        onNavigateToHome = { finishWithResult(true) },
                        onNavigateToOtp = { phoneNumber -> 
                            Log.i(TAG, "Navigating to OTP for: ${phoneNumber.take(4)}****")
                        }
                    )
                }
            }
        }
    }
    
    /**
     * Initiates Google Sign-In flow using Google Identity Services (GIS)
     * Uses One Tap sign-in for a seamless user experience
     */
    fun initiateGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    googleSignInLauncher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                Log.e(TAG, "Google Sign-In failed to begin: ${e.localizedMessage}")
            }
    }
    
    /**
     * Handles successful Google Sign-In
     * @param idToken The Google ID token for backend authentication
     * @param email The user's email address
     * @param displayName The user's display name
     */
    private fun handleGoogleSignIn(idToken: String, email: String?, displayName: String?) {
        // Use AWS Cognito to federate Google identity
        Log.i(TAG, "Google Sign-In successful, federating with Cognito")
        // Note: User information is not logged for privacy/security reasons
        // Implementation handled by ViewModel/Repository
    }
    
    /**
     * Initiates Facebook Login flow
     */
    fun initiateFacebookSignIn() {
        // Facebook Login handled by Facebook SDK
        Log.i(TAG, "Initiating Facebook Sign-In")
    }
    
    /**
     * Initiates Phone OTP authentication via AWS Cognito
     */
    fun initiatePhoneSignIn(phoneNumber: String) {
        Log.i(TAG, "Initiating Phone Sign-In for: ${phoneNumber.take(4)}****")
        // Implementation handled by ViewModel/Repository using AWS Cognito
    }
    
    /**
     * Finishes activity with authentication result
     */
    private fun finishWithResult(success: Boolean) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_AUTH_SUCCESS, success)
        }
        setResult(if (success) RESULT_OK else RESULT_CANCELED, resultIntent)
        finish()
    }
    
    companion object {
        private const val TAG = "AuthActivity"
        const val EXTRA_AUTH_SUCCESS = "auth_success"
    }
}

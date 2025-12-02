package com.aura.voicechat.ui.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.voicechat.BuildConfig
import com.aura.voicechat.R
import com.aura.voicechat.ui.theme.GradientPurpleEnd
import com.aura.voicechat.ui.theme.GradientPurpleStart
import com.aura.voicechat.ui.theme.Purple80
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

private const val TAG = "LoginScreen"

/**
 * Country data class for country code picker
 */
data class Country(
    val name: String,
    val code: String,
    val dialCode: String,
    val flag: String
)

/**
 * List of countries with their dial codes and flags
 */
private val countries = listOf(
    Country("United States", "US", "+1", "🇺🇸"),
    Country("United Kingdom", "GB", "+44", "🇬🇧"),
    Country("India", "IN", "+91", "🇮🇳"),
    Country("Pakistan", "PK", "+92", "🇵🇰"),
    Country("Canada", "CA", "+1", "🇨🇦"),
    Country("Australia", "AU", "+61", "🇦🇺"),
    Country("Germany", "DE", "+49", "🇩🇪"),
    Country("France", "FR", "+33", "🇫🇷"),
    Country("Italy", "IT", "+39", "🇮🇹"),
    Country("Spain", "ES", "+34", "🇪🇸"),
    Country("Brazil", "BR", "+55", "🇧🇷"),
    Country("Mexico", "MX", "+52", "🇲🇽"),
    Country("Japan", "JP", "+81", "🇯🇵"),
    Country("China", "CN", "+86", "🇨🇳"),
    Country("South Korea", "KR", "+82", "🇰🇷"),
    Country("Russia", "RU", "+7", "🇷🇺"),
    Country("Saudi Arabia", "SA", "+966", "🇸🇦"),
    Country("United Arab Emirates", "AE", "+971", "🇦🇪"),
    Country("Turkey", "TR", "+90", "🇹🇷"),
    Country("South Africa", "ZA", "+27", "🇿🇦"),
    Country("Nigeria", "NG", "+234", "🇳🇬"),
    Country("Egypt", "EG", "+20", "🇪🇬"),
    Country("Indonesia", "ID", "+62", "🇮🇩"),
    Country("Malaysia", "MY", "+60", "🇲🇾"),
    Country("Singapore", "SG", "+65", "🇸🇬"),
    Country("Thailand", "TH", "+66", "🇹🇭"),
    Country("Philippines", "PH", "+63", "🇵🇭"),
    Country("Vietnam", "VN", "+84", "🇻🇳"),
    Country("Bangladesh", "BD", "+880", "🇧🇩"),
    Country("Afghanistan", "AF", "+93", "🇦🇫"),
    Country("Argentina", "AR", "+54", "🇦🇷"),
    Country("Belgium", "BE", "+32", "🇧🇪"),
    Country("Chile", "CL", "+56", "🇨🇱"),
    Country("Colombia", "CO", "+57", "🇨🇴"),
    Country("Denmark", "DK", "+45", "🇩🇰"),
    Country("Finland", "FI", "+358", "🇫🇮"),
    Country("Greece", "GR", "+30", "🇬🇷"),
    Country("Hong Kong", "HK", "+852", "🇭🇰"),
    Country("Ireland", "IE", "+353", "🇮🇪"),
    Country("Israel", "IL", "+972", "🇮🇱"),
    Country("Kenya", "KE", "+254", "🇰🇪"),
    Country("Kuwait", "KW", "+965", "🇰🇼"),
    Country("Netherlands", "NL", "+31", "🇳🇱"),
    Country("New Zealand", "NZ", "+64", "🇳🇿"),
    Country("Norway", "NO", "+47", "🇳🇴"),
    Country("Poland", "PL", "+48", "🇵🇱"),
    Country("Portugal", "PT", "+351", "🇵🇹"),
    Country("Qatar", "QA", "+974", "🇶🇦"),
    Country("Sweden", "SE", "+46", "🇸🇪"),
    Country("Switzerland", "CH", "+41", "🇨🇭")
)

/**
 * Country Code Picker Dialog
 */
@Composable
fun CountryPickerDialog(
    countries: List<Country>,
    onCountrySelected: (Country) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCountries = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            countries
        } else {
            countries.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.dialCode.contains(searchQuery) 
            }
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Country",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple80,
                        focusedLabelColor = Purple80
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn {
                    items(filteredCountries) { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCountrySelected(country) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = country.flag,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = country.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = country.dialCode,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

/**
 * Login Screen with Google, Facebook, and Phone (OTP) login
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Performs a backend health check on first load to verify connectivity
 * to the EC2 backend server.
 * 
 * Social login flow:
 * 1. User taps Google/Facebook button
 * 2. SDK launcher is triggered (rememberLauncherForActivityResult)
 * 3. SDK handles auth flow and returns token
 * 4. Token is sent to backend via ViewModel
 * 5. Backend verifies token and creates/authenticates user
 */
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(countries[0]) } // Default to US
    var showCountryPicker by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Google Sign-In configuration using BuildConfig
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(com.aura.voicechat.BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    
    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    Log.d(TAG, "Google Sign-In successful, sending token to backend")
                    viewModel.onGoogleTokenReceived(
                        idToken = idToken,
                        email = account.email,
                        displayName = account.displayName
                    )
                } else {
                    Log.e(TAG, "Google Sign-In: ID token is null")
                    // Cannot proceed without ID token
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google Sign-In failed: ${e.statusCode}", e)
            }
        } else {
            Log.w(TAG, "Google Sign-In cancelled or failed: ${result.resultCode}")
        }
    }
    
    // Facebook Callback Manager
    val facebookCallbackManager = remember { CallbackManager.Factory.create() }
    
    // Register Facebook callback
    DisposableEffect(Unit) {
        LoginManager.getInstance().registerCallback(
            facebookCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val accessToken = result.accessToken
                    Log.d(TAG, "Facebook Sign-In successful, sending token to backend")
                    viewModel.onFacebookTokenReceived(
                        accessToken = accessToken.token,
                        userId = accessToken.userId,
                        email = null, // Facebook doesn't always return email
                        displayName = null
                    )
                }
                
                override fun onCancel() {
                    Log.w(TAG, "Facebook Sign-In cancelled")
                }
                
                override fun onError(error: FacebookException) {
                    Log.e(TAG, "Facebook Sign-In failed", error)
                }
            }
        )
        
        onDispose {
            LoginManager.getInstance().unregisterCallback(facebookCallbackManager)
        }
    }
    
    // Ping backend on first load to verify connectivity
    LaunchedEffect(Unit) {
        viewModel.pingBackend()
    }
    
    // Show error snackbar when error occurs
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }
    
    // Country picker dialog
    if (showCountryPicker) {
        CountryPickerDialog(
            countries = countries,
            onCountrySelected = { country ->
                selectedCountry = country
                showCountryPicker = false
            },
            onDismiss = { showCountryPicker = false }
        )
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientPurpleStart, GradientPurpleEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo - Using actual Aura Voice Chat logo
                Image(
                    painter = painterResource(id = R.drawable.ic_aura_logo),
                    contentDescription = "Aura Voice Chat Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Fit
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // App Name
                Text(
                    text = "Aura Voice Chat",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Connect with people around the world",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Phone Number Input with Country Code Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Code Picker Button
                    OutlinedButton(
                        onClick = { showCountryPicker = true },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = selectedCountry.flag,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedCountry.dialCode,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select country"
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Phone Number Field
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it.filter { char -> char.isDigit() } },
                        label = { Text("Phone Number") },
                        placeholder = { Text("234 567 8900") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple80,
                            focusedLabelColor = Purple80
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Continue with Phone Button
                Button(
                    onClick = {
                        if (phoneNumber.isNotBlank()) {
                            val fullPhoneNumber = "${selectedCountry.dialCode}$phoneNumber"
                            viewModel.sendOtp(fullPhoneNumber)
                            onNavigateToOtp(fullPhoneNumber)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple80
                    ),
                    enabled = phoneNumber.isNotBlank() && !uiState.isLoading
                ) {
                    Text(
                        text = "Continue with Phone",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // OR Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "  OR  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Google Sign In - Uses Google Sign-In SDK
                Button(
                    onClick = {
                        Log.d(TAG, "Launching Google Sign-In")
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Facebook Sign In - Uses Facebook Login SDK
                Button(
                    onClick = {
                        Log.d(TAG, "Launching Facebook Sign-In")
                        // Safe cast with null check
                        (context as? Activity)?.let { activity ->
                            LoginManager.getInstance().logInWithReadPermissions(
                                activity,
                                facebookCallbackManager,
                                listOf("email", "public_profile")
                            )
                        } ?: run {
                            Log.e(TAG, "Context is not an Activity, cannot launch Facebook login")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "Continue with Facebook",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Terms and Privacy
                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
            
            // Loading overlay
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Purple80)
                }
            }
        }
    }
    
    // Handle successful login
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onNavigateToHome()
        }
    }
}

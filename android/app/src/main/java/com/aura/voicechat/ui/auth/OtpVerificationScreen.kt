package com.aura.voicechat.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.voicechat.ui.theme.GradientPurpleEnd
import com.aura.voicechat.ui.theme.GradientPurpleStart
import com.aura.voicechat.ui.theme.Purple80
import kotlinx.coroutines.delay

/**
 * OTP Verification Screen
 * Developer: Hawkaye Visions LTD — Pakistan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OtpVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var otpValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientPurpleStart, GradientPurpleEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Back Button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = "Verify your phone",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "We sent a verification code to\n$phoneNumber",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // OTP Input
            OutlinedTextField(
                value = otpValue,
                onValueChange = { 
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        otpValue = it
                        if (it.length == 6) {
                            viewModel.verifyOtp(phoneNumber, it)
                        }
                    }
                },
                label = { Text("Enter 6-digit code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple80,
                    focusedLabelColor = Purple80
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Verify Button
            Button(
                onClick = { viewModel.verifyOtp(phoneNumber, otpValue) },
                enabled = otpValue.length == 6 && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple80
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Verify",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Resend Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Didn't receive code? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                TextButton(
                    onClick = { viewModel.resendOtp(phoneNumber) },
                    enabled = uiState.resendCooldown == 0
                ) {
                    Text(
                        text = if (uiState.resendCooldown > 0) 
                            "Resend in ${uiState.resendCooldown}s" 
                        else 
                            "Resend",
                        color = Purple80
                    )
                }
            }
            
            // Error message
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
    
    // Handle successful verification
    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) {
            onNavigateToHome()
        }
    }
}

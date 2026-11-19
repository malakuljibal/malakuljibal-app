package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.MalakulCard
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.DataMonoFamily
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.MalakulTheme

@Composable
fun AuthPreviewScreen(
    state: MalakulUiState,
    onLogin: (email: String, password: String) -> Unit,
    onUpdateApiBaseUrl: (String) -> Unit,
    onEnterOfflineDemo: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (onDismiss != null && state.authSession != null) {
        BackHandler { onDismiss() }
    }

    var email by remember { mutableStateOf(state.authSession?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showServerConfig by remember { mutableStateOf(false) }
    var baseUrlInput by remember(state.apiConfig.baseUrl) { mutableStateOf(state.apiConfig.baseUrl) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Brand Hero Crest Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(206.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = DeepNavy,
            border = BorderStroke(1.dp, MalakulTheme.colors.gold.copy(alpha = 0.4f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_family_banner_1795077629862),
                    contentDescription = "Malakul Jibal mountain sanctuary",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(DeepNavy.copy(alpha = 0.62f), DeepNavy.copy(alpha = 0.92f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_launcher_crest_1795077631320),
                        contentDescription = "Malakul Jibal crest",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "MALAKUL JIBAL",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Our Family. Our Health. Our Legacy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MalakulTheme.colors.gold
                    )
                }
            }
        }

        // Live JWT Login Form Card
        MalakulCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.Shield, contentDescription = null, tint = MalakulTheme.colors.gold, modifier = Modifier.size(18.dp))
                Text(
                    text = "PRIVATE FAMILY SIGN-IN",
                    style = MaterialTheme.typography.labelSmall,
                    color = MalakulTheme.colors.gold
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Authenticate securely via ${state.apiConfig.baseUrl}/v1/auth/login",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = DataMonoFamily),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state.authError != null) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MalakulTheme.colors.dangerContainer,
                    border = BorderStroke(1.dp, MalakulTheme.colors.danger.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = "Authentication error",
                            tint = MalakulTheme.colors.danger,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = state.authError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MalakulTheme.colors.danger,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
                label = { Text("Email") },
                placeholder = { Text("you@malakuljibal.com") },
                leadingIcon = { Icon(Icons.Outlined.MailOutline, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = !state.isAuthLoading,
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !state.isAuthLoading,
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    onUpdateApiBaseUrl(baseUrlInput)
                    onLogin(email, password)
                },
                enabled = !state.isAuthLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_login_button")
                    .defaultMinSize(minHeight = 52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (state.isAuthLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Signing In...")
                } else {
                    Text("Sign In to Malakul Jibal", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = { showServerConfig = !showServerConfig },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Outlined.Dns, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (showServerConfig) "Hide API Server Config" else "Configure API Server URL")
            }

            if (showServerConfig) {
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = baseUrlInput,
                    onValueChange = { baseUrlInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_api_url_input"),
                    label = { Text("API Base URL") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = DataMonoFamily),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Secondary Offline Demo Option
        OutlinedButton(
            onClick = onEnterOfflineDemo,
            enabled = !state.isAuthLoading,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_offline_demo_button")
                .defaultMinSize(minHeight = 48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Outlined.Science, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Explore Offline Demo Data (No Login)")
        }
    }
}

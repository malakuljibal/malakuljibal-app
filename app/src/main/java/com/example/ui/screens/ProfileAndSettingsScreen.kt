package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.DataMonoFamily
import com.example.ui.theme.MalakulTheme
import com.example.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndSettingsScreen(
    state: MalakulUiState,
    onRefreshLive: () -> Unit,
    onLogout: () -> Unit,
    onSwitchActiveUser: (String) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onUpdateApiBaseUrl: (String) -> Unit,
    onOpenAuthScreen: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeUser = state.activeUser
    var apiUrlInput by remember(state.apiConfig.baseUrl) { mutableStateOf(state.apiConfig.baseUrl) }
    var savedToast by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ACCOUNT & GOVERNANCE", style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.gold)
                        Text("Profile & System Settings", style = MaterialTheme.typography.titleLarge)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .testTag("profile_back_button")
                            .size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("profile_settings_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Session Header
            item {
                MalakulCard(containerColor = MalakulTheme.colors.navyHero) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        MemberAvatar(member = activeUser, size = 62.dp, isSelected = true)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(activeUser.fullName, style = MaterialTheme.typography.headlineSmall, color = MalakulTheme.colors.onNavyHero)
                            Text(activeUser.email ?: "Offline Demo Account", style = MaterialTheme.typography.bodySmall, color = MalakulTheme.colors.gold)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                RoleBadge(role = activeUser.role)
                                if (state.isLiveMode && state.authSession != null) {
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = MalakulTheme.colors.successContainer
                                    ) {
                                        Text(
                                            text = "JWT · Family #${state.authSession.familyId}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MalakulTheme.colors.success
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (state.isLiveMode) "Encrypted JWT stored via Android Keystore. Bearer token attached automatically to all API requests."
                        else activeUser.role.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MalakulTheme.colors.onNavyHero.copy(alpha = 0.8f)
                    )
                }
            }

            // Live Sync / Sign Out Actions
            item {
                if (state.isLiveMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = onRefreshLive,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_sync_now_btn")
                                .defaultMinSize(minHeight = 50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Sync All")
                        }
                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_logout_btn")
                                .defaultMinSize(minHeight = 50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Outlined.Logout, contentDescription = null, tint = MalakulTheme.colors.danger, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Sign Out", color = MalakulTheme.colors.danger)
                        }
                    }
                } else {
                    Button(
                        onClick = onOpenAuthScreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_auth_preview_button")
                            .defaultMinSize(minHeight = 52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Outlined.Login, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Sign In to Live Backend (${state.apiConfig.baseUrl})")
                    }
                }
            }

            // Offline Demo RBAC Account Switcher (when in offline demo mode)
            if (!state.isLiveMode) {
                item {
                    SectionHeader(
                        title = "Offline Demo Role Switcher",
                        subtitle = "Test record-level privacy in offline preview"
                    )
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.members.filter { it.hasIndependentAccount }.forEach { member ->
                            val isCurrent = member.id == activeUser.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("switch_role_${member.shortName.lowercase()}")
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onSwitchActiveUser(member.id) },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isCurrent) MalakulTheme.colors.goldContainer.copy(alpha = 0.45f)
                                else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = if (isCurrent) 1.5.dp else 1.dp,
                                    color = if (isCurrent) MalakulTheme.colors.gold else MalakulTheme.colors.cardBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    MemberAvatar(member = member, size = 42.dp, isSelected = isCurrent)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(member.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text("${member.role.displayName} · ${member.relationship.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (isCurrent) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Active session", tint = MalakulTheme.colors.gold)
                                    } else {
                                        Text("Switch", style = MaterialTheme.typography.labelLarge, color = MalakulTheme.colors.gold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Appearance (Light / Dark / System Theme)
            item {
                SectionHeader(title = "Appearance", subtitle = "Warm Ivory Light & Obsidian Navy Dark themes")
                Spacer(Modifier.height(8.dp))
                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.entries.forEach { mode ->
                            val selected = state.themeMode == mode
                            FilterChip(
                                selected = selected,
                                onClick = { onSetThemeMode(mode) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_mode_${mode.name.lowercase()}"),
                                label = { Text(mode.label) },
                                leadingIcon = {
                                    val icon = when (mode) {
                                        ThemeMode.LIGHT -> Icons.Outlined.LightMode
                                        ThemeMode.DARK -> Icons.Outlined.DarkMode
                                        ThemeMode.SYSTEM -> Icons.Outlined.SettingsBrightness
                                    }
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }

            // Configurable Backend Base URL
            item {
                SectionHeader(
                    title = "REST API Server Config",
                    subtitle = "Live Hostinger PHP 8.x REST API endpoint"
                )
                Spacer(Modifier.height(8.dp))
                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Connection Status", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = if (state.isLiveMode) MalakulTheme.colors.successContainer else MalakulTheme.colors.mockBannerBg
                        ) {
                            Text(
                                text = state.apiConfig.syncStatusLabel,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (state.isLiveMode) MalakulTheme.colors.success else MalakulTheme.colors.mockBannerContent
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = apiUrlInput,
                        onValueChange = {
                            apiUrlInput = it
                            savedToast = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_base_url_input"),
                        label = { Text("API Base URL (HTTPS)") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = DataMonoFamily)
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (savedToast) "Base URL saved" else "Bearer JWT + application/json",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (savedToast) MalakulTheme.colors.success else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FilledTonalButton(
                            onClick = {
                                onUpdateApiBaseUrl(apiUrlInput)
                                savedToast = true
                            },
                            modifier = Modifier
                                .testTag("save_api_url_button")
                                .defaultMinSize(minHeight = 48.dp)
                        ) {
                            Text("Save URL")
                        }
                    }
                }
            }

            // Module Permissions Matrix
            item {
                SectionHeader(
                    title = "Active Role Permissions",
                    subtitle = "Assigned module access for ${activeUser.shortName}"
                )
            }
            items(activeUser.permissions, key = { it.module.name }) { perm ->
                MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(perm.module.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(perm.scopeNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = when {
                                perm.canEdit -> MalakulTheme.colors.successContainer
                                perm.canView -> MalakulTheme.colors.goldContainer
                                else -> MalakulTheme.colors.dangerContainer
                            }
                        ) {
                            Text(
                                text = when {
                                    perm.canEdit -> "View & Edit"
                                    perm.canView -> "View Only"
                                    else -> "Restricted"
                                },
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = when {
                                    perm.canEdit -> MalakulTheme.colors.success
                                    perm.canView -> MalakulTheme.colors.onGoldContainer
                                    else -> MalakulTheme.colors.danger
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

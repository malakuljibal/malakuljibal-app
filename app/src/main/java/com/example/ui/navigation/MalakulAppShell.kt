package com.example.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.state.MainViewModel
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.MalakulTheme

object MalakulRoutes {
    const val ROUTE_HOME = "home_dashboard"
    const val ROUTE_FAMILY = "family_profiles"
    const val ROUTE_FINANCE = "finance_preview"
    const val ROUTE_HEALTH = "health_preview"
    const val ROUTE_DOCUMENTS = "documents_preview"
    const val ROUTE_CHILD_DEV = "child_development"
    const val ROUTE_CALENDAR = "family_calendar"
    const val ROUTE_PROFILE = "profile_settings"
}

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val PrimaryDestinations = listOf(
    TopLevelDestination(MalakulRoutes.ROUTE_HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    TopLevelDestination(MalakulRoutes.ROUTE_FAMILY, "Family", Icons.Filled.Groups, Icons.Outlined.Groups, "nav_family"),
    TopLevelDestination(MalakulRoutes.ROUTE_FINANCE, "Finance", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_finance"),
    TopLevelDestination(MalakulRoutes.ROUTE_HEALTH, "Health", Icons.Filled.MonitorHeart, Icons.Outlined.MonitorHeart, "nav_health"),
    TopLevelDestination(MalakulRoutes.ROUTE_DOCUMENTS, "Vault", Icons.Filled.FolderShared, Icons.Outlined.FolderShared, "nav_documents")
)

@Composable
fun MalakulAppShell(
    state: MalakulUiState,
    viewModel: MainViewModel
) {
    if (state.showAuthScreen) {
        AuthPreviewScreen(
            state = state,
            onLogin = { email, password -> viewModel.login(email, password) },
            onUpdateApiBaseUrl = { viewModel.updateApiBaseUrl(it) },
            onEnterOfflineDemo = { viewModel.enterOfflineDemoMode() },
            onDismiss = if (state.authSession != null) ({ viewModel.closeAuthScreen() }) else null
        )
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isPrimaryRoute = PrimaryDestinations.any { it.route == currentDestination?.route }

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 700.dp

        if (useNavRail) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            ) {
                if (isPrimaryRoute) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Spacer(Modifier.height(12.dp))
                        PrimaryDestinations.forEach { dest ->
                            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopLevel(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                modifier = Modifier.testTag(dest.testTag),
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MalakulTheme.colors.onGoldContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MalakulTheme.colors.goldContainer
                                )
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    MalakulNavContent(
                        navController = navController,
                        state = state,
                        viewModel = viewModel,
                        onNavigateTopLevel = ::navigateTopLevel,
                        modifier = Modifier
                            .widthIn(max = 760.dp)
                            .fillMaxHeight()
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
                    )
                }
            }
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (isPrimaryRoute) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp
                        ) {
                            PrimaryDestinations.forEach { dest ->
                                val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateTopLevel(dest.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                            contentDescription = dest.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = dest.label,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    modifier = Modifier.testTag(dest.testTag),
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MalakulTheme.colors.onGoldContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                        indicatorColor = MalakulTheme.colors.goldContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                MalakulNavContent(
                    navController = navController,
                    state = state,
                    viewModel = viewModel,
                    onNavigateTopLevel = ::navigateTopLevel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun MalakulNavContent(
    navController: androidx.navigation.NavHostController,
    state: MalakulUiState,
    viewModel: MainViewModel,
    onNavigateTopLevel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MalakulRoutes.ROUTE_HOME,
        modifier = modifier
    ) {
        composable(MalakulRoutes.ROUTE_HOME) {
            HomeDashboardScreen(
                state = state,
                onRefreshLive = { viewModel.refreshAllLiveData() },
                onDismissFeedback = { viewModel.clearFeedbackMessages() },
                onSelectFamilyMember = { viewModel.selectProfile(it) },
                onNavigateToFamily = { onNavigateTopLevel(MalakulRoutes.ROUTE_FAMILY) },
                onNavigateToFinance = { onNavigateTopLevel(MalakulRoutes.ROUTE_FINANCE) },
                onNavigateToHealth = { onNavigateTopLevel(MalakulRoutes.ROUTE_HEALTH) },
                onNavigateToDocuments = { onNavigateTopLevel(MalakulRoutes.ROUTE_DOCUMENTS) },
                onNavigateToChildDev = { navController.navigate(MalakulRoutes.ROUTE_CHILD_DEV) },
                onNavigateToCalendar = { navController.navigate(MalakulRoutes.ROUTE_CALENDAR) },
                onNavigateToProfile = { navController.navigate(MalakulRoutes.ROUTE_PROFILE) },
                onToggleTheme = { viewModel.toggleQuickTheme() }
            )
        }
        composable(MalakulRoutes.ROUTE_FAMILY) {
            FamilyProfilesScreen(
                state = state,
                onSelectProfile = { viewModel.selectProfile(it) },
                onOpenChildDevelopment = { childId ->
                    viewModel.selectChildMember(childId)
                    navController.navigate(MalakulRoutes.ROUTE_CHILD_DEV)
                },
                onOpenMemberHealth = { memberId ->
                    viewModel.selectHealthMember(memberId)
                    onNavigateTopLevel(MalakulRoutes.ROUTE_HEALTH)
                },
                onOpenDocuments = { onNavigateTopLevel(MalakulRoutes.ROUTE_DOCUMENTS) }
            )
        }
        composable(MalakulRoutes.ROUTE_FINANCE) {
            FinancePreviewScreen(
                state = state,
                onRefresh = { viewModel.loadFinanceData() },
                onDismissFeedback = { viewModel.clearFeedbackMessages() },
                onAddTransaction = { type, amount, desc, date ->
                    viewModel.addTransaction(type, amount, desc, date)
                },
                onDeleteTransaction = { viewModel.deleteTransaction(it) },
                onSelectPrivacyFilter = { viewModel.selectFinanceFilter(it) }
            )
        }
        composable(MalakulRoutes.ROUTE_HEALTH) {
            HealthPreviewScreen(
                state = state,
                onRefresh = { viewModel.loadHealthRecords() },
                onDismissFeedback = { viewModel.clearFeedbackMessages() },
                onAddHealthRecord = { diag, hosp, doc, notes, date ->
                    viewModel.addHealthRecord(diag, hosp, doc, notes, date)
                },
                onSelectHealthMember = { viewModel.selectHealthMember(it) },
                onOpenChildDevelopment = { childId ->
                    viewModel.selectChildMember(childId)
                    navController.navigate(MalakulRoutes.ROUTE_CHILD_DEV)
                }
            )
        }
        composable(MalakulRoutes.ROUTE_DOCUMENTS) {
            DocumentsPreviewScreen(
                state = state,
                onRefresh = { viewModel.loadDocuments() },
                onDismissFeedback = { viewModel.clearFeedbackMessages() },
                onAddDocument = { title, category, docDate, notes ->
                    viewModel.addDocument(title, category, docDate, notes)
                },
                onDeleteDocument = { viewModel.deleteDocument(it) },
                onSearchChange = { viewModel.updateDocumentSearch(it) },
                onSelectCategory = { viewModel.selectDocumentCategory(it) }
            )
        }
        composable(MalakulRoutes.ROUTE_CHILD_DEV) {
            ChildDevelopmentPreviewScreen(
                state = state,
                onRefreshBabyGrowth = { viewModel.loadBabyGrowth() },
                onUpdateLiveBabyId = { viewModel.updateLiveBabyId(it) },
                onAddBabyGrowth = { babyId, w, l, h, date ->
                    viewModel.addBabyGrowth(babyId, w, l, h, date)
                },
                onDismissFeedback = { viewModel.clearFeedbackMessages() },
                onSelectChild = { viewModel.selectChildMember(it) },
                onSelectGrowthMetric = { viewModel.selectGrowthMetric(it) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(MalakulRoutes.ROUTE_CALENDAR) {
            CalendarScreen(
                state = state,
                onSelectCategory = { viewModel.selectCalendarCategory(it) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(MalakulRoutes.ROUTE_PROFILE) {
            ProfileAndSettingsScreen(
                state = state,
                onRefreshLive = { viewModel.refreshAllLiveData() },
                onLogout = { viewModel.logout() },
                onSwitchActiveUser = { viewModel.switchActiveUser(it) },
                onSetThemeMode = { viewModel.setThemeMode(it) },
                onUpdateApiBaseUrl = { viewModel.updateApiBaseUrl(it) },
                onOpenAuthScreen = { viewModel.openAuthScreen() },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

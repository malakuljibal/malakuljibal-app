package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.ActivityModule
import com.example.domain.model.UserRole
import com.example.domain.rbac.AccessControl
import com.example.ui.components.*
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.DataMonoFamily
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.MalakulTheme

@Composable
fun HomeDashboardScreen(
    state: MalakulUiState,
    onRefreshLive: () -> Unit,
    onDismissFeedback: () -> Unit,
    onSelectFamilyMember: (String) -> Unit,
    onNavigateToFamily: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToHealth: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToChildDev: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeUser = state.activeUser
    val canSeeFinance = state.isLiveMode || AccessControl.canViewFinanceOverview(activeUser)
    val featuredChild = state.childProfiles.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_dashboard_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Editorial Hero Command Banner
        item {
            CommandCenterHeroBanner(
                familyName = if (state.isLiveMode && state.authSession != null)
                    "Keluarga Samawa"
                else state.family.name,
                motto = state.family.motto,
                activeUser = activeUser,
                memberCount = state.members.size,
                upcomingEventCount = state.visibleEvents.size,
                onOpenProfile = onNavigateToProfile,
                onOpenCalendar = onNavigateToCalendar,
                onToggleTheme = onToggleTheme
            )
        }

        // 2. Feedback Banner (when error/action occurs)
        if (state.globalError != null || state.actionMessage != null) {
            item {
                ApiFeedbackBanner(
                    errorMessage = state.globalError,
                    successMessage = state.actionMessage,
                    onRetry = if (state.isLiveMode) onRefreshLive else null,
                    onDismiss = onDismissFeedback
                )
            }
        }

        // 2b. Quick Action Hub (Aksi Cepat Keluarga)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Aksi Cepat Hari Ini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = "Catat Kas",
                        subtitle = "Pengeluaran",
                        containerColor = MalakulTheme.colors.goldContainer.copy(alpha = 0.5f),
                        contentColor = MalakulTheme.colors.onGoldContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFinance
                    )
                    QuickActionTile(
                        icon = Icons.Outlined.CalendarMonth,
                        title = "Jadwal Baru",
                        subtitle = "Agenda",
                        containerColor = MalakulTheme.colors.navyHero.copy(alpha = 0.15f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCalendar
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Outlined.ChildCare,
                        title = "Tumbuh Kembang",
                        subtitle = "Ukur BB/TB",
                        containerColor = MalakulTheme.colors.healthContainer.copy(alpha = 0.5f),
                        contentColor = MalakulTheme.colors.healthAccent,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToChildDev
                    )
                    QuickActionTile(
                        icon = Icons.Outlined.FolderShared,
                        title = "Arsip Berkas",
                        subtitle = "Dokumen",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDocuments
                    )
                }
            }
        }

        // 2c. Agenda & Safe-to-Spend Alert Card
        item {
            val fin = state.financialSummary
            val nextEvent = state.visibleEvents.firstOrNull()
            MalakulCard(
                modifier = Modifier.fillMaxWidth().testTag("home_today_agenda_card"),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsActive,
                            contentDescription = null,
                            tint = MalakulTheme.colors.gold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Pengingat & Agenda Terdekat",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (fin.monthlyBudgetLimit > 0L) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = if (fin.isBudgetExceeded) MalakulTheme.colors.danger.copy(alpha = 0.15f)
                            else if (fin.isBudgetWarning) MalakulTheme.colors.warning.copy(alpha = 0.2f)
                            else MalakulTheme.colors.goldContainer
                        ) {
                            Text(
                                text = "Aman: ${formatCurrency(fin.safeToSpend, fin.currencySymbol)}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (fin.isBudgetExceeded) MalakulTheme.colors.danger
                                else if (fin.isBudgetWarning) MalakulTheme.colors.warning
                                else MalakulTheme.colors.onGoldContainer
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                if (nextEvent != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { onNavigateToCalendar() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MalakulTheme.colors.navyHero,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = nextEvent.dateLabel.take(6),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MalakulTheme.colors.gold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nextEvent.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${nextEvent.timeLabel} · ${nextEvent.locationOrNote}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Belum ada agenda keluarga hari ini. Klik 'Jadwal Baru' di atas untuk menambah pengingat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 3. Family Overview & Quick Member Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    title = "Family Directory",
                    subtitle = "${state.members.size} members · Tap any profile to inspect",
                    actionLabel = "All Profiles",
                    onActionClick = onNavigateToFamily
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.members, key = { it.id }) { member ->
                        MalakulCard(
                            modifier = Modifier
                                .width(118.dp)
                                .testTag("home_member_card_${member.shortName.lowercase()}"),
                            contentPadding = PaddingValues(12.dp),
                            onClick = {
                                onSelectFamilyMember(member.id)
                                onNavigateToFamily()
                            }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MemberAvatar(
                                    member = member,
                                    size = 52.dp,
                                    isSelected = member.id == activeUser.id
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = member.shortName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = member.ageLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(6.dp))
                                RoleBadge(role = member.role)
                            }
                        }
                    }
                }
            }
        }

        // 4. Financial Overview Card (Live API Bound)
        item {
            if (canSeeFinance) {
                val fin = state.financialSummary
                MalakulCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_finance_card"),
                    onClick = onNavigateToFinance
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ModuleIconCircle(
                                icon = Icons.Outlined.AccountBalanceWallet,
                                bg = MalakulTheme.colors.goldContainer,
                                tint = MalakulTheme.colors.onGoldContainer
                            )
                            Column {
                                Text(
                                    text = "Financial Overview",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (state.isLiveMode) "${fin.monthLabel} · ${state.transactions.size} live entries"
                                    else "${fin.monthLabel} · Offline Demo Ledger",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (state.isLoadingFinance) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.ArrowForwardIos,
                                contentDescription = "Open Finance module",
                                tint = MalakulTheme.colors.gold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricStatBlock(
                            label = "Income",
                            value = formatCurrency(fin.monthlyIncome, fin.currencySymbol),
                            accentColor = MalakulTheme.colors.success,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatBlock(
                            label = "Expenses",
                            value = formatCurrency(fin.monthlyExpenses, fin.currencySymbol),
                            accentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatBlock(
                            label = "Net Balance",
                            value = formatCurrency(fin.netBalance, fin.currencySymbol),
                            accentColor = MalakulTheme.colors.gold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { fin.budgetUtilization.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = MalakulTheme.colors.gold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            } else {
                RestrictedAccessCard(
                    title = "Household Finances Restricted",
                    reason = "Signed in as ${activeUser.shortName} (${activeUser.role.shortBadge}). Family financial ledgers are restricted to Super Admin and authorized Parents."
                )
            }
        }

        // 5. Health & Document Vault Twin Summary Row (Live API Bound)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MalakulCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_health_card"),
                    onClick = onNavigateToHealth,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    ModuleIconCircle(
                        icon = Icons.Outlined.MonitorHeart,
                        bg = MalakulTheme.colors.healthContainer,
                        tint = MalakulTheme.colors.healthAccent
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Health Records",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (state.isLoadingHealth) "Loading..." else "${state.medicalRecords.size} records",
                        style = MaterialTheme.typography.headlineSmall.copy(fontFamily = DataMonoFamily),
                        color = MalakulTheme.colors.healthAccent
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = state.medicalRecords.firstOrNull()?.recordedDiagnosis ?: "Tap to log consultation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                MalakulCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_documents_card"),
                    onClick = onNavigateToDocuments,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    ModuleIconCircle(
                        icon = Icons.Outlined.FolderShared,
                        bg = MalakulTheme.colors.warningContainer,
                        tint = MalakulTheme.colors.warning
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Document Vault",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (state.isLoadingDocuments) "Loading..." else "${state.visibleDocuments.size} files",
                        style = MaterialTheme.typography.headlineSmall.copy(fontFamily = DataMonoFamily),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = state.visibleDocuments.firstOrNull()?.title ?: "Tap to store document",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 6. Child Development Spotlight Card (Module F — Live Bound)
        if (featuredChild != null && (state.isLiveMode || activeUser.role == UserRole.SUPER_ADMIN || activeUser.role == UserRole.PARENT)) {
            item {
                val childMember = state.members.firstOrNull { it.id == featuredChild.memberId }
                val latestLive = state.growthMeasurements.lastOrNull()
                MalakulCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_child_dev_card"),
                    onClick = onNavigateToChildDev
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (childMember != null) {
                            MemberAvatar(member = childMember, size = 50.dp, isSelected = true)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (state.isLiveMode) "BABY GROWTH · ID #${state.liveBabyId}" else "CHILD DEVELOPMENT OVERVIEW",
                                style = MaterialTheme.typography.labelSmall,
                                color = MalakulTheme.colors.gold
                            )
                            Text(
                                text = "${featuredChild.fullName} · ${featuredChild.ageSummary}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (state.isLoadingBaby) "Syncing growth logs..."
                                else if (latestLive != null) "Last recorded: ${latestLive.dateLabel}"
                                else featuredChild.nextImmunizationSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.ArrowForwardIos,
                            contentDescription = "Open Child Development",
                            tint = MalakulTheme.colors.gold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = MalakulTheme.colors.cardBorder)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricStatBlock(
                            label = "Weight",
                            value = "${latestLive?.weightKg ?: featuredChild.latestWeightKg} kg",
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatBlock(
                            label = "Length",
                            value = "${latestLive?.lengthCm ?: featuredChild.latestLengthCm} cm",
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatBlock(
                            label = "Head Circ.",
                            value = "${latestLive?.headCircumferenceCm ?: featuredChild.latestHeadCircumferenceCm} cm",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 7. Upcoming Family Calendar Events
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(
                    title = "Upcoming Family Schedule",
                    subtitle = "Appointments, deadlines & family events",
                    actionLabel = "Calendar",
                    onActionClick = onNavigateToCalendar
                )
                state.visibleEvents.take(3).forEach { event ->
                    MalakulCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(14.dp),
                        onClick = onNavigateToCalendar
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MalakulTheme.colors.navyHero,
                                modifier = Modifier.width(56.dp)
                            ) {
                                val parts = event.dateLabel.split(" ")
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = parts.getOrNull(0) ?: "",
                                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = DataMonoFamily),
                                        color = MalakulTheme.colors.gold
                                    )
                                    Text(
                                        text = (parts.getOrNull(1) ?: "").uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MalakulTheme.colors.onNavyHero
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${event.timeLabel} · ${event.locationOrNote}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                PrivacyBadge(accessLevel = event.accessLevel)
                            }
                        }
                    }
                }
            }
        }

        // 8. Chronological Family Activity Feed
        item {
            SectionHeader(
                title = "Family Activity Feed",
                subtitle = "Recent household updates"
            )
        }
        items(state.visibleActivityFeed, key = { it.id }) { item ->
            MalakulCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val icon = when (item.module) {
                        ActivityModule.FINANCE -> Icons.Outlined.Payments
                        ActivityModule.HEALTH -> Icons.Outlined.MedicalServices
                        ActivityModule.DOCUMENTS -> Icons.Outlined.Description
                        ActivityModule.CHILD_DEV -> Icons.Outlined.ChildCare
                        ActivityModule.CALENDAR -> Icons.Outlined.Event
                    }
                    ModuleIconCircle(
                        icon = icon,
                        bg = MaterialTheme.colorScheme.surfaceVariant,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.module.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MalakulTheme.colors.gold
                            )
                            Text(
                                text = item.timestampLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandCenterHeroBanner(
    familyName: String,
    motto: String,
    activeUser: com.example.domain.model.FamilyMember,
    memberCount: Int,
    upcomingEventCount: Int,
    onOpenProfile: () -> Unit,
    onOpenCalendar: () -> Unit,
    onToggleTheme: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge),
        shape = MaterialTheme.shapes.extraLarge,
        color = DeepNavy,
        border = BorderStroke(1.dp, MalakulTheme.colors.gold.copy(alpha = 0.35f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_family_banner_1795077629862),
                contentDescription = "Malakul Jibal mountain heritage horizon",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DeepNavy.copy(alpha = 0.84f),
                                DeepNavy.copy(alpha = 0.72f),
                                DeepNavy.copy(alpha = 0.94f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MALAKUL JIBAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = MalakulTheme.colors.gold
                        )
                        Text(
                            text = familyName,
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier
                                .testTag("quick_theme_toggle")
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DarkMode,
                                contentDescription = "Toggle light or dark theme",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = onOpenCalendar,
                            modifier = Modifier
                                .testTag("home_calendar_button")
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = "Open Family Calendar",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = motto,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.82f)
                )
                Spacer(Modifier.height(18.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_profile_chip")
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onOpenProfile),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.11f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MemberAvatar(member = activeUser, size = 40.dp, isSelected = true)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Welcome back, ${activeUser.shortName}",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${activeUser.role.displayName} · $memberCount Members · $upcomingEventCount Events",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.78f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "Profile and account settings",
                            tint = MalakulTheme.colors.gold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleIconCircle(
    icon: ImageVector,
    bg: Color,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AccessLevel
import com.example.domain.model.FamilyMember
import com.example.domain.model.MonthlyComparison
import com.example.domain.model.UserRole
import com.example.ui.theme.DataMonoFamily
import com.example.ui.theme.MalakulTheme
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Long, symbol: String = "Rp "): String {
    val formatted = NumberFormat.getNumberInstance(Locale.GERMANY).format(amount)
    return "$symbol$formatted"
}

@Composable
fun MalakulCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier.clip(MaterialTheme.shapes.large).clickable(onClick = onClick)
    } else {
        modifier
    }

    Surface(
        modifier = cardModifier,
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        border = BorderStroke(1.dp, MalakulTheme.colors.cardBorder),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun MockDataBanner(
    apiBaseUrl: String,
    activeRoleLabel: String,
    isLiveMode: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val bg = if (isLiveMode) MalakulTheme.colors.successContainer else MalakulTheme.colors.mockBannerBg
    val fg = if (isLiveMode) MalakulTheme.colors.success else MalakulTheme.colors.mockBannerContent
    val border = if (isLiveMode) MalakulTheme.colors.success.copy(alpha = 0.4f) else MalakulTheme.colors.gold.copy(alpha = 0.45f)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = if (isLiveMode) Icons.Rounded.CloudDone else Icons.Rounded.Science,
                contentDescription = if (isLiveMode) "Live API connected" else "Offline demo mode",
                tint = fg,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isLiveMode) "LIVE API · $activeRoleLabel" else "OFFLINE DEMO · $activeRoleLabel",
                    style = MaterialTheme.typography.labelSmall,
                    color = fg,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isLiveMode) "Connected to $apiBaseUrl" else "Offline preview · Sign in to connect $apiBaseUrl",
                    style = MaterialTheme.typography.bodySmall,
                    color = fg.copy(alpha = 0.88f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isLiveMode && onRefresh != null) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = fg
                    )
                } else {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .testTag("sync_refresh_button")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh live data",
                            tint = fg,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApiFeedbackBanner(
    errorMessage: String?,
    successMessage: String?,
    onRetry: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (errorMessage != null) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MalakulTheme.colors.dangerContainer,
            border = BorderStroke(1.dp, MalakulTheme.colors.danger.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = "API error",
                    tint = MalakulTheme.colors.danger,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MalakulTheme.colors.danger,
                    modifier = Modifier.weight(1f)
                )
                if (onRetry != null) {
                    TextButton(onClick = onRetry) {
                        Text("Retry", color = MalakulTheme.colors.danger)
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Rounded.Close, contentDescription = "Dismiss error", tint = MalakulTheme.colors.danger, modifier = Modifier.size(16.dp))
                }
            }
        }
    } else if (successMessage != null) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MalakulTheme.colors.successContainer,
            border = BorderStroke(1.dp, MalakulTheme.colors.success.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Success",
                    tint = MalakulTheme.colors.success,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = successMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MalakulTheme.colors.success,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Rounded.Close, contentDescription = "Dismiss notice", tint = MalakulTheme.colors.success, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MalakulTheme.colors.gold
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MalakulTheme.colors.gold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun PrivacyBadge(
    accessLevel: AccessLevel,
    modifier: Modifier = Modifier
) {
    val (bg, fg, icon) = when (accessLevel) {
        AccessLevel.FAMILY_SHARED -> Triple(
            MalakulTheme.colors.successContainer,
            MalakulTheme.colors.success,
            Icons.Outlined.Groups
        )
        AccessLevel.ADMIN_ONLY -> Triple(
            MalakulTheme.colors.goldContainer,
            MalakulTheme.colors.onGoldContainer,
            Icons.Outlined.AdminPanelSettings
        )
        AccessLevel.PRIVATE_PERSONAL -> Triple(
            MalakulTheme.colors.dangerContainer,
            MalakulTheme.colors.danger,
            Icons.Outlined.Lock
        )
        AccessLevel.PARENT_MANAGED -> Triple(
            MalakulTheme.colors.healthContainer,
            MalakulTheme.colors.healthAccent,
            Icons.Outlined.VerifiedUser
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = accessLevel.label,
                tint = fg,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = accessLevel.label,
                style = MaterialTheme.typography.labelMedium,
                color = fg,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RoleBadge(
    role: UserRole,
    modifier: Modifier = Modifier
) {
    val isGold = role == UserRole.SUPER_ADMIN || role == UserRole.PARENT
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = if (isGold) MalakulTheme.colors.goldContainer else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = role.shortBadge,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isGold) MalakulTheme.colors.onGoldContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun MemberAvatar(
    member: FamilyMember,
    size: Dp = 48.dp,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MalakulTheme.colors.gold else MalakulTheme.colors.cardBorder
    val borderWidth = if (isSelected) 2.5.dp else 1.dp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MalakulTheme.colors.navyHero)
            .border(borderWidth, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (member.avatarRes != null) {
            Image(
                painter = painterResource(id = member.avatarRes),
                contentDescription = "${member.fullName} portrait",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = member.initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (size.value * 0.34f).sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MalakulTheme.colors.gold
            )
        }
    }
}

@Composable
fun FamilyMemberSelectorStrip(
    members: List<FamilyMember>,
    selectedMemberId: String,
    onSelectMember: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(members, key = { it.id }) { member ->
            val selected = member.id == selectedMemberId
            Surface(
                modifier = Modifier
                    .testTag("member_selector_${member.shortName.lowercase()}")
                    .defaultMinSize(minHeight = 48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectMember(member.id) },
                shape = RoundedCornerShape(16.dp),
                color = if (selected) MalakulTheme.colors.navyHero else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = if (selected) 1.5.dp else 1.dp,
                    color = if (selected) MalakulTheme.colors.gold else MalakulTheme.colors.cardBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MemberAvatar(member = member, size = 34.dp, isSelected = selected)
                    Column {
                        Text(
                            text = member.shortName,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selected) MalakulTheme.colors.onNavyHero else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = member.role.shortBadge,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) MalakulTheme.colors.gold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestrictedAccessCard(
    title: String,
    reason: String,
    modifier: Modifier = Modifier
) {
    MalakulCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MalakulTheme.colors.dangerContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Restricted content",
                    tint = MalakulTheme.colors.danger
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MetricStatBlock(
    label: String,
    value: String,
    accentColor: Color = MaterialTheme.colorScheme.onSurface,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = DataMonoFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = accentColor,
            maxLines = 1
        )
    }
}

@Composable
fun IncomeExpenseBarChart(
    data: List<MonthlyComparison>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada data transaksi untuk grafik tren",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val incomeColor = MalakulTheme.colors.navyHero
    val expenseColor = MalakulTheme.colors.gold
    val gridColor = MalakulTheme.colors.cardBorder
    val maxVal = (data.maxOfOrNull { maxOf(it.income, it.expense) } ?: 1L).coerceAtLeast(1L)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val h = size.height
            val w = size.width
            // Horizontal reference lines
            for (i in 0..3) {
                val y = h * (i / 3f)
                drawLine(
                    color = gridColor.copy(alpha = 0.6f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            if (data.isEmpty()) return@Canvas
            val groupWidth = w / data.size
            val barWidth = (groupWidth * 0.28f).coerceAtMost(18.dp.toPx())
            val gap = 4.dp.toPx()

            data.forEachIndexed { index, item ->
                val centerX = groupWidth * index + groupWidth / 2f
                val incHeight = (item.income.toFloat() / maxVal.toFloat()) * (h - 10.dp.toPx())
                val expHeight = (item.expense.toFloat() / maxVal.toFloat()) * (h - 10.dp.toPx())

                drawRoundRect(
                    color = incomeColor,
                    topLeft = Offset(centerX - barWidth - gap / 2f, h - incHeight),
                    size = Size(barWidth, incHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                drawRoundRect(
                    color = expenseColor,
                    topLeft = Offset(centerX + gap / 2f, h - expHeight),
                    size = Size(barWidth, expHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            data.forEach { item ->
                Text(
                    text = item.monthShort,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LineTrendChart(
    points: List<Pair<String, Float>>,
    lineColor: Color,
    unitSuffix: String,
    modifier: Modifier = Modifier
) {
    val gridColor = MalakulTheme.colors.cardBorder
    val surfaceColor = MaterialTheme.colorScheme.surface
    if (points.isEmpty()) return

    val minVal = points.minOf { it.second }
    val maxVal = points.maxOf { it.second }
    val range = (maxVal - minVal).takeIf { it > 0.01f } ?: 1f

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
        ) {
            val w = size.width
            val h = size.height
            val padX = 14.dp.toPx()
            val padY = 14.dp.toPx()
            val plotW = (w - padX * 2).coerceAtLeast(1f)
            val plotH = (h - padY * 2).coerceAtLeast(1f)

            for (i in 0..2) {
                val y = padY + plotH * (i / 2f)
                drawLine(
                    color = gridColor.copy(alpha = 0.65f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val coords = points.mapIndexed { idx, (_, value) ->
                val x = if (points.size == 1) w / 2f else padX + (idx.toFloat() / (points.size - 1)) * plotW
                val norm = (value - minVal) / range
                val y = padY + plotH * (1f - norm)
                Offset(x, y)
            }

            if (coords.size > 1) {
                val path = Path().apply {
                    moveTo(coords.first().x, coords.first().y)
                    coords.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            coords.forEach { pt ->
                drawCircle(color = lineColor, radius = 5.dp.toPx(), center = pt)
                drawCircle(color = surfaceColor, radius = 2.5.dp.toPx(), center = pt)
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { (label, value) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format(Locale.US, "%.1f%s", value, unitSuffix),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

@Immutable
data class MalakulExtendedColors(
    val gold: Color,
    val goldContainer: Color,
    val onGoldContainer: Color,
    val navyHero: Color,
    val onNavyHero: Color,
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val danger: Color,
    val dangerContainer: Color,
    val healthAccent: Color,
    val healthContainer: Color,
    val cardBorder: Color,
    val mockBannerBg: Color,
    val mockBannerContent: Color
)

private val LightExtendedColors = MalakulExtendedColors(
    gold = MutedGold,
    goldContainer = WarmGoldContainer,
    onGoldContainer = DeepGoldText,
    navyHero = DeepNavy,
    onNavyHero = PureWhite,
    success = MutedEmerald,
    successContainer = MutedEmeraldContainer,
    warning = WarmAmber,
    warningContainer = WarmAmberContainer,
    danger = MutedCrimson,
    dangerContainer = MutedCrimsonContainer,
    healthAccent = HeritageTeal,
    healthContainer = HeritageTealContainer,
    cardBorder = WarmStoneOutline,
    mockBannerBg = Color(0xFFEDE4D0),
    mockBannerContent = Color(0xFF473616)
)

private val DarkExtendedColors = MalakulExtendedColors(
    gold = ChampagneGold,
    goldContainer = Color(0xFF2F281A),
    onGoldContainer = ChampagneGold,
    navyHero = Color(0xFF192842),
    onNavyHero = AlabasterText,
    success = MutedEmeraldDark,
    successContainer = Color(0xFF163128),
    warning = WarmAmberDark,
    warningContainer = Color(0xFF332613),
    danger = MutedCrimsonDark,
    dangerContainer = Color(0xFF3A1A1D),
    healthAccent = HeritageTealDark,
    healthContainer = Color(0xFF162E30),
    cardBorder = DarkOutline,
    mockBannerBg = Color(0xFF2A2419),
    mockBannerContent = ChampagneGold
)

val LocalMalakulColors = staticCompositionLocalOf { LightExtendedColors }

private val MalakulLightColorScheme = lightColorScheme(
    primary = DeepNavy,
    onPrimary = PureWhite,
    primaryContainer = DeepNavyContainer,
    onPrimaryContainer = WarmGoldContainer,
    secondary = MutedGold,
    onSecondary = DeepNavy,
    secondaryContainer = WarmGoldContainer,
    onSecondaryContainer = DeepGoldText,
    tertiary = HeritageTeal,
    onTertiary = PureWhite,
    tertiaryContainer = HeritageTealContainer,
    onTertiaryContainer = Color(0xFF123335),
    background = WarmIvory,
    onBackground = CharcoalText,
    surface = PureWhite,
    onSurface = CharcoalText,
    surfaceVariant = SoftParchment,
    onSurfaceVariant = MutedGrayText,
    outline = WarmStoneOutline,
    outlineVariant = Color(0xFFEBE6DB),
    error = MutedCrimson,
    onError = PureWhite,
    errorContainer = MutedCrimsonContainer,
    onErrorContainer = Color(0xFF5D191B)
)

private val MalakulDarkColorScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = MidnightNavy,
    primaryContainer = DeepNavyElevated,
    onPrimaryContainer = AlabasterText,
    secondary = MutedGold,
    onSecondary = MidnightNavy,
    secondaryContainer = Color(0xFF2F2719),
    onSecondaryContainer = ChampagneGold,
    tertiary = HeritageTealDark,
    onTertiary = MidnightNavy,
    tertiaryContainer = Color(0xFF162E30),
    onTertiaryContainer = Color(0xFFBFE6E7),
    background = MidnightNavy,
    onBackground = AlabasterText,
    surface = MidnightSurface,
    onSurface = AlabasterText,
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = CoolSlateText,
    outline = DarkOutline,
    outlineVariant = Color(0xFF21314A),
    error = MutedCrimsonDark,
    onError = MidnightNavy,
    errorContainer = Color(0xFF3A1A1D),
    onErrorContainer = Color(0xFFF6C2C4)
)

val MalakulShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

object MalakulTheme {
    val colors: MalakulExtendedColors
        @Composable get() = LocalMalakulColors.current
}

@Composable
fun MalakulJibalTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) MalakulDarkColorScheme else MalakulLightColorScheme
    val extendedColors = if (isDark) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalMalakulColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MalakulTypography,
            shapes = MalakulShapes,
            content = content
        )
    }
}

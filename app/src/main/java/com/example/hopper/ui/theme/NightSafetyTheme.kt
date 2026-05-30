package com.example.hopper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hopper.util.LocaleManager

/**
 * Night Safety Mode configuration providing minimum tap targets
 * and emergency element styling.
 */
@Immutable
data class NightSafetyConfig(
    /** Minimum tap target size for all interactive elements. */
    val minTapTarget: Dp = 48.dp,
    /** Color for Police booth pins with increased visual weight. */
    val policeColor: Color = NightBlue,
    /** Color for Medical camp pins with increased visual weight. */
    val medicalColor: Color = NightRed,
    /** Color for Metro station pins. */
    val metroColor: Color = NightGreen,
    /** Color for Railway station pins. */
    val railwayColor: Color = NightYellow,
    /** Whether Night Safety Mode is currently active. */
    val isActive: Boolean = true
)

/**
 * CompositionLocal providing [NightSafetyConfig] to the composition tree.
 * Components can read this to apply minimum tap targets and emergency styling.
 */
val LocalNightSafetyConfig = staticCompositionLocalOf { NightSafetyConfig(isActive = false) }

private val NightSafetyColorScheme = darkColorScheme(
    primary = NightYellow,
    onPrimary = NightBlack,
    primaryContainer = Color(0xFF3D3500),
    onPrimaryContainer = NightYellow,
    secondary = NightWhite,
    onSecondary = NightBlack,
    secondaryContainer = Color(0xFF2C2C2C),
    onSecondaryContainer = NightWhite,
    tertiary = NightGreen,
    onTertiary = NightBlack,
    tertiaryContainer = Color(0xFF003D1E),
    onTertiaryContainer = NightGreen,
    background = NightBlack,
    onBackground = NightWhite,
    surface = NightSurface,
    onSurface = NightWhite,
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFE0E0E0),
    error = NightRed,
    onError = NightBlack,
    errorContainer = Color(0xFF3D0000),
    onErrorContainer = NightRed,
    outline = Color(0xFF808080),
    outlineVariant = Color(0xFF404040)
)

/**
 * Night Safety Mode theme for Hopper.
 * Provides a high-contrast dark theme with:
 * - Pure black background for maximum contrast
 * - Bright yellow/white text for readability in low light
 * - Increased text sizes (body minimum 18sp)
 * - 48dp minimum tap targets via [LocalNightSafetyConfig]
 * - Increased visual weight for emergency elements (Police, Medical pins)
 *
 * Requirement 7.1: High-contrast dark theme with increased text size and minimum 48dp tap targets.
 * Requirement 10.3: Bengali-compatible font rendering in Night Safety Mode.
 *
 * @param localeManager The locale manager instance for locale-aware typography.
 * @param content The composable content to theme.
 */
@Composable
fun NightSafetyTheme(
    localeManager: LocaleManager,
    content: @Composable () -> Unit
) {
    val locale by localeManager.currentLocale.collectAsState()
    val baseTypography = HopperTypography(locale)

    // Increase all text sizes for night readability — body minimum 18sp
    val nightTypography = baseTypography.copy(
        displayLarge = baseTypography.displayLarge.copy(
            fontWeight = FontWeight.Bold,
            color = NightWhite
        ),
        displayMedium = baseTypography.displayMedium.copy(
            fontWeight = FontWeight.Bold,
            color = NightWhite
        ),
        displaySmall = baseTypography.displaySmall.copy(
            fontWeight = FontWeight.Bold,
            color = NightWhite
        ),
        headlineLarge = baseTypography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            color = NightWhite
        ),
        headlineMedium = baseTypography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = NightWhite
        ),
        headlineSmall = baseTypography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = NightWhite
        ),
        titleLarge = baseTypography.titleLarge.copy(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = NightYellow
        ),
        titleMedium = baseTypography.titleMedium.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = NightYellow
        ),
        titleSmall = baseTypography.titleSmall.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = NightYellow
        ),
        bodyLarge = baseTypography.bodyLarge.copy(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            color = NightWhite
        ),
        bodyMedium = baseTypography.bodyMedium.copy(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            color = NightWhite
        ),
        bodySmall = baseTypography.bodySmall.copy(
            fontSize = 16.sp,
            lineHeight = 22.sp,
            color = Color(0xFFE0E0E0)
        ),
        labelLarge = baseTypography.labelLarge.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = NightYellow
        ),
        labelMedium = baseTypography.labelMedium.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = NightYellow
        ),
        labelSmall = baseTypography.labelSmall.copy(
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFE0E0E0)
        )
    )

    val nightSafetyConfig = NightSafetyConfig(isActive = true)

    CompositionLocalProvider(LocalNightSafetyConfig provides nightSafetyConfig) {
        MaterialTheme(
            colorScheme = NightSafetyColorScheme,
            typography = nightTypography,
            content = content
        )
    }
}

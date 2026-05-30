package com.example.hopper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.hopper.util.LocaleManager

private val HopperLightColorScheme = lightColorScheme(
    primary = Saffron,
    onPrimary = LightOnPrimary,
    primaryContainer = SaffronLight,
    onPrimaryContainer = SaffronDark,
    secondary = DeepRed,
    onSecondary = LightOnSecondary,
    secondaryContainer = DeepRedLight,
    onSecondaryContainer = DeepRedDark,
    tertiary = Gold,
    onTertiary = LightOnTertiary,
    tertiaryContainer = GoldLight,
    onTertiaryContainer = GoldDark,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = WarmGrayLight,
    outlineVariant = Color(0xFFD1C4B9)
)

private val HopperDarkColorScheme = darkColorScheme(
    primary = SaffronLight,
    onPrimary = DarkOnPrimary,
    primaryContainer = SaffronDark,
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = DeepRedLight,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DeepRedDark,
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = GoldLight,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = GoldDark,
    onTertiaryContainer = Color(0xFFFFE08D),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF9C8E82),
    outlineVariant = Color(0xFF4E4039)
)

/**
 * Main application theme for Hopper (Festival Atlas).
 * Observes the current locale from [LocaleManager] and applies locale-aware typography.
 * Uses festival-themed warm color palette (saffron, deep red, gold).
 *
 * @param localeManager The locale manager instance for observing language changes.
 * @param darkTheme Whether to use the dark color scheme. Defaults to system setting.
 * @param content The composable content to theme.
 */
@Composable
fun HopperTheme(
    localeManager: LocaleManager,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val locale by localeManager.currentLocale.collectAsState()

    val colorScheme = if (darkTheme) HopperDarkColorScheme else HopperLightColorScheme
    val typography = HopperTypography(locale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

/**
 * Convenience overload for previews and tests that don't have access to [LocaleManager].
 * Defaults to English locale.
 *
 * @param darkTheme Whether to use the dark color scheme. Defaults to system setting.
 * @param locale The locale code to use for typography. Defaults to "en".
 * @param content The composable content to theme.
 */
@Composable
fun HopperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    locale: String = "en",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HopperDarkColorScheme else HopperLightColorScheme
    val typography = HopperTypography(locale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

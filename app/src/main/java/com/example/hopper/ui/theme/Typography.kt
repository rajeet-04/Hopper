package com.example.hopper.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.hopper.R

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val hindSiliguri = GoogleFont("Hind Siliguri")
private val inter = GoogleFont("Inter")

val HindSiliguriFontFamily = FontFamily(
    Font(googleFont = hindSiliguri, fontProvider = googleFontProvider, weight = FontWeight.Light),
    Font(googleFont = hindSiliguri, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = hindSiliguri, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = hindSiliguri, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = hindSiliguri, fontProvider = googleFontProvider, weight = FontWeight.Bold)
)

val InterFontFamily = FontFamily(
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.Light),
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.Bold)
)

/**
 * Returns locale-aware Typography.
 * Bengali locale ("bn"): Hind Siliguri for display/headline/title, Inter for body/label.
 * English locale ("en"): Inter for all text styles.
 */
fun HopperTypography(locale: String): Typography {
    val displayFont = if (locale == "bn") HindSiliguriFontFamily else InterFontFamily
    val headlineFont = if (locale == "bn") HindSiliguriFontFamily else InterFontFamily
    val titleFont = if (locale == "bn") HindSiliguriFontFamily else InterFontFamily
    val bodyFont = InterFontFamily
    val labelFont = InterFontFamily

    return Typography(
        displayLarge = TextStyle(
            fontFamily = displayFont,
            fontWeight = FontWeight.Normal,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = displayFont,
            fontWeight = FontWeight.Normal,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = displayFont,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = headlineFont,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = headlineFont,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = headlineFont,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = titleFont,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = titleFont,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = titleFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = labelFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = labelFont,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = labelFont,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}

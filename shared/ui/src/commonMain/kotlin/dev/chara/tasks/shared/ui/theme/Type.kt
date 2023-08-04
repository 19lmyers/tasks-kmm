package dev.chara.tasks.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

// The base styles provided by Material 3
private val base = Typography()

// Our chosen font families
expect val latoFontFamily: FontFamily

val typography: Typography
    @Composable get() = Typography(
        displayLarge = base.displayLarge.copy(color = MaterialTheme.colorScheme.primary, fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        displayMedium = base.displayMedium.copy(color = MaterialTheme.colorScheme.primary, fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        displaySmall = base.displaySmall.copy(color = MaterialTheme.colorScheme.primary, fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        headlineLarge = base.headlineLarge.copy(color = MaterialTheme.colorScheme.primary, fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        headlineMedium = base.headlineMedium.copy(color = MaterialTheme.colorScheme.primary, fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        headlineSmall = base.headlineSmall.copy(color = MaterialTheme.colorScheme.primary, fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        titleLarge = base.titleLarge.copy(color = MaterialTheme.colorScheme.primary, fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        titleMedium = base.titleMedium.copy(color = MaterialTheme.colorScheme.primary, fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        titleSmall = base.titleSmall.copy(color = MaterialTheme.colorScheme.primary, fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        bodyLarge = base.bodyLarge.copy(fontFamily = latoFontFamily),
        bodyMedium = base.bodyMedium.copy(fontFamily = latoFontFamily),
        bodySmall = base.bodySmall.copy(fontFamily = latoFontFamily),
        labelLarge = base.labelLarge.copy(fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        labelMedium = base.labelMedium.copy(fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
        labelSmall = base.labelSmall.copy(fontFamily = latoFontFamily, fontWeight = FontWeight.Bold),
    )
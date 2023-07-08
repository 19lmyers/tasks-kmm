package dev.chara.tasks.android.ui.theme.dynamic

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import dev.chara.tasks.android.model.hct
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.preference.ThemeVariant
import material_color_utilities.dynamiccolor.MaterialDynamicColors
import material_color_utilities.hct.Hct
import material_color_utilities.scheme.DynamicScheme

fun dynamicColorScheme(
    darkTheme: Boolean,
    scheme: DynamicScheme,
    material: MaterialDynamicColors = MaterialDynamicColors()
) = if (darkTheme) {
    darkColorScheme(
        primary = Color(material.primary().getArgb(scheme)),
        onPrimary = Color(material.onPrimary().getArgb(scheme)),
        primaryContainer = Color(material.primaryContainer().getArgb(scheme)),
        onPrimaryContainer = Color(material.onPrimaryContainer().getArgb(scheme)),
        secondary = Color(material.secondary().getArgb(scheme)),
        onSecondary = Color(material.onSecondary().getArgb(scheme)),
        secondaryContainer = Color(material.secondaryContainer().getArgb(scheme)),
        onSecondaryContainer = Color(material.onSecondaryContainer().getArgb(scheme)),
        tertiary = Color(material.tertiary().getArgb(scheme)),
        onTertiary = Color(material.onTertiary().getArgb(scheme)),
        tertiaryContainer = Color(material.tertiaryContainer().getArgb(scheme)),
        onTertiaryContainer = Color(material.onTertiaryContainer().getArgb(scheme)),
        error = Color(material.error().getArgb(scheme)),
        errorContainer = Color(material.errorContainer().getArgb(scheme)),
        onError = Color(material.onError().getArgb(scheme)),
        onErrorContainer = Color(material.onErrorContainer().getArgb(scheme)),
        background = Color(material.background().getArgb(scheme)),
        onBackground = Color(material.onBackground().getArgb(scheme)),
        surface = Color(material.surface().getArgb(scheme)),
        onSurface = Color(material.onSurface().getArgb(scheme)),
        surfaceVariant = Color(material.surfaceVariant().getArgb(scheme)),
        onSurfaceVariant = Color(material.onSurfaceVariant().getArgb(scheme)),
        outline = Color(material.outline().getArgb(scheme)),
        inverseOnSurface = Color(material.inverseOnSurface().getArgb(scheme)),
        inverseSurface = Color(material.inverseSurface().getArgb(scheme)),
        inversePrimary = Color(material.inversePrimary().getArgb(scheme)),
        surfaceTint = Color(material.surfaceTint().getArgb(scheme)),
        outlineVariant = Color(material.outlineVariant().getArgb(scheme)),
        scrim = Color(material.scrim().getArgb(scheme)),
        surfaceBright = Color(material.surfaceBright().getArgb(scheme)),
        surfaceContainer = Color(material.surfaceContainer().getArgb(scheme)),
        surfaceContainerHigh = Color(material.surfaceContainerHigh().getArgb(scheme)),
        surfaceContainerHighest = Color(
            material.surfaceContainerHighest().getArgb(scheme)
        ),
        surfaceContainerLow = Color(material.surfaceContainerLow().getArgb(scheme)),
        surfaceContainerLowest = Color(
            material.surfaceContainerLowest().getArgb(scheme)
        ),
        surfaceDim = Color(material.surfaceDim().getArgb(scheme))
    )
} else {
    lightColorScheme(
        primary = Color(material.primary().getArgb(scheme)),
        onPrimary = Color(material.onPrimary().getArgb(scheme)),
        primaryContainer = Color(material.primaryContainer().getArgb(scheme)),
        onPrimaryContainer = Color(material.onPrimaryContainer().getArgb(scheme)),
        secondary = Color(material.secondary().getArgb(scheme)),
        onSecondary = Color(material.onSecondary().getArgb(scheme)),
        secondaryContainer = Color(material.secondaryContainer().getArgb(scheme)),
        onSecondaryContainer = Color(material.onSecondaryContainer().getArgb(scheme)),
        tertiary = Color(material.tertiary().getArgb(scheme)),
        onTertiary = Color(material.onTertiary().getArgb(scheme)),
        tertiaryContainer = Color(material.tertiaryContainer().getArgb(scheme)),
        onTertiaryContainer = Color(material.onTertiaryContainer().getArgb(scheme)),
        error = Color(material.error().getArgb(scheme)),
        errorContainer = Color(material.errorContainer().getArgb(scheme)),
        onError = Color(material.onError().getArgb(scheme)),
        onErrorContainer = Color(material.onErrorContainer().getArgb(scheme)),
        background = Color(material.background().getArgb(scheme)),
        onBackground = Color(material.onBackground().getArgb(scheme)),
        surface = Color(material.surface().getArgb(scheme)),
        onSurface = Color(material.onSurface().getArgb(scheme)),
        surfaceVariant = Color(material.surfaceVariant().getArgb(scheme)),
        onSurfaceVariant = Color(material.onSurfaceVariant().getArgb(scheme)),
        outline = Color(material.outline().getArgb(scheme)),
        inverseOnSurface = Color(material.inverseOnSurface().getArgb(scheme)),
        inverseSurface = Color(material.inverseSurface().getArgb(scheme)),
        inversePrimary = Color(material.inversePrimary().getArgb(scheme)),
        surfaceTint = Color(material.surfaceTint().getArgb(scheme)),
        outlineVariant = Color(material.outlineVariant().getArgb(scheme)),
        scrim = Color(material.scrim().getArgb(scheme)),
        surfaceBright = Color(material.surfaceBright().getArgb(scheme)),
        surfaceContainer = Color(material.surfaceContainer().getArgb(scheme)),
        surfaceContainerHigh = Color(material.surfaceContainerHigh().getArgb(scheme)),
        surfaceContainerHighest = Color(
            material.surfaceContainerHighest().getArgb(scheme)
        ),
        surfaceContainerLow = Color(material.surfaceContainerLow().getArgb(scheme)),
        surfaceContainerLowest = Color(
            material.surfaceContainerLowest().getArgb(scheme)
        ),
        surfaceDim = Color(material.surfaceDim().getArgb(scheme))
    )
}

fun dynamicColorScheme(
    seed: Hct,
    variant: ThemeVariant = ThemeVariant.TONAL_SPOT,
    darkTheme: Boolean
): ColorScheme {
    val scheme = scheme(seed, darkTheme, variant)
    return dynamicColorScheme(darkTheme, scheme)
}

@Composable
fun GlanceColorTheme(
    color: TaskList.Color?,
    variant: ThemeVariant = ThemeVariant.TONAL_SPOT,
    content: @Composable () -> Unit
) {
    if (color != null) {
        val lightColors = dynamicColorScheme(color.hct, variant, false)
        val darkColors = dynamicColorScheme(color.hct, variant, true)

        GlanceTheme(
            colors = ColorProviders(lightColors, darkColors)
        ) {
            content()
        }
    } else {
        content()
    }
}
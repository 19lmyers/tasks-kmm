package dev.chara.tasks.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.preference.ThemeVariant
import dev.chara.tasks.shared.ui.model.seed
import dev.chara.tasks.shared.ui.theme.color.getPlatformColor
import dev.chara.tasks.shared.ui.theme.color.style
import dev.chara.tasks.shared.ui.theme.extend.ExtendSurfaceColors

val LocalDarkTheme = staticCompositionLocalOf {
    false // default to light theme
}

val LocalThemeVariant = staticCompositionLocalOf { ThemeVariant.TONAL_SPOT }

val LocalSeedColor = staticCompositionLocalOf { Color(0xFF6750A4) }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    variant: ThemeVariant = ThemeVariant.TONAL_SPOT,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val default = LocalSeedColor.current

    val seed =
        if (dynamicColor) {
            getPlatformColor(default)
        } else {
            default
        }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalThemeVariant provides variant,
        LocalSeedColor provides seed
    ) {
        DynamicMaterialTheme(
            seedColor = seed,
            useDarkTheme = darkTheme,
            style = variant.style,
        ) {
            ExtendSurfaceColors(seedColor = seed, useDarkTheme = darkTheme, style = variant.style) {
                MaterialTheme(typography = typography) { content() }
            }
        }
    }
}

@Composable
fun ColorTheme(
    color: TaskList.Color?,
    darkTheme: Boolean = LocalDarkTheme.current,
    variant: ThemeVariant = LocalThemeVariant.current,
    content: @Composable () -> Unit
) {
    val seed = color?.seed ?: LocalSeedColor.current

    DynamicMaterialTheme(
        seedColor = seed,
        useDarkTheme = darkTheme,
        style = variant.style,
    ) {
        ExtendSurfaceColors(seedColor = seed, useDarkTheme = darkTheme, style = variant.style) {
            MaterialTheme(typography = typography) { content() }
        }
    }
}

@Composable
fun DynamicTheme(
    seed: Color,
    darkTheme: Boolean = LocalDarkTheme.current,
    variant: ThemeVariant = LocalThemeVariant.current,
    content: @Composable () -> Unit
) {
    DynamicMaterialTheme(
        seedColor = seed,
        useDarkTheme = darkTheme,
        style = variant.style,
    ) {
        ExtendSurfaceColors(seedColor = seed, useDarkTheme = darkTheme, style = variant.style) {
            MaterialTheme(typography = typography) { content() }
        }
    }
}

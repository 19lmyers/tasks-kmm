package dev.chara.tasks.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.chara.tasks.android.model.hct
import dev.chara.tasks.android.ui.theme.dynamic.DynamicColorTheme
import dev.chara.tasks.android.ui.theme.dynamic.dynamicColorScheme
import dev.chara.tasks.android.ui.theme.dynamic.scheme
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.preference.ThemeVariant
import material_color_utilities.hct.Hct

val LocalDarkTheme = staticCompositionLocalOf {
    false //default to light theme
}

val LocalThemeVariant = staticCompositionLocalOf {
    ThemeVariant.TONAL_SPOT
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    variant: ThemeVariant = ThemeVariant.TONAL_SPOT,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current

            val seedColor =
                context.resources.getColor(android.R.color.system_accent1_600, context.theme)
            val scheme = scheme(Hct.fromInt(seedColor), darkTheme, variant)
            dynamicColorScheme(darkTheme, scheme)
        }

        else -> {
            val seedColor = Color(0xFF6750A4).toArgb()
            val scheme = scheme(Hct.fromInt(seedColor), darkTheme, variant)
            dynamicColorScheme(darkTheme, scheme)
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalThemeVariant provides variant,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = Shapes,
            typography = Typography,
            content = content
        )
    }

    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, darkTheme) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )

        onDispose {}
    }
}

@Composable
fun ColorTheme(
    color: TaskList.Color?,
    content: @Composable () -> Unit
) {
    val darkTheme = LocalDarkTheme.current
    val variant = LocalThemeVariant.current

    if (color != null) {
        DynamicColorTheme(
            seed = color.hct,
            variant = variant,
            darkTheme = darkTheme,
            shapes = Shapes,
            typography = Typography,
        ) {
            content()
        }
    } else {
        content()
    }
}
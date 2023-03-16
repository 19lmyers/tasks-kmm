package dev.chara.tasks.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.chara.tasks.android.ui.theme.color.blue.ColorThemeBlue
import dev.chara.tasks.android.ui.theme.color.green.ColorThemeGreen
import dev.chara.tasks.android.ui.theme.color.orange.ColorThemeOrange
import dev.chara.tasks.android.ui.theme.color.pink.ColorThemePink
import dev.chara.tasks.android.ui.theme.color.purple.ColorThemePurple
import dev.chara.tasks.android.ui.theme.color.red.ColorThemeRed
import dev.chara.tasks.android.ui.theme.color.yellow.ColorThemeYellow
import dev.chara.tasks.model.TaskList

val LocalDarkTheme = staticCompositionLocalOf {
    false //default to light theme
}

val LocalVibrantColors = staticCompositionLocalOf {
    false //default to muted colors
}

val LocalThemeColor = staticCompositionLocalOf {
    Color.Unspecified
}

val ColorScheme.themedContainer: Color
    @Composable
    @ReadOnlyComposable
    get(): Color {
        val vibrantColors = LocalVibrantColors.current

        return if (vibrantColors) {
            primaryContainer
        } else {
            surface
        }
    }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    vibrantColors: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalVibrantColors provides vibrantColors,
        LocalThemeColor provides colorScheme.primaryContainer
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

    when (color) {
        TaskList.Color.RED -> ColorThemeRed(
            darkTheme,
            shapes = Shapes,
            typography = Typography,
            content = content
        )

        TaskList.Color.ORANGE -> ColorThemeOrange(
            darkTheme,
            shapes = Shapes,
            typography = Typography,
            content = content
        )

        TaskList.Color.YELLOW -> ColorThemeYellow(
            darkTheme,
            shapes = Shapes,
            typography = Typography,
            content = content
        )

        TaskList.Color.GREEN -> ColorThemeGreen(
            darkTheme,
            shapes = Shapes,
            typography = Typography,
            content = content
        )

        TaskList.Color.BLUE -> ColorThemeBlue(
            darkTheme,
            shapes = Shapes,
            typography = Typography,
            content = content
        )

        TaskList.Color.PURPLE -> ColorThemePurple(
            darkTheme,
            shapes = Shapes,
            typography = Typography,
            content = content
        )

        TaskList.Color.PINK -> ColorThemePink(
            darkTheme,
            shapes = Shapes,
            typography = Typography,
            content = content
        )

        else -> content()
    }
}
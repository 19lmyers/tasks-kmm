package dev.chara.tasks.android.ui.widget.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.material3.ColorProviders
import com.materialkolor.dynamicColorScheme
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.preference.ThemeVariant
import dev.chara.tasks.shared.ui.model.seed
import dev.chara.tasks.shared.ui.theme.LocalSeedColor
import dev.chara.tasks.shared.ui.theme.LocalThemeVariant
import dev.chara.tasks.shared.ui.theme.color.style

@Composable
fun GlanceAppTheme(
    variant: ThemeVariant = ThemeVariant.TONAL_SPOT,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val default = LocalSeedColor.current

    val seed =
        if (dynamicColor) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                Color(context.resources.getColor(android.R.color.system_accent1_600, context.theme))
            } else {
                default
            }
        } else {
            default
        }

    val lightColors = dynamicColorScheme(seed, false, variant.style)
    val darkColors = dynamicColorScheme(seed, true, variant.style)

    CompositionLocalProvider(LocalThemeVariant provides variant, LocalSeedColor provides seed) {
        GlanceExtendSurfaceColors(seedColor = seed, style = variant.style) {
            GlanceTheme(colors = ColorProviders(lightColors, darkColors)) { content() }
        }
    }
}

@Composable
fun GlanceColorTheme(
    color: TaskList.Color?,
    variant: ThemeVariant = LocalThemeVariant.current,
    content: @Composable () -> Unit
) {
    val seed = color?.seed ?: LocalSeedColor.current

    val lightColors = dynamicColorScheme(seed, false, variant.style)
    val darkColors = dynamicColorScheme(seed, true, variant.style)

    GlanceExtendSurfaceColors(seedColor = seed, style = variant.style) {
        GlanceTheme(colors = ColorProviders(lightColors, darkColors)) { content() }
    }
}

@Composable
fun GlanceDynamicTheme(
    seed: Color,
    variant: ThemeVariant = LocalThemeVariant.current,
    content: @Composable () -> Unit
) {

    val lightColors = dynamicColorScheme(seed, false, variant.style)
    val darkColors = dynamicColorScheme(seed, true, variant.style)

    GlanceExtendSurfaceColors(seedColor = seed, style = variant.style) {
        GlanceTheme(colors = ColorProviders(lightColors, darkColors)) { content() }
    }
}

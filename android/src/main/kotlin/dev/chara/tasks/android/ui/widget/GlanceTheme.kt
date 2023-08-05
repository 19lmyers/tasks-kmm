package dev.chara.tasks.android.ui.widget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.materialkolor.dynamicColorScheme
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.preference.ThemeVariant
import dev.chara.tasks.shared.ui.model.seed
import dev.chara.tasks.shared.ui.theme.color.style

@Composable
fun GlanceColorTheme(
    color: TaskList.Color?,
    variant: ThemeVariant = ThemeVariant.TONAL_SPOT,
    content: @Composable () -> Unit
) {
    if (color != null) {
        val lightColors = dynamicColorScheme(color.seed, false, variant.style)
        val darkColors = dynamicColorScheme(color.seed, false, variant.style)

        GlanceTheme(colors = ColorProviders(lightColors, darkColors)) { content() }
    } else {
        content()
    }
}

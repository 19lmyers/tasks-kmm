package dev.chara.tasks.shared.ui.theme.color

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


@Composable
actual fun getPlatformColor(fallback: Color): Color {
    val context = LocalContext.current

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Color(context.resources.getColor(android.R.color.system_accent1_600, context.theme))
    } else {
        fallback
    }
}
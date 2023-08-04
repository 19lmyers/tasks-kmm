package dev.chara.tasks.shared.ui.theme.color

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
expect fun getPlatformColor(fallback: Color): Color
package dev.chara.tasks.shared.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.chara.tasks.shared.ui.R

actual val latoFontFamily: FontFamily =
    FontFamily(
        Font(R.font.lato_regular),
        Font(R.font.lato_medium, FontWeight.Medium),
        Font(R.font.lato_bold, FontWeight.Bold),
    )

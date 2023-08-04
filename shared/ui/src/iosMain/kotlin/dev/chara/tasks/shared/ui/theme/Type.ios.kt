package dev.chara.tasks.shared.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Typeface
import org.jetbrains.skia.FontStyle as SkFontStyle
import org.jetbrains.skia.Typeface as SkTypeface

private fun getTypeface(name: String, style: SkFontStyle) = SkTypeface.makeFromName(name, style)

actual val latoFontFamily: FontFamily = FontFamily(
    Typeface(getTypeface("Lato", SkFontStyle.NORMAL))
)
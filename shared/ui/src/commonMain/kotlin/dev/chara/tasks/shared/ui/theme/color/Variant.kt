package dev.chara.tasks.shared.ui.theme.color

import com.materialkolor.PaletteStyle
import dev.chara.tasks.shared.model.preference.ThemeVariant

val ThemeVariant.style: PaletteStyle
    get() = when (this) {
        ThemeVariant.MONOCHROME -> PaletteStyle.Monochrome
        ThemeVariant.NEUTRAL -> PaletteStyle.Neutral
        ThemeVariant.TONAL_SPOT -> PaletteStyle.TonalSpot
        ThemeVariant.VIBRANT -> PaletteStyle.Vibrant
        ThemeVariant.EXPRESSIVE -> PaletteStyle.Expressive
        ThemeVariant.FRUIT_SALAD -> PaletteStyle.FruitSalad
    }
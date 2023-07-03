package dev.chara.tasks.android.ui.theme.dynamic

import dev.chara.tasks.model.preference.ThemeVariant
import material_color_utilities.hct.Hct
import material_color_utilities.scheme.SchemeExpressive
import material_color_utilities.scheme.SchemeFruitSalad
import material_color_utilities.scheme.SchemeMonochrome
import material_color_utilities.scheme.SchemeNeutral
import material_color_utilities.scheme.SchemeTonalSpot
import material_color_utilities.scheme.SchemeVibrant

fun scheme(
    seed: Hct,
    darkTheme: Boolean,
    variant: ThemeVariant = ThemeVariant.TONAL_SPOT,
    contrastLevel: Double = 0.0
) = when (variant) {
    ThemeVariant.MONOCHROME -> SchemeMonochrome(seed, darkTheme, contrastLevel)
    ThemeVariant.NEUTRAL -> SchemeNeutral(seed, darkTheme, contrastLevel)
    ThemeVariant.TONAL_SPOT -> SchemeTonalSpot(seed, darkTheme, contrastLevel)
    ThemeVariant.VIBRANT -> SchemeVibrant(seed, darkTheme, contrastLevel)
    ThemeVariant.EXPRESSIVE -> SchemeExpressive(seed, darkTheme, contrastLevel)
    ThemeVariant.FRUIT_SALAD -> SchemeFruitSalad(seed, darkTheme, contrastLevel)
}
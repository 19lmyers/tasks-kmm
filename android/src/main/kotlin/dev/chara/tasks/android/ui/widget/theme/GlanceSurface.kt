package dev.chara.tasks.android.ui.widget.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.glance.color.ColorProvider
import androidx.glance.color.ColorProviders
import androidx.glance.unit.ColorProvider
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.MaterialDynamicColors
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.SchemeContent
import com.materialkolor.scheme.SchemeExpressive
import com.materialkolor.scheme.SchemeFidelity
import com.materialkolor.scheme.SchemeFruitSalad
import com.materialkolor.scheme.SchemeMonochrome
import com.materialkolor.scheme.SchemeNeutral
import com.materialkolor.scheme.SchemeRainbow
import com.materialkolor.scheme.SchemeTonalSpot
import com.materialkolor.scheme.SchemeVibrant

val LocalGlanceSurfaceColors = staticCompositionLocalOf { dynamicSurfaceColors(Color(0xFF6750A4)) }

val ColorProviders.surfaceBright
    @Composable get() = LocalGlanceSurfaceColors.current.surfaceBright

val ColorProviders.surfaceContainer
    @Composable get() = LocalGlanceSurfaceColors.current.surfaceContainer

val ColorProviders.surfaceContainerHigh
    @Composable get() = LocalGlanceSurfaceColors.current.surfaceContainerHigh

val ColorProviders.surfaceContainerHighest
    @Composable get() = LocalGlanceSurfaceColors.current.surfaceContainerHighest

val ColorProviders.surfaceContainerLow
    @Composable get() = LocalGlanceSurfaceColors.current.surfaceContainerLow

val ColorProviders.surfaceContainerLowest
    @Composable get() = LocalGlanceSurfaceColors.current.surfaceContainerLowest

val ColorProviders.surfaceDim
    @Composable get() = LocalGlanceSurfaceColors.current.surfaceDim

@Composable
fun GlanceExtendSurfaceColors(
    seedColor: Color,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
    content: @Composable () -> Unit
) {
    val surfaceColors: SurfaceColors by
        remember(seedColor, style, contrastLevel) {
            derivedStateOf {
                dynamicSurfaceColors(
                    seedColor = seedColor,
                    style = style,
                    contrastLevel = contrastLevel,
                )
            }
        }

    CompositionLocalProvider(LocalGlanceSurfaceColors provides surfaceColors) { content() }
}

class SurfaceColors(
    surfaceBright: ColorProvider,
    surfaceContainer: ColorProvider,
    surfaceContainerHigh: ColorProvider,
    surfaceContainerHighest: ColorProvider,
    surfaceContainerLow: ColorProvider,
    surfaceContainerLowest: ColorProvider,
    surfaceDim: ColorProvider
) {
    var surfaceBright by mutableStateOf(surfaceBright, structuralEqualityPolicy())
        internal set

    var surfaceContainer by mutableStateOf(surfaceContainer, structuralEqualityPolicy())
        internal set

    var surfaceContainerHigh by mutableStateOf(surfaceContainerHigh, structuralEqualityPolicy())
        internal set

    var surfaceContainerHighest by
        mutableStateOf(surfaceContainerHighest, structuralEqualityPolicy())
        internal set

    var surfaceContainerLow by mutableStateOf(surfaceContainerLow, structuralEqualityPolicy())
        internal set

    var surfaceContainerLowest by mutableStateOf(surfaceContainerLowest, structuralEqualityPolicy())
        internal set

    var surfaceDim by mutableStateOf(surfaceDim, structuralEqualityPolicy())
        internal set

    fun copy(
        surfaceBright: ColorProvider = this.surfaceBright,
        surfaceContainer: ColorProvider = this.surfaceContainer,
        surfaceContainerHigh: ColorProvider = this.surfaceContainerHigh,
        surfaceContainerHighest: ColorProvider = this.surfaceContainerHighest,
        surfaceContainerLow: ColorProvider = this.surfaceContainerLow,
        surfaceContainerLowest: ColorProvider = this.surfaceContainerLowest,
        surfaceDim: ColorProvider = this.surfaceDim
    ): SurfaceColors =
        SurfaceColors(
            surfaceBright,
            surfaceContainer,
            surfaceContainerHigh,
            surfaceContainerHighest,
            surfaceContainerLow,
            surfaceContainerLowest,
            surfaceDim
        )

    override fun toString(): String {
        return "SurfaceColors(" +
            "surfaceBright=$surfaceBright" +
            "surfaceContainer=$surfaceContainer" +
            "surfaceContainerHigh=$surfaceContainerHigh" +
            "surfaceContainerHighest=$surfaceContainerHighest" +
            "surfaceContainerLow=$surfaceContainerLow" +
            "surfaceContainerLowest=$surfaceContainerLowest" +
            "surfaceDim=$surfaceDim" +
            ")"
    }
}

private fun dynamicSurfaceColors(
    seedColor: Color,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
): SurfaceColors {
    val hct = Hct.fromInt(seedColor.toArgb())
    val colors = MaterialDynamicColors()

    val schemeLight = getScheme(hct, false, style, contrastLevel)
    val schemeDark = getScheme(hct, true, style, contrastLevel)

    return SurfaceColors(
        surfaceBright =
            ColorProvider(
                day = Color(colors.surfaceBright().getArgb(schemeLight)),
                night = Color(colors.surfaceBright().getArgb(schemeDark))
            ),
        surfaceContainer =
            ColorProvider(
                day = Color(colors.surfaceContainer().getArgb(schemeLight)),
                night = Color(colors.surfaceContainer().getArgb(schemeDark))
            ),
        surfaceContainerHigh =
            ColorProvider(
                day = Color(colors.surfaceContainerHigh().getArgb(schemeLight)),
                night = Color(colors.surfaceContainerHigh().getArgb(schemeDark))
            ),
        surfaceContainerHighest =
            ColorProvider(
                day = Color(colors.surfaceContainerHighest().getArgb(schemeLight)),
                night = Color(colors.surfaceContainerHighest().getArgb(schemeDark))
            ),
        surfaceContainerLow =
            ColorProvider(
                day = Color(colors.surfaceContainerLow().getArgb(schemeLight)),
                night = Color(colors.surfaceContainerLow().getArgb(schemeDark))
            ),
        surfaceContainerLowest =
            ColorProvider(
                day = Color(colors.surfaceContainerLowest().getArgb(schemeLight)),
                night = Color(colors.surfaceContainerLowest().getArgb(schemeDark))
            ),
        surfaceDim =
            ColorProvider(
                day = Color(colors.surfaceDim().getArgb(schemeLight)),
                night = Color(colors.surfaceDim().getArgb(schemeDark))
            ),
    )
}

private fun getScheme(
    hct: Hct,
    isDark: Boolean,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0
) =
    when (style) {
        PaletteStyle.TonalSpot -> SchemeTonalSpot(hct, isDark, contrastLevel)
        PaletteStyle.Neutral -> SchemeNeutral(hct, isDark, contrastLevel)
        PaletteStyle.Vibrant -> SchemeVibrant(hct, isDark, contrastLevel)
        PaletteStyle.Expressive -> SchemeExpressive(hct, isDark, contrastLevel)
        PaletteStyle.Rainbow -> SchemeRainbow(hct, isDark, contrastLevel)
        PaletteStyle.FruitSalad -> SchemeFruitSalad(hct, isDark, contrastLevel)
        PaletteStyle.Monochrome -> SchemeMonochrome(hct, isDark, contrastLevel)
        PaletteStyle.Fidelity -> SchemeFidelity(hct, isDark, contrastLevel)
        PaletteStyle.Content -> SchemeContent(hct, isDark, contrastLevel)
    }

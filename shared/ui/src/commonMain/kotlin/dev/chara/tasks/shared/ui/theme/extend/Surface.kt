package dev.chara.tasks.shared.ui.theme.extend

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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

val LocalSurfaceColors = staticCompositionLocalOf {
    dynamicSurfaceColors(Color(0xFF6750A4), false)
}

val ColorScheme.surfaceBright
    @Composable
    get() = LocalSurfaceColors.current.surfaceBright

val ColorScheme.surfaceContainer
    @Composable
    get() = LocalSurfaceColors.current.surfaceContainer

val ColorScheme.surfaceContainerHigh
    @Composable
    get() = LocalSurfaceColors.current.surfaceContainerHigh

val ColorScheme.surfaceContainerHighest
    @Composable
    get() = LocalSurfaceColors.current.surfaceContainerHighest

val ColorScheme.surfaceContainerLow
    @Composable
    get() = LocalSurfaceColors.current.surfaceContainerLow

val ColorScheme.surfaceContainerLowest
    @Composable
    get() = LocalSurfaceColors.current.surfaceContainerLowest

val ColorScheme.surfaceDim
    @Composable
    get() = LocalSurfaceColors.current.surfaceDim

@Composable
fun ExtendSurfaceColors(
    seedColor: Color,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
    content: @Composable () -> Unit
) {
    val surfaceColors: SurfaceColors by remember(seedColor, useDarkTheme, style, contrastLevel) {
        derivedStateOf {
            dynamicSurfaceColors(
                seedColor = seedColor,
                isDark = useDarkTheme,
                style = style,
                contrastLevel = contrastLevel,
            )
        }
    }

    CompositionLocalProvider(LocalSurfaceColors provides surfaceColors) {
        content()
    }
}

class SurfaceColors(
    surfaceBright: Color,
    surfaceContainer: Color,
    surfaceContainerHigh: Color,
    surfaceContainerHighest: Color,
    surfaceContainerLow: Color,
    surfaceContainerLowest: Color,
    surfaceDim: Color
) {
    var surfaceBright by mutableStateOf(surfaceBright, structuralEqualityPolicy())
        internal set
    var surfaceContainer by mutableStateOf(surfaceContainer, structuralEqualityPolicy())
        internal set
    var surfaceContainerHigh by mutableStateOf(
        surfaceContainerHigh,
        structuralEqualityPolicy()
    )
        internal set
    var surfaceContainerHighest by mutableStateOf(
        surfaceContainerHighest,
        structuralEqualityPolicy()
    )
        internal set
    var surfaceContainerLow by mutableStateOf(
        surfaceContainerLow,
        structuralEqualityPolicy()
    )
        internal set
    var surfaceContainerLowest by mutableStateOf(
        surfaceContainerLowest,
        structuralEqualityPolicy()
    )
        internal set
    var surfaceDim by mutableStateOf(surfaceDim, structuralEqualityPolicy())
        internal set

    fun copy(
        surfaceBright: Color = this.surfaceBright,
        surfaceContainer: Color = this.surfaceContainer,
        surfaceContainerHigh: Color = this.surfaceContainerHigh,
        surfaceContainerHighest: Color = this.surfaceContainerHighest,
        surfaceContainerLow: Color = this.surfaceContainerLow,
        surfaceContainerLowest: Color = this.surfaceContainerLowest,
        surfaceDim: Color = this.surfaceDim
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

fun dynamicSurfaceColors(
    seedColor: Color,
    isDark: Boolean,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
): SurfaceColors {
    val hct = Hct.fromInt(seedColor.toArgb())
    val colors = MaterialDynamicColors()
    val scheme = when (style) {
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

    return SurfaceColors(
        surfaceBright = Color(colors.surfaceBright().getArgb(scheme)),
        surfaceContainer = Color(colors.surfaceContainer().getArgb(scheme)),
        surfaceContainerHigh = Color(colors.surfaceContainerHigh().getArgb(scheme)),
        surfaceContainerHighest = Color(colors.surfaceContainerHighest().getArgb(scheme)),
        surfaceContainerLow = Color(colors.surfaceContainerLow().getArgb(scheme)),
        surfaceContainerLowest = Color(colors.surfaceContainerLowest().getArgb(scheme)),
        surfaceDim = Color(colors.surfaceDim().getArgb(scheme))
    )
}
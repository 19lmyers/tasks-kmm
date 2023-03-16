package dev.chara.tasks.android.ui.component.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.offset

/**
 * Adapted from M3 Scaffold.
 */
@Composable
fun SnackbarLayout(
    snackbarBottomOffset: Int,
    snackbar: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val contentWindowInsets = WindowInsets.systemBars

    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        layout(layoutWidth, layoutHeight) {
            val snackbarPlaceables = subcompose("Snackbar", snackbar).map {
                val leftInset = contentWindowInsets
                    .getLeft(this@SubcomposeLayout, layoutDirection)
                val rightInset = contentWindowInsets
                    .getRight(this@SubcomposeLayout, layoutDirection)
                val bottomInset = contentWindowInsets.getBottom(this@SubcomposeLayout)

                it.measure(
                    looseConstraints.offset(
                        -leftInset - rightInset,
                        -bottomInset
                    )
                )
            }

            val snackbarHeight = snackbarPlaceables.maxByOrNull { it.height }?.height ?: 0
            val snackbarWidth = snackbarPlaceables.maxByOrNull { it.width }?.width ?: 0

            val bottomInset = contentWindowInsets.getBottom(this@SubcomposeLayout)

            val snackbarOffsetFromBottom = if (snackbarHeight != 0) {
                snackbarHeight + snackbarBottomOffset + bottomInset
            } else {
                0
            }

            val bodyContentPlaceables = subcompose("Content") {
                content()
            }.map { it.measure(looseConstraints) }

            bodyContentPlaceables.forEach {
                it.place(0, 0)
            }

            snackbarPlaceables.forEach {
                it.place(
                    (layoutWidth - snackbarWidth) / 2 + contentWindowInsets.getLeft(
                        this@SubcomposeLayout,
                        layoutDirection
                    ),
                    layoutHeight - snackbarOffsetFromBottom
                )
            }
        }
    }
}
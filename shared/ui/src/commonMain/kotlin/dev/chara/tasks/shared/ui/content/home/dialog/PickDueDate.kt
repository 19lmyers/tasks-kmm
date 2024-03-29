package dev.chara.tasks.shared.ui.content.home.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHigh
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHighest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickDueDateDialog(onDismiss: () -> Unit, onConfirm: (LocalDateTime) -> Unit) {
    val localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                            .toLocalDateTime(TimeZone.UTC)
                    )
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text("OK")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            DatePicker(
                state = datePickerState,
                colors =
                    DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                dateValidator = {
                    it > localDateTime.date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@ExperimentalMaterial3Api
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    dismissButton: (@Composable () -> Unit)?,
    confirmButton: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(onDismissRequest = onDismissRequest, modifier = Modifier.wrapContentHeight()) {
        Surface(
            modifier = Modifier.requiredWidth(360.dp),
            shape = DatePickerDefaults.shape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest),
            tonalElevation = DatePickerDefaults.TonalElevation,
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                content()

                Box(modifier = Modifier.align(Alignment.End).padding(bottom = 8.dp, end = 6.dp)) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.primary
                    ) {
                        val textStyle = MaterialTheme.typography.labelLarge
                        ProvideTextStyle(value = textStyle) {
                            FlowRow {
                                dismissButton?.invoke()
                                confirmButton()
                            }
                        }
                    }
                }
            }
        }
    }
}

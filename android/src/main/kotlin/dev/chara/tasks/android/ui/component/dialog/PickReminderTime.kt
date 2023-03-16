package dev.chara.tasks.android.ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickReminderTimeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    Napier.d { initialHour.toString() }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )

    Napier.d { timePickerState.hour.toString() }

    var showAsTextEntry by remember { mutableStateOf(timePickerState.is24hour) }

    if (showAsTextEntry) {
        TimePickerDialog(
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            },
            switchLayoutButton = {
                IconButton(onClick = { showAsTextEntry = !showAsTextEntry }) {
                    Icon(Icons.Filled.Schedule, contentDescription = "Switch to picker")
                }
            }
        ) {
            TimeInput(
                modifier = Modifier.padding(24.dp),
                state = timePickerState
            )
        }
    } else {
        TimePickerDialog(
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            },
            switchLayoutButton = {
                IconButton(onClick = { showAsTextEntry = !showAsTextEntry }) {

                    Icon(Icons.Filled.Keyboard, contentDescription = "Switch to text entry")
                }
            }
        ) {
            TimePicker(
                modifier = Modifier.padding(24.dp),
                state = timePickerState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    switchLayoutButton: (@Composable () -> Unit)?,
    dismissButton: (@Composable () -> Unit)?,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = DatePickerDefaults.shape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = DatePickerDefaults.TonalElevation,
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                content()

                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(bottom = 8.dp, start = 6.dp)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.primary
                        ) {
                            val textStyle = MaterialTheme.typography.labelLarge
                            ProvideTextStyle(value = textStyle) {
                                switchLayoutButton?.invoke()
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(bottom = 8.dp, end = 6.dp),
                    ) {
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
}
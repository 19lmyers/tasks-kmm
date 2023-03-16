package dev.chara.tasks.android.ui.component.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.chara.tasks.util.time.FriendlyInstantFormatter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickReminderDateDialog(onDismiss: () -> Unit, onConfirm: (LocalDateTime) -> Unit) {
    val localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = localDateTime.date
            .atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds()
    )

    var selectedTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    var showTimePickerDialog by remember { mutableStateOf(false) }

    if (showTimePickerDialog) {
        PickReminderTimeDialog(
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { hour, minute ->
                selectedTime = hour to minute
                showTimePickerDialog = false
            },
            initialHour = selectedTime?.first ?: 0,
            initialMinute = selectedTime?.second ?: 0
        )
    }

    val context = LocalContext.current
    val formatter = FriendlyInstantFormatter(context)

    DatePickerDialog(
        onDismiss = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                            .plus(selectedTime!!.first.hours)
                            .plus(selectedTime!!.second.minutes)
                            .toLocalDateTime(TimeZone.UTC)
                    )
                },
                enabled = datePickerState.selectedDateMillis != null && selectedTime != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState, dateValidator = { epochMillis ->
            Instant.fromEpochMilliseconds(epochMillis)
                .toLocalDateTime(TimeZone.UTC).date >= Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
        })

        Divider()

        ListItem(
            modifier = Modifier
                .clickable {
                    showTimePickerDialog = true
                }
                .padding(horizontal = 8.dp),
            headlineContent = {
                if (selectedTime != null) {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, selectedTime!!.first)
                    calendar.set(Calendar.MINUTE, selectedTime!!.second)

                    val instant = calendar.toInstant().toKotlinInstant()

                    Text(formatter.formatDateTime(instant))
                } else {
                    Text("Set time")
                }
            },
            leadingContent = {
                Icon(Icons.Filled.Schedule, contentDescription = "Clock")
            }
        )

        Divider()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DatePickerDialog(
    onDismiss: () -> Unit,
    dismissButton: (@Composable () -> Unit)?,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.wrapContentHeight(),
    ) {
        Surface(
            modifier = Modifier
                .requiredWidth(360.dp),
            shape = DatePickerDefaults.shape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = DatePickerDefaults.TonalElevation,
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                content()

                Box(
                    modifier = Modifier
                        .align(Alignment.End)
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
package dev.chara.tasks.shared.ui.item

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.ui.model.icon
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.jvm.JvmName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListChip(modifier: Modifier = Modifier, list: TaskList, onClick: (TaskList) -> Unit) {
    InputChip(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .then(modifier),
        label = {
            Text(text = list.title)
        },
        leadingIcon = {
            Icon(list.icon.icon, contentDescription = "List")
        },
        selected = false,
        onClick = {
            onClick(list)
        }
    )
}

@JvmName("ListChipNullable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListChip(modifier: Modifier = Modifier, list: TaskList?, onClick: (TaskList?) -> Unit) {
    InputChip(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .then(modifier),
        label = {
            Text(text = list?.title ?: "No list selected")
        },
        leadingIcon = {
            if (list == null) {
                Icon(Icons.Filled.QuestionMark, contentDescription = "List")
            } else {
                Icon(list.icon.icon, contentDescription = "List")
            }
        },
        selected = false,
        onClick = {
            onClick(list)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderChip(
    reminderDate: Instant?,
    formatDateTime: (Instant) -> String,
    selectable: Boolean,
    withIcon: Boolean = true,
    onClick: () -> Unit
) {
    val currentTime = Clock.System.now()

    val chipColors = if (reminderDate != null && reminderDate < currentTime) {
        InputChipDefaults.inputChipColors(
            labelColor = MaterialTheme.colorScheme.error,
            leadingIconColor = MaterialTheme.colorScheme.error
        )
    } else {
        InputChipDefaults.inputChipColors()
    }

    InputChip(
        modifier = Modifier.padding(horizontal = 4.dp),
        label = {
            if (reminderDate != null) {
                Text(formatDateTime(reminderDate))
            } else {
                Text("Remind me")
            }
        },
        leadingIcon = {
            if (withIcon) {
                if (reminderDate != null) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Reminder")
                } else {
                    Icon(Icons.Filled.NotificationAdd, contentDescription = "Reminder")
                }
            }
        },
        trailingIcon = {
            if (selectable && reminderDate != null) {
                Icon(Icons.Filled.Clear, contentDescription = "Clear")
            }
        },
        selected = selectable && reminderDate != null,
        colors = chipColors,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DueDateChip(
    dueDate: Instant?,
    formatDate: (Instant) -> String,
    selectable: Boolean,
    withIcon: Boolean = true,
    onClick: () -> Unit
) {
    val currentTime = Clock.System.now()

    val chipColors = if (dueDate != null && dueDate < currentTime) {
        InputChipDefaults.inputChipColors(
            labelColor = MaterialTheme.colorScheme.error,
            leadingIconColor = MaterialTheme.colorScheme.error
        )
    } else {
        InputChipDefaults.inputChipColors()
    }

    InputChip(
        modifier = Modifier.padding(horizontal = 4.dp),
        label = {
            if (dueDate != null) {
                Text(formatDate(dueDate))
            } else {
                Text("Set due date")
            }
        },
        leadingIcon = {
            if (withIcon) {
                Icon(Icons.Filled.Event, contentDescription = "Due date")
            }
        },
        trailingIcon = {
            if (selectable && dueDate != null) {
                Icon(Icons.Filled.Clear, contentDescription = "Clear")
            }
        },
        selected = selectable && dueDate != null,
        colors = chipColors,
        onClick = onClick
    )
}
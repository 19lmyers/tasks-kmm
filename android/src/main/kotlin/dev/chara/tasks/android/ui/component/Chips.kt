package dev.chara.tasks.android.ui.component

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.model.vector
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.util.FriendlyDateFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

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
            Icon(list.icon.vector, contentDescription = "List")
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
                Icon(list.icon.vector, contentDescription = "List")
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
fun ReminderChip(reminderDate: Instant?, selectable: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val formatter = FriendlyDateFormat(context)
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
                Text(formatter.formatDateTime(reminderDate))
            } else {
                Text("Remind me")
            }
        },
        leadingIcon = {
            if (reminderDate != null) {
                Icon(Icons.Filled.Notifications, contentDescription = "Reminder")
            } else {
                Icon(Icons.Filled.NotificationAdd, contentDescription = "Reminder")
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
fun DueDateChip(dueDate: Instant?, selectable: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val formatter = FriendlyDateFormat(context)
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
                Text(formatter.formatDate(dueDate))
            } else {
                Text("Set due date")
            }
        },
        leadingIcon = {
            Icon(Icons.Filled.Event, contentDescription = "Due date")
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
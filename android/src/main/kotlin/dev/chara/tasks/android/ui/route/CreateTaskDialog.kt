package dev.chara.tasks.android.ui.route

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.model.vector
import dev.chara.tasks.android.ui.component.DueDateChip
import dev.chara.tasks.android.ui.component.ListChip
import dev.chara.tasks.android.ui.component.ReminderChip
import dev.chara.tasks.android.ui.component.dialog.PickDueDateDialog
import dev.chara.tasks.android.ui.component.dialog.PickReminderDateDialog
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Deprecated("todo replace with bottom sheet")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
)
@Composable
fun CreateTaskDialog(
    taskLists: List<TaskList>,
    current: Task,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var listId by remember { mutableStateOf<String?>(current.listId) }

    var label by remember { mutableStateOf(current.label) }

    var reminderDate by remember { mutableStateOf(current.reminderDate) }
    var dueDate by remember { mutableStateOf(current.dueDate) }

    var showReminderDateDialog by remember { mutableStateOf(false) }
    var showDueDatePickerDialog by remember { mutableStateOf(false) }

    val parentList by remember(listId) { mutableStateOf(taskLists.firstOrNull { it.id == listId }) }
    var showListDropdown by remember { mutableStateOf(false) }

    if (parentList == null) {
        listId = null
    }

    ColorTheme(color = parentList?.color) {
        if (showReminderDateDialog) {
            PickReminderDateDialog(
                onDismiss = {
                    showReminderDateDialog = false
                },
                onConfirm = { selectedDate ->
                    reminderDate = selectedDate.toInstant(TimeZone.currentSystemDefault())

                    showReminderDateDialog = false
                })
        }

        if (showDueDatePickerDialog) {
            PickDueDateDialog(
                onDismiss = {
                    showDueDatePickerDialog = false
                },
                onConfirm = { selectedDate ->
                    dueDate = selectedDate.toInstant(TimeZone.currentSystemDefault())

                    showDueDatePickerDialog = false
                }
            )
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "New item") },
            text = {
                Column {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        label = { Text(text = "Label") },
                        value = label,
                        onValueChange = { label = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (label.isNotBlank()) {
                                    keyboardController?.hide()
                                    onSave(
                                        current.copy(
                                            label = label,
                                            lastModified = Clock.System.now()
                                        )
                                    )
                                }
                            }
                        ),
                        trailingIcon = {
                            IconButton(onClick = { label = "" }) {
                                Icon(imageVector = Icons.Filled.Clear, "Clear")
                            }
                        }
                    )

                    ListChip(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        list = parentList,
                        onClick = { showListDropdown = true })

                    DropdownMenu(
                        expanded = showListDropdown,
                        onDismissRequest = { showListDropdown = false },
                    ) {
                        for (taskList in taskLists) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        taskList.icon.vector,
                                        contentDescription = "List"
                                    )
                                },
                                text = { Text(taskList.title) },
                                onClick = {
                                    showListDropdown = false
                                    listId = taskList.id
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        ReminderChip(reminderDate, selectable = true) {
                            if (reminderDate != null) {
                                reminderDate = null
                            } else {
                                showReminderDateDialog = true
                            }
                        }

                        DueDateChip(dueDate = dueDate, selectable = true) {
                            if (dueDate != null) {
                                dueDate = null
                            } else {
                                showDueDatePickerDialog = true
                            }
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        onSave(
                            current.copy(
                                listId = listId!!,
                                label = label,
                                reminderDate = reminderDate,
                                dueDate = dueDate
                            )
                        )
                    },
                    enabled = listId != null && label.isNotBlank()
                ) {
                    Text("Create")
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview_CreateTaskDialog() {
    CreateTaskDialog(
        taskLists = listOf(
            TaskList(
                id = "1",
                title = "My Tasks"
            )
        ),
        current = Task(
            id = "1",
            listId = "1",
            label = "Take out trash",
        ),
        onDismiss = {},
        onSave = {}
    )
}
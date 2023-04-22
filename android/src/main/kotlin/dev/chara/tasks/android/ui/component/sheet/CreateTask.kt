package dev.chara.tasks.android.ui.component.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.model.vector
import dev.chara.tasks.android.ui.component.DueDateChip
import dev.chara.tasks.android.ui.component.ReminderChip
import dev.chara.tasks.android.ui.component.dialog.PickDueDateDialog
import dev.chara.tasks.android.ui.component.dialog.PickReminderDateDialog
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskSheet(
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

    var showReminderDatePickerDialog by remember { mutableStateOf(false) }
    var showDueDatePickerDialog by remember { mutableStateOf(false) }

    val parentList by remember(listId) { mutableStateOf(taskLists.firstOrNull { it.id == listId }) }
    var listsExpanded by remember { mutableStateOf(false) }

    if (parentList == null) {
        listId = null
    }

    val sheetState = rememberModalBottomSheetState()

    ColorTheme(color = parentList?.color) {
        if (showReminderDatePickerDialog) {
            PickReminderDateDialog(
                onDismiss = {
                    showReminderDatePickerDialog = false
                },
                onConfirm = { selectedDate ->
                    reminderDate = selectedDate.toInstant(TimeZone.currentSystemDefault())

                    showReminderDatePickerDialog = false
                }
            )
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

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            Text(
                "New task",
                modifier = Modifier.padding(16.dp, 0.dp),
                style = MaterialTheme.typography.titleLarge
            )

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
                                    listId = listId!!,
                                    label = label,
                                    reminderDate = reminderDate,
                                    dueDate = dueDate,
                                    lastModified = Clock.System.now()
                                )
                            )
                        }
                    }
                )
            )

            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                expanded = listsExpanded,
                onExpandedChange = { listsExpanded = !listsExpanded },
            ) {
                val taskList = taskLists.first { it.id == listId }

                OutlinedTextField(
                    value = taskList.title,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    leadingIcon = {
                        Icon(
                            taskList.icon.vector,
                            contentDescription = "List"
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = listsExpanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = listsExpanded,
                    onDismissRequest = {
                        listsExpanded = false
                    },
                ) {
                    for (list in taskLists) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    list.icon.vector,
                                    contentDescription = "List"
                                )
                            },
                            text = { Text(list.title) },
                            onClick = {
                                listId = list.id
                                listsExpanded = false
                            }
                        )
                    }
                }
            }

            ListItem(
                modifier = Modifier.clickable {
                    showReminderDatePickerDialog = true
                },
                headlineContent = {
                    Text("Remind me")
                },
                leadingContent = {
                    if (reminderDate != null) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Reminder")
                    } else {
                        Icon(Icons.Filled.NotificationAdd, contentDescription = "Reminder")
                    }
                },
                trailingContent = {
                    if (reminderDate != null) {
                        ReminderChip(reminderDate, selectable = true, withIcon = false) {
                            reminderDate = null
                        }
                    }
                }
            )

            ListItem(
                modifier = Modifier.clickable {
                    showDueDatePickerDialog = true
                },
                headlineContent = {
                    Text("Set due date")
                },
                leadingContent = {
                    Icon(Icons.Filled.Event, contentDescription = "Remind me")
                },
                trailingContent = {
                    if (dueDate != null) {
                        DueDateChip(dueDate, selectable = true, withIcon = false) {
                            dueDate = null
                        }
                    }
                }
            )

            FilledTonalButton(
                modifier = Modifier
                    .padding(16.dp, 0.dp)
                    .align(Alignment.End),
                onClick = {
                    keyboardController?.hide()
                    onSave(
                        current.copy(
                            listId = listId!!,
                            label = label,
                            reminderDate = reminderDate,
                            dueDate = dueDate,
                            lastModified = Clock.System.now()
                        )
                    )
                },
                enabled = listId != null && label.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }

    // We need to do this explicitly to show the sheet on shortcut launch
    LaunchedEffect(sheetState) {
        sheetState.show()
    }
}

@Preview
@Composable
private fun Preview_CreateTaskSheet() {
    CreateTaskSheet(
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
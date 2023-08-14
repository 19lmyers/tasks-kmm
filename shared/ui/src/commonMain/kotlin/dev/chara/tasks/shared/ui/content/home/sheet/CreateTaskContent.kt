package dev.chara.tasks.shared.ui.content.home.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import com.androidx.material3.polyfill.ExposedDropdownMenuBox
import com.androidx.material3.polyfill.ExposedDropdownMenuDefaults
import com.androidx.material3.polyfill.ModalBottomSheet
import com.androidx.material3.polyfill.rememberModalBottomSheetState
import dev.chara.tasks.shared.component.home.create_task.CreateTaskComponent
import dev.chara.tasks.shared.domain.FriendlyDateFormatter
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.ui.content.home.dialog.PickDueDateDialog
import dev.chara.tasks.shared.ui.content.home.dialog.PickReminderDateDialog
import dev.chara.tasks.shared.ui.item.DueDateChip
import dev.chara.tasks.shared.ui.item.ReminderChip
import dev.chara.tasks.shared.ui.model.icon
import dev.chara.tasks.shared.ui.theme.ColorTheme
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHigh
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskContent(component: CreateTaskComponent) {
    val state = component.state.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    var listId by remember(component.defaultListId) { mutableStateOf(component.defaultListId) }

    var label by remember { mutableStateOf("") }

    var reminderDate by remember { mutableStateOf<Instant?>(null) }
    var dueDate by remember { mutableStateOf<Instant?>(null) }

    var showReminderDatePickerDialog by remember { mutableStateOf(false) }
    var showDueDatePickerDialog by remember { mutableStateOf(false) }

    val parentList by
        remember(state.value.allLists, listId) {
            derivedStateOf {
                state.value.allLists.firstOrNull { it.id == listId }
                    ?: state.value.allLists.firstOrNull()
            }
        }
    var listsExpanded by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    val dateFormatter: FriendlyDateFormatter = koinInject()

    ColorTheme(color = parentList?.color) {
        if (showReminderDatePickerDialog) {
            PickReminderDateDialog(
                onDismiss = { showReminderDatePickerDialog = false },
                onConfirm = { selectedDate ->
                    reminderDate = selectedDate.toInstant(TimeZone.currentSystemDefault())

                    showReminderDatePickerDialog = false
                }
            )
        }

        if (showDueDatePickerDialog) {
            PickDueDateDialog(
                onDismiss = { showDueDatePickerDialog = false },
                onConfirm = { selectedDate ->
                    dueDate = selectedDate.toInstant(TimeZone.currentSystemDefault())

                    showDueDatePickerDialog = false
                }
            )
        }

        ModalBottomSheet(
            onDismissRequest = { component.onDismiss() },
            sheetState = sheetState,
            windowInsets = WindowInsets.ime
        ) {
            Text(
                "New task",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )

            if (state.value.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }

            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                label = { Text(text = "Label") },
                value = label,
                onValueChange = { label = it },
                readOnly = state.value.isLoading,
                singleLine = true,
                keyboardOptions =
                    KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            if (
                                !state.value.isLoading && parentList != null && label.isNotBlank()
                            ) {
                                keyboardController?.hide()
                                component.onSave(
                                    Task(
                                        id = "",
                                        listId = parentList!!.id,
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
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                expanded = !state.value.isLoading && listsExpanded,
                onExpandedChange = { listsExpanded = it },
            ) {
                OutlinedTextField(
                    value = parentList?.title ?: "No list selected",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    leadingIcon = { Icon(parentList?.icon.icon, contentDescription = "List") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = listsExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = !state.value.isLoading && listsExpanded,
                    onDismissRequest = { listsExpanded = false },
                ) {
                    for (list in state.value.allLists) {
                        DropdownMenuItem(
                            leadingIcon = { Icon(list.icon.icon, contentDescription = "List") },
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
                colors =
                    ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                modifier =
                    Modifier.clickable(enabled = !state.value.isLoading) {
                        showReminderDatePickerDialog = true
                    },
                headlineContent = { Text("Remind me") },
                leadingContent = {
                    if (reminderDate != null) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Reminder")
                    } else {
                        Icon(Icons.Filled.NotificationAdd, contentDescription = "Reminder")
                    }
                },
                trailingContent = {
                    if (reminderDate != null) {
                        ReminderChip(
                            reminderDate,
                            formatDateTime = { dateFormatter.formatDateTime(it) },
                            selectable = true,
                            withIcon = false
                        ) {
                            reminderDate = null
                        }
                    }
                }
            )

            ListItem(
                colors =
                    ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                modifier =
                    Modifier.clickable(enabled = !state.value.isLoading) {
                        showDueDatePickerDialog = true
                    },
                headlineContent = { Text("Set due date") },
                leadingContent = { Icon(Icons.Filled.Event, contentDescription = "Remind me") },
                trailingContent = {
                    if (dueDate != null) {
                        DueDateChip(
                            dueDate,
                            formatDate = { dateFormatter.formatDate(it) },
                            selectable = true,
                            withIcon = false
                        ) {
                            dueDate = null
                        }
                    }
                }
            )

            FilledTonalButton(
                modifier =
                    Modifier.padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .align(Alignment.End),
                onClick = {
                    keyboardController?.hide()
                    component.onSave(
                        Task(
                            id = "",
                            listId = parentList!!.id,
                            label = label,
                            reminderDate = reminderDate,
                            dueDate = dueDate,
                            lastModified = Clock.System.now()
                        )
                    )
                },
                enabled = !state.value.isLoading && parentList != null && label.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}

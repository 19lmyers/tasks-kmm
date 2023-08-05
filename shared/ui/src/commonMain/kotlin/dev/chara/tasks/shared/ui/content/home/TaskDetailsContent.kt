package dev.chara.tasks.shared.ui.content.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.androidx.material3.polyfill.AlertDialog
import com.androidx.material3.polyfill.DropdownMenu
import com.androidx.material3.polyfill.DropdownMenuItem
import dev.chara.tasks.shared.component.home.task_details.TaskDetailsComponent
import dev.chara.tasks.shared.domain.FriendlyDateFormatter
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.ui.content.home.dialog.PickDueDateDialog
import dev.chara.tasks.shared.ui.content.home.dialog.PickReminderDateDialog
import dev.chara.tasks.shared.ui.item.DueDateChip
import dev.chara.tasks.shared.ui.item.ReminderChip
import dev.chara.tasks.shared.ui.model.icon
import dev.chara.tasks.shared.ui.theme.ColorTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskDetailsContent(component: TaskDetailsComponent) {
    val state = component.state.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var task by remember(state.value.selectedTask) { mutableStateOf(state.value.selectedTask) }
    var modified by remember { mutableStateOf(false) }

    val parentList by
        remember(state.value.allLists, task?.listId) {
            derivedStateOf { state.value.allLists.firstOrNull { it.id == task?.listId } }
        }

    val scrollState = rememberScrollState()

    ColorTheme(color = parentList?.color) {
        var showDeleteDialog by remember { mutableStateOf(false) }

        if (showDeleteDialog) {
            DeleteTaskDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    component.deleteTask(task!!)
                }
            )
        }

        if (state.value.showConfirmExit) {
            ConfirmExitDialog(
                onDismiss = { component.setShowConfirmExit(false) },
                onConfirm = {
                    component.setShowConfirmExit(false)
                    component.onUp()
                }
            )
        }

        Scaffold(
            topBar = {
                TopBarWithListSelector(
                    task = task!!,
                    taskLists = state.value.allLists,
                    selectedListId = task!!.listId,
                    scrollBehavior = scrollBehavior,
                    upAsCloseButton = true,
                    onUpClicked = { component.setShowConfirmExit(true) },
                    onDeleteClicked = { showDeleteDialog = true },
                    onListSelected = {
                        component.updateTask(task!!)
                        component.moveTask(task!!.listId, it, task!!.id)
                        modified = false
                    },
                    onUpdateTask = {
                        task = it
                        modified = true
                    }
                )
            },
            bottomBar = {
                BottomAppBar(modifier = Modifier.padding(WindowInsets.ime.asPaddingValues())) {
                    Spacer(Modifier.weight(1f, true))

                    Button(
                        modifier = Modifier.padding(16.dp, 0.dp),
                        onClick = {
                            component.updateTask(task!!)
                            modified = false
                            component.onUp()
                        },
                        enabled =
                            !state.value.isLoading && task?.label?.isNotBlank() == true && modified
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier =
                    Modifier.padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.verticalScroll(scrollState)
                ) {
                    TaskDetailsForm(task!!) {
                        task = it
                        modified = true
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarWithListSelector(
    task: Task,
    taskLists: List<TaskList>,
    selectedListId: String,
    scrollBehavior: TopAppBarScrollBehavior,
    upAsCloseButton: Boolean,
    onUpClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onListSelected: (String) -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    TopAppBar(
        title = {
            ListSelector(
                taskLists = taskLists,
                selectedListId = selectedListId,
                onListClicked = onListSelected
            )
        },
        navigationIcon = {
            IconButton(onClick = { onUpClicked() }) {
                if (upAsCloseButton) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Navigate up",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            IconToggleButton(
                checked = task.isStarred,
                onCheckedChange = { isStarred ->
                    onUpdateTask(
                        task.copy(isStarred = isStarred, lastModified = Clock.System.now())
                    )
                }
            ) {
                if (task.isStarred) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Unstar task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        Icons.Filled.StarOutline,
                        contentDescription = "Star task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = { onDeleteClicked() }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ListSelector(
    taskLists: List<TaskList>,
    selectedListId: String,
    onListClicked: (String) -> Unit
) {
    var showLists by remember { mutableStateOf(false) }

    Row(modifier = Modifier.clickable { showLists = !showLists }) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = taskLists.first { it.id == selectedListId }.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        IconButton(onClick = { showLists = !showLists }) {
            if (showLists) {
                Icon(
                    Icons.Filled.ExpandLess,
                    contentDescription = "Hide lists",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    Icons.Filled.ExpandMore,
                    contentDescription = "Show lists",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    DropdownMenu(
        expanded = showLists,
        onDismissRequest = { showLists = false },
    ) {
        for (taskList in taskLists) {
            DropdownMenuItem(
                leadingIcon = { Icon(taskList.icon.icon, contentDescription = "List") },
                text = { Text(taskList.title) },
                onClick = {
                    showLists = false
                    onListClicked(taskList.id)
                }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TaskDetailsForm(
    task: Task,
    onUpdateTask: (Task) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val dateFormatter: FriendlyDateFormatter = koinInject()

    var showReminderDateDialog by remember { mutableStateOf(false) }
    var showDueDatePickerDialog by remember { mutableStateOf(false) }

    if (showReminderDateDialog) {
        PickReminderDateDialog(
            onDismiss = { showReminderDateDialog = false },
            onConfirm = { selectedDate ->
                onUpdateTask(
                    task.copy(
                        reminderDate = selectedDate.toInstant(TimeZone.currentSystemDefault()),
                        lastModified = Clock.System.now()
                    )
                )

                showReminderDateDialog = false
            }
        )
    }

    if (showDueDatePickerDialog) {
        PickDueDateDialog(
            onDismiss = { showDueDatePickerDialog = false },
            onConfirm = { selectedDate ->
                onUpdateTask(
                    task.copy(
                        dueDate = selectedDate.toInstant(TimeZone.currentSystemDefault()),
                        lastModified = Clock.System.now()
                    )
                )

                showDueDatePickerDialog = false
            }
        )
    }

    Row(modifier = Modifier.padding(horizontal = 4.dp)) {
        Checkbox(
            modifier = Modifier.align(Alignment.CenterVertically),
            checked = task.isCompleted,
            onCheckedChange = { isChecked -> onUpdateTask(task.copy(isCompleted = isChecked)) }
        )

        BasicTextField(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 2.dp).fillMaxWidth(),
            value = task.label,
            onValueChange = {
                onUpdateTask(task.copy(label = it, lastModified = Clock.System.now()))
            },
            singleLine = false,
            textStyle =
                MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                if (task.label.isEmpty()) {
                    Text(
                        text = "Enter label",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                innerTextField()
            },
            keyboardOptions =
                KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
    }

    ListItem(
        headlineContent = {
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = task.details ?: "",
                onValueChange = {
                    if (it.isEmpty()) return@BasicTextField
                    onUpdateTask(task.copy(details = it, lastModified = Clock.System.now()))
                },
                singleLine = false,
                textStyle =
                    MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (task.details.isNullOrEmpty()) {
                        Text(
                            text = "Add details",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                    innerTextField()
                },
                keyboardOptions =
                    KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
        },
        leadingContent = { Icon(Icons.Filled.Notes, contentDescription = "Details") },
        trailingContent = {
            if (task.details != null) {
                IconButton(
                    onClick = {
                        onUpdateTask(task.copy(details = null, lastModified = Clock.System.now()))
                    }
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
                }
            }
        },
    )

    val currentTime = Clock.System.now()

    val reminderColors =
        if (task.reminderDate == null) {
            ListItemDefaults.colors(
                headlineColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
        } else if (task.reminderDate!! < currentTime) {
            ListItemDefaults.colors(
                headlineColor = MaterialTheme.colorScheme.error,
                leadingIconColor = MaterialTheme.colorScheme.error
            )
        } else {
            ListItemDefaults.colors()
        }

    ListItem(
        headlineContent = { Text("Remind me") },
        leadingContent = {
            if (task.reminderDate != null) {
                Icon(Icons.Filled.Notifications, contentDescription = "Reminder")
            } else {
                Icon(Icons.Filled.NotificationAdd, contentDescription = "Reminder")
            }
        },
        trailingContent = {
            if (task.reminderDate != null) {
                ReminderChip(
                    task.reminderDate,
                    formatDateTime = { dateFormatter.formatDateTime(it) },
                    selectable = true,
                    withIcon = false
                ) {
                    onUpdateTask(task.copy(reminderDate = null, lastModified = Clock.System.now()))
                }
            }
        },
        colors = reminderColors,
        modifier = Modifier.fillMaxWidth().clickable { showReminderDateDialog = true }
    )

    val dueDateColors =
        if (task.dueDate == null) {
            ListItemDefaults.colors(
                headlineColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
        } else if (task.dueDate!! < currentTime) {
            ListItemDefaults.colors(
                headlineColor = MaterialTheme.colorScheme.error,
                leadingIconColor = MaterialTheme.colorScheme.error
            )
        } else {
            ListItemDefaults.colors()
        }

    ListItem(
        headlineContent = { Text("Set due date") },
        leadingContent = { Icon(Icons.Filled.Event, contentDescription = "Due date") },
        trailingContent = {
            if (task.dueDate != null) {
                DueDateChip(
                    task.dueDate,
                    formatDate = { dateFormatter.formatDate(it) },
                    selectable = true,
                    withIcon = false
                ) {
                    onUpdateTask(task.copy(dueDate = null, lastModified = Clock.System.now()))
                }
            }
        },
        colors = dueDateColors,
        modifier = Modifier.fillMaxWidth().clickable { showDueDatePickerDialog = true }
    )
}

@Composable
private fun ConfirmExitDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Close without saving?") },
        text = { Text(text = "Your changes to this task will not be saved") },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Close") } }
    )
}

@Composable
private fun DeleteTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete task?") },
        text = { Text(text = "This task will be permanently deleted") },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Delete") } }
    )
}

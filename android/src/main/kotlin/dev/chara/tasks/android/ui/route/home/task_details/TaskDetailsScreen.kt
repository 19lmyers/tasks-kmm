package dev.chara.tasks.android.ui.route.home.task_details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.model.vector
import dev.chara.tasks.android.ui.component.DueDateChip
import dev.chara.tasks.android.ui.component.ReminderChip
import dev.chara.tasks.android.ui.component.dialog.PickDueDateDialog
import dev.chara.tasks.android.ui.component.dialog.PickReminderDateDialog
import dev.chara.tasks.android.ui.util.FriendlyDateFormat
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.task_details.TaskDetailsUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskDetailsScreen(
    state: TaskDetailsUiState,
    snackbarHostState: SnackbarHostState,
    onUpClicked: (Boolean) -> Unit,
    onDeleteClicked: () -> Unit,
    onUpdateTask: (Task) -> Unit,
    onMoveTask: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var task by remember(state.task) { mutableStateOf(state.task!!) }
    var modified by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBarWithListSelector(
                task = task,
                taskLists = state.taskLists,
                selectedListId = task.listId,
                scrollBehavior = scrollBehavior,
                upAsCloseButton = true,
                onUpClicked = { onUpClicked(modified) },
                onDeleteClicked = { onDeleteClicked() },
                onListSelected = {
                    onUpdateTask(task)
                    onMoveTask(it)
                    modified = false
                },
                onUpdateTask = {
                    task = it
                    modified = true
                }
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.imePadding()) {
                Spacer(Modifier.weight(1f, true))

                Button(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    onClick = {
                        onUpdateTask(task)
                        modified = false
                        onUpClicked(false)
                    },
                    enabled = !state.isLoading && task.label.isNotBlank() && modified
                ) {
                    Text(text = "Save")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TaskDetailsForm(task) {
                    task = it
                    modified = true
                }
            }
        }
    }

    BackHandler {
        onUpClicked(modified)
    }
}

@Preview
@Composable
private fun Preview_TaskDetailsScreen() {
    TaskDetailsScreen(
        state = TaskDetailsUiState(
            taskLists = listOf(
                TaskList(id = "1", title = "Tasks")
            ),
            task = Task(
                id = "1",
                listId = "1",
                label = "Take out trash",
                details = "It needed to be done yesterday but I waited until today",
            ),
        ),
        snackbarHostState = SnackbarHostState(),
        onUpClicked = {},
        onDeleteClicked = {},
        onUpdateTask = {},
        onMoveTask = {}
    )
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
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                } else {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Navigate up")
                }
            }
        },
        actions = {
            IconToggleButton(
                checked = task.isStarred,
                onCheckedChange = { isStarred ->
                    onUpdateTask(
                        task.copy(
                            isStarred = isStarred,
                            lastModified = Clock.System.now()
                        )
                    )
                }
            ) {
                if (task.isStarred) {
                    Icon(Icons.Filled.Star, contentDescription = "Unstar task")
                } else {
                    Icon(Icons.Filled.StarOutline, contentDescription = "Star task")
                }
            }
            IconButton(onClick = { onDeleteClicked() }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview_TopBarWithListSelector() {
    TopBarWithListSelector(
        task = Task("1", "1", "Take out trash"),
        taskLists = listOf(TaskList("1", "Tasks")),
        selectedListId = "1",
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        upAsCloseButton = false,
        onUpClicked = {},
        onDeleteClicked = {},
        onListSelected = {},
        onUpdateTask = {}
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
                Icon(Icons.Filled.ExpandLess, contentDescription = "Hide lists")
            } else {
                Icon(Icons.Filled.ExpandMore, contentDescription = "Show lists")
            }
        }
    }

    DropdownMenu(
        expanded = showLists,
        onDismissRequest = { showLists = false },
    ) {
        for (taskList in taskLists) {
            DropdownMenuItem(
                leadingIcon = { Icon(taskList.icon.vector, contentDescription = "List") },
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
    val context = LocalContext.current

    var showReminderDateDialog by remember { mutableStateOf(false) }
    var showDueDatePickerDialog by remember { mutableStateOf(false) }

    if (showReminderDateDialog) {
        PickReminderDateDialog(
            onDismiss = {
                showReminderDateDialog = false
            },
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
            onDismiss = {
                showDueDatePickerDialog = false
            },
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
            onCheckedChange = { isChecked ->
                onUpdateTask(task.copy(isCompleted = isChecked))
            }
        )

        BasicTextField(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 2.dp)
                .fillMaxWidth(),
            value = task.label,
            onValueChange = {
                onUpdateTask(
                    task.copy(
                        label = it,
                        lastModified = Clock.System.now()
                    )
                )
            },
            singleLine = false,
            textStyle = MaterialTheme.typography.titleLarge
                .copy(color = MaterialTheme.colorScheme.onBackground),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                if (task.label.isEmpty()) {
                    Text(
                        text = "Enter label",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                innerTextField()
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
    }

    ListItem(
        headlineContent = {
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = task.details ?: "",
                onValueChange = {
                    if (it.isEmpty()) return@BasicTextField
                    onUpdateTask(
                        task.copy(
                            details = it,
                            lastModified = Clock.System.now()
                        )
                    )
                },
                singleLine = false,
                textStyle = MaterialTheme.typography.bodyLarge
                    .copy(color = MaterialTheme.colorScheme.onBackground),
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
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
        },
        leadingContent = {
            Icon(Icons.Filled.Notes, contentDescription = "Details")
        },
        trailingContent = {
            if (task.details != null) {
                IconButton(
                    onClick = {
                        onUpdateTask(
                            task.copy(
                                details = null,
                                lastModified = Clock.System.now()
                            )
                        )
                    }
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
                }
            }
        },
    )

    val formatter = FriendlyDateFormat(context)
    val currentTime = Clock.System.now()

    val reminderColors = if (task.reminderDate == null) {
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
        headlineContent = {
            Text("Remind me")
        },
        leadingContent = {
            if (task.reminderDate != null) {
                Icon(Icons.Filled.Notifications, contentDescription = "Reminder")
            } else {
                Icon(Icons.Filled.NotificationAdd, contentDescription = "Reminder")
            }
        },
        trailingContent = {
            if (task.reminderDate != null) {
                ReminderChip(task.reminderDate, selectable = true, withIcon = false) {
                    onUpdateTask(
                        task.copy(
                            reminderDate = null,
                            lastModified = Clock.System.now()
                        )
                    )
                }
            }
        },
        colors = reminderColors,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showReminderDateDialog = true
            }
    )

    val dueDateColors = if (task.dueDate == null) {
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
        headlineContent = {
            Text("Set due date")
        },
        leadingContent = {
            Icon(Icons.Filled.Event, contentDescription = "Due date")
        },
        trailingContent = {
            if (task.dueDate != null) {
                DueDateChip(task.dueDate, selectable = true, withIcon = false) {
                    onUpdateTask(
                        task.copy(
                            dueDate = null,
                            lastModified = Clock.System.now()
                        )
                    )
                }
            }
        },
        colors = dueDateColors,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDueDatePickerDialog = true
            }
    )
}
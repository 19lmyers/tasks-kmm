package dev.chara.tasks.android.ui.route.home.task_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.chara.tasks.android.model.vector
import dev.chara.tasks.android.ui.component.dialog.PickDueDateDialog
import dev.chara.tasks.android.ui.component.dialog.PickReminderDateDialog
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.util.time.FriendlyInstantFormatter
import dev.chara.tasks.viewmodel.home.task_details.TaskDetailsUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskDetailsScreen(
    state: TaskDetailsUiState,
    snackbarHostState: SnackbarHostState,
    upAsCloseButton: Boolean,
    onUpClicked: () -> Unit,
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
                upAsCloseButton = upAsCloseButton,
                onUpClicked = { onUpClicked() },
                onDeleteClicked = { onDeleteClicked() },
                onListSelected = {
                    onUpdateTask(
                        task.copy(
                            lastModified = Clock.System.now()
                        )
                    )
                    onMoveTask(it)
                },
                onUpdateTask = {
                    task = it
                    modified = true
                }
            )
        },
        bottomBar = {
            BottomBar(
                task = task,
                onUpdateTask = {
                    task = it
                    modified = true
                },
                onNavigateUp = {
                    onUpClicked()
                }
            )
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

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if ((event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) && modified) {
                onUpdateTask(
                    task.copy(
                        lastModified = Clock.System.now()
                    )
                )
                modified = false
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            if (modified) {
                onUpdateTask(
                    task.copy(
                        lastModified = Clock.System.now()
                    )
                )
                modified = false
            }

            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
        upAsCloseButton = false,
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
private fun BottomBar(
    task: Task,
    onUpdateTask: (Task) -> Unit,
    onNavigateUp: () -> Unit
) {
    BottomAppBar(
        actions = {},
        floatingActionButton = {
            ExtendedFloatingActionButton(
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                onClick = {
                    val isCompleted = !task.isCompleted

                    onUpdateTask(
                        task.copy(
                            isCompleted = isCompleted,
                            lastModified = Clock.System.now()
                        )
                    )
                    if (isCompleted) {
                        onNavigateUp()
                    }
                },
                icon = {
                    Icon(Icons.Filled.Check, "Check")
                },
                text = {
                    if (task.isCompleted) {
                        Text(text = "Mark as incomplete")
                    } else {
                        Text(text = "Mark as complete")
                    }
                }
            )
        }
    )
}

@Preview
@Composable
private fun Preview_BottomBar() {
    BottomBar(
        task = Task("1", "1", "Take out trash"),
        onUpdateTask = {},
        onNavigateUp = {}
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

    BasicTextField(
        modifier = Modifier
            .padding(16.dp)
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

    val formatter = FriendlyInstantFormatter(context)
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
            if (task.reminderDate != null) {
                Text(formatter.formatDateTime(task.reminderDate!!))
            } else {
                Text("Remind me")
            }
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
                IconButton(
                    onClick = {
                        onUpdateTask(
                            task.copy(
                                reminderDate = null,
                                lastModified = Clock.System.now()
                            )
                        )
                    }
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
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
            if (task.dueDate != null) {
                Text(formatter.formatDate(task.dueDate!!))
            } else {
                Text("Set due date")
            }
        },
        leadingContent = {
            Icon(Icons.Filled.Event, contentDescription = "Remind me")
        },
        trailingContent = {
            if (task.dueDate != null) {
                IconButton(
                    onClick = {
                        onUpdateTask(
                            task.copy(
                                dueDate = null,
                                lastModified = Clock.System.now()
                            )
                        )
                    }
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
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
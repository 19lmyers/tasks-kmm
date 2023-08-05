package dev.chara.tasks.shared.ui.content.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.androidx.material3.polyfill.AlertDialog
import com.androidx.material3.polyfill.DropdownMenu
import com.androidx.material3.polyfill.DropdownMenuItem
import dev.chara.tasks.shared.component.home.list_details.ListDetailsComponent
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.ui.item.Tasks
import dev.chara.tasks.shared.ui.model.icon
import dev.chara.tasks.shared.ui.theme.ColorTheme
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHighest
import kotlinx.datetime.Clock

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun ListDetailsContent(component: ListDetailsComponent, upAsCloseButton: Boolean) {
    val state = component.state.collectAsState()

    var showOverflowMenu by remember { mutableStateOf(false) }

    val pullRefreshState =
        rememberPullRefreshState(state.value.isRefreshing, { component.onRefresh() })

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    ColorTheme(color = state.value.selectedList?.color) {
        var showDeleteListDialog by remember { mutableStateOf(false) }

        if (showDeleteListDialog) {
            DeleteListDialog(
                onDismiss = { showDeleteListDialog = false },
                onConfirm = {
                    showDeleteListDialog = false
                    component.deleteList(state.value.selectedList!!.id)
                }
            )
        }

        var showDeleteCompletedTasksDialog by remember { mutableStateOf(false) }

        if (showDeleteCompletedTasksDialog) {
            DeleteCompletedTasksDialog(
                onDismiss = { showDeleteCompletedTasksDialog = false },
                onConfirm = {
                    component.clearCompletedTasks(state.value.selectedList!!.id)
                    showDeleteCompletedTasksDialog = false
                }
            )
        }

        var showSortDialog by remember { mutableStateOf(false) }

        if (showSortDialog) {
            SortListDialog(
                sortType = state.value.selectedList!!.sortType,
                onDismiss = { showSortDialog = false },
                onSelect = { sortType ->
                    component.updateList(
                        state.value.selectedList!!.copy(
                            sortType = sortType,
                            lastModified = Clock.System.now()
                        )
                    )
                    showSortDialog = false
                }
            )
        }

        Scaffold(
            topBar = {
                TopBar(
                    scrollBehavior,
                    state.value.selectedList?.title,
                    upAsCloseButton = upAsCloseButton,
                    onUpClicked = { component.onUp() },
                    enableActions = !state.value.isLoading,
                    onEditClicked = { component.onEditClicked(state.value.selectedList!!.id) },
                    showOverflowMenu = showOverflowMenu,
                    setOverflowShown = { showOverflowMenu = it },
                    onDeleteTasksClicked = { showDeleteCompletedTasksDialog = true },
                    onDeleteListClicked = { showDeleteListDialog = true }
                )
            },
            bottomBar = {
                BottomBar(
                    sortType = state.value.selectedList?.sortType,
                    sortDirection = state.value.selectedList?.sortDirection,
                    onSortTypeClicked = { showSortDialog = true },
                    onSortDirectionClicked = {
                        component.updateList(
                            state.value.selectedList!!.copy(
                                sortDirection =
                                    if (
                                        state.value.selectedList!!.sortDirection ==
                                            TaskList.SortDirection.ASCENDING
                                    ) {
                                        TaskList.SortDirection.DESCENDING
                                    } else {
                                        TaskList.SortDirection.ASCENDING
                                    },
                                lastModified = Clock.System.now()
                            )
                        )
                    },
                    enableActions = !state.value.isLoading,
                    onCreateClicked = { component.onCreateTask() }
                )
            },
            content = { innerPadding ->
                Box(
                    modifier =
                        Modifier.padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .pullRefresh(pullRefreshState)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (state.value.isLoading) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else if (
                            state.value.currentTasks.isEmpty() &&
                                state.value.completedTasks.isEmpty()
                        ) {
                            TaskPlaceholder()
                        } else {
                            Tasks(
                                currentTasks = state.value.currentTasks,
                                completedTasks = state.value.completedTasks,
                                onClick = { task -> component.onTaskClicked(task.id) },
                                onUpdate = { task -> component.updateTask(task) },
                                allowReorder =
                                    state.value.selectedList?.sortType == TaskList.SortType.ORDINAL,
                                onReorder = { taskId, fromIndex, toIndex ->
                                    component.reorderTask(
                                        state.value.selectedList!!.id,
                                        taskId,
                                        fromIndex,
                                        toIndex
                                    )
                                },
                                showIndexNumbers =
                                    state.value.selectedList?.showIndexNumbers == true,
                            )
                        }
                    }

                    PullRefreshIndicator(
                        state.value.isRefreshing,
                        pullRefreshState,
                        Modifier.align(Alignment.TopCenter),
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    title: String?,
    upAsCloseButton: Boolean,
    onUpClicked: () -> Unit,
    enableActions: Boolean,
    onEditClicked: () -> Unit,
    showOverflowMenu: Boolean,
    setOverflowShown: (Boolean) -> Unit,
    onDeleteTasksClicked: () -> Unit,
    onDeleteListClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
            IconButton(onClick = { onEditClicked() }, enabled = enableActions) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { setOverflowShown(true) }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "More actions",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = showOverflowMenu,
                onDismissRequest = { setOverflowShown(false) },
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Delete completed tasks") },
                    leadingIcon = {
                        Icon(Icons.Filled.CheckCircleOutline, contentDescription = "Delete")
                    },
                    onClick = {
                        onDeleteTasksClicked()
                        setOverflowShown(false)
                    },
                    enabled = enableActions
                )
                DropdownMenuItem(
                    text = { Text(text = "Delete list") },
                    leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = "Delete") },
                    onClick = {
                        onDeleteListClicked()
                        setOverflowShown(false)
                    },
                    enabled = enableActions
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun TaskPlaceholder() {
    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "No tasks found",
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BottomBar(
    sortType: TaskList.SortType?,
    sortDirection: TaskList.SortDirection?,
    onSortTypeClicked: () -> Unit,
    onSortDirectionClicked: () -> Unit,
    enableActions: Boolean,
    onCreateClicked: () -> Unit
) {
    BottomAppBar(
        actions = {
            if (sortType != null) {
                TextButton(onClick = onSortTypeClicked) {
                    Icon(sortType.icon, contentDescription = "Sort by")
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = sortType.toString())
                }
            }
            if (sortDirection != null && sortType != TaskList.SortType.ORDINAL) {
                TextButton(onClick = onSortDirectionClicked) {
                    Icon(sortDirection.icon, contentDescription = "Sort order")
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = sortDirection.toString())
                }
            }
        },
        floatingActionButton = {
            if (enableActions) {
                FloatingActionButton(
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    onClick = { onCreateClicked() },
                ) {
                    Icon(Icons.Filled.Add, "New")
                }
            }
        }
    )
}

@Composable
private fun DeleteCompletedTasksDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete all completed tasks?") },
        text = { Text(text = "Completed tasks will be permanently deleted from this list") },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Delete") } }
    )
}

@Composable
private fun DeleteListDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete list?") },
        text = { Text(text = "All tasks in this list will be permanently deleted") },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Delete") } }
    )
}

@Composable
private fun SortListDialog(
    sortType: TaskList.SortType,
    onDismiss: () -> Unit,
    onSelect: (TaskList.SortType) -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 6.0.dp,
        ) {
            Column(modifier = Modifier.padding(24.dp).selectableGroup()) {
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(text = "Sort by", style = MaterialTheme.typography.headlineSmall)
                }
                for (type in TaskList.SortType.values()) {
                    ListItem(
                        modifier =
                            Modifier.selectable(
                                selected = sortType == type,
                                onClick = { onSelect(type) },
                                role = Role.RadioButton
                            ),
                        headlineContent = { Text(type.toString()) },
                        leadingContent = {
                            RadioButton(selected = sortType == type, onClick = null)
                        }
                    )
                }
            }
        }
    }
}

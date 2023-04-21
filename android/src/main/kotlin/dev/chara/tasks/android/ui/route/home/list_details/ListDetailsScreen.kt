package dev.chara.tasks.android.ui.route.home.list_details

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
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import dev.chara.tasks.android.model.icon
import dev.chara.tasks.android.ui.component.Tasks
import dev.chara.tasks.android.ui.component.util.PullRefreshLayout
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.list_details.ListDetailsUiState
import kotlinx.datetime.Clock

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun ListDetailsScreen(
    state: ListDetailsUiState,
    upAsCloseButton: Boolean,
    onRefresh: () -> Unit,
    onUpClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteCompletedTasksClicked: () -> Unit,
    onDeleteListClicked: () -> Unit,
    onTaskClicked: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onReorderTask: (String, Int, Int) -> Unit,
    onSortTypeClicked: () -> Unit,
    onSortDirectionClicked: () -> Unit,
    onCreateClicked: () -> Unit
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val pullRefreshState = rememberPullRefreshState(state.isLoading, onRefresh)

    Scaffold(
        topBar = {
            TopBar(
                scrollBehavior,
                state.selectedList!!,
                upAsCloseButton = upAsCloseButton,
                onUpClicked = { onUpClicked() },
                onEditClicked = { onEditClicked() },
                showOverflowMenu = showOverflowMenu,
                setOverflowShown = { showOverflowMenu = it },
                onDeleteTasksClicked = { onDeleteCompletedTasksClicked() },
                onDeleteListClicked = { onDeleteListClicked() }
            )
        },
        bottomBar = {
            BottomBar(
                sortType = state.selectedList!!.sortType,
                sortDirection = state.selectedList!!.sortDirection,
                onSortTypeClicked = onSortTypeClicked,
                onSortDirectionClicked = onSortDirectionClicked,
                onCreateClicked = onCreateClicked
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                PullRefreshLayout(
                    isRefreshing = state.isLoading,
                    refreshState = pullRefreshState
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (state.currentTasks.isEmpty() && state.completedTasks.isEmpty()) {
                            TaskPlaceholder()
                        } else {
                            Tasks(
                                currentTasks = state.currentTasks,
                                completedTasks = state.completedTasks,
                                onClick = { task ->
                                    onTaskClicked(task)
                                },
                                onUpdate = { task ->
                                    onUpdateTask(task)
                                },
                                allowReorder = state.selectedList!!.sortType == TaskList.SortType.ORDINAL,
                                onReorder = { taskId, fromIndex, toIndex ->
                                    onReorderTask(taskId, fromIndex, toIndex)
                                },
                                showIndexNumbers = state.selectedList!!.showIndexNumbers,
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun Preview_ListDetailsScreen() {
    ListDetailsScreen(
        state = ListDetailsUiState(
            selectedList = TaskList(id = "1", title = "Tasks"),
            currentTasks = listOf(
                Task(
                    id = "1",
                    listId = "1",
                    label = "Take out trash",
                ),
                Task(
                    id = "2",
                    listId = "1",
                    label = "Clean desk",
                    isStarred = true,
                ),
            ),
            completedTasks = listOf(
                Task(
                    id = "4",
                    listId = "1",
                    label = "Take out trash",
                    isCompleted = true,
                ),
                Task(
                    id = "5",
                    listId = "1",
                    label = "Clean desk",
                    isStarred = true,
                    isCompleted = true,
                ),
                Task(
                    id = "6",
                    listId = "1",
                    label = "Do laundry",
                    isCompleted = true,
                    details = "A very long and arduous process.",
                )
            ),
        ),
        upAsCloseButton = true,
        onRefresh = {},
        onUpClicked = {},
        onUpdateTask = {},
        onReorderTask = { _, _, _ -> },
        onTaskClicked = {},
        onSortTypeClicked = {},
        onSortDirectionClicked = {},
        onCreateClicked = {},
        onDeleteCompletedTasksClicked = {},
        onDeleteListClicked = {},
        onEditClicked = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    taskList: TaskList,
    upAsCloseButton: Boolean,
    onUpClicked: () -> Unit,
    onEditClicked: () -> Unit,
    showOverflowMenu: Boolean,
    setOverflowShown: (Boolean) -> Unit,
    onDeleteTasksClicked: () -> Unit,
    onDeleteListClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = taskList.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
            IconButton(onClick = { onEditClicked() }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { setOverflowShown(true) }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More actions")
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
                )
                DropdownMenuItem(
                    text = { Text(text = "Delete list") },
                    leadingIcon = {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    },
                    onClick = {
                        onDeleteListClicked()
                        setOverflowShown(false)
                    },
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview_TopBar() {
    TopBar(
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        taskList = TaskList(id = "1", title = "Tasks", lastModified = Clock.System.now()),
        upAsCloseButton = true,
        onUpClicked = {},
        onEditClicked = {},
        showOverflowMenu = false,
        setOverflowShown = {},
        onDeleteTasksClicked = {},
        onDeleteListClicked = {}
    )
}

@Preview
@Composable
private fun TaskPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "No tasks found",
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BottomBar(
    sortType: TaskList.SortType,
    sortDirection: TaskList.SortDirection,
    onSortTypeClicked: () -> Unit,
    onSortDirectionClicked: () -> Unit,
    onCreateClicked: () -> Unit
) {
    BottomAppBar(
        actions = {
            TextButton(onClick = onSortTypeClicked) {
                Icon(
                    sortType.icon,
                    contentDescription = "Sort by"
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = sortType.toString())
            }
            if (sortType != TaskList.SortType.ORDINAL) {
                TextButton(onClick = onSortDirectionClicked) {
                    Icon(
                        sortDirection.icon,
                        contentDescription = "Sort order"
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = sortDirection.toString())
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                onClick = { onCreateClicked() },
            ) {
                Icon(Icons.Filled.Add, "New")
            }
        }
    )
}

@Preview
@Composable
private fun Preview_BottomBar() {
    BottomBar(
        sortType = TaskList.SortType.DATE_CREATED,
        sortDirection = TaskList.SortDirection.ASCENDING,
        onSortTypeClicked = {},
        onSortDirectionClicked = {},
        onCreateClicked = {}
    )
}
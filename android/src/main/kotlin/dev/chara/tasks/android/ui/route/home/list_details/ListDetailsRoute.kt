package dev.chara.tasks.android.ui.route.home.list_details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.android.ui.component.dialog.ModifyListDialog
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.list_details.ListDetailsUiState
import dev.chara.tasks.viewmodel.home.list_details.ListDetailsViewModel
import kotlinx.datetime.Clock

@Composable
fun ListDetailsRoute(
    presenter: ListDetailsViewModel,
    snackbarHostState: SnackbarHostState,
    upAsCloseButton: Boolean,
    navigateUp: () -> Unit,
    navigateToTaskDetails: (Task) -> Unit,
    onCreateTaskClicked: (TaskList) -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    if (state.value is ListDetailsUiState.Loaded) {
        val loadedState = (state.value as ListDetailsUiState.Loaded)

        ColorTheme(color = loadedState.selectedList.color) {
            var showEditDialog by remember { mutableStateOf(false) }

            if (showEditDialog) {
                ModifyListDialog(
                    title = "Edit list",
                    current = loadedState.selectedList,
                    onDismiss = {
                        showEditDialog = false
                    },
                    onSave = { taskList ->
                        presenter.updateList(loadedState.selectedList.id, taskList)
                        showEditDialog = false
                    }
                )
            }

            var showDeleteListDialog by remember { mutableStateOf(false) }

            if (showDeleteListDialog) {
                DeleteListDialog(
                    onDismiss = {
                        showDeleteListDialog = false
                    },
                    onConfirm = {
                        showDeleteListDialog = false
                        presenter.deleteList(loadedState.selectedList.id)
                        navigateUp()
                    }
                )
            }

            var showDeleteCompletedTasksDialog by remember { mutableStateOf(false) }

            if (showDeleteCompletedTasksDialog) {
                DeleteCompletedTasksDialog(
                    onDismiss = {
                        showDeleteCompletedTasksDialog = false
                    },
                    onConfirm = {
                        presenter.clearCompletedTasks(loadedState.selectedList.id)
                        showDeleteCompletedTasksDialog = false
                    }
                )
            }

            var showSortDialog by remember { mutableStateOf(false) }

            if (showSortDialog) {
                SortListDialog(
                    sortType = loadedState.selectedList.sortType,
                    onDismiss = {
                        showSortDialog = false
                    },
                    onSelect = { sortType ->
                        presenter.updateList(
                            loadedState.selectedList.id,
                            loadedState.selectedList.copy(
                                sortType = sortType,
                                lastModified = Clock.System.now()
                            )
                        )
                        showSortDialog = false
                    }
                )
            }

            ListDetailsScreen(
                state = state.value as ListDetailsUiState.Loaded,
                upAsCloseButton = upAsCloseButton,
                onRefresh = presenter::refreshCache,
                onUpClicked = { navigateUp() },
                onEditClicked = { showEditDialog = true },
                onDeleteCompletedTasksClicked = { showDeleteCompletedTasksDialog = true },
                onDeleteListClicked = { showDeleteListDialog = true },
                onTaskClicked = { task ->
                    navigateToTaskDetails(task)
                },
                onUpdateTask = { task ->
                    presenter.updateTask(loadedState.selectedList.id, task.id, task)
                },
                onReorderTask = { taskId, fromIndex, toIndex ->
                    presenter.reorderTask(
                        loadedState.selectedList.id,
                        taskId,
                        fromIndex,
                        toIndex,
                        Clock.System.now()
                    )
                },
                onSortTypeClicked = { showSortDialog = true },
                onSortDirectionClicked = {
                    presenter.updateList(
                        loadedState.selectedList.id,
                        loadedState.selectedList.copy(
                            sortDirection = if (loadedState.selectedList.sortDirection == TaskList.SortDirection.ASCENDING) {
                                TaskList.SortDirection.DESCENDING
                            } else {
                                TaskList.SortDirection.ASCENDING
                            },
                            lastModified = Clock.System.now()
                        )
                    )
                },
                onCreateClicked = { onCreateTaskClicked(loadedState.selectedList) }
            )
        }
    } else {
        Surface(modifier = Modifier.fillMaxSize()) {
            // Placeholder
        }
    }

    LaunchedEffect(presenter.messages) {
        presenter.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.text,
                duration = SnackbarDuration.Short,
                withDismissAction = message.action == null,
                actionLabel = message.action?.text
            ).let {
                if (it == SnackbarResult.ActionPerformed) {
                    message.action?.function?.invoke()
                }
            }
        }
    }
}
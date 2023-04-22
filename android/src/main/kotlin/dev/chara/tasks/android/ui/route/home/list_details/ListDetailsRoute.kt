package dev.chara.tasks.android.ui.route.home.list_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.android.ui.component.sheet.ModifyListSheet
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.list_details.ListDetailsViewModel
import kotlinx.datetime.Clock

@Composable
fun ListDetailsRoute(
    listId: String,
    snackbarHostState: SnackbarHostState,
    upAsCloseButton: Boolean,
    navigateUp: () -> Unit,
    navigateToTaskDetails: (Task) -> Unit,
    onCreateTaskClicked: (TaskList) -> Unit
) {
    val viewModel: ListDetailsViewModel = viewModel(key = listId) {
        ListDetailsViewModel(listId)
    }
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val selectedList = state.value.selectedList

    if (!state.value.firstLoad) {
        if (selectedList == null) {
            navigateUp()
            return
        }

        ColorTheme(color = selectedList.color) {
            var showEditDialog by remember { mutableStateOf(false) }

            if (showEditDialog) {
                ModifyListSheet(
                    title = "Edit list",
                    current = selectedList,
                    onDismiss = {
                        showEditDialog = false
                    },
                    onSave = { taskList ->
                        viewModel.updateList(selectedList.id, taskList)
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
                        viewModel.deleteList(selectedList.id)
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
                        viewModel.clearCompletedTasks(selectedList.id)
                        showDeleteCompletedTasksDialog = false
                    }
                )
            }

            var showSortDialog by remember { mutableStateOf(false) }

            if (showSortDialog) {
                SortListDialog(
                    sortType = selectedList.sortType,
                    onDismiss = {
                        showSortDialog = false
                    },
                    onSelect = { sortType ->
                        viewModel.updateList(
                            selectedList.id,
                            selectedList.copy(
                                sortType = sortType,
                                lastModified = Clock.System.now()
                            )
                        )
                        showSortDialog = false
                    }
                )
            }

            ListDetailsScreen(
                state = state.value,
                upAsCloseButton = upAsCloseButton,
                onRefresh = viewModel::refreshCache,
                onUpClicked = { navigateUp() },
                onEditClicked = { showEditDialog = true },
                onDeleteCompletedTasksClicked = { showDeleteCompletedTasksDialog = true },
                onDeleteListClicked = { showDeleteListDialog = true },
                onTaskClicked = { task ->
                    navigateToTaskDetails(task)
                },
                onUpdateTask = { task ->
                    viewModel.updateTask(selectedList.id, task.id, task)
                },
                onReorderTask = { taskId, fromIndex, toIndex ->
                    viewModel.reorderTask(
                        selectedList.id,
                        taskId,
                        fromIndex,
                        toIndex,
                        Clock.System.now()
                    )
                },
                onSortTypeClicked = { showSortDialog = true },
                onSortDirectionClicked = {
                    viewModel.updateList(
                        selectedList.id,
                        selectedList.copy(
                            sortDirection = if (selectedList.sortDirection == TaskList.SortDirection.ASCENDING) {
                                TaskList.SortDirection.DESCENDING
                            } else {
                                TaskList.SortDirection.ASCENDING
                            },
                            lastModified = Clock.System.now()
                        )
                    )
                },
                onCreateClicked = { onCreateTaskClicked(selectedList) }
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    LaunchedEffect(viewModel.messages) {
        viewModel.messages.collect { message ->
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
package dev.chara.tasks.android.ui.route.home.task_details

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
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.viewmodel.home.task_details.TaskDetailsViewModel
import kotlinx.datetime.Clock

@Composable
fun TaskDetailsRoute(
    taskId: String,
    navigateUp: () -> Unit
) {
    val viewModel: TaskDetailsViewModel = viewModel(key = taskId) {
        TaskDetailsViewModel().observeTask(taskId)
    }
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val task = state.value.task

    if (!state.value.firstLoad) {
        if (task == null) {
            navigateUp()
            return
        }

        ColorTheme(color = state.value.taskLists.first { it.id == task.listId }.color) {
            var showDeleteDialog by remember { mutableStateOf(false) }

            if (showDeleteDialog) {
                DeleteTaskDialog(
                    onDismiss = {
                        showDeleteDialog = false
                    },
                    onConfirm = {
                        showDeleteDialog = false
                        viewModel.deleteTask(task.listId, task.id)
                        navigateUp()
                    }
                )
            }

            var showExitDialog by remember { mutableStateOf(false) }

            if (showExitDialog) {
                ConfirmExitDialog(
                    onDismiss = { showExitDialog = false },
                    onConfirm = {
                        showExitDialog = false
                        navigateUp()
                    }
                )
            }

            TaskDetailsScreen(
                state.value,
                snackbarHostState = snackbarHostState,
                onUpClicked = { modified ->
                    if (modified) {
                        showExitDialog = true
                    } else {
                        navigateUp()
                    }
                },
                onUpdateTask = {
                    viewModel.updateTask(
                        task.listId,
                        task.id,
                        it
                    )
                },
                onMoveTask = { newListId ->
                    viewModel.moveTask(
                        task.listId,
                        newListId,
                        task.id,
                        Clock.System.now()
                    )
                },
                onDeleteClicked = {
                    showDeleteDialog = true
                }
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
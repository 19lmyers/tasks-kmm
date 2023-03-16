package dev.chara.tasks.android.ui.route.home.task_details

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
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.viewmodel.home.task_details.TaskDetailsUiState
import dev.chara.tasks.viewmodel.home.task_details.TaskDetailsViewModel
import kotlinx.datetime.Clock

@Composable
fun TaskDetailsRoute(
    presenter: TaskDetailsViewModel,
    upAsCloseButton: Boolean,
    navigateUp: () -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value is TaskDetailsUiState.Loaded) {
        val loadedState = (state.value as TaskDetailsUiState.Loaded)

        ColorTheme(color = loadedState.taskLists.first { it.id == loadedState.task.listId }.color) {
            var showDeleteDialog by remember { mutableStateOf(false) }

            if (showDeleteDialog) {
                DeleteTaskDialog(
                    onDismiss = {
                        showDeleteDialog = false
                    },
                    onConfirm = {
                        showDeleteDialog = false
                        presenter.deleteTask(loadedState.task.listId, loadedState.task.id)
                        navigateUp()
                    }
                )
            }

            TaskDetailsScreen(
                state.value as TaskDetailsUiState.Loaded,
                snackbarHostState = snackbarHostState,
                upAsCloseButton = upAsCloseButton,
                onUpClicked = { navigateUp() },
                onUpdateTask = {
                    presenter.updateTask(
                        loadedState.task.listId,
                        loadedState.task.id,
                        it
                    )
                },
                onMoveTask = { newListId ->
                    presenter.moveTask(
                        loadedState.task.listId,
                        newListId,
                        loadedState.task.id,
                        Clock.System.now()
                    )
                },
                onDeleteClicked = {
                    showDeleteDialog = true
                }
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
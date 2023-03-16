package dev.chara.tasks.android.ui.route.home.board

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.board.BoardUiState
import dev.chara.tasks.viewmodel.home.board.BoardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardRoute(
    presenter: BoardViewModel,
    snackbarHostState: SnackbarHostState,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToTaskDetails: (Task) -> Unit,
    navigateToListDetails: (TaskList) -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    if (state.value is BoardUiState.Loaded) {
        BoardScreen(
            state.value as BoardUiState.Loaded,
            scrollBehavior,
            onTaskClicked = {
                navigateToTaskDetails(it)
            },
            onListClicked = {
                navigateToListDetails(it)
            },
            onRefresh = presenter::refreshCache,
            onUpdateTask = presenter::updateTask
        )
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
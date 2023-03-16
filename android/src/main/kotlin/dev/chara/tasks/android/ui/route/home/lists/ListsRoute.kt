package dev.chara.tasks.android.ui.route.home.lists

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.android.ui.component.dialog.ModifyListDialog
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.lists.ListsUiState
import dev.chara.tasks.viewmodel.home.lists.ListsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsRoute(
    presenter: ListsViewModel,
    snackbarHostState: SnackbarHostState,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToListDetails: (TaskList) -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        ModifyListDialog(
            title = "New list",
            current = TaskList(id = "", title = ""),
            onDismiss = {
                showCreateDialog = false
            },
            onSave = { taskList ->
                presenter.createList(taskList)
                showCreateDialog = false
            }
        )
    }

    if (state.value is ListsUiState.Loaded) {
        ListsScreen(
            state.value as ListsUiState.Loaded,
            scrollBehavior,
            onRefresh = presenter::refreshCache,
            onListClicked = { taskList ->
                navigateToListDetails(taskList)
            },
            onCreateClicked = { showCreateDialog = true }
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
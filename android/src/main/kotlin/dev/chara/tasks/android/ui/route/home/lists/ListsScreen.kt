package dev.chara.tasks.android.ui.route.home.lists

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import dev.chara.tasks.android.ui.component.TaskLists
import dev.chara.tasks.android.ui.component.util.PullRefreshLayout
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.lists.ListsUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ListsScreen(
    state: ListsUiState,
    scrollBehavior: TopAppBarScrollBehavior,
    onRefresh: () -> Unit,
    onListClicked: (TaskList) -> Unit,
    onCreateClicked: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(state.isLoading, onRefresh)

    PullRefreshLayout(isRefreshing = state.isLoading, refreshState = pullRefreshState) {
        TaskLists(
            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            state.taskLists,
            onListClicked,
            onCreateClicked
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview_ListsScreen() {
    ListsScreen(
        ListsUiState(
            taskLists = listOf(
                TaskList(id = "1", title = "Tasks"),
                TaskList(id = "2", title = "Reminders"),
                TaskList(id = "3", title = "Shopping List")
            )
        ),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        onRefresh = {},
        onListClicked = {},
        onCreateClicked = {}
    )
}
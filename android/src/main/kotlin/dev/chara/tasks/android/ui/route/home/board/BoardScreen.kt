package dev.chara.tasks.android.ui.route.home.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.chara.tasks.android.ui.component.BoardSections
import dev.chara.tasks.android.ui.component.util.PullRefreshLayout
import dev.chara.tasks.model.BoardSection
import dev.chara.tasks.model.PinnedList
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.board.BoardUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun BoardScreen(
    state: BoardUiState.Loaded,
    scrollBehavior: TopAppBarScrollBehavior,
    onTaskClicked: (Task) -> Unit,
    onListClicked: (TaskList) -> Unit,
    onRefresh: () -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(state.isRefreshing, onRefresh)

    PullRefreshLayout(isRefreshing = state.isRefreshing, refreshState = pullRefreshState) {
        if (state.boardSections.isEmpty() && state.pinnedLists.isEmpty()) {
            BoardPlaceholder()
        } else {
            BoardSections(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                sections = state.boardSections,
                pinnedLists = state.pinnedLists,
                allLists = state.allLists,
                onTaskClicked = onTaskClicked,
                onListClicked = onListClicked,
                onUpdate = onUpdateTask
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Preview_BoardScreen() {
    BoardScreen(
        state = BoardUiState.Loaded(
            boardSections = listOf(
                BoardSection(
                    type = BoardSection.Type.STARRED,
                    tasks = listOf(
                        Task(
                            id = "1",
                            listId = "1",
                            label = "Take out trash",
                            isStarred = true,
                        ),
                        Task(
                            id = "2",
                            listId = "1",
                            label = "Do laundry",
                            isStarred = true,
                        ),
                        Task(
                            id = "3",
                            listId = "2",
                            label = "Rotisserie chicken",
                            isStarred = true,
                        )
                    )
                )
            ),
            pinnedLists = listOf(
                PinnedList(
                    taskList = TaskList(
                        id = "1",
                        title = "Tasks",
                    ),
                    topTasks = listOf(
                        Task(
                            id = "1",
                            listId = "1",
                            label = "Take out trash",
                            isStarred = true,
                        )
                    ),
                    totalTaskCount = 2
                )
            ),
            allLists = listOf(
                TaskList(
                    id = "1",
                    title = "Tasks",
                ),
                TaskList(
                    id = "2",
                    title = "Shopping list",
                )
            )
        ),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        onTaskClicked = {},
        onListClicked = {},
        onRefresh = {},
        onUpdateTask = {}
    )
}

@Preview
@Composable
private fun BoardPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "No suggestions found",
            textAlign = TextAlign.Center,
        )
    }
}
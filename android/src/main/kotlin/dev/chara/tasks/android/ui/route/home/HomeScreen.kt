package dev.chara.tasks.android.ui.route.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.ui.component.Dashboard
import dev.chara.tasks.android.ui.component.ProfileImage
import dev.chara.tasks.android.ui.component.util.PullRefreshLayout
import dev.chara.tasks.model.Profile
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.HomeUiState

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    showCreateTaskButton: Boolean,
    onAccountPressed: () -> Unit,
    onCreateListPressed: () -> Unit,
    onCreateTaskPressed: () -> Unit,
    navigateToListDetails: (TaskList) -> Unit,
    navigateToTaskDetails: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onRefresh: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val refreshState = rememberPullRefreshState(state.isLoading, onRefresh)

    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopBar(scrollBehavior, state.profile!!) {
            onAccountPressed()
        }
    }, floatingActionButton = {
        AnimatedVisibility(
            visible = showCreateTaskButton,
            enter = scaleIn(),
            exit = scaleOut(),
        ) {
            ExtendedFloatingActionButton(text = { Text(text = "New task") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "New task") },
                onClick = { onCreateTaskPressed() },
                expanded = scrollBehavior.state.collapsedFraction == 0.0f
            )
        }
    }, content = { innerPadding ->
        val topPadding = PaddingValues(top = innerPadding.calculateTopPadding())
        val paddingBottom = PaddingValues(bottom = innerPadding.calculateBottomPadding())

        Box(
            modifier = Modifier
                .padding(topPadding)
                .consumeWindowInsets(topPadding)
        ) {
            PullRefreshLayout(state.isLoading, refreshState) {
                Dashboard(
                    paddingBottom,
                    sections = state.boardSections,
                    pinnedLists = state.pinnedLists,
                    allLists = state.allLists,
                    onListClicked = navigateToListDetails,
                    onTaskClicked = navigateToTaskDetails,
                    onCreateListClicked = onCreateListPressed,
                    onUpdate = onUpdateTask
                )
            }
        }
    })
}

@Composable
fun HomeScreenWithDetailPane(
    state: HomeUiState,
    showCreateTaskButton: Boolean,
    onAccountPressed: () -> Unit,
    onCreateListPressed: () -> Unit,
    onCreateTaskPressed: () -> Unit,
    navigateToListDetails: (TaskList) -> Unit,
    navigateToTaskDetails: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onRefresh: () -> Unit,
    homePaneWidth: Dp,
    detailPaneWidth: Dp,
    detailPaneContent: @Composable () -> Unit
) {
    Row {
        HomeScreen(
            state = state,
            modifier = Modifier.width(homePaneWidth),
            showCreateTaskButton = showCreateTaskButton && detailPaneWidth == 0.dp,
            onAccountPressed = onAccountPressed,
            onCreateListPressed = onCreateListPressed,
            onCreateTaskPressed = onCreateTaskPressed,
            navigateToListDetails = { taskList -> navigateToListDetails(taskList) },
            navigateToTaskDetails = { task -> navigateToTaskDetails(task) },
            onUpdateTask = onUpdateTask,
            onRefresh = onRefresh
        )
        Surface(
            modifier = Modifier.width(detailPaneWidth)
        ) {
            Surface(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 1.dp
            ) {
                detailPaneContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior, profile: Profile, onAccountClicked: () -> Unit
) {
    TopAppBar(title = { Text(text = "Tasks") }, actions = {
        IconButton(onClick = { onAccountClicked() }) {
            ProfileImage(profile.email, profile.profilePhotoUri, Modifier.requiredSize(24.dp))
        }
    }, scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview_TopBar() {
    TopBar(scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(), profile = Profile(
        email = "user@email.com", displayName = "User", profilePhotoUri = null
    ), onAccountClicked = {})
}
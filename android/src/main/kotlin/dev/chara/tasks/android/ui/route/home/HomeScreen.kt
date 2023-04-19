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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    ExperimentalLayoutApi::class,
    ExperimentalAnimationApi::class, ExperimentalMaterialApi::class
)
@Composable
fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    showCreateTaskButton: Boolean,
    onAccountPressed: () -> Unit,
    onSettingsPressed: () -> Unit,
    onSignOutPressed: () -> Unit,
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
        TopBar(
            scrollBehavior,
            state.profile!!,
            onAccountPressed,
            onSettingsPressed,
            onSignOutPressed
        )
    }, floatingActionButton = {
        AnimatedVisibility(
            visible = showCreateTaskButton,
            enter = scaleIn(),
            exit = scaleOut(),
        ) {
            ExtendedFloatingActionButton(
                text = { Text(text = "New task") },
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
    onSettingsPressed: () -> Unit,
    onSignOutPressed: () -> Unit,
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
            onSettingsPressed = onSettingsPressed,
            onSignOutPressed = onSignOutPressed,
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
    scrollBehavior: TopAppBarScrollBehavior,
    profile: Profile,
    onEditProfileClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onSignOutClicked: () -> Unit
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = "Tasks") },
        actions = {
            IconButton(onClick = { showOverflowMenu = true }) {
                ProfileImage(profile.email, profile.profilePhotoUri, Modifier.requiredSize(28.dp))
            }
            DropdownMenu(
                expanded = showOverflowMenu,
                onDismissRequest = { showOverflowMenu = false }) {
                DropdownMenuItem(
                    onClick = {
                        showOverflowMenu = false
                        onEditProfileClicked()
                    },
                    text = {
                        Text("Edit profile")
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Profile")
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        showOverflowMenu = false
                        onSettingsClicked()
                    },
                    text = {
                        Text("Settings")
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        showOverflowMenu = false
                        onSignOutClicked()
                    },
                    text = {
                        Text("Sign out")
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Logout, contentDescription = "Sign out")
                    }
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
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
        profile = Profile(
            id = "1", email = "user@email.com", displayName = "User", profilePhotoUri = null
        ),
        onEditProfileClicked = {},
        onSettingsClicked = {},
        onSignOutClicked = {}
    )
}
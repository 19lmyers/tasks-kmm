package dev.chara.tasks.android.ui.route.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.ui.component.ProfileImage
import dev.chara.tasks.android.ui.route.home.board.BoardRoute
import dev.chara.tasks.android.ui.route.home.lists.ListsRoute
import dev.chara.tasks.model.Profile
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.HomeUiState
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.replaceAll

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    destinations: List<HomeNavTarget>,
    navController: NavController<HomeNavTarget>,
    state: HomeUiState.Authenticated,
    snackbarHostState: SnackbarHostState,
    useNavRail: Boolean,
    onAccountPressed: () -> Unit,
    onCreateTaskPressed: () -> Unit,
    navigateToListDetails: (TaskList) -> Unit,
    navigateToTaskDetails: (Task) -> Unit,
) {
    HomeScreen(
        modifier = modifier,
        menuItems = destinations,
        selectedMenuItem = navController.backstack.entries.last().destination,
        onMenuItemSelected = { navTarget ->
            navController.replaceAll(navTarget)
        },
        state = state,
        useNavRail = useNavRail,
        onAccountPressed = onAccountPressed,
        onCreateTaskPressed = onCreateTaskPressed,
    ) { scrollBehavior ->
        AnimatedNavHost(navController) { navTarget ->
            when (navTarget) {
                HomeNavTarget.Board -> BoardRoute(
                    snackbarHostState,
                    scrollBehavior,
                    navigateToTaskDetails = { task ->
                        navigateToTaskDetails(task)
                    },
                    navigateToListDetails = { taskList ->
                        navigateToListDetails(taskList)
                    }
                )

                HomeNavTarget.Lists -> ListsRoute(
                    snackbarHostState,
                    scrollBehavior,
                    navigateToListDetails = { taskList ->
                        navigateToListDetails(taskList)
                    }
                )

                HomeNavTarget.Shared -> {} // TODO
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    menuItems: List<HomeNavTarget>,
    selectedMenuItem: HomeNavTarget,
    onMenuItemSelected: (HomeNavTarget) -> Unit,
    state: HomeUiState.Authenticated,
    useNavRail: Boolean,
    onAccountPressed: () -> Unit,
    onCreateTaskPressed: () -> Unit,
    content: @Composable (TopAppBarScrollBehavior) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(scrollBehavior, state.profile) {
                onAccountPressed()
            }
        },
        bottomBar = {
            if (!useNavRail) {
                BottomNav(
                    menuItems = menuItems,
                    selectedItem = selectedMenuItem,
                    onMenuItemSelect = { navTarget ->
                        if (selectedMenuItem != navTarget) {
                            onMenuItemSelected(navTarget)
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "New task") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "New task") },
                onClick = { onCreateTaskPressed() },
                expanded = scrollBehavior.state.collapsedFraction == 0.0f
            )
        },
        content = { contentPadding ->
            val padding = if (useNavRail) {
                PaddingValues(top = contentPadding.calculateTopPadding())
            } else {
                contentPadding
            }

            Row(
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {
                if (useNavRail) {
                    NavRail(
                        menuItems = menuItems,
                        selectedItem = selectedMenuItem,
                        onMenuItemSelect = { navTarget ->
                            if (selectedMenuItem != navTarget) {
                                onMenuItemSelected(navTarget)
                            }
                        }
                    )
                }
                content(scrollBehavior)
            }
        }
    )
}

@Composable
fun HomeScreenWithDetailPane(
    destinations: List<HomeNavTarget>,
    navController: NavController<HomeNavTarget>,
    state: HomeUiState.Authenticated,
    snackbarHostState: SnackbarHostState,
    useNavRail: Boolean,
    onAccountPressed: () -> Unit,
    onCreateTaskPressed: () -> Unit,
    navigateToListDetails: (TaskList) -> Unit,
    navigateToTaskDetails: (Task) -> Unit,
    homePaneWidth: Dp,
    detailPaneWidth: Dp,
    detailPaneContent: @Composable () -> Unit
) {
    Row {
        HomeScreen(
            modifier = Modifier.width(homePaneWidth),
            destinations = destinations,
            navController = navController,
            state = state,
            snackbarHostState = snackbarHostState,
            useNavRail = useNavRail,
            onAccountPressed = onAccountPressed,
            onCreateTaskPressed = onCreateTaskPressed,
            navigateToListDetails = { taskList -> navigateToListDetails(taskList) },
            navigateToTaskDetails = { task -> navigateToTaskDetails(task) }
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
    onAccountClicked: () -> Unit
) {
    TopAppBar(
        title = { Text(text = "Tasks") },
        actions = {
            IconButton(onClick = { onAccountClicked() }) {
                ProfileImage(profile.email, profile.profilePhotoUri, Modifier.requiredSize(24.dp))
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
            email = "user@email.com",
            displayName = "User",
            profilePhotoUri = null
        ),
        onAccountClicked = {}
    )
}

@Composable
fun BottomNav(
    menuItems: List<HomeNavTarget>,
    selectedItem: HomeNavTarget,
    onMenuItemSelect: (HomeNavTarget) -> Unit
) {
    NavigationBar {
        menuItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedItem == item,
                onClick = {
                    onMenuItemSelect(item)
                }
            )
        }
    }
}

@Preview
@Composable
private fun Preview_BottomNav() {
    BottomNav(
        menuItems = listOf(HomeNavTarget.Board, HomeNavTarget.Lists, HomeNavTarget.Shared),
        selectedItem = HomeNavTarget.Board,
        onMenuItemSelect = {}
    )
}


@Composable
fun NavRail(
    menuItems: List<HomeNavTarget>,
    selectedItem: HomeNavTarget,
    onMenuItemSelect: (HomeNavTarget) -> Unit
) {
    NavigationRail {
        Spacer(Modifier.weight(1f))
        menuItems.forEach { item ->
            NavigationRailItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedItem == item,
                onClick = {
                    onMenuItemSelect(item)
                }
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun Preview_NavRail() {
    NavRail(
        menuItems = listOf(HomeNavTarget.Board, HomeNavTarget.Lists, HomeNavTarget.Shared),
        selectedItem = HomeNavTarget.Board,
        onMenuItemSelect = {}
    )
}
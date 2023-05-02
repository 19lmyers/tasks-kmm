package dev.chara.tasks.android.ui.route.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import dev.chara.tasks.android.ui.NavTarget
import dev.chara.tasks.android.ui.route.home.list_details.ListDetailsRoute
import dev.chara.tasks.android.ui.route.home.task_details.TaskDetailsRoute
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeNavHost(
    navController: NavController<NavTarget.Home>,
    snackbarHostState: SnackbarHostState,
    isDualPane: Boolean,
    onCreateTaskClicked: (String?) -> Unit,
    defaultContent: @Composable () -> Unit
) {
    NavBackHandler(navController)

    AnimatedNavHost(
        navController,
        emptyBackstackPlaceholder = {
            defaultContent()
        }
    ) { navTarget ->
        when (navTarget) {
            NavTarget.Home.Default -> {
                defaultContent()
            }

            is NavTarget.Home.WithList -> {
                ListDetailsRoute(
                    navTarget.listId,
                    snackbarHostState = snackbarHostState,
                    upAsCloseButton = isDualPane,
                    navigateUp = {
                        navController.pop()
                    },
                    navigateToTaskDetails = { task ->
                        navController.navigate(NavTarget.Home.WithTask(task.id))
                    },
                    onCreateTaskClicked = {
                        onCreateTaskClicked(it.id)
                    }
                )
            }

            is NavTarget.Home.WithTask -> {
                TaskDetailsRoute(
                    taskId = navTarget.taskId,
                    navigateUp = {
                        navController.pop()
                    }
                )
            }
        }
    }
}
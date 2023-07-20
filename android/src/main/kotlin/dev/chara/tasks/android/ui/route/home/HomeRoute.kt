package dev.chara.tasks.android.ui.route.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.android.ui.NavTarget
import dev.chara.tasks.android.ui.component.sheet.CreateTaskSheet
import dev.chara.tasks.android.ui.component.sheet.ModifyListSheet
import dev.chara.tasks.android.ui.component.util.SnackbarLayout
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.HomeViewModel
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.replaceAll
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    useDualPane: Boolean,
    initialNavTarget: NavTarget.Home,
    initCreateTaskSheet: Boolean,
    navigateToWelcome: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToSettings: () -> Unit,
) {
    val viewModel: HomeViewModel = viewModel()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
    }

    if (!state.value.firstLoad) {
        if (!state.value.isAuthenticated) {
            navigateToWelcome()
            return
        }

        // Request notification permission - TODO move to sensible place
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        var showCreateListSheet by rememberSaveable { mutableStateOf(false) }

        if (showCreateListSheet) {
            ModifyListSheet(
                title = "New list",
                current = TaskList(id = "", title = ""),
                onDismiss = {
                    showCreateListSheet = false
                },
                onSave = { taskList ->
                    viewModel.createList(taskList)
                    showCreateListSheet = false
                }
            )
        }

        var showCreateTaskSheet by rememberSaveable(initCreateTaskSheet) {
            mutableStateOf(
                initCreateTaskSheet
            )
        }
        var defaultListIdForCreatedTask by rememberSaveable { mutableStateOf<String?>(null) }

        if (showCreateTaskSheet) {
            CreateTaskSheet(
                taskLists = state.value.allLists,
                current = Task(
                    id = "",
                    listId = defaultListIdForCreatedTask ?: state.value.allLists.first().id,
                    label = "",
                    lastModified = Clock.System.now()
                ),
                onDismiss = {
                    showCreateTaskSheet = false
                },
                onSave = { task ->
                    viewModel.createTask(task.listId, task)
                    showCreateTaskSheet = false
                }
            )
        }

        var showVerifyEmailDialog by rememberSaveable { mutableStateOf(false) }

        if (showVerifyEmailDialog) {
            AlertDialog(
                onDismissRequest = { showVerifyEmailDialog = false },
                title = { Text("Verify your email") },
                text = { Text("To unlock the full functionality of Tasks, please verify your email address.") },
                dismissButton = {
                    TextButton(onClick = { showVerifyEmailDialog = false }) {
                        Text("Cancel")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.requestVerifyEmailResend()
                        showVerifyEmailDialog = false
                    }) {
                        Text("Verify")
                    }
                }
            )
        }

        if (state.value.verifyEmailSent) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.clearVerifyEmailNotice()
                },
                title = { Text("Verification email sent") },
                text = { Text("Follow the link provided to verify your email address.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearVerifyEmailNotice()
                    }) {
                        Text("OK")
                    }
                }
            )
        }

        val navController: NavController<NavTarget.Home> = rememberSaveable(initialNavTarget) {
            navController(initialNavTarget)
        }

        val snackbarBottomOffset = with(LocalDensity.current) {
            80.dp.roundToPx()
        }

        SnackbarLayout(
            snackbarBottomOffset = snackbarBottomOffset,
            snackbar = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) {
            if (useDualPane) {
                HomeScreenWithDetailPane(
                    state = state.value,
                    onNotificationsPressed = {
                        if (state.value.profile!!.emailVerified) {
                            Toast.makeText(context, "No notifications", Toast.LENGTH_LONG).show()
                        } else {
                            showVerifyEmailDialog = true
                        }
                    },
                    onAccountPressed = navigateToProfile,
                    onSettingsPressed = navigateToSettings,
                    onSignOutPressed = {
                        viewModel.logout()
                    },
                    onCreateListPressed = {
                        showCreateListSheet = true
                    },
                    onCreateTaskPressed = {
                        showCreateTaskSheet = true
                    },
                    navigateToListDetails = { taskList ->
                        navController.replaceAll(
                            listOf(NavTarget.Home.WithList(taskList.id))
                        )
                    },
                    navigateToTaskDetails = { task ->
                        navController.replaceAll(
                            listOf(NavTarget.Home.WithTask(task.id))
                        )
                    },
                    onUpdateTask = { task ->
                        viewModel.updateTask(task)
                    },
                    onRefresh = {
                        viewModel.refreshCache()
                    },
                ) {
                    HomeNavHost(
                        navController,
                        snackbarHostState,
                        isDualPane = true,
                        onCreateTaskClicked = { listId ->
                            defaultListIdForCreatedTask = listId
                            showCreateTaskSheet = true
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                "Select a list or task",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            } else {
                HomeNavHost(
                    navController,
                    snackbarHostState,
                    isDualPane = false,
                    onCreateTaskClicked = { listId ->
                        defaultListIdForCreatedTask = listId
                        showCreateTaskSheet = true
                    }
                ) {
                    HomeScreen(
                        state = state.value,
                        showCreateTaskButton = state.value.allLists.isNotEmpty(),
                        onNotificationsPressed = {
                            if (state.value.profile!!.emailVerified) {
                                Toast.makeText(context, "No notifications", Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                showVerifyEmailDialog = true
                            }
                        },
                        onAccountPressed = navigateToProfile,
                        onSettingsPressed = navigateToSettings,
                        onSignOutPressed = {
                            viewModel.logout()
                        },
                        onCreateListPressed = {
                            showCreateListSheet = true
                        },
                        onCreateTaskPressed = {
                            showCreateTaskSheet = true
                        },
                        navigateToListDetails = { taskList ->
                            navController.navigate(NavTarget.Home.WithList(taskList.id))
                        },
                        navigateToTaskDetails = { task ->
                            navController.navigate(NavTarget.Home.WithTask(task.id))
                        },
                        onUpdateTask = { task ->
                            viewModel.updateTask(task)
                        },
                        onRefresh = {
                            viewModel.refreshCache()
                        }
                    )
                }
            }
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
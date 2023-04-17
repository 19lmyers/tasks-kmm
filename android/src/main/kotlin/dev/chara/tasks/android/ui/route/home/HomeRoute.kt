package dev.chara.tasks.android.ui.route.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.android.R
import dev.chara.tasks.android.ui.NavTarget
import dev.chara.tasks.android.ui.component.dialog.CreateTaskDialog
import dev.chara.tasks.android.ui.component.dialog.ModifyListDialog
import dev.chara.tasks.android.ui.component.util.SnackbarLayout
import dev.chara.tasks.android.ui.route.home.list_details.ListDetailsRoute
import dev.chara.tasks.android.ui.route.home.profile.UserProfileDialog
import dev.chara.tasks.android.ui.route.home.task_details.TaskDetailsRoute
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.home.HomeViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.io.ByteArrayOutputStream

@Composable
fun HomeRoute(
    useDualPane: Boolean,
    initialNavTarget: NavTarget.Home,
    navigateToWelcome: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToChangeEmail: () -> Unit,
    navigateToChangePassword: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initNotifications(context)
        }
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

    val coroutineScope = rememberCoroutineScope()
    val selectProfilePhoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // TODO should I do this in a different class?
            coroutineScope.launch {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)
                viewModel.updateUserProfilePhoto(stream.toByteArray())
            }
        }
    }

    if (!state.value.firstLoad) {
        if (!state.value.isAuthenticated) {
            navigateToWelcome()
            return
        }

        val profile = state.value.profile!!

        var showProfileDialog by remember { mutableStateOf(false) }

        if (showProfileDialog) {
            UserProfileDialog(
                profile,
                onDismiss = {
                    showProfileDialog = false
                },
                onChangePhotoClicked = {
                    selectProfilePhoto.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onChangeEmailClicked = {
                    showProfileDialog = false
                    navigateToChangeEmail()
                },
                onChangePasswordClicked = {
                    showProfileDialog = false
                    navigateToChangePassword()
                },
                onUpdateUserProfile = {
                    viewModel.updateUserProfile(it)
                }
            )
        }

        var showCreateListDialog by remember { mutableStateOf(false) }

        if (showCreateListDialog) {
            ModifyListDialog(
                title = "New list",
                current = TaskList(id = "", title = ""),
                onDismiss = {
                    showCreateListDialog = false
                },
                onSave = { taskList ->
                    viewModel.createList(taskList)
                    showCreateListDialog = false
                }
            )
        }

        var showCreateTaskDialog by remember { mutableStateOf(false) }
        var defaultListIdForCreatedTask by remember { mutableStateOf<String?>(null) }

        if (showCreateTaskDialog) {
            CreateTaskDialog(
                taskLists = state.value.allLists,
                current = Task(
                    id = "",
                    listId = defaultListIdForCreatedTask ?: state.value.allLists.first().id,
                    label = "",
                    lastModified = Clock.System.now()
                ),
                onDismiss = {
                    showCreateTaskDialog = false
                },
                onSave = { task ->
                    viewModel.createTask(task.listId, task)
                    showCreateTaskDialog = false
                }
            )
        }

        var isListShown by rememberSaveable { mutableStateOf(initialNavTarget is NavTarget.Home.WithList) }
        var selectedListId by rememberSaveable(initialNavTarget) {
            mutableStateOf(
                if (initialNavTarget is NavTarget.Home.WithList) {
                    initialNavTarget.listId
                } else {
                    ""
                }
            )
        }

        var isTaskShown by rememberSaveable { mutableStateOf(initialNavTarget is NavTarget.Home.WithTask) }
        var selectedTaskId by rememberSaveable(initialNavTarget) {
            mutableStateOf(
                if (initialNavTarget is NavTarget.Home.WithTask) {
                    initialNavTarget.taskId
                } else {
                    ""
                }
            )
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
                BoxWithConstraints {
                    val detailPaneWidth by animateDpAsState(
                        if (isListShown || isTaskShown) maxWidth / 2 else 0.dp,
                        label = "detailPane"
                    )
                    val homePaneWidth = maxWidth - detailPaneWidth

                    HomeScreenWithDetailPane(
                        state = state.value,
                        showCreateTaskButton = state.value.allLists.isNotEmpty(),
                        onAccountPressed = { showProfileDialog = true },
                        onSettingsPressed = navigateToSettings,
                        onSignOutPressed = {
                            viewModel.logout()
                        },
                        onCreateListPressed = {
                            showCreateListDialog = true
                        },
                        onCreateTaskPressed = {
                            showCreateTaskDialog = true
                        },
                        navigateToListDetails = { taskList ->
                            selectedListId = taskList.id
                            isListShown = true
                        },
                        navigateToTaskDetails = { task ->
                            selectedTaskId = task.id
                            isTaskShown = true
                        },
                        onUpdateTask = { task ->
                            viewModel.updateTask(task)
                        },
                        onRefresh = {
                            viewModel.refreshCache()
                        },
                        homePaneWidth = homePaneWidth,
                        detailPaneWidth = detailPaneWidth
                    ) {
                        Crossfade(targetState = isTaskShown, label = "showTask") { showTaskState ->
                            if (showTaskState) {
                                TaskDetailsRoute(
                                    taskId = selectedTaskId,
                                    upAsCloseButton = !isListShown,
                                    navigateUp = {
                                        isTaskShown = false
                                    }
                                )
                            } else {
                                ListDetailsRoute(
                                    selectedListId,
                                    snackbarHostState = snackbarHostState,
                                    upAsCloseButton = true,
                                    navigateUp = { isListShown = false },
                                    navigateToTaskDetails = { task ->
                                        selectedTaskId = task.id
                                        isTaskShown = true
                                    },
                                    onCreateTaskClicked = {
                                        defaultListIdForCreatedTask = it.id
                                        showCreateTaskDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                val inputState = if (isTaskShown) {
                    ScreenState.SHOW_TASK
                } else if (isListShown) {
                    ScreenState.SHOW_LIST
                } else {
                    ScreenState.HOME
                }

                Crossfade(targetState = inputState, label = "ScreenState") { screenState ->
                    when (screenState) {
                        ScreenState.SHOW_TASK -> {
                            TaskDetailsRoute(
                                taskId = selectedTaskId,
                                upAsCloseButton = false,
                                navigateUp = {
                                    isTaskShown = false
                                }
                            )

                            BackHandler {
                                isTaskShown = false
                            }
                        }

                        ScreenState.SHOW_LIST -> {
                            ListDetailsRoute(
                                selectedListId,
                                snackbarHostState = snackbarHostState,
                                upAsCloseButton = false,
                                navigateUp = { isListShown = false },
                                navigateToTaskDetails = { task ->
                                    selectedTaskId = task.id
                                    isTaskShown = true
                                },
                                onCreateTaskClicked = {
                                    defaultListIdForCreatedTask = it.id
                                    showCreateTaskDialog = true
                                }
                            )

                            BackHandler {
                                isListShown = false
                            }
                        }

                        else -> {
                            HomeScreen(
                                state = state.value,
                                showCreateTaskButton = state.value.allLists.isNotEmpty(),
                                onAccountPressed = { showProfileDialog = true },
                                onSettingsPressed = navigateToSettings,
                                onSignOutPressed = {
                                    viewModel.logout()
                                },
                                onCreateListPressed = {
                                    showCreateListDialog = true
                                },
                                onCreateTaskPressed = {
                                    showCreateTaskDialog = true
                                },
                                navigateToListDetails = { taskList ->
                                    selectedListId = taskList.id
                                    isListShown = true
                                },
                                navigateToTaskDetails = { task ->
                                    selectedTaskId = task.id
                                    isTaskShown = true
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

/**
 * Used for single pane screen transitions.
 */
enum class ScreenState {
    HOME,
    SHOW_LIST,
    SHOW_TASK
}

fun initNotifications(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = context.getString(R.string.notification_channel_reminders)
        val mChannel =
            NotificationChannel(channelId, "Reminders", NotificationManager.IMPORTANCE_DEFAULT)

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}
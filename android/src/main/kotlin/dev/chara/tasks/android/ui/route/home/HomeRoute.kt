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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.android.R
import dev.chara.tasks.android.model.navTarget
import dev.chara.tasks.android.ui.RootNavTarget
import dev.chara.tasks.android.ui.component.dialog.CreateTaskDialog
import dev.chara.tasks.android.ui.component.util.MaterialDialog
import dev.chara.tasks.android.ui.component.util.SnackbarLayout
import dev.chara.tasks.android.ui.route.home.list_details.ListDetailsRoute
import dev.chara.tasks.android.ui.route.home.profile.UserProfileDialog
import dev.chara.tasks.android.ui.route.home.task_details.TaskDetailsRoute
import dev.chara.tasks.model.StartScreen
import dev.chara.tasks.model.Task
import dev.chara.tasks.viewmodel.home.HomeViewModel
import dev.olshevski.navigation.reimagined.rememberNavController
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.io.ByteArrayOutputStream

@Composable
fun HomeRoute(
    useNavRail: Boolean,
    useEditDialog: Boolean,
    useDualPane: Boolean,
    initialNavTarget: RootNavTarget.Home,
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

        val profile = state.value.profile!!

        var showProfileDialog by remember { mutableStateOf(false) }

        if (showProfileDialog) {
            UserProfileDialog(
                profile,
                onDismiss = {
                    showProfileDialog = false
                },
                onSettingsClicked = {
                    showProfileDialog = false
                    navigateToSettings()
                },
                onLogoutClicked = viewModel::logout,
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

        var showCreateTaskDialog by remember { mutableStateOf(false) }
        var defaultListIdForCreatedTask by remember(state.value.taskLists) { mutableStateOf(state.value.taskLists.firstOrNull()?.id) }

        if (showCreateTaskDialog && defaultListIdForCreatedTask != null) {
            CreateTaskDialog(
                taskLists = state.value.taskLists,
                current = Task(
                    id = "",
                    listId = defaultListIdForCreatedTask!!,
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

        val startScreen = state.value.startScreen

        val allDestinations = StartScreen.values().map { it.navTarget }

        val navController = rememberNavController(startDestination = startScreen.navTarget)

        var isListShown by rememberSaveable { mutableStateOf(initialNavTarget is RootNavTarget.Home.WithList) }
        var selectedListId by rememberSaveable(initialNavTarget) {
            mutableStateOf(
                if (initialNavTarget is RootNavTarget.Home.WithList) {
                    initialNavTarget.listId
                } else {
                    ""
                }
            )
        }

        var isTaskShown by rememberSaveable { mutableStateOf(initialNavTarget is RootNavTarget.Home.WithTask) }
        var selectedTaskId by rememberSaveable(initialNavTarget) {
            mutableStateOf(
                if (initialNavTarget is RootNavTarget.Home.WithTask) {
                    initialNavTarget.taskId
                } else {
                    ""
                }
            )
        }

        if (isTaskShown && useEditDialog) {
            MaterialDialog(
                semanticTitle = "Task details",
                modifier = Modifier.fillMaxSize(),
                onClose = { isTaskShown = false }
            ) {
                TaskDetailsRoute(
                    selectedTaskId,
                    upAsCloseButton = true,
                    navigateUp = { isTaskShown = false }
                )
            }
        }

        val snackbarBottomOffset = with(LocalDensity.current) {
            if (useNavRail && !isListShown) {
                0.dp.roundToPx()
            } else if (useDualPane) {
                // see HomeScreenWithDetailPane
                96.dp.roundToPx()
            } else {
                // see M3 BottomNavigationTokens
                80.dp.roundToPx()
            }
        }

        SnackbarLayout(
            snackbarBottomOffset = snackbarBottomOffset,
            snackbar = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) {
            Crossfade(targetState = isTaskShown, label = "showTask") { isTaskShownState ->
                if (isTaskShownState && !useEditDialog) {
                    TaskDetailsRoute(
                        selectedTaskId,
                        upAsCloseButton = false,
                        navigateUp = { isTaskShown = false }
                    )

                    BackHandler {
                        isTaskShown = false
                    }
                } else {
                    if (useDualPane) {
                        BoxWithConstraints {
                            val detailPaneWidth by animateDpAsState(if (isListShown) maxWidth / 2 else 0.dp,
                                label = "detailPane"
                            )
                            val homePaneWidth = maxWidth - detailPaneWidth

                            HomeScreenWithDetailPane(
                                destinations = allDestinations,
                                navController = navController,
                                state = state.value,
                                snackbarHostState = snackbarHostState,
                                useNavRail = useNavRail,
                                showCreateButton = defaultListIdForCreatedTask != null,
                                onAccountPressed = { showProfileDialog = true },
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
                                homePaneWidth = homePaneWidth,
                                detailPaneWidth = detailPaneWidth
                            ) {
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
                    } else {
                        Crossfade(targetState = isListShown, label = "showList") { isListShownState ->
                            if (isListShownState) {
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
                            } else {
                                HomeScreen(
                                    destinations = allDestinations,
                                    navController = navController,
                                    state = state.value,
                                    snackbarHostState = snackbarHostState,
                                    useNavRail = useNavRail,
                                    showCreateButton = defaultListIdForCreatedTask != null,
                                    onAccountPressed = { showProfileDialog = true },
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
                                    }
                                )
                            }
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
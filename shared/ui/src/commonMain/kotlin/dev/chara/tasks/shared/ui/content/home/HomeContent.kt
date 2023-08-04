package dev.chara.tasks.shared.ui.content.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidx.material3.polyfill.AlertDialog
import com.androidx.material3.polyfill.DropdownMenu
import com.androidx.material3.polyfill.DropdownMenuItem
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.chara.tasks.shared.component.home.HomeComponent
import dev.chara.tasks.shared.model.Profile
import dev.chara.tasks.shared.ui.content.home.sheet.CreateTaskContent
import dev.chara.tasks.shared.ui.content.home.sheet.ModifyListContent
import dev.chara.tasks.shared.ui.item.ProfileImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    component: HomeComponent,
    windowSizeClass: WindowSizeClass
) {
    val children by component.children.subscribeAsState()

    val displayedSheet by component.displayedSheet.subscribeAsState()

    val state = component.state.collectAsState()

    LaunchedEffect(windowSizeClass) {
        component.setDualPane(windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded)
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }

    val saveableStateHolder = rememberSaveableStateHolder()

    val coroutineScope = rememberCoroutineScope()

    var showVerifyEmailDialog by remember { mutableStateOf(false) }

    if (showVerifyEmailDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Verify your email") },
            text = { Text("To unlock the full functionality of Tasks, please verify your email address.") },
            dismissButton = {
                TextButton(onClick = { showVerifyEmailDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    component.requestVerifyEmailResend()
                    showVerifyEmailDialog = false
                }) {
                    Text("Verify")
                }
            }
        )
    }

    if (state.value.verifyEmailSent) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Verification email sent") },
            text = { Text("Follow the link provided to verify your email address.") },
            confirmButton = {
                TextButton(onClick = {
                    component.clearVerifyEmailNotice()
                }) {
                    Text("OK")
                }
            }
        )
    }

    val mainContent: @Composable (Child.Created<*, HomeComponent.Child.Main>) -> Unit =
        remember {
            movableContentOf { (_, child) ->
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        TopBar(
                            scrollBehavior,
                            state.value.profile,
                            getGravatarUri = { email -> component.getGravatarUri(email) },
                            onNotificationsClicked = {
                                if (state.value.profile?.emailVerified == false) {
                                    showVerifyEmailDialog = true
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("No notifications")
                                    }
                                }
                            },
                            onEditProfileClicked = {
                                component.onProfile()
                            },
                            onSettingsClicked = {
                                component.onSettings()
                            },
                            onSignOutClicked = {
                                component.signOut()
                            }
                        )
                    }
                ) { innerPadding ->
                    val paddingTop = PaddingValues(top = innerPadding.calculateTopPadding())
                    val paddingBottom =
                        PaddingValues(bottom = innerPadding.calculateBottomPadding())

                    when (child) {
                        is HomeComponent.Child.Main.Dashboard -> DashboardContent(
                            child.component,
                            paddingTop,
                            paddingBottom,
                            scrollBehavior.nestedScrollConnection
                        )
                    }
                }
            }
        }

    val stackContent: @Composable (List<Child.Created<*, HomeComponent.Child.Stack>>) -> Unit =
        remember {
            movableContentOf { stack ->
                AnimatedContent(
                    targetState = stack,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { targetStack ->
                    if (targetStack.isNotEmpty()) {
                        when (val child = targetStack.last().instance) {
                            is HomeComponent.Child.Stack.ListDetails -> ListDetailsContent(
                                component = child.component,
                                upAsCloseButton = children.isDualPane
                            )

                            is HomeComponent.Child.Stack.TaskDetails -> TaskDetailsContent(
                                component = child.component
                            )
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                "Select a list or task",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }

    if (displayedSheet.child != null) {
        val (config, child) = displayedSheet.child!!

        saveableStateHolder.SaveableStateProvider(key = config) {
            when (child) {
                is HomeComponent.Sheet.ModifyList -> ModifyListContent(component = child.component)
                is HomeComponent.Sheet.CreateTask -> CreateTaskContent(component = child.component)
            }
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(0.5F)) {
            AnimatedContent(
                targetState = !children.isDualPane && children.stack.isNotEmpty(),
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { showStack ->
                if (showStack) {
                    stackContent(children.stack)
                } else {
                    mainContent(children.main)
                }
            }
        }

        if (children.isDualPane) {
            Surface(modifier = Modifier.weight(0.5F)) {
                Surface(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 1.dp
                ) {
                    stackContent(children.stack)
                }
            }
        }
    }

    LaunchedEffect(component.messages) {
        component.messages.collect { message ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    profile: Profile?,
    getGravatarUri: (String) -> String,
    onNotificationsClicked: () -> Unit,
    onEditProfileClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onSignOutClicked: () -> Unit
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(text = "Tasks")
        },
        actions = {
            IconButton(
                onClick = onNotificationsClicked,
            ) {
                BadgedBox(badge = {
                    if (profile?.emailVerified == false) {
                        Badge(modifier = Modifier.offset(x = (-4).dp, y = 4.dp))
                    }
                }) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                }
            }
            Box {
                IconButton(onClick = { showOverflowMenu = true }) {
                    ProfileImage(
                        profile?.email,
                        profile?.profilePhotoUri,
                        getGravatarUri = getGravatarUri,
                        modifier = Modifier.requiredSize(28.dp)
                    )
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
            }
        },
        scrollBehavior = scrollBehavior
    )
}

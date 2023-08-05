package dev.chara.tasks.shared.ui.content.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.androidx.material3.polyfill.AlertDialog
import com.github.michaelbull.result.mapBoth
import dev.chara.tasks.shared.component.profile.ProfileComponent
import dev.chara.tasks.shared.ui.item.ProfileImage
import dev.chara.tasks.shared.ui.picker.photoPicker
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHigh
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHighest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileContent(component: ProfileComponent) {
    val state = component.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val selectPhotoAction = photoPicker { result ->
        result.mapBoth(
            success = { component.uploadProfilePhoto(it) },
            failure = { Logger.d("Error occurred while uploading photo: ", it) },
        )
    }

    var showExitDialog by remember { mutableStateOf(false) }

    if (showExitDialog) {
        ConfirmExitDialog(
            onDismiss = { showExitDialog = false },
            onConfirm = {
                showExitDialog = false
                component.onUp()
            }
        )
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val scrollState = rememberScrollState()

    var displayName by
        rememberSaveable(state.value.profile!!.displayName) {
            mutableStateOf(state.value.profile!!.displayName)
        }

    var modified by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit profile") },
                navigationIcon = {
                    IconButton(onClick = { component.onUp() }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.padding(WindowInsets.ime.asPaddingValues())) {
                Spacer(Modifier.weight(1f, true))

                Button(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    onClick = {
                        component.updateUserProfile(
                            state.value.profile!!.copy(
                                displayName = displayName,
                                profilePhotoUri = state.value.profile?.profilePhotoUri
                            )
                        )
                        modified = false
                    },
                    enabled = !state.value.isUploading && displayName.isNotBlank() && modified
                ) {
                    Text(text = "Save")
                }
            }
        },
        content = { innerPadding ->
            val paddingTop = PaddingValues(top = innerPadding.calculateTopPadding())
            val paddingBottom = PaddingValues(bottom = innerPadding.calculateBottomPadding())

            Column(
                modifier =
                    Modifier.padding(paddingTop)
                        .consumeWindowInsets(paddingTop)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(paddingBottom)
            ) {
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    ListItem(
                        colors =
                            ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                        headlineContent = { Text(displayName) },
                        supportingContent = { Text(state.value.profile!!.email) },
                        leadingContent = {
                            ProfileImage(
                                email = state.value.profile!!.email,
                                profilePhotoUri = state.value.profile!!.profilePhotoUri,
                                getGravatarUri = { component.getGravatarUri(it) },
                                modifier = Modifier.requiredSize(48.dp)
                            )
                        }
                    )
                }

                OutlinedTextField(
                    modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth(),
                    value = displayName,
                    singleLine = true,
                    onValueChange = {
                        displayName = it
                        modified = true
                    },
                    label = { Text(text = "Display Name") },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Person",
                        )
                    },
                    trailingIcon = {
                        if (displayName != state.value.profile!!.displayName) {
                            IconButton(
                                onClick = {
                                    displayName = state.value.profile!!.displayName
                                    modified = false
                                }
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset")
                            }
                        }
                    }
                )

                ListItem(
                    headlineContent = {
                        if (state.value.profile!!.profilePhotoUri != null) {
                            Text("Change profile picture")
                        } else {
                            Text("Add profile picture")
                        }
                    },
                    leadingContent = { Icon(Icons.Filled.AddCircle, contentDescription = "Add") },
                    modifier =
                        Modifier.padding(horizontal = 16.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .clickable { selectPhotoAction() }
                )

                if (state.value.profile!!.profilePhotoUri != null) {
                    ListItem(
                        headlineContent = { Text("Remove profile picture") },
                        leadingContent = {
                            Icon(Icons.Filled.RemoveCircle, contentDescription = "Remove")
                        },
                        modifier =
                            Modifier.padding(horizontal = 16.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                                .clickable {
                                    component.updateUserProfile(
                                        state.value.profile!!.copy(profilePhotoUri = null)
                                    )
                                }
                    )
                }

                ListItem(
                    headlineContent = { Text(state.value.profile!!.email) },
                    supportingContent = {
                        if (state.value.profile!!.emailVerified) {
                            Text("Tap to change")
                        } else {
                            Text("Unverified")
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Filled.AlternateEmail, contentDescription = "Email")
                    },
                    modifier =
                        Modifier.padding(horizontal = 16.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .clickable(enabled = state.value.profile?.emailVerified == true) {
                                component.onChangeEmail()
                            }
                )

                if (state.value.profile!!.emailVerified) {
                    ListItem(
                        headlineContent = { Text("Change password") },
                        leadingContent = {
                            Icon(Icons.Filled.Password, contentDescription = "Email")
                        },
                        modifier =
                            Modifier.padding(horizontal = 16.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                                .clickable(enabled = state.value.profile?.emailVerified == true) {
                                    component.onChangePassword()
                                }
                    )
                }
            }
        }
    )

    LaunchedEffect(component.messages) {
        component.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.text,
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
        }
    }
}

@Composable
private fun ConfirmExitDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Close without saving?") },
        text = { Text(text = "Your changes to your profile will not be saved") },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Close") } }
    )
}

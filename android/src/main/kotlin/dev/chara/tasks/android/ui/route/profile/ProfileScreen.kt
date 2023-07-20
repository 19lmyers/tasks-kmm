package dev.chara.tasks.android.ui.route.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.ui.component.ProfileImage
import dev.chara.tasks.model.Profile
import dev.chara.tasks.viewmodel.profile.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    snackbarHostState: SnackbarHostState,
    navigateUp: (Boolean) -> Unit,
    onChangePhotoClicked: () -> Unit,
    onChangeEmailClicked: () -> Unit,
    onChangePasswordClicked: () -> Unit,
    onUpdateProfile: (Profile) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val scrollState = rememberScrollState()

    var displayName by rememberSaveable(state.profile!!.displayName) {
        mutableStateOf(state.profile!!.displayName)
    }

    var modified by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                onUpClicked = {
                    navigateUp(modified)
                }
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.imePadding()) {
                Spacer(Modifier.weight(1f, true))

                Button(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    onClick = {
                        onUpdateProfile(
                            state.profile!!.copy(
                                displayName = displayName,
                                profilePhotoUri = state.profile!!.profilePhotoUri
                            )
                        )
                        modified = false
                    },
                    enabled = !state.isLoading && displayName.isNotBlank() && modified
                ) {
                    Text(text = "Save")
                }
            }
        },
        content = { innerPadding ->
            val paddingTop = PaddingValues(top = innerPadding.calculateTopPadding())
            val paddingBottom = PaddingValues(bottom = innerPadding.calculateBottomPadding())

            Column(
                modifier = Modifier
                    .padding(paddingTop)
                    .consumeWindowInsets(paddingTop)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingBottom)
            ) {
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        headlineContent = {
                            Text(displayName)
                        },
                        supportingContent = {
                            Text(state.profile!!.email)
                        },
                        leadingContent = {
                            ProfileImage(
                                email = state.profile!!.email,
                                profilePhotoUri = state.profile!!.profilePhotoUri,
                                modifier = Modifier
                                    .requiredSize(48.dp)
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .clickable {
                                        onChangePhotoClicked()
                                    }
                            )
                        }
                    )
                }

                OutlinedTextField(
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .fillMaxWidth(),
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
                        if (displayName != state.profile!!.displayName) {
                            IconButton(onClick = {
                                displayName = state.profile!!.displayName
                                modified = false
                            }) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset")
                            }
                        }
                    }
                )

                ListItem(
                    headlineContent = {
                        if (state.profile!!.profilePhotoUri != null) {
                            Text("Change profile picture")
                        } else {
                            Text("Add profile picture")
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Add")
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            onChangePhotoClicked()
                        }
                )

                if (state.profile!!.profilePhotoUri != null) {
                    ListItem(
                        headlineContent = {
                            Text("Remove profile picture")
                        },
                        leadingContent = {
                            Icon(Icons.Filled.RemoveCircle, contentDescription = "Remove")
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .clickable {
                                onUpdateProfile(
                                    state.profile!!.copy(
                                        profilePhotoUri = null
                                    )
                                )
                            }
                    )
                }

                ListItem(
                    headlineContent = {
                        Text(state.profile!!.email)
                    },
                    supportingContent = {
                        if (state.profile!!.emailVerified) {
                            Text("Tap to change")
                        } else {
                            Text("Unverified")
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Filled.AlternateEmail, contentDescription = "Email")
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable(enabled = state.profile!!.emailVerified) {
                            onChangeEmailClicked()
                        }
                )

                if (state.profile!!.emailVerified) {
                    ListItem(
                        headlineContent = {
                            Text("Change password")
                        },
                        leadingContent = {
                            Icon(Icons.Filled.Password, contentDescription = "Email")
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .clickable(enabled = state.profile!!.emailVerified) {
                                onChangePasswordClicked()
                            }
                    )
                }
            }
        }
    )

    BackHandler {
        navigateUp(modified)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onUpClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Edit profile")
        },
        navigationIcon = {
            IconButton(onClick = { onUpClicked() }) {
                Icon(Icons.Filled.Close, contentDescription = "Cancel")
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Preview
@Composable
private fun Preview_UserProfileScreen() {
    ProfileScreen(
        state = ProfileUiState(
            isLoading = false,
            profile = Profile(
                id = "1",
                email = "username@email.com",
                displayName = "User McUserface",
                profilePhotoUri = null
            )
        ),
        snackbarHostState = SnackbarHostState(),
        navigateUp = {},
        onChangePhotoClicked = {},
        onChangeEmailClicked = {},
        onChangePasswordClicked = {},
        onUpdateProfile = {}
    )
}
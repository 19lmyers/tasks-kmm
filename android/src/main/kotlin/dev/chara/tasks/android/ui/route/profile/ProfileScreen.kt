package dev.chara.tasks.android.ui.route.profile

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
    navigateUp: () -> Unit,
    onChangePhotoClicked: () -> Unit,
    onChangeEmailClicked: () -> Unit,
    onChangePasswordClicked: () -> Unit,
    onUpdateProfile: (Profile) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val scrollState = rememberScrollState()

    val displayName =
        rememberSaveable(state.profile!!.displayName) { mutableStateOf(state.profile!!.displayName) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(scrollBehavior = scrollBehavior, onUpClicked = navigateUp)
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.imePadding()) {
                Spacer(Modifier.weight(1f, true))

                Button(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    onClick = {
                        onUpdateProfile(
                            state.profile!!.copy(
                                displayName = displayName.value,
                                profilePhotoUri = state.profile!!.profilePhotoUri
                            )
                        )
                    },
                    enabled = !state.isLoading && displayName.value.isNotBlank()
                            && displayName.value != state.profile!!.displayName
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
                    tonalElevation = 1.dp
                ) {
                    ListItem(
                        headlineContent = {
                            Text(state.profile!!.displayName)
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
                    value = displayName.value,
                    singleLine = true,
                    onValueChange = {
                        displayName.value = it
                    },
                    label = { Text(text = "Display Name") },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Person",
                        )
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
                                onUpdateProfile(state.profile!!.copy(
                                    profilePhotoUri = null
                                ))
                            }
                    )
                }

                ListItem(
                    headlineContent = {
                        Text("Change email")
                    },
                    leadingContent = {
                        Icon(Icons.Filled.AlternateEmail, contentDescription = "Email")
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            onChangeEmailClicked()
                        }
                )

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
                        .clickable {
                            onChangePasswordClicked()
                        }
                )
            }
        }
    )
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
                Icon(Icons.Filled.ArrowBack, contentDescription = "Navigate up")
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
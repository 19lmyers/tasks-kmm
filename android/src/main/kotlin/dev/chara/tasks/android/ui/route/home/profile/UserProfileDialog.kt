package dev.chara.tasks.android.ui.route.home.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.ui.component.ProfileImage
import dev.chara.tasks.android.ui.component.util.MaterialDialog
import dev.chara.tasks.model.Profile

@Composable
fun UserProfileDialog(
    userProfile: Profile,
    onDismiss: () -> Unit,
    onSettingsClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onChangePhotoClicked: () -> Unit,
    onChangeEmailClicked: () -> Unit,
    onChangePasswordClicked: () -> Unit,
    onUpdateUserProfile: (Profile) -> Unit,
    defaultEditMode: Boolean = false
) {
    var showEditNameDialog by remember { mutableStateOf(false) }

    if (showEditNameDialog) {
        EditNameDialog(
            userProfile.displayName,
            onDismiss = {
                showEditNameDialog = false
            },
            onUpdate = {
                onUpdateUserProfile(userProfile.copy(displayName = it))
                showEditNameDialog = false
            }
        )
    }

    var editMode by remember { mutableStateOf(defaultEditMode) }

    MaterialDialog(semanticTitle = userProfile.displayName, onClose = { onDismiss() }) {
        Column(
            modifier = Modifier
                .sizeIn(minWidth = 320.dp, maxWidth = 720.dp)
                .padding(vertical = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ProfileImage(
                    email = userProfile.email,
                    profilePhotoUri = userProfile.profilePhotoUri,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(bottom = 16.dp)
                        .requiredSize(48.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            if (editMode) {
                                onChangePhotoClicked()
                            } else {
                                editMode = true
                            }
                        }
                )
                if (editMode) {
                    IconButton(
                        onClick = { editMode = false },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 16.dp)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                    IconButton(
                        onClick = {
                            if (userProfile.profilePhotoUri == null) {
                                onChangePhotoClicked()
                            } else {
                                onUpdateUserProfile(userProfile.copy(profilePhotoUri = null))
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 16.dp)
                    ) {
                        if (userProfile.profilePhotoUri == null) {
                            Icon(
                                Icons.Filled.AddPhotoAlternate,
                                contentDescription = "Add profile picture"
                            )
                        } else {
                            Icon(
                                Icons.Filled.HideImage,
                                contentDescription = "Remove profile picture"
                            )
                        }
                    }
                }
            }
            if (editMode) {
                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            showEditNameDialog = true
                        },
                    headlineContent = {
                        Text(
                            text = userProfile.displayName,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    supportingContent = {
                        Text(text = "Edit name")
                    },
                    leadingContent = {
                        Icon(Icons.Filled.Person, contentDescription = "Display Name")
                    }
                )
                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            //onChangeEmailClicked() TODO
                        },
                    headlineContent = {
                        Text(
                            text = userProfile.email,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    supportingContent = {
                        //Text(text = "Change email") TODO
                    },
                    leadingContent = {
                        Icon(Icons.Filled.Email, contentDescription = "Email")
                    }
                )
                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            onChangePasswordClicked()
                        },
                    headlineContent = {
                        Text(text = "Password")
                    },
                    supportingContent = {
                        Text(text = "Change password")
                    },
                    leadingContent = {
                        Icon(Icons.Filled.Password, contentDescription = "Password")
                    }
                )
            } else {
                Text(
                    text = userProfile.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                )
                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            editMode = true
                        },
                    headlineContent = {
                        Text(text = "Edit profile")
                    },
                    leadingContent = {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                )
                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            onSettingsClicked()
                        },
                    headlineContent = {
                        Text(text = "Settings")
                    },
                    leadingContent = {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                )
                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            onLogoutClicked()
                        },
                    headlineContent = {
                        Text(text = "Log out")
                    },
                    leadingContent = {
                        Icon(Icons.Filled.Logout, contentDescription = "Log out")
                    }
                )
            }
        }

        BackHandler(enabled = editMode) {
            editMode = false
        }
    }
}

@Preview
@Composable
private fun Preview_AccountInfoDialog() {
    UserProfileDialog(
        userProfile = Profile(
            email = "username@email.com",
            displayName = "User McUserface",
            profilePhotoUri = null
        ),
        onDismiss = {},
        onSettingsClicked = {},
        onLogoutClicked = {},
        onChangePhotoClicked = {},
        onChangeEmailClicked = {},
        onChangePasswordClicked = {},
        onUpdateUserProfile = {}
    )
}

@Preview
@Composable
private fun Preview_AccountInfoDialog_EditMode() {
    UserProfileDialog(
        userProfile = Profile(
            email = "username@email.com",
            displayName = "User McUserface",
            profilePhotoUri = null
        ),
        onDismiss = {},
        onSettingsClicked = {},
        onLogoutClicked = {},
        onChangePhotoClicked = {},
        onChangeEmailClicked = {},
        onChangePasswordClicked = {},
        onUpdateUserProfile = {},
        defaultEditMode = true,
    )
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditNameDialog(
    oldDisplayName: String,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var displayName by remember { mutableStateOf(oldDisplayName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Edit name")
        },
        text = {
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusRequester = remember { FocusRequester() }

            OutlinedTextField(
                modifier = Modifier
                    .padding(16.dp, 8.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = displayName,
                singleLine = true,
                onValueChange = { displayName = it },
                label = { Text(text = "Name") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions {
                    keyboardController?.hide()
                    onUpdate(displayName)
                }
            )

            LaunchedEffect(focusRequester) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onUpdate(displayName)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
private fun Preview_EditNameDialog() {
    EditNameDialog(
        oldDisplayName = "User McUserface",
        onDismiss = {},
        onUpdate = {}
    )
}
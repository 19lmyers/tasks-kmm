package dev.chara.tasks.android.ui.route.home.profile

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
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
    onChangePhotoClicked: () -> Unit,
    onChangeEmailClicked: () -> Unit,
    onChangePasswordClicked: () -> Unit,
    onUpdateUserProfile: (Profile) -> Unit,
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
                            onChangePhotoClicked()
                        }
                )
                IconButton(
                    onClick = {
                        onChangePhotoClicked()
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp)
                ) {
                    Icon(Icons.Filled.AddPhotoAlternate, contentDescription = "Add profile picture")
                }
                IconButton(
                    onClick = {
                        onUpdateUserProfile(userProfile.copy(profilePhotoUri = null))
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp)
                ) {
                    Icon(
                        Icons.Filled.HideImage,
                        contentDescription = "Remove profile picture"
                    )
                }
            }

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
        }
    }
}

@Preview
@Composable
private fun Preview_AccountInfoDialog() {
    UserProfileDialog(
        userProfile = Profile(
            id = "1",
            email = "username@email.com",
            displayName = "User McUserface",
            profilePhotoUri = null
        ),
        onDismiss = {},
        onChangePhotoClicked = {},
        onChangeEmailClicked = {},
        onChangePasswordClicked = {},
        onUpdateUserProfile = {}
    )
}

@OptIn(ExperimentalComposeUiApi::class)
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
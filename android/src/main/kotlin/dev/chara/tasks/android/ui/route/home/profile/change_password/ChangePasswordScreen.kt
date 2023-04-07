package dev.chara.tasks.android.ui.route.home.profile.change_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.viewmodel.profile.change_password.ChangePasswordUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChangePasswordScreen(
    state: ChangePasswordUiState,
    snackbarHostState: SnackbarHostState,
    onUpClicked: () -> Unit,
    onChangePasswordClicked: (String, String) -> Unit,
) {
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Change password") },
                navigationIcon = {
                    IconButton(onClick = onUpClicked) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Navigate up")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                ChangePasswordForm(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    onCurrentPasswordChanged = { currentPassword = it },
                    onNewPasswordChanged = { newPassword = it },
                    changePasswordPending = state.isLoading,
                    onChangePasswordClicked = {
                        onChangePasswordClicked(
                            currentPassword,
                            newPassword
                        )
                    }
                )
            }
        })
}

@Preview
@Composable
private fun Preview_SignUpScreen() {
    ChangePasswordScreen(
        ChangePasswordUiState(),
        snackbarHostState = SnackbarHostState(),
        onUpClicked = {},
        onChangePasswordClicked = { _, _ -> },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun ChangePasswordForm(
    currentPassword: String,
    newPassword: String,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    changePasswordPending: Boolean,
    onChangePasswordClicked: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = currentPassword,
        singleLine = true,
        onValueChange = onCurrentPasswordChanged,
        label = { Text(text = "Current Password") },
        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password, imeAction = ImeAction.Next,
        ),
        trailingIcon = {
            val image = if (currentPasswordVisible) Icons.Filled.VisibilityOff
            else Icons.Filled.Visibility

            val description = if (currentPasswordVisible) "Hide password" else "Show password"

            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )

    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth(),
        value = newPassword,
        singleLine = true,
        onValueChange = onNewPasswordChanged,
        label = { Text(text = "New Password") },
        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (currentPassword.isNotBlank() && !changePasswordPending) {
                    keyboardController?.hide()
                    onChangePasswordClicked()
                }
            }
        ),
        trailingIcon = {
            val image = if (newPasswordVisible) Icons.Filled.VisibilityOff
            else Icons.Filled.Visibility

            val description = if (newPasswordVisible) "Hide password" else "Show password"

            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )

    FilledTonalButton(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth(),
        onClick = {
            keyboardController?.hide()
            onChangePasswordClicked()
        },
        enabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && newPassword != currentPassword && !changePasswordPending
    ) {
        Text(text = "Change")
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}
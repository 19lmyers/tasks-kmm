package dev.chara.tasks.shared.ui.content.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import com.androidx.material3.polyfill.AlertDialog
import dev.chara.tasks.shared.component.profile.change_password.ChangePasswordComponent

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ChangePasswordContent(component: ChangePasswordComponent) {
    val state = component.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value.passwordChanged) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Password changed") },
            confirmButton = { TextButton(onClick = { component.onConfirmUp() }) { Text("OK") } },
        )
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Change password") },
                navigationIcon = {
                    IconButton(onClick = { component.onUp() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Navigate up",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.padding(WindowInsets.ime.asPaddingValues())) {
                Spacer(Modifier.weight(1f, true))

                FilledTonalButton(
                    modifier = Modifier.padding(16.dp, 8.dp),
                    onClick = {
                        keyboardController?.hide()
                        component.changePassword(currentPassword, newPassword)
                    },
                    enabled =
                        currentPassword.isNotBlank() &&
                            newPassword.isNotBlank() &&
                            newPassword != currentPassword &&
                            !state.value.isLoading
                ) {
                    Text(text = "Change")
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.value.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                ChangePasswordForm(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    onCurrentPasswordChanged = { currentPassword = it },
                    onNewPasswordChanged = { newPassword = it },
                    changePasswordPending = state.value.isLoading,
                    onChangePasswordClicked = {
                        component.changePassword(currentPassword, newPassword)
                    }
                )
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

@OptIn(ExperimentalComposeUiApi::class)
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
        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth().focusRequester(focusRequester),
        value = currentPassword,
        singleLine = true,
        onValueChange = onCurrentPasswordChanged,
        label = { Text(text = "Current Password") },
        readOnly = changePasswordPending,
        visualTransformation =
            if (currentPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
            ),
        trailingIcon = {
            val image =
                if (currentPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility

            val description = if (currentPasswordVisible) "Hide password" else "Show password"

            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )

    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth(),
        value = newPassword,
        singleLine = true,
        onValueChange = onNewPasswordChanged,
        readOnly = changePasswordPending,
        label = { Text(text = "New Password") },
        visualTransformation =
            if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
        keyboardActions =
            KeyboardActions(
                onDone = {
                    if (currentPassword.isNotBlank() && !changePasswordPending) {
                        keyboardController?.hide()
                        onChangePasswordClicked()
                    }
                }
            ),
        trailingIcon = {
            val image =
                if (newPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility

            val description = if (newPasswordVisible) "Hide password" else "Show password"

            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

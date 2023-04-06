package dev.chara.tasks.android.ui.route.auth.sign_up

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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.viewmodel.auth.sign_up.SignUpUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SignUpScreen(
    state: SignUpUiState,
    snackbarHostState: SnackbarHostState,
    onUpClicked: () -> Unit,
    onSignUpClicked: (String, String, String) -> Unit,
    validateEmail: (String) -> Result<String>,
    validatePassword: (String) -> Result<String>
) {
    var email by rememberSaveable { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Sign up") },
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
                SignInForm(
                    email = email,
                    displayName = displayName,
                    password = password,
                    onEmailChanged = { email = it },
                    onDisplayNameChanged = { displayName = it },
                    onPasswordChanged = { password = it },
                    signUpPending = state.isLoading,
                    onSignInClicked = { onSignUpClicked(email, displayName, password) },
                    validateEmail = { validateEmail(it) },
                    validatePassword = { validatePassword(it) }
                )
            }
        })
}

@Preview
@Composable
private fun Preview_SignUpScreen() {
    SignUpScreen(
        SignUpUiState(),
        snackbarHostState = SnackbarHostState(),
        onUpClicked = {},
        onSignUpClicked = { _, _, _ -> },
        validateEmail = { Result.success("") },
        validatePassword = { Result.success("") }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun SignInForm(
    email: String,
    displayName: String,
    password: String,
    onEmailChanged: (String) -> Unit,
    onDisplayNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    signUpPending: Boolean,
    onSignInClicked: () -> Unit,
    validateEmail: (String) -> Result<String>,
    validatePassword: (String) -> Result<String>
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val emailResult = validateEmail(email)

    OutlinedTextField(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = email,
        singleLine = true,
        onValueChange = onEmailChanged,
        label = { Text(text = "Email") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
        ),
        isError = email.isNotEmpty() && emailResult.isFailure,
        supportingText = {
            if (email.isNotEmpty() && emailResult.isFailure) {
                Text(text = emailResult.exceptionOrNull()?.message ?: "Invalid email")
            }
        }
    )

    OutlinedTextField(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth(),
        value = displayName,
        singleLine = true,
        onValueChange = onDisplayNameChanged,
        label = { Text(text = "Display Name") },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        )
    )

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val passwordResult = validatePassword(password)

    OutlinedTextField(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth(),
        value = password,
        singleLine = true,
        onValueChange = onPasswordChanged,
        label = { Text(text = "Password") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (emailResult.isSuccess && passwordResult.isSuccess && !signUpPending) {
                    keyboardController?.hide()
                    onSignInClicked()
                }
            }
        ),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.VisibilityOff
            else Icons.Filled.Visibility

            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        },
        isError = password.isNotEmpty() && passwordResult.isFailure,
        supportingText = {
            if (password.isNotEmpty() && passwordResult.isFailure) {
                Text(text = passwordResult.exceptionOrNull()?.message ?: "Invalid password")
            }
        }
    )

    FilledTonalButton(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth(),
        onClick = {
            keyboardController?.hide()
            onSignInClicked()
        },
        enabled = emailResult.isSuccess && displayName.isNotBlank() && passwordResult.isSuccess && !signUpPending
    ) {
        Text(text = "Sign Up")
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}
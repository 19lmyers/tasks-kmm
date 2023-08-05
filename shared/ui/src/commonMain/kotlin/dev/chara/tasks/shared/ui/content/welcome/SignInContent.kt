package dev.chara.tasks.shared.ui.content.welcome

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
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.unwrapError
import dev.chara.tasks.shared.component.welcome.sign_in.SignInComponent

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun SignInContent(component: SignInComponent) {
    val state = component.state.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val emailResult = component.validate(email)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("Sign in") },
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
                TextButton(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    onClick = { component.onForgotPassword() },
                    enabled = !state.value.isLoading
                ) {
                    Text(text = "Forgot password?")
                }

                Spacer(Modifier.weight(1f, true))

                FilledTonalButton(
                    modifier = Modifier.padding(16.dp, 8.dp),
                    onClick = {
                        keyboardController?.hide()
                        component.signIn(email, password)
                    },
                    enabled = emailResult is Ok && password.isNotBlank() && !state.value.isLoading
                ) {
                    Text(text = "Sign in")
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier =
                    Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.value.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                SignInForm(
                    email = email,
                    password = password,
                    onEmailChanged = { email = it },
                    onPasswordChanged = { password = it },
                    signInPending = state.value.isLoading,
                    onSignInPressed = { component.signIn(email, password) },
                    emailResult = emailResult
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
private fun SignInForm(
    email: String,
    password: String,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    signInPending: Boolean,
    onSignInPressed: () -> Unit,
    emailResult: Result<Unit, String>
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth().focusRequester(focusRequester),
        value = email,
        singleLine = true,
        onValueChange = onEmailChanged,
        readOnly = signInPending,
        label = { Text(text = "Email") },
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        isError = email.isNotEmpty() && emailResult is Err,
        supportingText = {
            if (email.isNotEmpty() && emailResult is Err) {
                Text(text = emailResult.unwrapError())
            }
        }
    )

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth(),
        value = password,
        singleLine = true,
        onValueChange = onPasswordChanged,
        label = { Text(text = "Password") },
        readOnly = signInPending,
        visualTransformation =
            if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
        keyboardActions =
            KeyboardActions(
                onDone = {
                    if (emailResult is Ok && password.isNotBlank() && !signInPending) {
                        keyboardController?.hide()
                        onSignInPressed()
                    }
                }
            ),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility

            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

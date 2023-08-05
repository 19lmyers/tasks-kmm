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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.chara.tasks.shared.component.welcome.sign_up.SignUpComponent

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun SignUpContent(component: SignUpComponent) {
    val state = component.state.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    var email by rememberSaveable { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val emailResult = component.validate(email)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("Sign up") },
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
                        component.signUp(email, displayName, password)
                    },
                    enabled =
                        emailResult is Ok &&
                            displayName.isNotBlank() &&
                            password.isNotBlank() &&
                            !state.value.isLoading
                ) {
                    Text(text = "Sign up")
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
                SignUpForm(
                    email = email,
                    displayName = displayName,
                    password = password,
                    onEmailChanged = { email = it },
                    onDisplayNameChanged = { displayName = it },
                    onPasswordChanged = { password = it },
                    signUpPending = state.value.isLoading,
                    onSignUpClicked = { component.signUp(email, displayName, password) },
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
private fun SignUpForm(
    email: String,
    displayName: String,
    password: String,
    onEmailChanged: (String) -> Unit,
    onDisplayNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    signUpPending: Boolean,
    onSignUpClicked: () -> Unit,
    emailResult: Result<Unit, String>
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth().focusRequester(focusRequester),
        value = email,
        singleLine = true,
        onValueChange = onEmailChanged,
        readOnly = signUpPending,
        label = { Text(text = "Email") },
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        isError = email.isNotEmpty() && emailResult is Err,
        supportingText = {
            if (email.isNotEmpty() && emailResult is Err) {
                Text(text = emailResult.error)
            }
        }
    )

    OutlinedTextField(
        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth(),
        value = displayName,
        singleLine = true,
        onValueChange = onDisplayNameChanged,
        readOnly = signUpPending,
        label = { Text(text = "Display Name") },
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
    )

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth(),
        value = password,
        singleLine = true,
        onValueChange = onPasswordChanged,
        label = { Text(text = "Password") },
        readOnly = signUpPending,
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
                    if (
                        emailResult is Ok &&
                            displayName.isNotBlank() &&
                            password.isNotBlank() &&
                            !signUpPending
                    ) {
                        keyboardController?.hide()
                        onSignUpClicked()
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

package dev.chara.tasks.shared.ui.content.link

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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.androidx.material3.polyfill.AlertDialog
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.chara.tasks.shared.component.link.verify_email.VerifyEmailComponent

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun VerifyEmailContent(component: VerifyEmailComponent) {
    val state = component.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value.emailVerified) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Email verified") },
            confirmButton = { TextButton(onClick = { component.onHome() }) { Text("OK") } },
        )
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    var email by rememberSaveable { mutableStateOf("") }

    val emailResult = component.validateEmail(email)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Confirm email") },
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.padding(WindowInsets.ime.asPaddingValues())) {
                Spacer(Modifier.weight(1f, true))

                FilledTonalButton(
                    modifier = Modifier.padding(16.dp, 8.dp),
                    onClick = {
                        keyboardController?.hide()
                        component.verifyEmail(email)
                    },
                    enabled = emailResult is Ok && !state.value.isLoading
                ) {
                    Text(text = "Verify")
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
                VerifyEmailForm(
                    email = email,
                    onEmailChanged = { email = it },
                    verifyPending = state.value.isLoading,
                    onVerifyClicked = { component.verifyEmail(email) },
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
private fun VerifyEmailForm(
    email: String,
    onEmailChanged: (String) -> Unit,
    verifyPending: Boolean,
    onVerifyClicked: () -> Unit,
    emailResult: Result<Unit, String>
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth().focusRequester(focusRequester),
        value = email,
        singleLine = true,
        onValueChange = onEmailChanged,
        readOnly = verifyPending,
        label = { Text(text = "Email") },
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        keyboardActions =
            KeyboardActions(
                onDone = {
                    if (emailResult is Ok && !verifyPending) {
                        keyboardController?.hide()
                        onVerifyClicked()
                    }
                }
            ),
        isError = email.isNotEmpty() && emailResult is Err,
        supportingText = {
            if (email.isNotEmpty() && emailResult is Err) {
                Text(text = emailResult.error)
            }
        }
    )

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

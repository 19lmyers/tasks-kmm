package dev.chara.tasks.android.ui.route.auth.forgot_password

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.viewmodel.auth.forgot_password.ForgotPasswordUiState
import dev.chara.tasks.viewmodel.auth.forgot_password.ForgotPasswordViewModel

@Composable
fun ForgotPasswordRoute(
    presenter: ForgotPasswordViewModel,
    navigateUp: () -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value is ForgotPasswordUiState.PasswordResetLinkSent) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Password reset link sent") },
            text = { Text(text = "An email with a password reset link should be arriving shortly.") },
            confirmButton = {
                TextButton(onClick = navigateUp) {
                    Text("OK")
                }
            },
        )
    }

    ForgotPasswordScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onUpClicked = {
            if (state.value !is ForgotPasswordUiState.Loading) {
                navigateUp()
            }
        },
        onResetClicked = {
            presenter.sendResetEmail(it)
        },
        validateEmail = {
            presenter.validateEmail(it)
        },
    )

    LaunchedEffect(presenter.messages) {
        presenter.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.text,
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
        }
    }

    BackHandler(state.value is ForgotPasswordUiState.Loading) {
        // Stub
    }
}
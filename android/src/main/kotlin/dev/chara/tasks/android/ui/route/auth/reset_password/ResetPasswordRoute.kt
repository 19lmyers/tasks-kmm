package dev.chara.tasks.android.ui.route.auth.reset_password

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
import dev.chara.tasks.viewmodel.auth.reset_password.ResetPasswordUiState
import dev.chara.tasks.viewmodel.auth.reset_password.ResetPasswordViewModel

@Composable
fun ResetPasswordRoute(
    presenter: ResetPasswordViewModel,
    navigateToSignIn: () -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value is ResetPasswordUiState.PasswordReset) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Password reset") },
            text = { Text(text = "You may now sign in.") },
            confirmButton = {
                TextButton(onClick = navigateToSignIn) {
                    Text("OK")
                }
            },
        )
    }

    ResetPasswordScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onResetClicked = { password ->
            presenter.resetPassword(password)
        },
        validatePassword = {
            presenter.validatePassword(it)
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


    BackHandler(state.value is ResetPasswordUiState.Loading) {
        // Stub
    }
}
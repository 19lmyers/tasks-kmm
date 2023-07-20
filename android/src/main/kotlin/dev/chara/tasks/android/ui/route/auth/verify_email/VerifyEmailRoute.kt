package dev.chara.tasks.android.ui.route.auth.verify_email

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
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.viewmodel.auth.verify_email.VerifyEmailViewModel

@Composable
fun VerifyEmailRoute(
    resetToken: String,
    navigateToHome: () -> Unit
) {
    val viewModel: VerifyEmailViewModel = viewModel {
        VerifyEmailViewModel()
    }
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value.emailVerified) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Email verified") },
            confirmButton = {
                TextButton(onClick = navigateToHome) {
                    Text("OK")
                }
            },
        )
    }

    VerifyEmailScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onVerifyClicked = { email ->
            viewModel.verifyEmail(resetToken, email)
        },
        validateEmail = { email ->
            viewModel.validateEmail(email)
        }
    )

    LaunchedEffect(viewModel.messages) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.text,
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
        }
    }


    BackHandler(state.value.isLoading) {
        // Stub
    }
}
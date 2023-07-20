package dev.chara.tasks.android.ui.route.profile.change_email

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
import dev.chara.tasks.viewmodel.profile.change_email.ChangeEmailViewModel

@Composable
fun ChangeEmailRoute(
    navigateToHome: () -> Unit,
    navigateUp: () -> Unit
) {
    val viewModel: ChangeEmailViewModel = viewModel()

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value.emailChanged) {
        AlertDialog(
            onDismissRequest = { navigateToHome() },
            title = { Text(text = "Verification email sent") },
            text = { Text("Follow the link provided to verify your email address.") },
            confirmButton = {
                TextButton(onClick = navigateToHome) {
                    Text("OK")
                }
            },
        )
    }

    ChangeEmailScreen(
        state.value,
        snackbarHostState = snackbarHostState,
        onUpClicked = {
            if (!state.value.isLoading) {
                navigateUp()
            }
        },
        onChangeEmailClicked = { email ->
            viewModel.changeEmail(email)
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
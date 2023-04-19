package dev.chara.tasks.android.ui.route.profile.change_password

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.viewmodel.profile.change_password.ChangePasswordViewModel

@Composable
fun ChangePasswordRoute(
    viewModel: ChangePasswordViewModel,
    navigateToHome: () -> Unit,
    navigateUp: () -> Unit
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value.passwordChanged) {
        navigateToHome()
    } else {
        ChangePasswordScreen(
            state.value,
            snackbarHostState = snackbarHostState,
            onUpClicked = {
                if (!state.value.isLoading) {
                    navigateUp()
                }
            },
            onChangePasswordClicked = { current, new ->
                viewModel.changePassword(current, new)
            }
        )
    }

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
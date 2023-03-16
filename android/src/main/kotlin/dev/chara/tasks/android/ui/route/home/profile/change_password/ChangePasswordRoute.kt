package dev.chara.tasks.android.ui.route.home.profile.change_password

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.viewmodel.profile.change_password.ChangePasswordUiState
import dev.chara.tasks.viewmodel.profile.change_password.ChangePasswordViewModel

@Composable
fun ChangePasswordRoute(
    presenter: ChangePasswordViewModel,
    navigateToHome: () -> Unit,
    navigateUp: () -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value is ChangePasswordUiState.PasswordChanged) {
        navigateToHome()
    } else {
        ChangePasswordScreen(
            state.value,
            snackbarHostState = snackbarHostState,
            onUpClicked = {
                if (state.value !is ChangePasswordUiState.Loading) {
                    navigateUp()
                }
            },
            onChangePasswordClicked = { current, new ->
                presenter.changePassword(current, new)
            },
            validatePassword = {
                presenter.validatePassword(it)
            }
        )
    }

    LaunchedEffect(presenter.messages) {
        presenter.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.text,
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
        }
    }

    BackHandler(state.value is ChangePasswordUiState.Loading) {
        // Stub
    }
}
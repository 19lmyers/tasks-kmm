package dev.chara.tasks.android.ui.route.auth.sign_in

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.viewmodel.auth.sign_in.SignInUiState
import dev.chara.tasks.viewmodel.auth.sign_in.SignInViewModel

@Composable
fun SignInRoute(
    presenter: SignInViewModel,
    navigateToHome: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    navigateUp: () -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value is SignInUiState.Authenticated) {
        navigateToHome()
    } else {
        SignInScreen(
            state.value,
            snackbarHostState = snackbarHostState,
            onUpClicked = {
                if (state.value !is SignInUiState.Loading) {
                    navigateUp()
                }
            },
            onForgotPasswordClicked = {
                navigateToForgotPassword()
            },
            onSignInClicked = { username, password ->
                presenter.signIn(username, password)
            },
            validateEmail = { presenter.validateEmail(it) }
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

    BackHandler(state.value is SignInUiState.Loading) {
        // Stub
    }
}
package dev.chara.tasks.android.ui.route.auth.sign_in

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.viewmodel.auth.sign_in.SignInViewModel

@Composable
fun SignInRoute(
    navigateToHome: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    navigateUp: () -> Unit
) {
    val viewModel: SignInViewModel = viewModel()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value.isAuthenticated) {
        navigateToHome()
    } else {
        SignInScreen(
            state.value,
            snackbarHostState = snackbarHostState,
            onUpClicked = {
                if (!state.value.isLoading) {
                    navigateUp()
                }
            },
            onForgotPasswordClicked = {
                navigateToForgotPassword()
            },
            onSignInClicked = { username, password ->
                viewModel.signIn(username, password)
            },
            validateEmail = { viewModel.validateEmail(it) }
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
package dev.chara.tasks.android.ui.route.auth.sign_up

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.viewmodel.auth.sign_up.SignUpViewModel

@Composable
fun SignUpRoute(
    navigateToHome: () -> Unit,
    navigateUp: () -> Unit
) {
    val viewModel: SignUpViewModel = viewModel()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value.isAuthenticated) {
        navigateToHome()
    } else {
        SignUpScreen(
            state.value,
            snackbarHostState = snackbarHostState,
            onUpClicked = {
                if (!state.value.isLoading) {
                    navigateUp()
                }
            },
            onSignUpClicked = { username, displayName, password ->
                viewModel.signUp(username, displayName, password)
            },
            validateEmail = {
                viewModel.validateEmail(it)
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
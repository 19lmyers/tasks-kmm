package dev.chara.tasks.android.ui.route.auth.sign_up

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.viewmodel.auth.sign_up.SignUpUiState
import dev.chara.tasks.viewmodel.auth.sign_up.SignUpViewModel

@Composable
fun SignUpRoute(
    presenter: SignUpViewModel,
    navigateToHome: () -> Unit,
    navigateUp: () -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value is SignUpUiState.Authenticated) {
        navigateToHome()
    } else {
        SignUpScreen(
            state.value,
            snackbarHostState = snackbarHostState,
            onUpClicked = {
                if (state.value !is SignUpUiState.Loading) {
                    navigateUp()
                }
            },
            onSignUpClicked = { username, displayName, password ->
                presenter.signUp(username, displayName, password)
            },
            validateEmail = {
                presenter.validateEmail(it)
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


    BackHandler(state.value is SignUpUiState.Loading) {
        // Stub
    }
}
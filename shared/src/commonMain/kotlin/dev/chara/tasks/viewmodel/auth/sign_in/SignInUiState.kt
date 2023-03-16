package dev.chara.tasks.viewmodel.auth.sign_in

sealed class SignInUiState {
    object Authenticated : SignInUiState()
    object NotAuthenticated : SignInUiState()
    object Loading : SignInUiState()
}
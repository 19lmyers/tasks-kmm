package dev.chara.tasks.viewmodel.auth.sign_up

sealed class SignUpUiState {
    object Authenticated : SignUpUiState()
    object NotAuthenticated : SignUpUiState()
    object Loading : SignUpUiState()
}
package dev.chara.tasks.viewmodel.auth.reset_password

sealed class ResetPasswordUiState {
    object PasswordReset : ResetPasswordUiState()
    object PasswordNotReset : ResetPasswordUiState()
    object Loading : ResetPasswordUiState()
}
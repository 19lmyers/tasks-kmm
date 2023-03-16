package dev.chara.tasks.viewmodel.auth.forgot_password

sealed class ForgotPasswordUiState {
    object PasswordResetLinkSent : ForgotPasswordUiState()
    object PasswordResetFailed : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
}
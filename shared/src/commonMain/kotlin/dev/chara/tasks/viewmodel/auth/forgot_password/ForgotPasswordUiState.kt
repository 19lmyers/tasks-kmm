package dev.chara.tasks.viewmodel.auth.forgot_password

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,

    val passwordResetLinkSent: Boolean = false,
)
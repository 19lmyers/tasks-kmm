package dev.chara.tasks.shared.component.welcome.forgot_password

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val passwordResetLinkSent: Boolean = false
)
package dev.chara.tasks.shared.component.link.verify_email

data class VerifyEmailUiState(
    val isLoading: Boolean = false,
    val emailVerified: Boolean = false
)
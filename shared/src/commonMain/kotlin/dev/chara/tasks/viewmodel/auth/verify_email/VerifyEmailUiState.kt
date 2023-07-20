package dev.chara.tasks.viewmodel.auth.verify_email

data class VerifyEmailUiState(
    val isLoading: Boolean = false,

    val emailVerified: Boolean = false,
)
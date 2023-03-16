package dev.chara.tasks.viewmodel.profile.change_password

sealed class ChangePasswordUiState {
    object PasswordChanged : ChangePasswordUiState()
    object PasswordNotChanged : ChangePasswordUiState()
    object Loading : ChangePasswordUiState()
}
package dev.chara.tasks.viewmodel.auth.reset_password

import com.chrynan.validator.ValidationResult
import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.ValidationFailure
import dev.chara.tasks.util.validate.PasswordValidator
import dev.chara.tasks.viewmodel.ViewModel
import dev.chara.tasks.viewmodel.util.SnackbarMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ResetPasswordViewModel(private val resetToken: String, coroutineScope: CoroutineScope) :
    ViewModel(
        coroutineScope
    ) {
    private val repository: Repository by inject()

    private val passwordValidator = PasswordValidator()

    private var _uiState =
        MutableStateFlow<ResetPasswordUiState>(ResetPasswordUiState.PasswordNotReset)
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    fun validatePassword(password: String): Result<String> {
        val result = passwordValidator.validate(password)

        return if (result is ValidationResult.Invalid) {
            Result.failure(ValidationFailure(result.errors.first().details ?: "Invalid password"))
        } else {
            Result.success(password)
        }
    }

    fun resetPassword(newPassword: String) {
        coroutineScope.launch {
            _uiState.value = ResetPasswordUiState.Loading

            val result = repository.resetPassword(resetToken, newPassword)
            _messages.emitAsMessage(result)

            _uiState.value = if (result.isSuccess) {
                ResetPasswordUiState.PasswordReset
            } else {
                ResetPasswordUiState.PasswordNotReset
            }
        }
    }
}
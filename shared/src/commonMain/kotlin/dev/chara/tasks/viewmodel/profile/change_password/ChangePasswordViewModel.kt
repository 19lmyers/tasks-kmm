package dev.chara.tasks.viewmodel.profile.change_password

import com.chrynan.validator.ValidationResult
import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.ValidationFailure
import dev.chara.tasks.util.validate.PasswordValidator
import dev.chara.tasks.viewmodel.util.SnackbarMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChangePasswordViewModel : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private val passwordValidator = PasswordValidator()

    private var _uiState =
        MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.PasswordNotChanged)
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

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState.Loading

            val result = repository.changePassword(currentPassword, newPassword)
            _messages.emitAsMessage(result)

            _uiState.value = if (result.isSuccess) {
                ChangePasswordUiState.PasswordChanged
            } else {
                ChangePasswordUiState.PasswordNotChanged
            }
        }
    }
}
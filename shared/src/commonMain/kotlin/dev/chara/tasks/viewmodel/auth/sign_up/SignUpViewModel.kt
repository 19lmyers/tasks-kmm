package dev.chara.tasks.viewmodel.auth.sign_up

import com.chrynan.validator.EmailValidator
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

class SignUpViewModel : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private val emailValidator = EmailValidator()
    private val passwordValidator = PasswordValidator()

    private var _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    fun validateEmail(email: String): Result<String> {
        val result = emailValidator.validate(email)

        return if (result is ValidationResult.Invalid) {
            Result.failure(ValidationFailure(result.errors.first().details ?: "Invalid email"))
        } else {
            Result.success(email)
        }
    }

    fun validatePassword(password: String): Result<String> {
        val result = passwordValidator.validate(password)

        return if (result is ValidationResult.Invalid) {
            Result.failure(ValidationFailure(result.errors.first().details ?: "Invalid password"))
        } else {
            Result.success(password)
        }
    }

    fun signUp(email: String, displayName: String, password: String) {
        viewModelScope.launch {
            val result = repository.createUser(email, displayName, password)
            _messages.emitAsMessage(result)

            _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = result.isSuccess)
        }
    }
}
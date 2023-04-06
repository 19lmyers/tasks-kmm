package dev.chara.tasks.viewmodel.auth.sign_in

import com.chrynan.validator.EmailValidator
import com.chrynan.validator.ValidationResult
import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.ValidationFailure
import dev.chara.tasks.viewmodel.util.SnackbarMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignInViewModel : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private val emailValidator = EmailValidator()

    private var _uiState = MutableStateFlow(SignInUiState())
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    fun validateEmail(email: String): Result<String> {
        val result = emailValidator.validate(email)

        return if (result is ValidationResult.Invalid) {
            Result.failure(ValidationFailure(result.errors.first().details ?: "Invalid email"))
        } else {
            Result.success(email)
        }
    }

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            val result = repository.authenticateUser(username, password)
            _messages.emitAsMessage(result)

            _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = result.isSuccess)
        }
    }
}
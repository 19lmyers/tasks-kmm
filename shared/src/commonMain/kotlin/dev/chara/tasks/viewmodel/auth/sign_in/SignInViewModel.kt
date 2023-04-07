package dev.chara.tasks.viewmodel.auth.sign_in

import com.chrynan.validator.EmailValidator
import com.chrynan.validator.ValidationResult
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.chara.tasks.data.Repository
import dev.chara.tasks.viewmodel.util.PopupMessage
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

    private val _messages = MutableSharedFlow<PopupMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    fun validateEmail(email: String): Result<Unit, String> {
        val result = emailValidator.validate(email)

        return if (result is ValidationResult.Invalid) {
            Err(result.errors.first().details ?: "Invalid email")
        } else {
            Ok(Unit)
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.authenticateUser(email, password)
            _messages.emitAsMessage(result)

            _uiState.value =
                _uiState.value.copy(isLoading = false, isAuthenticated = result.isSuccess)
        }
    }
}
package dev.chara.tasks.viewmodel.auth.forgot_password

import com.github.michaelbull.result.Ok
import dev.chara.tasks.data.Repository
import dev.chara.tasks.data.validator.EmailValidator
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

class ForgotPasswordViewModel : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<PopupMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    fun validateEmail(email: String) = EmailValidator.validate(email)

    fun sendResetEmail(email: String) {
        viewModelScope.launch {
            val result = repository.requestPasswordResetEmail(email)
            _messages.emitAsMessage(result)

            _uiState.value =
                _uiState.value.copy(isLoading = false, passwordResetLinkSent = result is Ok)
        }
    }
}
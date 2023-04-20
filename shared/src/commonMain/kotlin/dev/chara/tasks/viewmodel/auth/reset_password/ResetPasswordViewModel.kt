package dev.chara.tasks.viewmodel.auth.reset_password

import com.github.michaelbull.result.Ok
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

class ResetPasswordViewModel(private val resetToken: String) : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<PopupMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    fun resetPassword(newPassword: String) {
        viewModelScope.launch {
            val result = repository.resetPassword(resetToken, newPassword)
            _messages.emitAsMessage(result)

            _uiState.value = _uiState.value.copy(isLoading = false, passwordReset = result is Ok)
        }
    }
}
package dev.chara.tasks.shared.component.link.reset_password

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ResetPasswordComponent {
    val state: StateFlow<ResetPasswordUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun onSignIn()

    fun resetPassword(newPassword: String)
}

class DefaultResetPasswordComponent(
    componentContext: ComponentContext,
    private val resetToken: String,
    private val navigateUp: () -> Unit,
    private val navigateToSignIn: () -> Unit
) : ResetPasswordComponent, KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(ResetPasswordUiState())
    override val state = _state.asStateFlow()

    private var _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    override fun onUp() {
        if (!state.value.isLoading) {
            navigateUp()
        }
    }

    private val callback = BackCallback {
        onUp()
    }

    init {
        backHandler.register(callback)
    }

    override fun onSignIn() = navigateToSignIn()

    override fun resetPassword(newPassword: String) {
        coroutineScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val result = repository.resetPassword(resetToken, newPassword)
            _messages.emitAsMessage(result)

            _state.value = state.value.copy(isLoading = false, passwordReset = result is Ok)
        }
    }
}
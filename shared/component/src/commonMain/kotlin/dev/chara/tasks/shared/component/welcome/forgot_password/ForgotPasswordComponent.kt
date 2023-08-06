package dev.chara.tasks.shared.component.welcome.forgot_password

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.domain.EmailValidator
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

interface ForgotPasswordComponent {
    val state: StateFlow<ForgotPasswordUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun validate(email: String): Result<Unit, String>

    fun sendResetEmail(email: String)
}

class DefaultForgotPasswordComponent(
    componentContext: ComponentContext,
    private val navigateUp: () -> Unit
) : ForgotPasswordComponent, KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(ForgotPasswordUiState())
    override val state = _state.asStateFlow()

    private var _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    override fun onUp() {
        if (!state.value.isLoading) {
            navigateUp()
        }
    }

    private val callback = BackCallback { onUp() }

    init {
        backHandler.register(callback)
    }

    override fun validate(email: String) = EmailValidator(email)

    override fun sendResetEmail(email: String) {
        coroutineScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val result = repository.requestPasswordResetEmail(email)
            _messages.emitAsMessage(result)

            _state.value = state.value.copy(isLoading = false, passwordResetLinkSent = result is Ok)
        }
    }
}

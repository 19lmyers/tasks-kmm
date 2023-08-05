package dev.chara.tasks.shared.component.link.verify_email

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

interface VerifyEmailComponent {
    val state: StateFlow<VerifyEmailUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun onHome()

    fun validateEmail(email: String): Result<Unit, String>

    fun verifyEmail(email: String)
}

class DefaultVerifyEmailComponent(
    componentContext: ComponentContext,
    private val verifyToken: String,
    private val navigateUp: () -> Unit,
    private val navigateToHome: () -> Unit,
) : VerifyEmailComponent, KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(VerifyEmailUiState())
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

    override fun onHome() = navigateToHome()

    override fun validateEmail(email: String) = EmailValidator(email)

    override fun verifyEmail(email: String) {
        coroutineScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val result = repository.verifyEmail(verifyToken, email)
            _messages.emitAsMessage(result)

            _state.value = state.value.copy(isLoading = false, emailVerified = result is Ok)
        }
    }
}

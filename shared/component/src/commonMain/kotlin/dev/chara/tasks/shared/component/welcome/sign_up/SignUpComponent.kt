package dev.chara.tasks.shared.component.welcome.sign_up

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.domain.EmailValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SignUpComponent {
    val state: StateFlow<SignUpUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun validate(email: String): Result<Unit, String>

    fun signUp(email: String, displayName: String, password: String)
}

class DefaultSignUpComponent(
    componentContext: ComponentContext,
    private val navigateUp: () -> Unit,
    private val navigateToHome: () -> Unit,
) : SignUpComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(SignUpUiState())
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

    override fun signUp(email: String, displayName: String, password: String) {
        coroutineScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = repository.createUser(email, displayName, password)
            _messages.emitAsMessage(result)

            _state.value = _state.value.copy(isLoading = false)

            if (result is Ok) {
                withContext(Dispatchers.Main) { navigateToHome() }
            }
        }
    }
}

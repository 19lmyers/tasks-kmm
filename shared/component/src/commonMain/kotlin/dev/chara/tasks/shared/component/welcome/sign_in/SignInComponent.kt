package dev.chara.tasks.shared.component.welcome.sign_in

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandlerOwner
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
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SignInComponent {
    val state: StateFlow<SignInUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun onForgotPassword()

    fun validate(email: String): Result<Unit, String>

    fun signIn(email: String, password: String)
}

class DefaultSignInComponent(
    componentContext: ComponentContext,
    private val navigateUp: () -> Unit,
    private val navigateToForgotPassword: () -> Unit,
    private val navigateToHome: () -> Unit
) : SignInComponent, KoinComponent, ComponentContext by componentContext, BackHandlerOwner {

    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(SignInUiState())
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

    override fun onForgotPassword() = navigateToForgotPassword()

    override fun validate(email: String) = EmailValidator(email)

    override fun signIn(email: String, password: String) {
        coroutineScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = repository.authenticateUser(email, password)
            _messages.emitAsMessage(result)

            _state.value = _state.value.copy(isLoading = false)

            if (result is Ok) {
                withContext(Dispatchers.Main) { navigateToHome() }
            }
        }
    }
}

package dev.chara.tasks.shared.component.profile.change_email

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.domain.EmailValidator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ChangeEmailComponent {
    val state: StateFlow<ChangeEmailUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun onConfirmUp()

    fun validateEmail(email: String): Result<Unit, String>

    fun changeEmail(newEmail: String)
}

class DefaultChangeEmailComponent(
    componentContext: ComponentContext,
    private val navigateUp: () -> Unit,
) : ChangeEmailComponent, KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope()

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(ChangeEmailUiState())
    override val state = _state.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    override fun onUp() {
        if (!state.value.isLoading && !state.value.emailChanged) {
            navigateUp()
        }
    }

    private val callback = BackCallback { onUp() }

    init {
        backHandler.register(callback)
    }

    override fun onConfirmUp() = navigateUp()

    override fun validateEmail(email: String) = EmailValidator(email)

    override fun changeEmail(newEmail: String) {
        coroutineScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val result = repository.changeEmail(newEmail)
            _messages.emitAsMessage(result)

            _state.value = state.value.copy(isLoading = false, emailChanged = result is Ok)
        }
    }
}

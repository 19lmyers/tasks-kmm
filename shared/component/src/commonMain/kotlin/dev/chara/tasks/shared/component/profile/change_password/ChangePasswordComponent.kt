package dev.chara.tasks.shared.component.profile.change_password

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ChangePasswordComponent {
    val state: StateFlow<ChangePasswordUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun onConfirmUp()

    fun changePassword(currentPassword: String, newPassword: String)
}

class DefaultChangePasswordComponent(
    componentContext: ComponentContext,
    private val navigateUp: () -> Unit,
) : ChangePasswordComponent, KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope()

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(ChangePasswordUiState())
    override val state = _state.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    override fun onUp() {
        if (!state.value.isLoading && !state.value.passwordChanged) {
            navigateUp()
        }
    }

    private val callback = BackCallback { onUp() }

    init {
        backHandler.register(callback)
    }

    override fun onConfirmUp() = navigateUp()

    override fun changePassword(currentPassword: String, newPassword: String) {
        coroutineScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val result = repository.changePassword(currentPassword, newPassword)
            _messages.emitAsMessage(result)

            _state.value = state.value.copy(isLoading = false, passwordChanged = result is Ok)
        }
    }
}

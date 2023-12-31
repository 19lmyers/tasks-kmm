package dev.chara.tasks.shared.component.home.join_list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.onSuccess
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.domain.Gravatar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface JoinListComponent {
    val state: StateFlow<JoinListUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onDismiss()

    fun requestJoin()

    fun getGravatarUri(email: String): String
}

class DefaultJoinListComponent(
    componentContext: ComponentContext,
    private val inviteToken: String,
    private val dismiss: () -> Unit,
) : JoinListComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(JoinListUiState())
    override val state = _state.asStateFlow()

    private var _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            val taskList = repository.getListInviteInfo(inviteToken)
            if (taskList !is Ok) {
                _state.value = JoinListUiState(isLoading = false)
                _messages.emitAsMessage(taskList)
                return@launch
            }

            val owner = repository.getUserProfileFor(taskList.value.ownerId)
            if (owner !is Ok) {
                _state.value = JoinListUiState(isLoading = false)
                _messages.emitAsMessage(owner)
                return@launch
            }

            _state.value =
                JoinListUiState(isLoading = false, taskList = taskList.value, owner = owner.value)
        }
    }

    override fun onDismiss() = dismiss()

    override fun requestJoin() {
        coroutineScope.launch { repository.requestListJoin(inviteToken).onSuccess { onDismiss() } }
    }

    override fun getGravatarUri(email: String) = Gravatar.getUri(email)
}

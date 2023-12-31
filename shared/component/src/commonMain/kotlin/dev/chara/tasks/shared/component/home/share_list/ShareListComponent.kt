package dev.chara.tasks.shared.component.home.share_list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.DataError
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.domain.Gravatar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ShareListComponent {
    val state: StateFlow<ShareListUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onDismiss()

    fun removeMember(memberId: String)

    fun requestShare(onToken: (Result<String, DataError>) -> Unit)

    fun getGravatarUri(email: String): String
}

class DefaultShareListComponent(
    componentContext: ComponentContext,
    private val listId: String,
    private val dismiss: () -> Unit,
) : ShareListComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(ShareListUiState())
    override val state = _state.asStateFlow()

    private var _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            combine(
                    repository.getListById(listId),
                    repository.getUserProfile(),
                ) { list, profile ->
                    if (list == null) return@combine null

                    val owner = repository.getUserProfileFor(list.ownerId)
                    val members = repository.getListMembers(listId)

                    if (owner is Ok && members is Ok) {
                        ShareListUiState(
                            isLoading = false,
                            list = list,
                            profile = profile,
                            owner = owner.value,
                            members = members.value
                        )
                    } else {
                        if (owner is Err) {
                            _messages.emitAsMessage(owner)
                        } else if (members is Err) {
                            _messages.emitAsMessage(members)
                        }
                        null
                    }
                }
                .collect {
                    if (it == null) {
                        withContext(Dispatchers.Main) { onDismiss() }
                    } else {
                        _state.value = it
                    }
                }
        }
    }

    override fun onDismiss() = dismiss()

    override fun removeMember(memberId: String) {
        coroutineScope.launch {
            val result = repository.removeMemberFromList(listId, memberId)
            _messages.emitAsMessage(result)
        }
    }

    override fun requestShare(onToken: (Result<String, DataError>) -> Unit) {
        coroutineScope.launch {
            val result = repository.requestListInvite(listId)
            onToken(result)
        }
    }

    override fun getGravatarUri(email: String) = Gravatar.getUri(email)
}

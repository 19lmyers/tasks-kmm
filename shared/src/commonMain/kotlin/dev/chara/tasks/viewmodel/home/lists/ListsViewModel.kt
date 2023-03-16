package dev.chara.tasks.viewmodel.home.lists

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.network.ConnectivityStatusManager
import dev.chara.tasks.viewmodel.ViewModel
import dev.chara.tasks.viewmodel.util.SnackbarMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ListsViewModel(coroutineScope: CoroutineScope) : ViewModel(coroutineScope) {

    private val repository: Repository by inject()
    private val connectivityStatusManager: ConnectivityStatusManager by inject()

    private var _uiState = MutableStateFlow<ListsUiState>(ListsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            combine(
                repository.getLists(),
                connectivityStatusManager.isInternetConnected
            ) { taskLists, isInternetConnected ->
                ListsUiState.Loaded(
                    taskLists = taskLists,
                    isInternetConnected = isInternetConnected,
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun refreshCache() {
        coroutineScope.launch {
            if (_uiState.value is ListsUiState.Loaded) {
                val oldState = _uiState.value as ListsUiState.Loaded

                _uiState.value = oldState.copy(isRefreshing = true)

                val result = repository.refresh()
                _messages.emitAsMessage(result)

                _uiState.value = oldState.copy(isRefreshing = false)
            }
        }
    }

    fun createList(taskList: TaskList) {
        coroutineScope.launch {
            val result = repository.createList(taskList)
            _messages.emitAsMessage(result)
        }
    }
}
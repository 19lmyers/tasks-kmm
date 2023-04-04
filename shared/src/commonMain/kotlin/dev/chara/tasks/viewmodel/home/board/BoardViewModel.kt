package dev.chara.tasks.viewmodel.home.board

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.network.ConnectivityStatusManager
import dev.chara.tasks.viewmodel.ViewModel
import dev.chara.tasks.viewmodel.util.SnackbarMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class BoardViewModel : ViewModel() {

    private val repository: Repository by inject()
    private val connectivityStatusManager: ConnectivityStatusManager by inject()

    private var _uiState = MutableStateFlow<BoardUiState>(BoardUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            combine(
                repository.getBoardSections(),
                repository.getPinnedLists(),
                repository.getLists(),
                connectivityStatusManager.isInternetConnected
            ) { boardSections, pinnedLists, allLists, isInternetConnected ->
                BoardUiState.Loaded(
                    boardSections = boardSections,
                    pinnedLists = pinnedLists,
                    allLists = allLists,
                    isInternetConnected = isInternetConnected,
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun refreshCache() {
        coroutineScope.launch {
            if (_uiState.value is BoardUiState.Loaded) {
                val oldState = _uiState.value as BoardUiState.Loaded

                _uiState.value = oldState.copy(isRefreshing = true)

                val result = repository.refresh()
                _messages.emitAsMessage(result)

                _uiState.value = oldState.copy(isRefreshing = false)
            }
        }
    }

    fun updateTask(task: Task) {
        coroutineScope.launch {
            val result = repository.updateTask(task.listId, task.id, task)
            _messages.emitAsMessage(result, successMessage = "Task updated")
        }
    }
}
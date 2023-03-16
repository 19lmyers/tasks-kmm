package dev.chara.tasks.viewmodel.home.list_details

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
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
import kotlinx.datetime.Instant
import org.koin.core.component.inject

class ListDetailsViewModel(private val listId: String, coroutineScope: CoroutineScope) : ViewModel(
    coroutineScope
) {

    private val repository: Repository by inject()
    private val connectivityStatusManager: ConnectivityStatusManager by inject()

    private var _uiState = MutableStateFlow<ListDetailsUiState>(ListDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            combine(
                repository.getListById(listId),
                repository.getTasksByList(listId, isCompleted = false),
                repository.getTasksByList(listId, isCompleted = true),
                connectivityStatusManager.isInternetConnected,
            ) { selectedList, currentTasks, completedTasks, isInternetConnected ->
                if (selectedList != null) {
                    ListDetailsUiState.Loaded(
                        selectedList = selectedList,
                        currentTasks = currentTasks,
                        completedTasks = completedTasks,
                        isInternetConnected = isInternetConnected
                    )
                } else {
                    ListDetailsUiState.NotFound
                }
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun refreshCache() {
        coroutineScope.launch {
            if (_uiState.value is ListDetailsUiState.Loaded) {
                val oldState = _uiState.value as ListDetailsUiState.Loaded

                _uiState.value = oldState.copy(isRefreshing = true)

                val result = repository.refresh()
                _messages.emitAsMessage(result)

                _uiState.value = oldState.copy(isRefreshing = false)
            }
        }
    }

    fun updateList(listId: String, taskList: TaskList) {
        coroutineScope.launch {
            val result = repository.updateList(listId, taskList)
            _messages.emitAsMessage(result)
        }
    }

    fun deleteList(listId: String) {
        coroutineScope.launch {
            val result = repository.deleteList(listId)
            _messages.emitAsMessage(result, successMessage = "List deleted")
        }
    }

    fun updateTask(listId: String, taskId: String, task: Task) {
        coroutineScope.launch {
            val result = repository.updateTask(listId, taskId, task)
            _messages.emitAsMessage(result, successMessage = "Task updated")
        }
    }

    fun reorderTask(
        listId: String,
        taskId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ) {
        if (fromIndex == toIndex) return

        coroutineScope.launch {
            val result = repository.reorderTask(listId, taskId, fromIndex, toIndex, lastModified)
            _messages.emitAsMessage(result)
        }
    }

    fun clearCompletedTasks(listId: String) {
        coroutineScope.launch {
            val result = repository.clearCompletedTasks(listId)
            _messages.emitAsMessage(result)
        }
    }
}
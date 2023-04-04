package dev.chara.tasks.viewmodel.home.list_details

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.network.ConnectivityStatusManager
import dev.chara.tasks.viewmodel.util.SnackbarMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ListDetailsViewModel(private val listId: String) : ViewModel(), KoinComponent {

    private val repository: Repository by inject()
    private val connectivityStatusManager: ConnectivityStatusManager by inject()

    private var _uiState = MutableStateFlow<ListDetailsUiState>(ListDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    init {
        viewModelScope.launch {
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
        viewModelScope.launch {
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
        viewModelScope.launch {
            val result = repository.updateList(listId, taskList)
            _messages.emitAsMessage(result)
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            val result = repository.deleteList(listId)
            _messages.emitAsMessage(result, successMessage = "List deleted")
        }
    }

    fun updateTask(listId: String, taskId: String, task: Task) {
        viewModelScope.launch {
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

        viewModelScope.launch {
            val result = repository.reorderTask(listId, taskId, fromIndex, toIndex, lastModified)
            _messages.emitAsMessage(result)
        }
    }

    fun clearCompletedTasks(listId: String) {
        viewModelScope.launch {
            val result = repository.clearCompletedTasks(listId)
            _messages.emitAsMessage(result)
        }
    }
}
package dev.chara.tasks.viewmodel.home.task_details

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
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

class TaskDetailsViewModel(private val taskId: String): ViewModel(), KoinComponent {

    private val repository: Repository by inject()
    private val connectivityStatusManager: ConnectivityStatusManager by inject()

    private var _uiState = MutableStateFlow<TaskDetailsUiState>(TaskDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getTaskById(taskId),
                repository.getLists(),
                connectivityStatusManager.isInternetConnected,
            ) { task, taskLists, isInternetConnected ->
                if (task != null) {
                    TaskDetailsUiState.Loaded(
                        task = task,
                        taskLists = taskLists,
                        isInternetConnected = isInternetConnected
                    )
                } else {
                    TaskDetailsUiState.NotFound
                }
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun updateTask(listId: String, taskId: String, task: Task) {
        viewModelScope.launch {
            val result = repository.updateTask(listId, taskId, task)
            _messages.emitAsMessage(result)
        }
    }

    fun moveTask(oldListId: String, newListId: String, taskId: String, lastModified: Instant) {
        viewModelScope.launch {
            val result = repository.moveTask(oldListId, newListId, taskId, lastModified)
            _messages.emitAsMessage(result)
        }
    }

    fun deleteTask(listId: String, taskId: String) {
        viewModelScope.launch {
            val result = repository.deleteTask(listId, taskId)
            _messages.emitAsMessage(result, "")
        }
    }
}
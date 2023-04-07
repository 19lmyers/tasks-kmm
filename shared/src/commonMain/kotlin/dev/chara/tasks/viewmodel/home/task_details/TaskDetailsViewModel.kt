package dev.chara.tasks.viewmodel.home.task_details

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.viewmodel.util.PopupMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
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

class TaskDetailsViewModel(private val taskId: String) : ViewModel(), KoinComponent {

    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(TaskDetailsUiState(isLoading = true, firstLoad = true))
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<PopupMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getTaskById(taskId),
                repository.getLists(),
            ) { task, taskLists ->
                TaskDetailsUiState(
                    isLoading = false,
                    firstLoad = false,
                    task = task,
                    taskLists = taskLists,
                )
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
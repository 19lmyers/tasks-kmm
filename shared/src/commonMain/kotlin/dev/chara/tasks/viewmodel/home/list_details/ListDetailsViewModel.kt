package dev.chara.tasks.viewmodel.home.list_details

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.util.PopupMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ListDetailsViewModel : ViewModel(), KoinComponent {
    private var observer: Job? = null

    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(ListDetailsUiState(isLoading = true, firstLoad = true))
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<PopupMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    fun observeList(listId: String) = apply {
        observer?.cancel()
        observer = viewModelScope.launch {
            combine(
                repository.getListById(listId),
                repository.getTasksByList(listId, isCompleted = false),
                repository.getTasksByList(listId, isCompleted = true),
            ) { selectedList, currentTasks, completedTasks ->
                ListDetailsUiState(
                    isLoading = false,
                    firstLoad = false,
                    selectedList = selectedList,
                    currentTasks = currentTasks,
                    completedTasks = completedTasks
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun refreshCache() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.refresh()
            _messages.emitAsMessage(result)

            _uiState.value = _uiState.value.copy(isLoading = false)
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

    fun createTask(listId: String, task: Task) {
        viewModelScope.launch {
            val result = repository.createTask(listId, task)
            _messages.emitAsMessage(result, successMessage = "Task created")
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
            val result =
                repository.reorderTask(listId, taskId, fromIndex, toIndex, lastModified)
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
package dev.chara.tasks.viewmodel.home

import com.github.michaelbull.result.Ok
import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(HomeUiState(isLoading = true, firstLoad = true))
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<PopupMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.isUserAuthenticated(),
                repository.getUserProfile(),
                repository.getBoardSections(),
                repository.getPinnedLists(),
                repository.getLists(),
            ) { isUserAuthenticated, profile, boardSections, pinnedLists, allLists ->
                HomeUiState(
                    isLoading = false,
                    firstLoad = false,
                    isAuthenticated = isUserAuthenticated,
                    profile = profile,
                    boardSections = boardSections,
                    pinnedLists = pinnedLists,
                    allLists = allLists
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun requestVerifyEmailResend() {
        viewModelScope.launch {
            val result = repository.requestVerifyEmailResend()
            _messages.emitAsMessage(result)

            _uiState.value = _uiState.value.copy(verifyEmailSent = result is Ok)
        }
    }

    fun clearVerifyEmailNotice() {
        _uiState.value = _uiState.value.copy(verifyEmailSent = false)
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
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

    fun createList(taskList: TaskList) {
        viewModelScope.launch {
            val result = repository.createList(taskList)
            _messages.emitAsMessage(result)
        }
    }

    fun updateList(taskList: TaskList) {
        viewModelScope.launch {
            val result = repository.updateList(taskList.id, taskList)
            _messages.emitAsMessage(result)
        }
    }

    fun createTask(listId: String, task: Task) {
        viewModelScope.launch {
            val result = repository.createTask(listId, task)
            _messages.emitAsMessage(result)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val result = repository.updateTask(task.listId, task.id, task)
            _messages.emitAsMessage(result, successMessage = "Task updated")
        }
    }

    fun markTaskAsCompleted(taskId: String) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId).first() ?: return@launch

            val result = repository.updateTask(
                task.listId, task.id, task.copy(
                    isCompleted = true,
                    lastModified = Clock.System.now()
                )
            )
            _messages.emitAsMessage(result, successMessage = "Task completed")
        }
    }
}
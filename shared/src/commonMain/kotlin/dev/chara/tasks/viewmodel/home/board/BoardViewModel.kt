package dev.chara.tasks.viewmodel.home.board

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.viewmodel.util.SnackbarMessage
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BoardViewModel : ViewModel(), KoinComponent {

    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(BoardUiState(isLoading = true, firstLoad = true))
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getBoardSections(),
                repository.getPinnedLists(),
                repository.getLists(),
            ) { boardSections, pinnedLists, allLists ->
                BoardUiState(
                    isLoading = false,
                    firstLoad = false,
                    boardSections = boardSections,
                    pinnedLists = pinnedLists,
                    allLists = allLists,
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

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val result = repository.updateTask(task.listId, task.id, task)
            _messages.emitAsMessage(result, successMessage = "Task updated")
        }
    }
}
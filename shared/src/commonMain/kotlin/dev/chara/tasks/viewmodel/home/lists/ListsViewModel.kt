package dev.chara.tasks.viewmodel.home.lists

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.viewmodel.util.SnackbarMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ListsViewModel : ViewModel(), KoinComponent {

    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(ListsUiState(isLoading = true, firstLoad = true))
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    init {
        viewModelScope.launch {
            repository.getLists().collect { taskLists ->
                _uiState.value = ListsUiState(
                    isLoading = false,
                    firstLoad = false,
                    taskLists = taskLists
                )
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

    fun createList(taskList: TaskList) {
        viewModelScope.launch {
            val result = repository.createList(taskList)
            _messages.emitAsMessage(result)
        }
    }
}
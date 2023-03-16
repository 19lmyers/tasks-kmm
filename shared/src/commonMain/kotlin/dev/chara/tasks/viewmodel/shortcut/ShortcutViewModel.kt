package dev.chara.tasks.viewmodel.shortcut

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.Theme
import dev.chara.tasks.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ShortcutViewModel(coroutineScope: CoroutineScope) : ViewModel(coroutineScope) {
    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow<ShortcutUiState>(ShortcutUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _statuses = MutableSharedFlow<Pair<Boolean, String?>>()
    val statuses = _statuses.asSharedFlow()

    init {
        coroutineScope.launch {
            combine(
                repository.isUserAuthenticated(),
                repository.getLists()
            ) { isUserAuthenticated, taskLists ->
                if (isUserAuthenticated) {
                    ShortcutUiState.Authenticated(
                        taskLists = taskLists
                    )
                } else {
                    ShortcutUiState.NotAuthenticated
                }
            }.collect {
                _uiState.value = it
            }

            if (uiState.value is ShortcutUiState.Authenticated) {
                repository.refresh()
            }
        }
    }

    fun getAppTheme(): Flow<Theme> = repository.getAppTheme()

    fun useVibrantColors(): Flow<Boolean> = repository.useVibrantColors()

    fun createTask(listId: String, task: Task) {
        coroutineScope.launch {
            val result = repository.createTask(listId, task)

            result.fold(
                onSuccess = {
                    true to null
                },
                onFailure = { errorMessage ->
                    false to errorMessage
                }
            ).let { status ->
                _statuses.emit(status)
            }
        }
    }
}
package dev.chara.tasks.viewmodel.shortcut

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.Theme
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShortcutViewModel : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(ShortcutUiState(isLoading = true, firstLoad = true))
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _statuses = MutableSharedFlow<Pair<Boolean, String?>>()
    val statuses = _statuses.asSharedFlow().cFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.isUserAuthenticated(),
                repository.getLists()
            ) { isUserAuthenticated, taskLists ->
                ShortcutUiState(
                    isLoading = false,
                    firstLoad = false,
                    isAuthenticated = isUserAuthenticated,
                    taskLists = taskLists
                )
            }.collect {
                _uiState.value = it
            }

            if (uiState.value.isAuthenticated) {
                Napier.d("REFRESH ME")
                repository.refresh()
            }
        }
    }

    fun getAppTheme(): Flow<Theme> = repository.getAppTheme()

    fun useVibrantColors(): Flow<Boolean> = repository.useVibrantColors()

    fun createTask(listId: String, task: Task) {
        viewModelScope.launch {
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
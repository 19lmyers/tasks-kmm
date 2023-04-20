package dev.chara.tasks.viewmodel.shortcut

import com.github.michaelbull.result.mapBoth
import dev.chara.tasks.data.ApiError
import dev.chara.tasks.data.ClientError
import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.preference.Theme
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

    private val _statuses = MutableSharedFlow<String?>()
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
            repository.createTask(listId, task).mapBoth(
                success = {
                    null
                },
                failure = { error ->
                    when (error) {
                        is ApiError -> {
                            error.message
                        }

                        is ClientError -> {
                            error.exception.message
                        }

                        else -> {
                            "An unknown error occured"
                        }
                    }
                }
            ).let { status ->
                _statuses.emit(status)
            }
        }
    }
}
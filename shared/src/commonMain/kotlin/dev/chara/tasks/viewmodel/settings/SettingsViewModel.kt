package dev.chara.tasks.viewmodel.settings

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.board.BoardSection
import dev.chara.tasks.model.preference.Theme
import dev.chara.tasks.model.preference.ThemeVariant
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(SettingsUiState(isLoading = true, firstLoad = true))
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<PopupMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAppTheme(),
                repository.getAppThemeVariant(),
                repository.getLists(),
                repository.getEnabledBoardSections()
            ) { theme, variant, taskLists, enabledBoardSections ->
                SettingsUiState(
                    isLoading = false,
                    firstLoad = false,
                    appTheme = theme,
                    appThemeVariant = variant,
                    taskLists = taskLists,
                    enabledBoardSections = enabledBoardSections
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun setAppTheme(theme: Theme) {
        viewModelScope.launch {
            repository.setAppTheme(theme)
        }
    }

    fun setAppThemeVariant(variant: ThemeVariant) {
        viewModelScope.launch {
            repository.setAppThemeVariant(variant)
        }
    }

    fun setEnabledForBoardSection(boardSection: BoardSection.Type, enabled: Boolean) {
        viewModelScope.launch {
            repository.setEnabledForBoardSection(boardSection, enabled)
        }
    }

    fun updateList(taskList: TaskList) {
        viewModelScope.launch {
            val result = repository.updateList(taskList.id, taskList)
            _messages.emitAsMessage(result)
        }
    }
}
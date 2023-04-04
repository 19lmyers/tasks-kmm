package dev.chara.tasks.viewmodel.settings

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.BoardSection
import dev.chara.tasks.model.StartScreen
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.Theme
import dev.chara.tasks.viewmodel.util.SnackbarMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
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

    private var _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAppTheme(),
                repository.useVibrantColors(),
                repository.getStartScreen(),
                repository.getLists(),
                repository.getEnabledBoardSections()
            ) { theme, useVibrantColors, startScreen, taskLists, enabledBoardSections ->
                SettingsUiState.Loaded(
                    appTheme = theme,
                    useVibrantColors = useVibrantColors,
                    startScreen = startScreen,
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

    fun setVibrantColors(useVibrantColors: Boolean) {
        viewModelScope.launch {
            repository.setVibrantColors(useVibrantColors)
        }
    }

    fun setStartScreen(startScreen: StartScreen) {
        viewModelScope.launch {
            repository.setStartScreen(startScreen)
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
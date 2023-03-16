package dev.chara.tasks.viewmodel.settings

import dev.chara.tasks.model.BoardSection
import dev.chara.tasks.model.StartScreen
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.Theme

sealed class SettingsUiState {
    data class Loaded(
        val appTheme: Theme = Theme.SYSTEM_DEFAULT,
        val useVibrantColors: Boolean = false,
        val startScreen: StartScreen = StartScreen.BOARD,
        val taskLists: List<TaskList> = listOf(),
        val enabledBoardSections: List<BoardSection.Type> = listOf()
    ) : SettingsUiState()

    object Loading : SettingsUiState()
}


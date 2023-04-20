package dev.chara.tasks.viewmodel.settings

import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.board.BoardSection
import dev.chara.tasks.model.preference.Theme

data class SettingsUiState(
    val isLoading: Boolean = false,
    val firstLoad: Boolean = false,

    val appTheme: Theme = Theme.SYSTEM_DEFAULT,
    val useVibrantColors: Boolean = false,
    val taskLists: List<TaskList> = listOf(),
    val enabledBoardSections: List<BoardSection.Type> = listOf()
)


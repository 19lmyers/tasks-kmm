package dev.chara.tasks.shared.component.settings

import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.board.BoardSection
import dev.chara.tasks.shared.model.preference.Theme
import dev.chara.tasks.shared.model.preference.ThemeVariant

data class SettingsUiState(
    val isLoading: Boolean = true,

    val appTheme: Theme = Theme.SYSTEM_DEFAULT,
    val appThemeVariant: ThemeVariant = ThemeVariant.TONAL_SPOT,
    val taskLists: List<TaskList> = listOf(),
    val enabledBoardSections: List<BoardSection.Type> = listOf()
)


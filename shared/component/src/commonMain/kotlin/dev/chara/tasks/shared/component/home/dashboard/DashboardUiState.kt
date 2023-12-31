package dev.chara.tasks.shared.component.home.dashboard

import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.TaskListPrefs
import dev.chara.tasks.shared.model.board.BoardList
import dev.chara.tasks.shared.model.board.BoardSection

data class DashboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val boardSections: List<BoardSection> = listOf(),
    val boardLists: List<BoardList> = listOf(),
    val allLists: List<TaskList> = listOf(),
    val allPrefs: List<TaskListPrefs> = listOf()
)

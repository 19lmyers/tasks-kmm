package dev.chara.tasks.viewmodel.home.board

import dev.chara.tasks.model.BoardSection
import dev.chara.tasks.model.PinnedList
import dev.chara.tasks.model.TaskList

data class BoardUiState(
    val isLoading: Boolean = false,
    val firstLoad: Boolean = false,

    val boardSections: List<BoardSection> = listOf(),
    val pinnedLists: List<PinnedList> = listOf(),
    val allLists: List<TaskList> = listOf(),
)

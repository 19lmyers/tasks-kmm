package dev.chara.tasks.viewmodel.home.board

import dev.chara.tasks.model.BoardSection
import dev.chara.tasks.model.PinnedList
import dev.chara.tasks.model.TaskList

sealed class BoardUiState {
    data class Loaded(
        val boardSections: List<BoardSection> = listOf(),
        val pinnedLists: List<PinnedList> = listOf(),
        val allLists: List<TaskList> = listOf(),
        val isInternetConnected: Boolean = false,
        val isRefreshing: Boolean = false,
    ) : BoardUiState()

    object Loading : BoardUiState()
}

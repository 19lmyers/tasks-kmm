package dev.chara.tasks.viewmodel.home

import dev.chara.tasks.model.BoardSection
import dev.chara.tasks.model.PinnedList
import dev.chara.tasks.model.Profile
import dev.chara.tasks.model.TaskList

data class HomeUiState(
    val isLoading: Boolean = false,
    val firstLoad: Boolean = false,

    val isAuthenticated: Boolean = false,
    val profile: Profile? = null,
    val boardSections: List<BoardSection> = listOf(),
    val pinnedLists: List<PinnedList> = listOf(),
    val allLists: List<TaskList> = listOf(),
)
package dev.chara.tasks.viewmodel.shortcut

import dev.chara.tasks.model.TaskList

data class ShortcutUiState(
    val isLoading: Boolean = false,
    val firstLoad: Boolean = false,

    val isAuthenticated: Boolean = false,
    val taskLists: List<TaskList> = listOf()
)

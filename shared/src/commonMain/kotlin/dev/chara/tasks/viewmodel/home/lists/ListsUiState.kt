package dev.chara.tasks.viewmodel.home.lists

import dev.chara.tasks.model.TaskList

data class ListsUiState(
    val isLoading: Boolean = false,
    val firstLoad: Boolean = false,

    val taskLists: List<TaskList> = listOf(),
)

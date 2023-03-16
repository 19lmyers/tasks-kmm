package dev.chara.tasks.viewmodel.home.lists

import dev.chara.tasks.model.TaskList

sealed class ListsUiState {

    data class Loaded(
        val taskLists: List<TaskList> = listOf(),
        val isInternetConnected: Boolean = false,
        val isRefreshing: Boolean = false,
    ) : ListsUiState()

    object Loading : ListsUiState()
}

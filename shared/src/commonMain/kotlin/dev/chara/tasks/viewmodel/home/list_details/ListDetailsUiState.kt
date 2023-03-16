package dev.chara.tasks.viewmodel.home.list_details

import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList

sealed class ListDetailsUiState {

    data class Loaded(
        val selectedList: TaskList,
        val currentTasks: List<Task> = listOf(),
        val completedTasks: List<Task> = listOf(),
        val isInternetConnected: Boolean = false,
        val isRefreshing: Boolean = false,
    ) : ListDetailsUiState()

    object NotFound : ListDetailsUiState()
    object Loading : ListDetailsUiState()
}
package dev.chara.tasks.viewmodel.home.task_details

import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList

sealed class TaskDetailsUiState {
    data class Loaded(
        val task: Task,
        val taskLists: List<TaskList>,
        val isInternetConnected: Boolean = false,
        val isRefreshing: Boolean = false
    ) : TaskDetailsUiState()

    object NotFound : TaskDetailsUiState()
    object Loading : TaskDetailsUiState()
}
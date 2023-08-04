package dev.chara.tasks.shared.component.home.list_details

import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList

data class ListDetailsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,

    val selectedList: TaskList? = null,
    val currentTasks: List<Task> = listOf(),
    val completedTasks: List<Task> = listOf(),
)
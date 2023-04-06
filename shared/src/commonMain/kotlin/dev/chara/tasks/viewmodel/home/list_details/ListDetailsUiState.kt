package dev.chara.tasks.viewmodel.home.list_details

import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList

data class ListDetailsUiState(
    val isLoading: Boolean = false,
    val firstLoad: Boolean = false,

    val selectedList: TaskList? = null,
    val currentTasks: List<Task> = listOf(),
    val completedTasks: List<Task> = listOf(),
)
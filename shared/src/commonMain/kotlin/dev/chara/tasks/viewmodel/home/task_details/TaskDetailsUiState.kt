package dev.chara.tasks.viewmodel.home.task_details

import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList

data class TaskDetailsUiState(
    val isLoading: Boolean = false,
    val firstLoad: Boolean = false,

    val task: Task? = null,
    val taskLists: List<TaskList> = listOf(),
)

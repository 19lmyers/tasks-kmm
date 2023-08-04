package dev.chara.tasks.shared.component.home.task_details

import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList

data class TaskDetailsUiState(
    val isLoading: Boolean = true,

    val showConfirmExit: Boolean = false,

    val selectedTask: Task? = null,
    val allLists: List<TaskList> = listOf(),
)

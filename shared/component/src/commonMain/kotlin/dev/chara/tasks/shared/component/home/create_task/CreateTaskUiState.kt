package dev.chara.tasks.shared.component.home.create_task

import dev.chara.tasks.shared.model.TaskList

data class CreateTaskUiState(
    val isLoading: Boolean = true,

    val allLists: List<TaskList> = listOf()
)

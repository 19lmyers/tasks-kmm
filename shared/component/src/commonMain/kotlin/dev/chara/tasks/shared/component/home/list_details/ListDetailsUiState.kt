package dev.chara.tasks.shared.component.home.list_details

import dev.chara.tasks.shared.model.Profile
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.TaskListPrefs

data class ListDetailsUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val selectedList: TaskList? = null,
    val prefs: TaskListPrefs? = null,
    val currentTasks: List<Task> = listOf(),
    val completedTasks: List<Task> = listOf(),
)

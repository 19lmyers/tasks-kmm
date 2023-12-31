package dev.chara.tasks.shared.component.home.join_list

import dev.chara.tasks.shared.model.Profile
import dev.chara.tasks.shared.model.TaskList

data class JoinListUiState(
    val isLoading: Boolean = true,
    val taskList: TaskList? = null,
    val owner: Profile? = null
)

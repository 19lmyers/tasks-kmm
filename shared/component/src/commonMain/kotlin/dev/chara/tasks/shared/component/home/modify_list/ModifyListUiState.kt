package dev.chara.tasks.shared.component.home.modify_list

import dev.chara.tasks.shared.model.TaskList

data class ModifyListUiState(
    val isLoading: Boolean = true,

    val selectedList: TaskList? = null
)

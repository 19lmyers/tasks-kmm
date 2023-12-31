package dev.chara.tasks.shared.component.home.share_list

import dev.chara.tasks.shared.model.Profile
import dev.chara.tasks.shared.model.TaskList

data class ShareListUiState(
    val isLoading: Boolean = true,
    val profile: Profile? = null,
    val list: TaskList? = null,
    val owner: Profile? = null,
    val members: List<Profile> = emptyList()
)

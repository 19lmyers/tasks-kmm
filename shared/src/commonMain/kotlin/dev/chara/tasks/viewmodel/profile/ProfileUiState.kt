package dev.chara.tasks.viewmodel.profile

import dev.chara.tasks.model.Profile

data class ProfileUiState(
    val isLoading: Boolean = false,

    val profile: Profile? = null,
)

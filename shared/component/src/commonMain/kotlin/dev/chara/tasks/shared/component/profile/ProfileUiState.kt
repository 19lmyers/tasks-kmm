package dev.chara.tasks.shared.component.profile

import dev.chara.tasks.shared.model.Profile

data class ProfileUiState(
    val isUploading: Boolean = false,

    val profile: Profile? = null,
)

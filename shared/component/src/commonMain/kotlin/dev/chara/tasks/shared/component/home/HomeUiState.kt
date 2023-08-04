package dev.chara.tasks.shared.component.home

import dev.chara.tasks.shared.model.Profile

data class HomeUiState(
    val verifyEmailSent: Boolean = false,

    val profile: Profile? = null
)
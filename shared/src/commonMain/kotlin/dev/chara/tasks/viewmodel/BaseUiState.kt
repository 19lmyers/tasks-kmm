package dev.chara.tasks.viewmodel

import dev.chara.tasks.model.Theme

data class BaseUiState(
    val appTheme: Theme = Theme.SYSTEM_DEFAULT,
    val useVibrantColors: Boolean = false
)
package dev.chara.tasks.viewmodel

import dev.chara.tasks.model.preference.Theme

data class BaseUiState(
    val appTheme: Theme = Theme.SYSTEM_DEFAULT,
    val useVibrantColors: Boolean = false
)
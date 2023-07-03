package dev.chara.tasks.viewmodel

import dev.chara.tasks.model.preference.Theme
import dev.chara.tasks.model.preference.ThemeVariant

data class BaseUiState(
    val appTheme: Theme = Theme.SYSTEM_DEFAULT,
    val appThemeVariant: ThemeVariant = ThemeVariant.TONAL_SPOT
)
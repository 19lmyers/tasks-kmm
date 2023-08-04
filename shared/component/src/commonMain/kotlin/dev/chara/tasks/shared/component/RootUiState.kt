package dev.chara.tasks.shared.component

import dev.chara.tasks.shared.model.preference.Theme
import dev.chara.tasks.shared.model.preference.ThemeVariant

data class RootUiState(
    val appTheme: Theme = Theme.SYSTEM_DEFAULT,
    val appThemeVariant: ThemeVariant = ThemeVariant.TONAL_SPOT
)
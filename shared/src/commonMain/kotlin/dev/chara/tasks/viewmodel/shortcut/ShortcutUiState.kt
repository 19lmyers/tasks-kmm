package dev.chara.tasks.viewmodel.shortcut

import dev.chara.tasks.model.TaskList

sealed class ShortcutUiState {
    data class Authenticated(
        val taskLists: List<TaskList>
    ) : ShortcutUiState()

    object NotAuthenticated : ShortcutUiState()
    object Loading : ShortcutUiState()
}

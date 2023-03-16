package dev.chara.tasks.viewmodel.home

import dev.chara.tasks.model.Profile
import dev.chara.tasks.model.StartScreen
import dev.chara.tasks.model.TaskList

sealed class HomeUiState {
    data class Authenticated(
        val profile: Profile,
        val startScreen: StartScreen,
        val taskLists: List<TaskList>
    ) : HomeUiState()

    object NotAuthenticated : HomeUiState()
    object Loading : HomeUiState()
}

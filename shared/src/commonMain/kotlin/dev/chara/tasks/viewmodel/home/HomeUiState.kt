package dev.chara.tasks.viewmodel.home

import dev.chara.tasks.model.Profile
import dev.chara.tasks.model.StartScreen
import dev.chara.tasks.model.TaskList

data class HomeUiState(
    val isLoading: Boolean = false,
    val firstLoad: Boolean = false,

    val isAuthenticated: Boolean = false,
    val profile: Profile? = null,
    val startScreen: StartScreen = StartScreen.BOARD,
    val taskLists: List<TaskList> = listOf()
)
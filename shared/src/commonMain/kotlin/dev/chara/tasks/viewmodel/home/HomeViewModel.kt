package dev.chara.tasks.viewmodel.home

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Profile
import dev.chara.tasks.model.Task
import dev.chara.tasks.viewmodel.util.SnackbarMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.isUserAuthenticated(),
                repository.getStartScreen(),
                repository.getLists()
            ) { isUserAuthenticated, startScreen, taskLists ->
                if (isUserAuthenticated) {
                    val profile = repository.getUserProfile().first()!!
                    HomeUiState.Authenticated(
                        profile = profile,
                        startScreen = startScreen,
                        taskLists = taskLists
                    )
                } else {
                    HomeUiState.NotAuthenticated
                }
            }.collect {
                _uiState.value = it
            }

            if (uiState.value is HomeUiState.Authenticated) {
                repository.refresh()
            }
        }
    }

    fun updateUserProfile(profile: Profile) {
        viewModelScope.launch {
            val result = repository.updateUserProfile(profile)

            _messages.emitAsMessage(
                result = result,
                successMessage = "Profile updated"
            )
        }
    }

    fun updateUserProfilePhoto(photo: ByteArray) {
        viewModelScope.launch {
            val result = repository.updateUserProfilePhoto(photo)

            _messages.emitAsMessage(
                result = result,
                successMessage = "Photo updated"
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun createTask(listId: String, task: Task) {
        viewModelScope.launch {
            val result = repository.createTask(listId, task)
            _messages.emitAsMessage(result)
        }
    }
}
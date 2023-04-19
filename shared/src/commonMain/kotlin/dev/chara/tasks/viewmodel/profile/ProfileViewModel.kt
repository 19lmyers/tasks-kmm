package dev.chara.tasks.viewmodel.profile

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Profile
import dev.chara.tasks.viewmodel.util.PopupMessage
import dev.chara.tasks.viewmodel.util.emitAsMessage
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileViewModel : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState = _uiState.asStateFlow().cStateFlow()

    private val _messages = MutableSharedFlow<PopupMessage>()
    val messages = _messages.asSharedFlow().cFlow()

    init {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    profile = profile
                )
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
}
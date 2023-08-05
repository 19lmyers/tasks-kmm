package dev.chara.tasks.shared.component.profile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.domain.Gravatar
import dev.chara.tasks.shared.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ProfileComponent : BackHandlerOwner {
    val state: StateFlow<ProfileUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun onChangeEmail()

    fun onChangePassword()

    fun getGravatarUri(email: String): String

    fun updateUserProfile(profile: Profile)

    fun uploadProfilePhoto(photo: ByteArray)
}

class DefaultProfileComponent(
    componentContext: ComponentContext,
    private val navigateUp: () -> Unit,
    private val navigateToChangeEmail: () -> Unit,
    private val navigateToChangePassword: () -> Unit
) : ProfileComponent, KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(ProfileUiState())
    override val state = _state.asStateFlow()

    private val _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            repository.getUserProfile().collect { profile ->
                _state.value = ProfileUiState(profile = profile)
            }
        }
    }

    override fun onUp() = navigateUp()

    override fun onChangeEmail() = navigateToChangeEmail()

    override fun onChangePassword() = navigateToChangePassword()

    override fun getGravatarUri(email: String) = Gravatar.getUri(email)

    override fun updateUserProfile(profile: Profile) {
        coroutineScope.launch {
            _state.value = state.value.copy(isUploading = true)

            val result = repository.updateUserProfile(profile)

            _state.value = state.value.copy(isUploading = false)

            _messages.emitAsMessage(result = result, successMessage = "Profile updated")
        }
    }

    override fun uploadProfilePhoto(photo: ByteArray) {
        coroutineScope.launch {
            _state.value = state.value.copy(isUploading = true)

            val result = repository.updateUserProfilePhoto(photo)

            _state.value = state.value.copy(isUploading = false)

            _messages.emitAsMessage(result = result, successMessage = "Photo updated")
        }
    }
}

package dev.chara.tasks.util

import dev.chara.tasks.data.Repository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MessagingHelper : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    fun onNewToken(token: String) {
        viewModelScope.launch {
            if (!repository.isUserAuthenticated().first()) return@launch

            repository.linkFCMToken(token)
        }
    }
}
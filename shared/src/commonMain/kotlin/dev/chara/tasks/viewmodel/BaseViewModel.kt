package dev.chara.tasks.viewmodel

import dev.chara.tasks.data.Repository
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BaseViewModel : ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private var _uiState = MutableStateFlow(BaseUiState())
    val uiState = _uiState.asStateFlow().cStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAppTheme(),
                repository.getAppThemeVariant()
            ) { appTheme, variant ->
                BaseUiState(
                    appTheme = appTheme,
                    appThemeVariant = variant
                )
            }.collect {
                _uiState.value = it
            }
        }
    }
}
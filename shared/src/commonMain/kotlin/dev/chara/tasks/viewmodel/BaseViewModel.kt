package dev.chara.tasks.viewmodel

import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject

class BaseViewModel(coroutineScope: CoroutineScope) : ViewModel(coroutineScope) {
    private val repository: Repository by inject()

    fun getAppTheme(): Flow<Theme> = repository.getAppTheme()

    fun useVibrantColors(): Flow<Boolean> = repository.useVibrantColors()
}
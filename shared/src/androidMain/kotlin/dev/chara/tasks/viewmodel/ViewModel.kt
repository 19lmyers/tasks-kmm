package dev.chara.tasks.viewmodel

import androidx.lifecycle.viewModelScope
import org.koin.core.component.KoinComponent
import androidx.lifecycle.ViewModel as AndroidXViewModel

actual abstract class ViewModel : AndroidXViewModel(), KoinComponent {
    actual val coroutineScope = viewModelScope
}
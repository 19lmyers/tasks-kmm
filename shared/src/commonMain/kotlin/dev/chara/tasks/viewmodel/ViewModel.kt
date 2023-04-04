package dev.chara.tasks.viewmodel

import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent


expect abstract class ViewModel() : KoinComponent {
    val coroutineScope: CoroutineScope
}
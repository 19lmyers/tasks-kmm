package dev.chara.tasks.viewmodel

import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent

actual abstract class ViewModel: KoinComponent {
    actual val coroutineScope: CoroutineScope
        get() {
            return getScopeInstance() ?: createScopeInstance()
        }

    fun clear() {

    }

}
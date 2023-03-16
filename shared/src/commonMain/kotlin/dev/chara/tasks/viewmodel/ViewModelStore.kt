package dev.chara.tasks.viewmodel

import io.github.aakira.napier.Napier
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job

/**
 * Adapted from https://github.com/chrisbanes/tivi/pull/806/files
 */
class ViewModelStore(coroutineScope: CoroutineScope) {

    private val presenterMap = HashMap<Any, PresenterStoreEntry<*>>()

    private val mapLock = reentrantLock()

    private val coroutineScope =
        CoroutineScope(coroutineScope.coroutineContext + SupervisorJob(coroutineScope.coroutineContext.job))

    fun <T : ViewModel> viewModelFlow(
        key: Any,
        cancellationSignal: (suspend () -> Unit)? = null,
        factory: (CoroutineScope) -> T
    ): StateFlow<T> = mapLock.withLock {
        val cached = presenterMap[key]?.takeIf { it.isActive }

        if (cached != null) {
            @Suppress("UNCHECKED_CAST")
            return cached.flow as StateFlow<T>
        }

        val presenterScope =
            CoroutineScope(Dispatchers.Main.immediate + SupervisorJob(coroutineScope.coroutineContext.job))

        val flow = flow<T> {
            Napier.d("Creating CoroutineScope with key: $key")

            if (cancellationSignal != null) {
                cancellationSignal()
            } else {
                awaitCancellation()
            }
        }.onCompletion {
            Napier.d("Cancelling CoroutineScope with key: $key.")
            presenterScope.cancel()
            mapLock.withLock { presenterMap.remove(key) }
        }.stateIn(
            scope = coroutineScope,
            started = when {
                cancellationSignal != null -> SharingStarted.Eagerly
                else -> SharingStarted.WhileSubscribed(5000)
            },
            initialValue = factory(presenterScope)
        )

        presenterMap[key] = PresenterStoreEntry(
            coroutineScope = presenterScope,
            flow = flow,
        )

        return flow
    }
}

private data class PresenterStoreEntry<T : ViewModel>(
    val coroutineScope: CoroutineScope,
    val flow: StateFlow<T>
) {
    val isActive: Boolean get() = coroutineScope.isActive
}
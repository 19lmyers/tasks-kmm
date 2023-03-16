package dev.chara.tasks.android.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.chara.tasks.viewmodel.ViewModel
import dev.chara.tasks.viewmodel.ViewModelStore
import dev.olshevski.navigation.reimagined.NavHostEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine

fun <T : ViewModel> ViewModelStore.viewModelFlow(
    navHostEntry: NavHostEntry<*>,
    factory: (scope: CoroutineScope) -> T,
): StateFlow<T> = viewModelFlow(
    key = navHostEntry.id,
    cancellationSignal = {
        suspendCancellableCoroutine<Unit> { cont ->
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    cont.cancel()
                }
            }

            navHostEntry.lifecycle.addObserver(observer)

            cont.invokeOnCancellation {
                navHostEntry.lifecycle.removeObserver(observer)
            }
        }
    },
    factory = factory,
)

@Composable
fun <T : ViewModel> ViewModelStore.viewModel(
    key: Any,
    factory: (CoroutineScope) -> T
): T = viewModelFlow(key, null, factory).collectAsState().value

@Composable
fun <T : ViewModel> ViewModelStore.viewModel(
    navHostEntry: NavHostEntry<*>,
    factory: (CoroutineScope) -> T,
): T = viewModelFlow(navHostEntry, factory).collectAsState().value

val LocalViewModelStore = staticCompositionLocalOf<ViewModelStore> {
    error("CompositionLocal PresenterStore not present")
}
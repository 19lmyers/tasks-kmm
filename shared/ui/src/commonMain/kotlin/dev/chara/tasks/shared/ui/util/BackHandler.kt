package dev.chara.tasks.shared.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler

@Composable
fun BackHandler(
    backHandler: BackHandler,
    onBack: () -> Unit,
) {
    val callback = remember { BackCallback(onBack = onBack) }

    DisposableEffect(backHandler, callback) {
        backHandler.register(callback)
        onDispose { backHandler.unregister(callback) }
    }
}

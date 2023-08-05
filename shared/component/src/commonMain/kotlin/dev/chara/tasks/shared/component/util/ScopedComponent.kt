package dev.chara.tasks.shared.component.util

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

fun CoroutineScope(context: CoroutineContext, lifecycle: Lifecycle): CoroutineScope {
    val scope = CoroutineScope(context)
    lifecycle.doOnDestroy(scope::cancel)
    return scope
}

fun LifecycleOwner.coroutineScope(context: CoroutineContext) = CoroutineScope(context, lifecycle)

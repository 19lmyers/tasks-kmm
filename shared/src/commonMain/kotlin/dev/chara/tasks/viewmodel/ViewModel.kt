package dev.chara.tasks.viewmodel

import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent


abstract class ViewModel(val coroutineScope: CoroutineScope) : KoinComponent
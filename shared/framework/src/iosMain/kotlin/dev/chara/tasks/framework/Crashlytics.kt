@file:Suppress("unused")

package dev.chara.tasks.framework

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin

fun recordException(throwable: Throwable) = CrashlyticsKotlin.sendHandledException(throwable)

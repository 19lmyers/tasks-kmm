package dev.chara.tasks.bridge

import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import platform.Foundation.NSDate

fun NSDate.toInstant(): Instant {
    return this.toKotlinInstant()
}
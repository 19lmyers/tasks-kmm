package dev.chara.tasks

import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSinceNow
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

fun NSDate?.toInstantOrNull(): Instant? {
    return this?.toInstant()
}

fun NSDate.toInstant(): Instant {
    val instant = toKotlinInstant().plus(timeIntervalSinceNow().seconds)
    val excess = instant.nanosecondsOfSecond % 1000
    return instant.minus(excess.nanoseconds)
}
fun Instant.toDate(): NSDate = toNSDate()
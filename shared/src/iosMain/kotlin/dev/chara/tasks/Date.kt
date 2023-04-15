package dev.chara.tasks

import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDate

fun NSDate?.toInstantOrNull(): Instant? {
    return this?.toInstant()
}

fun NSDate.toInstant(): Instant = toKotlinInstant()
fun Instant.toDate(): NSDate = toNSDate()
package dev.chara.tasks.util.time

import kotlinx.datetime.Instant

expect class SQLInstantFormatter() {
    fun decode(encodedValue: String): Instant
    fun encode(value: Instant): String
}
package dev.chara.tasks.util.time

import kotlinx.datetime.Instant

expect class FriendlyInstantFormatter {
    fun formatDate(value: Instant): String
    fun formatDateTime(value: Instant): String
}
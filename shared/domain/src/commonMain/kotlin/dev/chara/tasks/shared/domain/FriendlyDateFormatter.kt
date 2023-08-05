package dev.chara.tasks.shared.domain

import kotlinx.datetime.Instant

expect class FriendlyDateFormatter {
    fun formatDate(value: Instant): String

    fun formatDateTime(value: Instant): String

    fun formatTime(value: Instant): String
}

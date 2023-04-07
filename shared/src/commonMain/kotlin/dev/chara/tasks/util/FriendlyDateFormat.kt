package dev.chara.tasks.util

import kotlinx.datetime.Instant

expect class FriendlyDateFormat {
    fun formatDate(value: Instant): String
    fun formatDateTime(value: Instant): String
}
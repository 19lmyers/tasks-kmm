package dev.chara.tasks.shared.domain

import android.content.Context
import android.text.format.DateUtils
import kotlinx.datetime.Instant

actual class FriendlyDateFormatter(private val context: Context) {
    actual fun formatDate(value: Instant): String {
        val millis = value.toEpochMilliseconds()

        val flags = (DateUtils.FORMAT_SHOW_DATE
                or DateUtils.FORMAT_SHOW_WEEKDAY
                or DateUtils.FORMAT_ABBREV_ALL)

        return DateUtils.formatDateTime(context, millis, flags)
    }

    actual fun formatDateTime(value: Instant): String {
        val millis = value.toEpochMilliseconds()

        var flags = DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_ABBREV_TIME

        if (!DateUtils.isToday(millis)) {
            flags =
                flags or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_ALL
        }

        return DateUtils.formatDateTime(context, millis, flags)
    }

    actual fun formatTime(value: Instant): String {
        val millis = value.toEpochMilliseconds()

        val flags = DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_ABBREV_TIME

        return DateUtils.formatDateTime(context, millis, flags)
    }
}
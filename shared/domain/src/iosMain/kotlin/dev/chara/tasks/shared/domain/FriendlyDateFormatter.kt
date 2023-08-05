package dev.chara.tasks.shared.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual class FriendlyDateFormatter {
    actual fun formatDate(value: Instant): String {
        val formatter = NSDateFormatter()

        formatter.dateStyle = NSDateFormatterMediumStyle
        formatter.timeStyle = NSDateFormatterNoStyle
        formatter.locale = NSLocale.currentLocale

        return formatter.stringFromDate(value.toNSDate())
    }

    actual fun formatDateTime(value: Instant): String {
        val date = value.toNSDate()

        val formatter = NSDateFormatter()

        formatter.dateStyle =
            if (NSCalendar.currentCalendar.isDateInToday(date)) {
                NSDateFormatterNoStyle
            } else {
                NSDateFormatterMediumStyle
            }
        formatter.timeStyle = NSDateFormatterShortStyle
        formatter.locale = NSLocale.currentLocale

        return formatter.stringFromDate(date)
    }

    actual fun formatTime(value: Instant): String {
        val formatter = NSDateFormatter()

        formatter.dateStyle = NSDateFormatterNoStyle
        formatter.timeStyle = NSDateFormatterShortStyle
        formatter.locale = NSLocale.currentLocale

        return formatter.stringFromDate(value.toNSDate())
    }
}

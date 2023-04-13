package dev.chara.tasks.util

import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

class FriendlyDateFormat {
    fun formatDate(date: NSDate): String {
        val formatter = NSDateFormatter()

        formatter.dateStyle = NSDateFormatterMediumStyle
        formatter.timeStyle = NSDateFormatterNoStyle
        formatter.locale = NSLocale.currentLocale

        return formatter.stringFromDate(date)
    }

    fun formatDateTie(date: NSDate): String {
        val formatter = NSDateFormatter()

        formatter.dateStyle = if (NSCalendar.currentCalendar.isDateInToday(date)) {
            NSDateFormatterNoStyle
        } else {
            NSDateFormatterMediumStyle
        }
        formatter.timeStyle = NSDateFormatterShortStyle
        formatter.locale = NSLocale.currentLocale

        return formatter.stringFromDate(date)
    }
}
package dev.chara.tasks.util.time

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoUnit
import java.time.LocalDateTime as JavaLocalDateTime

actual class SQLInstantFormatter {
    private val formatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .append(DateTimeFormatter.ISO_LOCAL_TIME)
        .toFormatter()

    actual fun decode(encodedValue: String) = formatter.parse(encodedValue)
        .query(JavaLocalDateTime::from)
        .toKotlinLocalDateTime()
        .toInstant(TimeZone.UTC)

    actual fun encode(value: Instant): String =
        formatter.format(
            value
                .toLocalDateTime(TimeZone.UTC)
                .toJavaLocalDateTime()
                .truncatedTo(ChronoUnit.SECONDS)
        )
}
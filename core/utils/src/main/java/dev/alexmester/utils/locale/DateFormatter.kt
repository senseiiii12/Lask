package dev.alexmester.utils.locale

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

object DateFormatter {
    fun formatCachedAtDate(timestamp: Long): String {
        val localDate = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
            .format(localDate)
    }

    fun formatPublishDate(dateString: String): String {
        val localDateTime = LocalDateTime
            .parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        return DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
            .withLocale(Locale.getDefault())
            .format(localDateTime)
    }
}
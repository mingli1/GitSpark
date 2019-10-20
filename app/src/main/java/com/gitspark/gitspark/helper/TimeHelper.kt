package com.gitspark.gitspark.helper

import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeHelper @Inject constructor() {

    fun now(): OffsetDateTime = OffsetDateTime.now()

    fun nowAsString(): String = now().format(ISO_OFFSET_DATE_TIME)

    fun parse(date: String) = ISO_OFFSET_DATE_TIME.parse(date, OffsetDateTime::from)

    fun isExpiredMinutes(date: OffsetDateTime, minutes: Long): Boolean =
            date.plusMinutes(minutes).isBefore(now())

    fun getRelativeTimeFormat(instant: Instant): String {
        val now = Instant.now().toEpochMilli()
        val time = instant.toEpochMilli()
        val elapsed = now - time

        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed)
        val hours = TimeUnit.MILLISECONDS.toHours(elapsed)
        val days = TimeUnit.MILLISECONDS.toDays(elapsed)

        return when {
            seconds < 60 -> formatAgo(seconds, "second")
            minutes < 60 -> formatAgo(minutes, "minute")
            hours < 24 -> formatAgo(hours, "hour")
            days < 7 -> formatAgo(days, "day")
            days < 30 -> formatAgo(days / 7, "week")
            days < 365 -> formatAgo(days / 30, "month")
            else -> formatAgo(days / 365, "year")
        }
    }

    private fun formatAgo(value: Long, time: String): String {
        return when (value) {
            1L -> "$value $time ago"
            else -> "$value ${time}s ago"
        }
    }
}
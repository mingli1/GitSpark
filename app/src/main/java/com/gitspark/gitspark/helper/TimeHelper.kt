package com.gitspark.gitspark.helper

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeHelper @Inject constructor() {

    fun now(): OffsetDateTime = OffsetDateTime.now()

    fun nowAsString(): String = now().format(ISO_OFFSET_DATE_TIME)

    fun parse(date: String) = ISO_OFFSET_DATE_TIME.parse(date, OffsetDateTime::from)

    fun isExpiredMinutes(date: OffsetDateTime, minutes: Long): Boolean =
            date.plusMinutes(minutes).isBefore(now())
}
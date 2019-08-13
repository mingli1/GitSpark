package com.gitspark.gitspark.helper

import org.threeten.bp.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeHelper @Inject constructor() {

    fun now(): Instant = Instant.now()

    fun nowPlusSeconds(seconds: Long) = instantPlusSeconds(Instant.now(), seconds)

    fun nowPlusMinutes(minutes: Long) = instantPlusMinutes(Instant.now(), minutes)

    fun instantPlusSeconds(instant: Instant, seconds: Long): Instant = instant.plusSeconds(seconds)

    fun instantPlusMinutes(instant: Instant, minutes: Long): Instant =
            instant.plusSeconds(TimeUnit.MINUTES.toSeconds(minutes))

    fun isExpiredMinutes(instant: Instant, minutes: Long): Boolean =
            instantPlusMinutes(instant, minutes).isAfter(now())
}
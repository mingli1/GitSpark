package com.gitspark.gitspark.helper

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.threeten.bp.Instant

class TimeHelperTest {

    private lateinit var timeHelper: TimeHelper

    @Before
    fun setup() {
        timeHelper = TimeHelper()
    }

    @Test
    fun shouldGetCurrentInstant() {
        val instant = timeHelper.now()
        assertThat(instant).isLessThanOrEqualTo(Instant.now())
    }

    @Test
    fun shouldGetSecondsFromNow() {
        val instant = timeHelper.nowPlusSeconds(3)
        assertThat(instant).isBetween(Instant.now(), Instant.now().plusSeconds(3))
    }

    @Test
    fun shouldGetMinutesFromNow() {
        val instant = timeHelper.nowPlusMinutes(3)
        assertThat(instant).isBetween(Instant.now(), Instant.now().plusSeconds(180))
    }
}
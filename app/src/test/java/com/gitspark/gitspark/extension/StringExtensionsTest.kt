package com.gitspark.gitspark.extension

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun shouldContainOneOfStrings() {
        assertThat("apple".containsOneOf("orange", "app", "grape")).isTrue()
        assertThat("apple".containsOneOf("orange", "grape")).isFalse()
    }

    @Test
    fun shouldContainAllOfStrings() {
        assertThat("apple melon".containsAll("apple", "melon")).isTrue()
        assertThat("banana apple".containsAll("banana", "apple", "orange")).isFalse()
    }

    @Test
    fun shouldGetMonthValue() {
        assertThat("January".monthValue()).isEqualTo(0)
        assertThat("February".monthValue()).isEqualTo(1)
        assertThat("March".monthValue()).isEqualTo(2)
        assertThat("April".monthValue()).isEqualTo(3)
        assertThat("May".monthValue()).isEqualTo(4)
        assertThat("June".monthValue()).isEqualTo(5)
        assertThat("July".monthValue()).isEqualTo(6)
        assertThat("August".monthValue()).isEqualTo(7)
        assertThat("September".monthValue()).isEqualTo(8)
        assertThat("October".monthValue()).isEqualTo(9)
        assertThat("November".monthValue()).isEqualTo(10)
        assertThat("December".monthValue()).isEqualTo(11)
    }

    @Test
    fun shouldGetSuffix() {
        assertThat(withSuffix(25)).isEqualTo("25")
        assertThat(withSuffix(1000)).isEqualTo("1.0K")
        assertThat(withSuffix(1250)).isEqualTo("1.3K")
        assertThat(withSuffix(25140)).isEqualTo("25.1K")
        assertThat(withSuffix(1234567)).isEqualTo("1.2M")
    }
}
package com.gitspark.gitspark.helper

import android.content.SharedPreferences
import com.gitspark.gitspark.model.PREFERENCES_TOKEN
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

private const val KEY = "key"
private const val VALUE = "value"

class PreferencesHelperTest {

    private lateinit var preferencesHelper: PreferencesHelper
    @MockK private lateinit var sharedPreferences: SharedPreferences
    @RelaxedMockK private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        preferencesHelper = PreferencesHelper(sharedPreferences)
        every { sharedPreferences.edit() } returns editor
    }

    @Test
    fun shouldSaveString() {
        preferencesHelper.saveString(KEY, VALUE)
        verify { editor.putString(KEY, VALUE).apply() }
    }

    @Test
    fun shouldGetStringIfKeyExists() {
        every { sharedPreferences.contains(KEY) } returns true
        every { sharedPreferences.getString(KEY, null) } returns VALUE

        val str = preferencesHelper.getString(KEY)

        assertThat(str).isEqualTo(VALUE)
    }

    @Test
    fun shouldGetNullIfKeyDoesNotExist() {
        every { sharedPreferences.contains(KEY) } returns false
        val str = preferencesHelper.getString(KEY)
        assertThat(str).isNull()
    }

    @Test
    fun shouldContainKey() {
        every { sharedPreferences.contains(KEY) } returns true
        val contains = preferencesHelper.contains(KEY)
        assertThat(contains).isTrue()
    }

    @Test
    fun shouldCacheAccessToken() {
        preferencesHelper.cacheAccessToken("value")
        verify { editor.putString(PREFERENCES_TOKEN, "value") }
    }

    @Test
    fun shouldCheckForExistingAccessToken() {
        every { sharedPreferences.contains(PREFERENCES_TOKEN) } returns true
        assertThat(preferencesHelper.hasExistingAccessToken()).isTrue()
    }
}
package com.gitspark.gitspark.helper

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        if (sharedPreferences.contains(key)) return sharedPreferences.getString(key, null)
        return null
    }
}
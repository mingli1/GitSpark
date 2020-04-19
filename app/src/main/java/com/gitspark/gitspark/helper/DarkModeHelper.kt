package com.gitspark.gitspark.helper

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class DarkModeHelper(private val context: Context) {

    fun isDarkMode(): Boolean {
        val mode = context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    fun setDarkMode(config: DarkModeConfig) {
        when (config) {
            DarkModeConfig.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            DarkModeConfig.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DarkModeConfig.System -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}

enum class DarkModeConfig {
    Light,
    Dark,
    System
}
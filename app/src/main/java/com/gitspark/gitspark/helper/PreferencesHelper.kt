package com.gitspark.gitspark.helper

import android.content.SharedPreferences
import com.gitspark.gitspark.model.PREFERENCES_LOGIN
import com.gitspark.gitspark.model.PREFERENCES_TOKEN
import com.gitspark.gitspark.model.Token
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

    fun contains(key: String) = sharedPreferences.contains(key)

    fun cacheAccessToken(token: Token) {
        saveString(PREFERENCES_TOKEN, token.value)
        saveString(token.hashedValue, token.value)
    }

    fun hasExistingAccessToken() = contains(PREFERENCES_TOKEN)

    fun isTokenCached(token: Token) = contains(token.hashedValue)

    fun getCachedToken() = checkNotNull(getString(PREFERENCES_TOKEN)) {
        "No token cached. Check that access token exists before calling this method."
    }

    fun getAuthUsername() = checkNotNull(getString(PREFERENCES_LOGIN)) {
        "No username cached. This method was called before a successful login."
    }
}
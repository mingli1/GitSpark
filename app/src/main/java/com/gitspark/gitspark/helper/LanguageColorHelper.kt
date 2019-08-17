package com.gitspark.gitspark.helper

import android.content.Context
import android.graphics.Color
import com.gitspark.gitspark.R
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageColorHelper @Inject constructor(context: Context) {

    private val json: JSONObject

    init {
        val jsonText = context.resources
            .openRawResource(R.raw.lang_colors)
            .bufferedReader().use { it.readText() }
        json = JSONObject(jsonText)
    }

    fun getColor(language: String): Int? {
        return try {
            Color.parseColor(json.getString(language))
        } catch (e: JSONException) {
            null
        }
    }
}
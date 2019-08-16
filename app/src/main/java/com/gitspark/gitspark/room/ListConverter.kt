package com.gitspark.gitspark.room

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi

class ListConverter {

    private val stringAdapter = Moshi.Builder().build().adapter<List<String>>(List::class.java)

    @TypeConverter
    fun stringsToJson(strs: List<String>): String {
        return stringAdapter.toJson(strs)
    }

    @TypeConverter
    fun jsonToStrings(json: String): List<String> {
        return stringAdapter.fromJson(json) ?: emptyList()
    }
}
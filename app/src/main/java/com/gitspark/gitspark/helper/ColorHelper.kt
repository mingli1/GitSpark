package com.gitspark.gitspark.helper

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColorHelper @Inject constructor() {

    fun getTextColor(bgColor: String): String {
        val r = Integer.parseInt(bgColor.substring(0, 2), 16).toFloat()
        val g = Integer.parseInt(bgColor.substring(2, 4), 16).toFloat()
        val b = Integer.parseInt(bgColor.substring(4, 6), 16).toFloat()

        return if ((r * 0.299f + g * 0.587f + b * 0.114f) > 186f) "#000000" else "#ffffff"
    }
}
package com.gitspark.gitspark.extension

import kotlin.math.pow

fun String.containsOneOf(vararg strs: String): Boolean {
    return strs.any { this.contains(it) }
}

fun String.containsAll(vararg strs: String): Boolean {
    return strs.all { this.contains(it) }
}

fun String.monthValue(): Int {
    return when (this) {
        "January" -> 0
        "February" -> 1
        "March" -> 2
        "April" -> 3
        "May" -> 4
        "June" -> 5
        "July" -> 6
        "August" -> 7
        "September" -> 8
        "October" -> 9
        "November" -> 10
        else -> 11
    }
}

fun withSuffix(num: Int): String {
    if (num < 1000) return num.toString()
    val exp = (Math.log(num.toDouble()) / Math.log(1000.0)).toInt()
    return String.format("%.1f%c", num / 1000f.pow(exp), "KMGTPE"[exp - 1])
}

fun String.getExtension(): String {
    val i = lastIndexOf(".")
    if (i < 0) return ""
    return substring(i + 1).toLowerCase()
}
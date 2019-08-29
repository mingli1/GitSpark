package com.gitspark.gitspark.model

data class Page<T>(
    val next: Int = 0,
    val last: Int = 0,
    val first: Int = 0,
    val prev: Int = 0,
    var value: List<T> = emptyList()
)

fun Int.isFirstPage() = this == 1

fun Int.isLastPage(last: Int) = if (last == -1) true else this == last
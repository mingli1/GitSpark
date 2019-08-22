package com.gitspark.gitspark.model

data class Page<T>(
    val next: Int,
    val last: Int,
    val first: Int,
    val prev: Int,
    var value: List<T> = emptyList()
)
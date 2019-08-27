package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.Page
import com.squareup.moshi.Json

data class ApiPage<T>(
    @field:Json(name = "next") val next: Int?,
    @field:Json(name = "last") val last: Int?,
    @field:Json(name = "first") val first: Int?,
    @field:Json(name = "prev") val prev: Int?,
    @field:Json(name = "response") val response: List<T>
) {

    fun <S> toModel() = Page<S>(
        next = next ?: -1,
        last = last ?: -1,
        first = first ?: -1,
        prev = prev ?: -1
    )
}
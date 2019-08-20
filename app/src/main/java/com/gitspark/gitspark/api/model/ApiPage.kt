package com.gitspark.gitspark.api.model

import com.squareup.moshi.Json

data class ApiPage<T>(
    @field:Json(name = "next") val next: Int?,
    @field:Json(name = "last") val last: Int?,
    @field:Json(name = "first") val first: Int?,
    @field:Json(name = "prev") val prev: Int?,
    @field:Json(name = "response") val response: T
)
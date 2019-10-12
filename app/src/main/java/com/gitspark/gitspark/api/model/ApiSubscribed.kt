package com.gitspark.gitspark.api.model

import com.squareup.moshi.Json

data class ApiSubscribed(
    @field:Json(name = "subscribed") val subscribed: Boolean,
    @field:Json(name = "ignored") val ignored: Boolean
)
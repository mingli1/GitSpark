package com.gitspark.gitspark.api.model

data class ApiRateLimit(
    val rate: ApiRate
)

data class ApiRate(
    val limit: Int,
    val remaining: Int,
    val reset: Long
)
package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.App
import com.gitspark.gitspark.model.Check
import com.gitspark.gitspark.model.CheckSuite
import com.squareup.moshi.Json

data class ApiCheck(
    @field:Json(name = "total_count") val totalCount: Int?,
    @field:Json(name = "check_suites") val suites: List<ApiCheckSuite>?
) {

    fun toModel() = Check(
        totalCount = totalCount ?: 0,
        suites = suites?.map { it.toModel() } ?: emptyList()
    )
}

data class ApiCheckSuite(
    @field:Json(name = "id") val id: Long?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "conclusion") val conclusion: String?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "latest_check_runs_count") val latestCheckRunsCount: Int?,
    @field:Json(name = "app") val app: ApiApp?
) {

    fun toModel() = CheckSuite(
        id = id ?: 0L,
        status = status ?: "",
        conclusion = conclusion ?: "",
        createdAt = createdAt ?: "",
        latestCheckRunsCount = latestCheckRunsCount ?: 0,
        app = app?.toModel() ?: App()
    )
}
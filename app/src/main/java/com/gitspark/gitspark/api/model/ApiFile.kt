package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.File
import com.gitspark.gitspark.model.Repo
import com.squareup.moshi.Json

data class ApiFile(
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "path") val path: String?,
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "url") val url: String?,
    @field:Json(name = "repository") val repo: ApiRepo?
) {
    fun toModel() = File(
        name = name ?: "",
        path = path ?: "",
        sha = sha ?: "",
        url = url ?: "",
        repo = repo?.toModel() ?: Repo()
    )
}
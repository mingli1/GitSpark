package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.Branch
import com.gitspark.gitspark.model.BranchCommit
import com.squareup.moshi.Json

data class ApiBranch(
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "commit") val commit: ApiBranchCommit?
) {

    fun toModel() = Branch(
        name = name ?: "",
        commit = commit?.toModel() ?: BranchCommit()
    )
}

data class ApiBranchCommit(
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "url") val url: String?
) {

    fun toModel() = BranchCommit(
        sha = sha ?: "",
        url = url ?: ""
    )
}
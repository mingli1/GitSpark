package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.RepoContent
import com.gitspark.gitspark.model.RepoLinks
import com.squareup.moshi.Json

data class ApiRepoContent(
    @field:Json(name = "type") val type: String?,
    @field:Json(name = "encoding") val encoding: String?,
    @field:Json(name = "size") val size: Int?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "path") val path: String?,
    @field:Json(name = "content") val content: String?,
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "url") val url: String?,
    @field:Json(name = "git_url") val gitUrl: String?,
    @field:Json(name = "html_url") val htmlUrl: String?,
    @field:Json(name = "download_url") val downloadUrl: String?,
    @field:Json(name = "_links") val links: ApiRepoLinks?
) {

    fun toModel() = RepoContent(
        type = type ?: "",
        encoding = encoding ?: "",
        size = size ?: 0,
        name = name ?: "",
        path = path ?: "",
        content = content ?: "",
        sha = sha ?: "",
        url = url ?: "",
        gitUrl = gitUrl ?: "",
        htmlUrl = htmlUrl ?: "",
        downloadUrl = downloadUrl ?: "",
        links = links?.toModel() ?: RepoLinks()
    )
}

data class ApiRepoLinks(
    @field:Json(name = "git") val git: String?,
    @field:Json(name = "self") val self: String?,
    @field:Json(name = "html") val html: String?
) {

    fun toModel() = RepoLinks(
        git = git ?: "",
        self = self ?: "",
        html = html ?: ""
    )
}
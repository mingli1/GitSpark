package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.App
import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiApp(
    @field:Json(name = "id") val id: Long?,
    @field:Json(name = "slug") val slug: String?,
    @field:Json(name = "owner") val owner: ApiUser?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "html_url") val htmlUrl: String?
) {

    fun toModel() = App(
        id = id ?: 0L,
        slug = slug ?: "",
        owner = owner?.toModel() ?: User(),
        name = name ?: "",
        description = description ?: "",
        createdAt = createdAt ?: "",
        htmlUrl = htmlUrl ?: ""
    )
}
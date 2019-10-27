package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiIssue(
    @field:Json(name = "id") val id: Int?,
    @field:Json(name = "number") val number: Int?,
    @field:Json(name = "state") val state: String?,
    @field:Json(name = "title") val title: String?,
    @field:Json(name = "body") val body: String?,
    @field:Json(name = "user") val user: ApiUser?,
    @field:Json(name = "labels") val labels: List<ApiLabel>?,
    @field:Json(name = "assignee") val assignee: ApiUser?,
    @field:Json(name = "assignees") val assignees: List<ApiUser>?,
    // todo milestone?
    @field:Json(name = "locked") val locked: Boolean?,
    @field:Json(name = "comments") val numComments: Int?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "updated_at") val updatedAt: String?,
    @field:Json(name = "repository") val repo: ApiRepo?
) {
    fun toModel() = Issue(
        id = id ?: 0,
        number = number ?: 0,
        state = state ?: "",
        title = title ?: "",
        body = body ?: "",
        user = user?.toModel() ?: User(),
        labels = labels?.map { it.toModel() } ?: emptyList(),
        assignee = assignee?.toModel() ?: User(),
        assignees = assignees?.map{ it.toModel() } ?: emptyList(),
        locked = locked ?: false,
        numComments = numComments ?: 0,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        repo = repo?.toModel() ?: Repo()
    )
}

data class ApiLabel(
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "color") val color: String?,
    @field:Json(name = "default") val default: Boolean?
) {
    fun toModel() = Label(
        name = name ?: "",
        description = description ?: "",
        color = color ?: "",
        default = default ?: false
    )
}
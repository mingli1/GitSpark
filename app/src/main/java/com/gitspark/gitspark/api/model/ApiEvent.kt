package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.Payload
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiEvent(
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "type") val type: String?,
    @field:Json(name = "actor") val actor: ApiUser?,
    @field:Json(name = "repo") val repo: ApiRepo?,
    @field:Json(name = "payload") val payload: ApiPayload?,
    @field:Json(name = "public") val public: Boolean?,
    @field:Json(name = "created_at") val createdAt: String?
) {
    fun toModel() = Event(
        id = id ?: "",
        type = type ?: "",
        actor = actor?.toModel() ?: User(),
        repo = repo?.toModel() ?: Repo(),
        payload = payload?.toModel() ?: Payload(),
        public = public ?: false,
        createdAt = createdAt ?: ""
    )
}
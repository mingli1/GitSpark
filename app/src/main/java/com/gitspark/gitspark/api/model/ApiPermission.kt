package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.PERMISSION_NONE
import com.gitspark.gitspark.model.Permission
import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiPermission(
    @field:Json(name = "permission") val permission: String?,
    @field:Json(name = "user") val user: ApiUser?
) {
    fun toModel() = Permission(
        permission = permission ?: PERMISSION_NONE,
        user = user?.toModel() ?: User()
    )
}
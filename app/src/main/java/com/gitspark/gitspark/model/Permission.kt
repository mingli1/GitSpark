package com.gitspark.gitspark.model

const val PERMISSION_ADMIN = "admin"
const val PERMISSION_WRITE = "write"
const val PERMISSION_READ = "read"
const val PERMISSION_NONE = "none"

data class Permission(
    val permission: String = PERMISSION_NONE,
    val user: User = User()
)
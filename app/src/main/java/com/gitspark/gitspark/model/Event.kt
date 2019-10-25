package com.gitspark.gitspark.model

data class Event(
    val id: String = "",
    val type: String = "",
    val actor: User = User(),
    val repo: Repo = Repo(),
    val payload: Payload = Payload(),
    val public: Boolean = false,
    val createdAt: String = ""
)
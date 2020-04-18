package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class Event(
    val id: String = "",
    val type: String = "",
    val actor: User = User(),
    val repo: Repo = Repo(),
    val payload: Payload = Payload(),
    val public: Boolean = false,
    val createdAt: String = ""
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW

    override fun areItemsTheSame(other: Pageable) = this == (other as? Event)
}
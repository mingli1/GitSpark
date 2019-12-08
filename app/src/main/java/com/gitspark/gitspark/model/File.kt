package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class File(
    val name: String = "",
    val path: String = "",
    val sha: String = "",
    val url: String = "",
    val repo: Repo = Repo()
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW
}
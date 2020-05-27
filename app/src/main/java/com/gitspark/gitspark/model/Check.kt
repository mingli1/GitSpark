package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class Check(
    val totalCount: Int = 0,
    val suites: List<CheckSuite> = emptyList()
)

data class CheckSuite(
    val id: Long = 0L,
    val status: String = "",
    val conclusion: String = "",
    val createdAt: String = "",
    val latestCheckRunsCount: Int = 0,
    val app: App = App()
) : Pageable {

    override fun areItemsTheSame(other: Pageable) = this == (other as? CheckSuite) ?: false

    override fun getViewType() = VIEW_TYPE_VIEW
}
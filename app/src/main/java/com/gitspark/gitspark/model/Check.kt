package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

const val CHECK_STATUS_QUEUED = "queued"
const val CHECK_STATUS_IN_PROGRESS = "in_progress"
const val CHECK_STATUS_COMPLETED = "completed"

const val CHECK_CONCLUSION_SUCCESS = "success"
const val CHECK_CONCLUSION_FAILURE = "failure"
const val CHECK_CONCLUSION_NEUTRAL = "neutral"
const val CHECK_CONCLUSION_CANCELLED = "cancelled"
const val CHECK_CONCLUSION_SKIPPED = "skipped"

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

    fun isSuccessful() = status == CHECK_STATUS_COMPLETED &&
            (conclusion == CHECK_CONCLUSION_SUCCESS || conclusion == CHECK_CONCLUSION_SKIPPED)

    fun isPending() = status != CHECK_STATUS_COMPLETED

    fun isFailure() = status == CHECK_STATUS_COMPLETED &&
            conclusion != CHECK_CONCLUSION_SUCCESS && conclusion != CHECK_CONCLUSION_SKIPPED
}
package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.Issue
import com.squareup.moshi.Json

data class ApiIssueEditRequest(
    @field:Json(name = "title") val title: String,
    @field:Json(name = "body") val body: String,
    @field:Json(name = "state") val state: String,
    @field:Json(name = "milestone") val milestone: Int? = null,
    @field:Json(name = "labels") val labels: List<String>,
    @field:Json(name = "assignees") val assignees: List<String>
) {

    companion object {
        fun changeState(state: String, issue: Issue) = ApiIssueEditRequest(
            title = issue.title,
            body = issue.body,
            state = state,
            labels = issue.labels.map { it.name },
            assignees = issue.assignees.map { it.login }
        )
    }
}
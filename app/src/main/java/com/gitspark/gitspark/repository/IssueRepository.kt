package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.service.ISSUE_EVENTS_PER_PAGE
import com.gitspark.gitspark.api.service.IssueService
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.IssueComment
import com.gitspark.gitspark.model.IssueEvent
import com.gitspark.gitspark.model.Page
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueRepository @Inject constructor(
    private val prefsHelper: PreferencesHelper,
    private val retrofitHelper: RetrofitHelper
) {

    fun getIssue(
        username: String,
        repoName: String,
        issueNum: Int
    ): Observable<IssueResult<Issue>> {
        return getIssueService()
            .getIssue(username, repoName, issueNum)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to obtain issue.") }
    }

    fun getIssueComments(
        username: String,
        repoName: String,
        issueNum: Int,
        page: Int,
        perPage: Int = ISSUE_EVENTS_PER_PAGE
    ): Observable<IssueResult<Page<IssueComment>>> {
        return getIssueService()
            .getIssueComments(username, repoName, issueNum, page, perPage)
            .map {
                getSuccess(it.toModel<IssueComment>().apply {
                    value = it.response.map { comment -> comment.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain issue comments.") }
    }

    fun getIssueEvents(
        username: String,
        repoName: String,
        issueNum: Int,
        page: Int,
        perPage: Int = ISSUE_EVENTS_PER_PAGE
    ): Observable<IssueResult<Page<IssueEvent>>> {
        return getIssueService()
            .getIssueEvents(username, repoName, issueNum, page, perPage)
            .map {
                getSuccess(it.toModel<IssueEvent>().apply {
                    value = it.response.map { event -> event.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain issue events.") }
    }

    private fun getIssueService() =
        retrofitHelper.getRetrofit(token = prefsHelper.getCachedToken())
            .create(IssueService::class.java)

    private fun <T> getSuccess(value: T): IssueResult<T> = IssueResult.Success(value)

    private fun <T> getFailure(error: String): IssueResult<T> = IssueResult.Failure(error)
}

sealed class IssueResult<T> {
    data class Success<T>(val value: T) : IssueResult<T>()
    data class Failure<T>(val error: String) : IssueResult<T>()
}
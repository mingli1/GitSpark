package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.model.ApiIssueCommentRequest
import com.gitspark.gitspark.api.model.ApiIssueEditRequest
import com.gitspark.gitspark.api.service.ISSUE_EVENTS_PER_PAGE
import com.gitspark.gitspark.api.service.IssueService
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
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

    @Deprecated("Replaced with timeline events")
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

    @Deprecated("Replaced with timeline events")
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

    fun getIssueTimeline(
        username: String,
        repoName: String,
        issueNum: Int,
        page: Int,
        perPage: Int = ISSUE_EVENTS_PER_PAGE
    ): Single<IssueResult<Page<IssueEvent>>> {
        return getIssueService()
            .getIssueTimeline(username, repoName, issueNum, page, perPage)
            .map {
                getSuccess(it.toModel<IssueEvent>().apply {
                    value = it.response.map { event -> event.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain issue timeline.") }
    }

    fun lockIssue(
        username: String,
        repoName: String,
        issueNum: Int,
        reason: String
    ): Completable {
        return getIssueService().lockIssue(username, repoName, issueNum, reason)
    }

    fun unlockIssue(
        username: String,
        repoName: String,
        issueNum: Int
    ): Completable {
        return getIssueService().unlockIssue(username, repoName, issueNum)
    }

    fun createIssue(
        username: String,
        repoName: String,
        request: ApiIssueEditRequest
    ): Observable<IssueResult<Issue>> {
        return getIssueService()
            .createIssue(username, repoName, request)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to create issue.") }
    }

    fun editIssue(
        username: String,
        repoName: String,
        issueNum: Int,
        request: ApiIssueEditRequest
    ): Observable<IssueResult<Issue>> {
        return getIssueService()
            .editIssue(username, repoName, issueNum, request)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to edit this issue.") }
    }

    fun createComment(
        username: String,
        repoName: String,
        issueNum: Int,
        body: ApiIssueCommentRequest
    ): Observable<IssueResult<IssueComment>> {
        return getIssueService()
            .createComment(username, repoName, issueNum, body)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to create comment.") }
    }

    fun editComment(
        username: String,
        repoName: String,
        commentId: Long,
        body: ApiIssueCommentRequest
    ): Completable {
        return getIssueService().editComment(username, repoName, commentId, body)
    }

    fun deleteComment(username: String, repoName: String, commentId: Long): Completable {
        return getIssueService().deleteComment(username, repoName, commentId)
    }

    fun getAvailableAssignees(username: String, repoName: String): Observable<IssueResult<Page<User>>> {
        return getIssueService()
            .getAvailableAssignees(username, repoName)
            .map {
                getSuccess(it.toModel<User>().apply {
                    value = it.response.map { user -> user.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain available assignees.") }
    }

    fun getAvailableLabels(username: String, repoName: String): Observable<IssueResult<Page<Label>>> {
        return getIssueService()
            .getAvailableLabels(username, repoName)
            .map {
                getSuccess(it.toModel<Label>().apply {
                    value = it.response.map { label -> label.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain available labels.") }
    }

    fun getPullRequest(
        username: String,
        repoName: String,
        pullNumber: Int
    ): Single<IssueResult<PullRequest>> {
        return getIssueService()
            .getPullRequest(username, repoName, pullNumber)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to obtain pull request.") }
    }

    fun getPullRequestCommits(
        username: String,
        repoName: String,
        pullNumber: Int,
        page: Int
    ): Single<IssueResult<Page<Commit>>> {
        return getIssueService()
            .getPullRequestCommits(username, repoName, pullNumber, page)
            .map {
                getSuccess(it.toModel<Commit>().apply {
                    value = it.response.map { commit -> commit.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain pull request files.") }
    }

    fun getPullRequestFiles(
        username: String,
        repoName: String,
        pullNumber: Int,
        page: Int
    ): Single<IssueResult<Page<PullRequestFile>>> {
        return getIssueService()
            .getPullRequestFiles(username, repoName, pullNumber, page)
            .map {
                getSuccess(it.toModel<PullRequestFile>().apply {
                    value = it.response.map { file -> file.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain pull request files.") }
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
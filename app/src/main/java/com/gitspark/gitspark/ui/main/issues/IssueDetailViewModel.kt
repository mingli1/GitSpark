package com.gitspark.gitspark.ui.main.issues

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.IssueRepository
import com.gitspark.gitspark.repository.IssueResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import org.threeten.bp.Instant
import javax.inject.Inject

class IssueDetailViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    private val timeHelper: TimeHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<IssueDetailViewState>()
    private var started = false

    private var username = ""
    private var repoName = ""
    private var issueNum = 0

    private var page = 1
    private var commentsFinished = false
    private var eventsFinished = false

    fun onStart(simpleIssue: Issue) {
        if (!started) {
            val split = simpleIssue.getRepoFullNameFromUrl().split("/")
            username = split[0]
            repoName = split[1]
            issueNum = simpleIssue.number

            updateViewState(reset = true)
            started = false
        }
    }

    fun onDestroy() {
        started = false
    }

    fun onMenuCreated() {
        viewState.value = viewState.value?.copy()
    }

    fun onScrolledToEnd() = updateViewState()

    private fun updateViewState(reset: Boolean = false) {
        viewState.value = viewState.value?.copy(
            loading = true,
            updateAdapter = false,
            commentsFinishedLoading = commentsFinished,
            eventsFinishedLoading = eventsFinished
        ) ?: IssueDetailViewState(
            loading = true,
            updateAdapter = false,
            commentsFinishedLoading = commentsFinished,
            eventsFinishedLoading = eventsFinished
        )

        if (!commentsFinished || !eventsFinished) page++

        if (reset) {
            page = 1
            commentsFinished = false
            eventsFinished = false

            requestIssue()
        }
        requestEvents()
    }

    private fun requestIssue() {
        subscribe(issueRepository.getIssue(username, repoName, issueNum)) {
            when (it) {
                is IssueResult.Success -> {
                    val issue = it.value

                    val date = Instant.parse(issue.createdAt)
                    val formatted = timeHelper.getRelativeTimeFormat(date)

                    viewState.value = viewState.value?.copy(
                        issueTitle = issue.title,
                        isOpen = issue.state == "open",
                        issueDesc = "${issue.user.login} opened this issue $formatted",
                        numComments = issue.numComments,
                        labels = issue.labels,
                        assignees = issue.assignees,
                        locked = issue.locked,
                        authorAvatarUrl = issue.user.avatarUrl,
                        authorUsername = issue.user.login,
                        authorComment = issue.body,
                        authorCommentDate = formatted
                    )
                }
                is IssueResult.Failure -> alert(it.error)
            }
        }
    }

    private fun requestEvents() {
        if (!commentsFinished) {
            subscribe(issueRepository.getIssueComments(username, repoName, issueNum, page)) {
                when (it) {
                    is IssueResult.Success -> {
                        val updatedList = viewState.value?.events ?: arrayListOf()
                        updatedList.addAll(it.value.value)

                        commentsFinished = page.isLastPage(it.value.last)

                        viewState.value = viewState.value?.copy(
                            events = updatedList,
                            isLastPage = commentsFinished && eventsFinished,
                            commentsFinishedLoading = true,
                            updateAdapter = true,
                            loading = false
                        )
                    }
                    is IssueResult.Failure -> {
                        viewState.value = viewState.value?.copy(
                            commentsFinishedLoading = false,
                            updateAdapter = true,
                            loading = false
                        )
                        alert(it.error)
                    }
                }
            }
        }
        if (!eventsFinished) {
            subscribe(issueRepository.getIssueEvents(username, repoName, issueNum, page)) {
                when (it) {
                    is IssueResult.Success -> {
                        val updatedList = viewState.value?.events ?: arrayListOf()
                        updatedList.addAll(it.value.value)

                        eventsFinished = page.isLastPage(it.value.last)

                        viewState.value = viewState.value?.copy(
                            events = updatedList,
                            isLastPage = commentsFinished && eventsFinished,
                            eventsFinishedLoading = true,
                            updateAdapter = true,
                            loading = false
                        )
                    }
                    is IssueResult.Failure -> {
                        viewState.value = viewState.value?.copy(
                            eventsFinishedLoading = false,
                            updateAdapter = true,
                            loading = false
                        )
                        alert(it.error)
                    }
                }
            }
        }
    }
}
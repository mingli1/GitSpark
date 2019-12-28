package com.gitspark.gitspark.ui.main.issues

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.ApiIssueCommentRequest
import com.gitspark.gitspark.api.model.ApiIssueEditRequest
import com.gitspark.gitspark.helper.ClipboardHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.IssueComment
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.IssueRepository
import com.gitspark.gitspark.repository.IssueResult
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import org.threeten.bp.Instant
import javax.inject.Inject
import kotlin.math.max

class IssueDetailViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    private val repoRepository: RepoRepository,
    private val timeHelper: TimeHelper,
    private val prefsHelper: PreferencesHelper,
    private val clipboardHelper: ClipboardHelper
) : BaseViewModel(), CommentMenuCallback {

    val viewState = MutableLiveData<IssueDetailViewState>()
    val toggleCommentEdit = SingleLiveEvent<Boolean>()
    val deleteCommentRequest = SingleLiveAction()
    val quoteReplyAction = SingleLiveEvent<String>()
    val clearCommentEdit = SingleLiveAction()
    val updateCommentRequest = SingleLiveEvent<IssueComment>()
    val navigateToRepoDetail = SingleLiveEvent<String>()
    val navigateToIssueEdit = SingleLiveEvent<Pair<String, Issue>>()
    private var started = false

    private var username = ""
    private var repoName = ""
    private var issueNum = 0

    private var page = 1
    private var commentsFinished = false
    private var eventsFinished = false
    private var last = -1
    private var issue = Issue()

    private var deletedCommentId = 0L

    fun onStart(simpleIssue: Issue) {
        if (!started) {
            val split = simpleIssue.getRepoFullNameFromUrl().split("/")
            username = split[0]
            repoName = split[1]
            issueNum = simpleIssue.number

            requestPermissionLevel()
            updateViewState(reset = true)
            started = true
        }
    }

    fun onDestroy() {
        started = false
    }

    fun onMenuCreated() {
        viewState.value = viewState.value?.copy()
    }

    fun onScrolledToEnd() = updateViewState()

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onDeleteCommentConfirmed() {
        subscribe(issueRepository.deleteComment(username, repoName, deletedCommentId),
            {
                val events = viewState.value?.events ?: arrayListOf()
                events.removeAll { it is IssueComment && it.id == deletedCommentId }
                val numComments = viewState.value?.numComments ?: 0

                viewState.value = viewState.value?.copy(
                    events = events,
                    numComments = numComments - 1,
                    updateAdapter = true
                )
            },
            { alert("Failed to delete comment.") }
        )
    }

    fun onSendComment(body: String) {
        viewState.value = viewState.value?.copy(loading = true, updateAdapter = false)
        subscribe(issueRepository.createComment(username, repoName, issueNum, ApiIssueCommentRequest(body))) {
            when (it) {
                is IssueResult.Success -> {
                    val numComments = viewState.value?.numComments ?: 0
                    if (last == -1 || page == last) {
                        val events = viewState.value?.events ?: arrayListOf()
                        events.add(it.value)
                        viewState.value = viewState.value?.copy(
                            events = events,
                            loading = false,
                            updateAdapter = true,
                            numComments = numComments + 1
                        )
                    } else {
                        viewState.value = viewState.value?.copy(loading = false, numComments = numComments + 1)
                    }
                    clearCommentEdit.call()
                }
                is IssueResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }

    fun onAuthorCommentQuoteReply() = onQuoteReplySelected(issue.body)

    fun onIssueLockRequest(reason: String) {
        viewState.value = viewState.value?.copy(loading = true, updateAdapter = false)
        subscribe(issueRepository.lockIssue(username, repoName, issueNum, reason),
            {
                viewState.value = viewState.value?.copy(
                    loading = false,
                    locked = true
                )
            },
            {
                alert("Failed to lock issue.")
                viewState.value = viewState.value?.copy(loading = false)
            }
        )
    }

    fun onIssueUnlockRequest() {
        viewState.value = viewState.value?.copy(loading = true, updateAdapter = false)
        subscribe(issueRepository.unlockIssue(username, repoName, issueNum),
            {
                viewState.value = viewState.value?.copy(
                    loading = false,
                    locked = false
                )
            },
            {
                alert("Failed to unlock issue.")
                viewState.value = viewState.value?.copy(loading = false)
            }
        )
    }

    fun onIssueStateChange(state: String) {
        viewState.value = viewState.value?.copy(loading = true, updateAdapter = false)
        subscribe(issueRepository.editIssue(
            username,
            repoName,
            issueNum,
            ApiIssueEditRequest.changeState(state, issue)
        )) {
            when (it) {
                is IssueResult.Success -> {
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        isOpen = state == "open"
                    )
                }
                is IssueResult.Failure -> {
                    alert("Failed to ${if (state == "open") "open" else "close"} this issue.")
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }

    fun onRepoSelected() {
        navigateToRepoDetail.value = "$username/$repoName"
    }

    fun onEditSelected() {
        navigateToIssueEdit.value = Pair("Editing $username/$repoName #$issueNum", issue)
    }

    override fun onDeleteSelected(id: Long) {
        deletedCommentId = id
        deleteCommentRequest.call()
    }

    override fun onCopyLinkSelected(url: String) {
        alert("Copied comment link to the clipboard.")
        clipboardHelper.copy(url)
    }

    override fun onEditCommentFocused() {
        toggleCommentEdit.value = false
    }

    override fun onEditCommentUnfocused() {
        toggleCommentEdit.value = true
    }

    override fun onCommentUpdated(id: Long, body: String) {
        viewState.value = viewState.value?.copy(loading = true, updateAdapter = false)
        subscribe(issueRepository.editComment(username, repoName, id, ApiIssueCommentRequest(body = body)),
            {
                val events = viewState.value?.events ?: arrayListOf()
                val updatedComment = events.find { it is IssueComment && it.id == id }
                updatedComment?.let {
                    (it as IssueComment).body = body
                    updateCommentRequest.value = it
                }
                viewState.value = viewState.value?.copy(
                    events = events,
                    loading = false
                )
            },
            {
                alert("Failed to update comment")
                viewState.value = viewState.value?.copy(loading = false)
            }
        )
    }

    override fun onQuoteReplySelected(body: String) {
        var quote = ""
        body.lines().forEach { quote += ">$it\n" }
        quoteReplyAction.value = quote
    }

    private fun updateViewState(reset: Boolean = false, refresh: Boolean = false) {
        viewState.value = viewState.value?.copy(
            events = if (reset) arrayListOf() else viewState.value?.events ?: arrayListOf(),
            loading = reset,
            refreshing = refresh,
            updateAdapter = false,
            commentsFinishedLoading = commentsFinished,
            eventsFinishedLoading = eventsFinished
        ) ?: IssueDetailViewState(
            events = if (reset) arrayListOf() else viewState.value?.events ?: arrayListOf(),
            loading = reset,
            refreshing = refresh,
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
                    this.issue = issue

                    val date = Instant.parse(issue.createdAt)
                    val formatted = timeHelper.getRelativeTimeFormat(date)

                    viewState.value = viewState.value?.copy(
                        issueTitle = issue.title,
                        authUserIsAuthor = prefsHelper.getAuthUsername() == issue.user.login,
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

                        last = max(last, it.value.last)
                        commentsFinished = page.isLastPage(it.value.last)

                        viewState.value = viewState.value?.copy(
                            events = updatedList,
                            isLastPage = commentsFinished && eventsFinished,
                            commentsFinishedLoading = true,
                            updateAdapter = true,
                            loading = false,
                            refreshing = false
                        )
                    }
                    is IssueResult.Failure -> {
                        viewState.value = viewState.value?.copy(
                            commentsFinishedLoading = false,
                            updateAdapter = true,
                            loading = false,
                            refreshing = false
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

                        last = max(last, it.value.last)
                        eventsFinished = page.isLastPage(it.value.last)

                        viewState.value = viewState.value?.copy(
                            events = updatedList,
                            isLastPage = commentsFinished && eventsFinished,
                            eventsFinishedLoading = true,
                            updateAdapter = true,
                            loading = false,
                            refreshing = false
                        )
                    }
                    is IssueResult.Failure -> {
                        viewState.value = viewState.value?.copy(
                            eventsFinishedLoading = false,
                            updateAdapter = true,
                            loading = false,
                            refreshing = false
                        )
                        alert(it.error)
                    }
                }
            }
        }
    }

    private fun requestPermissionLevel() {
        subscribe(repoRepository.getPermissionLevel(username, repoName, prefsHelper.getAuthUsername())) {
            if (it is RepoResult.Success) {
                viewState.value = viewState.value?.copy(permissionLevel = it.value.permission)
                    ?: IssueDetailViewState(permissionLevel = it.value.permission)
            }
        }
    }
}
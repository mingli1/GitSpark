package com.gitspark.gitspark.ui.main.issues

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.ApiIssueCommentRequest
import com.gitspark.gitspark.api.model.ApiIssueEditRequest
import com.gitspark.gitspark.helper.ClipboardHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.IssueComment
import com.gitspark.gitspark.model.PullRequest
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
    val recyclerViewState = MutableLiveData<IssueRecyclerViewState>()
    val toggleCommentEdit = SingleLiveEvent<Boolean>()
    val deleteCommentRequest = SingleLiveAction()
    val quoteReplyAction = SingleLiveEvent<String>()
    val clearCommentEdit = SingleLiveAction()
    val updateCommentRequest = SingleLiveEvent<IssueComment>()
    val navigateToRepoDetail = SingleLiveEvent<String>()
    val navigateToIssueEdit = SingleLiveEvent<Triple<String, Issue, String>>()
    private var started = false

    private var username = ""
    private var repoName = ""
    private var issueNum = 0
    private var isPullRequest = false

    private var page = 1
    private var commentsFinished = false
    private var eventsFinished = false
    private var last = -1
    private var issue = Issue()
    private var pullRequest = PullRequest()

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

    fun onStart(pullRequest: PullRequest, args: String) {
        if (!started) {
            val split = args.split("/")
            username = split[0]
            repoName = split[1]
            issueNum = split[2].toInt()
            page = 0
            isPullRequest = true
            this.pullRequest = pullRequest

            updateViewStateWithPullRequest(pullRequest)
            requestPermissionLevel()
            updateViewState()
            started = true
        }
    }

    fun onDestroy() {
        started = false
    }

    fun onMenuCreated() {
        viewState.value = viewState.value?.copy() ?: IssueDetailViewState()
    }

    fun onScrolledToEnd() = updateViewState()

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onDeleteCommentConfirmed() {
        subscribe(issueRepository.deleteComment(username, repoName, deletedCommentId),
            {
                val events = recyclerViewState.value?.events ?: mutableListOf()
                events.removeAll { it is IssueComment && it.id == deletedCommentId }
                val numComments = viewState.value?.numComments ?: 0

                viewState.value = viewState.value?.copy(numComments = numComments - 1)
                recyclerViewState.value = recyclerViewState.value?.copy(events = events)
            },
            { alert("Failed to delete comment.") }
        )
    }

    fun onSendComment(body: String) {
        viewState.value = viewState.value?.copy(loading = true)
        subscribe(issueRepository.createComment(username, repoName, issueNum, ApiIssueCommentRequest(body))) {
            when (it) {
                is IssueResult.Success -> {
                    val numComments = viewState.value?.numComments ?: 0
                    if (last == -1 || page == last) {
                        val events = recyclerViewState.value?.events ?: mutableListOf()
                        events.add(it.value)
                        viewState.value = viewState.value?.copy(
                            loading = false,
                            numComments = numComments + 1
                        )
                        recyclerViewState.value = recyclerViewState.value?.copy(events = events)
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

    fun onAuthorCommentQuoteReply() = onQuoteReplySelected(if (isPullRequest) pullRequest.body else issue.body)

    fun onIssueLockRequest(reason: String) {
        viewState.value = viewState.value?.copy(loading = true)
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
        viewState.value = viewState.value?.copy(loading = true)
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
        viewState.value = viewState.value?.copy(loading = true)
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
        navigateToIssueEdit.value = Triple("Editing $username/$repoName #$issueNum", issue, "$username/$repoName")
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
        viewState.value = viewState.value?.copy(loading = true)
        subscribe(issueRepository.editComment(username, repoName, id, ApiIssueCommentRequest(body = body)),
            {
                val events = recyclerViewState.value?.events ?: mutableListOf()
                val updatedComment = events.find { it is IssueComment && it.id == id }
                updatedComment?.let {
                    (it as IssueComment).body = body
                    updateCommentRequest.value = it
                }
                viewState.value = viewState.value?.copy(loading = false)
                recyclerViewState.value = recyclerViewState.value?.copy(events = events)
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
            loading = reset,
            refreshing = refresh
        ) ?: IssueDetailViewState(
            loading = reset,
            refreshing = refresh
        )

        recyclerViewState.value = recyclerViewState.value?.copy(
            events = if (reset) mutableListOf() else recyclerViewState.value?.events ?: mutableListOf(),
            commentsFinishedLoading = commentsFinished,
            eventsFinishedLoading = eventsFinished
        ) ?: IssueRecyclerViewState(
            events = if (reset) mutableListOf() else recyclerViewState.value?.events ?: mutableListOf(),
            commentsFinishedLoading = commentsFinished,
            eventsFinishedLoading = eventsFinished
        )

        if (!commentsFinished || !eventsFinished) page++

        if (reset) {
            page = 1
            commentsFinished = false
            eventsFinished = false

            if (isPullRequest) requestPullRequest() else requestIssue()
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
                    val formatted = timeHelper.getRelativeAndExactTimeFormat(date, short = true)

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

    private fun requestPullRequest() {
        subscribe(issueRepository.getPullRequest(username, repoName, issueNum)) {
            when (it) {
                is IssueResult.Success -> {
                    pullRequest = it.value
                    updateViewStateWithPullRequest(pullRequest)
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
                        val updatedList = recyclerViewState.value?.events ?: mutableListOf()
                        updatedList.addAll(it.value.value)

                        last = max(last, it.value.last)
                        commentsFinished = page.isLastPage(it.value.last)

                        viewState.value = viewState.value?.copy(
                            loading = false,
                            refreshing = false
                        )
                        recyclerViewState.value = recyclerViewState.value?.copy(
                            isLastPage = commentsFinished && eventsFinished,
                            events = updatedList,
                            commentsFinishedLoading = true
                        )
                    }
                    is IssueResult.Failure -> {
                        viewState.value = viewState.value?.copy(
                            loading = false,
                            refreshing = false
                        )
                        recyclerViewState.value = recyclerViewState.value?.copy(commentsFinishedLoading = false)
                        alert(it.error)
                    }
                }
            }
        }
        if (!eventsFinished) {
            subscribe(issueRepository.getIssueEvents(username, repoName, issueNum, page)) {
                when (it) {
                    is IssueResult.Success -> {
                        val updatedList = recyclerViewState.value?.events ?: mutableListOf()
                        updatedList.addAll(it.value.value)

                        last = max(last, it.value.last)
                        eventsFinished = page.isLastPage(it.value.last)

                        viewState.value = viewState.value?.copy(
                            loading = false,
                            refreshing = false
                        )
                        recyclerViewState.value = recyclerViewState.value?.copy(
                            isLastPage = commentsFinished && eventsFinished,
                            events = updatedList,
                            eventsFinishedLoading = true
                        )
                    }
                    is IssueResult.Failure -> {
                        viewState.value = viewState.value?.copy(
                            loading = false,
                            refreshing = false
                        )
                        recyclerViewState.value = recyclerViewState.value?.copy(eventsFinishedLoading = false)
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

    private fun updateViewStateWithPullRequest(pr: PullRequest) {
        val date = Instant.parse(pr.createdAt)
        val formatted = timeHelper.getRelativeAndExactTimeFormat(date, short = true)

        viewState.value = viewState.value?.copy(
            issueTitle = pr.title,
            isPullRequest = true,
            authUserIsAuthor = prefsHelper.getAuthUsername() == pr.user.login,
            isOpen = pr.state == "open",
            issueDesc = "${pr.user.login} opened this pull request $formatted",
            numComments = pr.numComments,
            labels = pr.labels,
            assignees = pr.assignees,
            locked = pr.locked,
            authorAvatarUrl = pr.user.avatarUrl,
            authorUsername = pr.user.login,
            authorComment = pr.body,
            authorCommentDate = formatted,
            isMerged = pr.merged,
            numAdditions = pr.numAdditions,
            numDeletions = pr.numDeletions,
            baseBranch = pr.base.ref,
            headBranch = pr.head.ref
        ) ?: IssueDetailViewState(
            issueTitle = pr.title,
            isPullRequest = true,
            authUserIsAuthor = prefsHelper.getAuthUsername() == pr.user.login,
            isOpen = pr.state == "open",
            issueDesc = "${pr.user.login} opened this pull request $formatted",
            numComments = pr.numComments,
            labels = pr.labels,
            assignees = pr.assignees,
            locked = pr.locked,
            authorAvatarUrl = pr.user.avatarUrl,
            authorUsername = pr.user.login,
            authorComment = pr.body,
            authorCommentDate = formatted,
            isMerged = pr.merged,
            numAdditions = pr.numAdditions,
            numDeletions = pr.numDeletions,
            baseBranch = pr.base.ref,
            headBranch = pr.head.ref
        )
    }
}
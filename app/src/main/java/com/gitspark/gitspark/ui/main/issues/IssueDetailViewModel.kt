package com.gitspark.gitspark.ui.main.issues

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.ApiIssueCommentRequest
import com.gitspark.gitspark.api.model.ApiIssueEditRequest
import com.gitspark.gitspark.helper.ClipboardHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.*
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.main.issues.pullrequest.CheckState
import com.gitspark.gitspark.ui.main.issues.pullrequest.ChecksViewState
import org.threeten.bp.Instant
import javax.inject.Inject

class IssueDetailViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    private val repoRepository: RepoRepository,
    private val checksRepository: ChecksRepository,
    private val timeHelper: TimeHelper,
    private val prefsHelper: PreferencesHelper,
    private val clipboardHelper: ClipboardHelper
) : BaseViewModel(), CommentMenuCallback {

    val viewState = MutableLiveData<IssueDetailViewState>()
    val recyclerViewState = MutableLiveData<PaginatedViewState<IssueEvent>>()
    val checksState = MutableLiveData<ChecksViewState>()
    val toggleCommentEdit = SingleLiveEvent<Boolean>()
    val deleteCommentRequest = SingleLiveAction()
    val quoteReplyAction = SingleLiveEvent<String>()
    val clearCommentEdit = SingleLiveAction()
    val updateCommentRequest = SingleLiveEvent<IssueEvent>()
    val navigateToRepoDetail = SingleLiveEvent<Pair<String, Boolean>>()
    val navigateToIssueEdit = SingleLiveEvent<Triple<String, Any, String>>()
    val pullRequestRefresh = SingleLiveEvent<PullRequest>()
    private var started = false

    private var username = ""
    private var repoName = ""
    private var issueNum = 0
    private var isPullRequest = false

    private var page = 1
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
            page = 1
            isPullRequest = true
            this.pullRequest = pullRequest

            updateViewStateWithPullRequest(pullRequest)
            requestPermissionLevel()
            requestCheckStatus(pullRequest)
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
                val events = recyclerViewState.value?.items ?: mutableListOf()
                events.removeAll { it.id == deletedCommentId }
                val numComments = viewState.value?.numComments ?: 0

                viewState.value = viewState.value?.copy(numComments = numComments - 1)
                recyclerViewState.value = recyclerViewState.value?.copy(items = events)
                    ?: PaginatedViewState(items = events)
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
                        val events = recyclerViewState.value?.items ?: mutableListOf()
                        events.add(it.value.toIssueEvent())
                        viewState.value = viewState.value?.copy(
                            loading = false,
                            numComments = numComments + 1
                        )
                        recyclerViewState.value = recyclerViewState.value?.copy(items = events)
                            ?: PaginatedViewState(items = events)
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
            if (isPullRequest)
                ApiIssueEditRequest.changeState(state, pullRequest)
            else
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
        navigateToRepoDetail.value = Pair("$username/$repoName", isPullRequest)
    }

    fun onEditSelected() {
        navigateToIssueEdit.value = Triple(
            "Editing $username/$repoName #$issueNum",
            if (isPullRequest) pullRequest else issue,
            "$username/$repoName"
        )
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
                val events = recyclerViewState.value?.items ?: mutableListOf()
                val updatedComment = events.find { it.id == id }
                updatedComment?.let {
                    it.body = body
                    updateCommentRequest.value = it
                }
                viewState.value = viewState.value?.copy(loading = false)
                recyclerViewState.value = recyclerViewState.value?.copy(items = events)
                    ?: PaginatedViewState(items = events)
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

        if (reset) {
            page = 1

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
                        issueUsername = issue.user.login,
                        issueDesc = "opened this issue $formatted",
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
                    pullRequestRefresh.value = it.value
                    updateViewStateWithPullRequest(pullRequest)
                }
                is IssueResult.Failure -> alert(it.error)
            }
        }
    }

    private fun requestEvents() {
        subscribe(issueRepository.getIssueTimeline(username, repoName, issueNum, page)) {
            when (it) {
                is IssueResult.Success -> {
                    val updatedList = if (page.isFirstPage()) mutableListOf() else recyclerViewState.value?.items ?: mutableListOf()
                    updatedList.addAll(it.value.value)

                    last = it.value.last

                    recyclerViewState.value = recyclerViewState.value?.copy(
                        isLastPage = page.isLastPage(it.value.last),
                        items = updatedList
                    ) ?: PaginatedViewState(
                        isLastPage = page.isLastPage(it.value.last),
                        items = updatedList
                    )

                    if (page < last) page++
                }
                is IssueResult.Failure -> alert(it.error)
            }
            viewState.value = viewState.value?.copy(
                loading = false,
                refreshing = false
            )
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
            issueUsername = pr.user.login,
            issueDesc = "opened this pull request $formatted",
            numComments = pr.numComments,
            labels = pr.labels,
            assignees = pr.assignees,
            reviewers = pr.requestedReviewers,
            locked = pr.locked,
            authorAvatarUrl = pr.user.avatarUrl,
            authorUsername = pr.user.login,
            authorComment = pr.body,
            authorCommentDate = formatted,
            isMerged = pr.merged,
            isDraft = pr.draft,
            mergable = pr.mergeable,
            mergeableState = pr.mergeableState,
            numAdditions = pr.numAdditions,
            numDeletions = pr.numDeletions,
            baseBranch = pr.base.ref,
            headBranch = pr.head.ref
        ) ?: IssueDetailViewState(
            issueTitle = pr.title,
            isPullRequest = true,
            authUserIsAuthor = prefsHelper.getAuthUsername() == pr.user.login,
            isOpen = pr.state == "open",
            issueUsername = pr.user.login,
            issueDesc = "opened this pull request $formatted",
            numComments = pr.numComments,
            labels = pr.labels,
            assignees = pr.assignees,
            reviewers = pr.requestedReviewers,
            locked = pr.locked,
            authorAvatarUrl = pr.user.avatarUrl,
            authorUsername = pr.user.login,
            authorComment = pr.body,
            authorCommentDate = formatted,
            isMerged = pr.merged,
            isDraft = pr.draft,
            mergable = pr.mergeable,
            mergeableState = pr.mergeableState,
            numAdditions = pr.numAdditions,
            numDeletions = pr.numDeletions,
            baseBranch = pr.base.ref,
            headBranch = pr.head.ref
        )
    }

    private fun requestCheckStatus(pr: PullRequest) {
        if (pr.merged || pr.draft) return
        subscribe(checksRepository.getCheckSuites(username, repoName, pr.head.ref)) {
            when (it) {
                is ChecksResult.Success -> {
                    val checks = it.value.suites.filter { check -> check.app.slug != "dependabot" && check.app.slug != "github-pages" }
                    val state = when {
                        checks.any { check -> check.isPending() } -> CheckState.Pending
                        checks.any { check -> check.isFailure() } -> CheckState.Failed
                        else -> CheckState.Success
                    }
                    val numPassed = checks.count { check -> check.isSuccessful() }
                    val numPending = checks.count { check -> check.isPending() }
                    val numFailed = checks.count { check -> check.isFailure() }

                    checksState.value = checksState.value?.copy(
                        state = state,
                        checks = checks,
                        showChecks = true,
                        numPassed = numPassed,
                        numPending = numPending,
                        numFailed = numFailed
                    ) ?: ChecksViewState(
                        state = state,
                        checks = checks,
                        showChecks = true,
                        numPassed = numPassed,
                        numPending = numPending,
                        numFailed = numFailed
                    )
                }
                is ChecksResult.Failure -> {
                    checksState.value = checksState.value?.copy(showChecks = false) ?: ChecksViewState(showChecks = false)
                    alert(it.error)
                }
            }
        }
    }
}
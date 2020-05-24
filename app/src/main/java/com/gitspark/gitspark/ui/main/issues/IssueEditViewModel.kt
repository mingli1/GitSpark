package com.gitspark.gitspark.ui.main.issues

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.ApiIssueEditRequest
import com.gitspark.gitspark.api.model.ApiReviewerRequest
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.model.PullRequest
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.repository.IssueRepository
import com.gitspark.gitspark.repository.IssueResult
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import io.reactivex.rxkotlin.Singles
import javax.inject.Inject

class IssueEditViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    private val repoRepository: RepoRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<IssueEditViewState>()
    val loadListData = SingleLiveEvent<Triple<List<User>, List<Label>, List<User>>>()
    val showAssigneesDialog = SingleLiveEvent<List<User>>()
    val showLabelsDialog = SingleLiveEvent<List<Label>>()
    val showReviewersDialog = SingleLiveEvent<List<User>>()
    val updateIssueAction = SingleLiveEvent<Issue>()
    val createIssueAction = SingleLiveEvent<Issue>()

    var assignees: List<User>? = null
    var labels: List<Label>? = null
    var reviewers: List<User>? = null

    private lateinit var issue: Issue
    private lateinit var pullRequest: PullRequest
    private var isPullRequest = false
    private var username = ""
    private var repoName = ""
    private var started = false
    private var creating = false

    fun setInitialState(issue: Issue, repoFullName: String) {
        val split = repoFullName.split("/")
        username = split[0]
        repoName = split[1]
        if (!started) {
            this.issue = issue
            val assignees = if (assignees != null) assignees!! else issue.assignees
            val labels = if (labels != null) labels!! else issue.labels
            viewState.value = viewState.value?.copy(
                title = issue.title,
                body = issue.body,
                assignees = assignees.map { it.login },
                labels = labels.map { it.name }
            ) ?: IssueEditViewState(
                title = issue.title,
                body = issue.body,
                assignees = assignees.map { it.login },
                labels = labels.map { it.name }
            )
            loadListData.value = Triple(assignees, labels, emptyList())
            started = true
        }
    }

    fun setInitialState(pr: PullRequest, repoFullName: String) {
        val split = repoFullName.split("/")
        username = split[0]
        repoName = split[1]
        if (!started) {
            this.pullRequest = pr
            isPullRequest = true
            val assignees = if (assignees != null) assignees!! else pr.assignees
            val labels = if (labels != null) labels!! else pr.labels
            val reviewers = if (reviewers != null) reviewers!! else pr.requestedReviewers

            viewState.value = viewState.value?.copy(
                title = pr.title,
                body = pr.body,
                reviewers = reviewers.map { it.login },
                assignees = assignees.map { it.login },
                labels = labels.map { it.name }
            ) ?: IssueEditViewState(
                title = pr.title,
                body = pr.body,
                reviewers = reviewers.map { it.login },
                assignees = assignees.map { it.login },
                labels = labels.map { it.name }
            )

            loadListData.value = Triple(assignees, labels, reviewers)
            started = true
        }
    }

    fun setCreatingState(repoFullName: String) {
        val split = repoFullName.split("/")
        username = split[0]
        repoName = split[1]
        if (!started) {
            creating = true
            viewState.value = viewState.value?.copy(creating = true) ?: IssueEditViewState(creating = true)
            started = true
        }
    }

    fun onTitleChanged(title: String) {
        viewState.value = viewState.value?.copy(title = title)
            ?: IssueEditViewState(title = title)
    }

    fun onBodyChanged(body: String) {
        viewState.value = viewState.value?.copy(body = body)
            ?: IssueEditViewState(body = body)
    }

    fun onDestroy() {
        started = false
    }

    fun onAssigneesButtonClicked() {
        viewState.value = viewState.value?.copy(loading = true)
        subscribe(issueRepository.getAvailableAssignees(username, repoName)) {
            when (it) {
                is IssueResult.Success -> {
                    showAssigneesDialog.value = it.value.value
                }
                is IssueResult.Failure -> alert(it.error)
            }
            viewState.value = viewState.value?.copy(loading = false)
        }
    }

    fun onAssigneesSet(assignees: List<User>) {
        this.assignees = assignees
        viewState.value = viewState.value?.copy(assignees = assignees.map { it.login })
        loadListData.value = loadListData.value?.copy(
            first = assignees
        ) ?: Triple(first = assignees, second = emptyList(), third = emptyList())
    }

    fun onLabelsButtonClicked() {
        viewState.value = viewState.value?.copy(loading = true)
        subscribe(issueRepository.getAvailableLabels(username, repoName)) {
            when (it) {
                is IssueResult.Success -> {
                    showLabelsDialog.value = it.value.value
                }
                is IssueResult.Failure -> alert(it.error)
            }
            viewState.value = viewState.value?.copy(loading = false)
        }
    }

    fun onLabelsSet(labels: List<Label>) {
        this.labels = labels
        viewState.value = viewState.value?.copy(labels = labels.map { it.name })
        loadListData.value = loadListData.value?.copy(
            second = labels
        ) ?: Triple(first = emptyList(), second = labels, third = emptyList())
    }

    fun onReviewersButtonClicked() {
        viewState.value = viewState.value?.copy(loading = true)
        subscribe(repoRepository.getCollaborators(username, repoName)) {
            when (it) {
                is RepoResult.Success -> {
                    showReviewersDialog.value = it.value.value
                }
                is RepoResult.Failure -> alert(it.error)
            }
            viewState.value = viewState.value?.copy(loading = false)
        }
    }

    fun onReviewersSet(reviewers: List<User>) {
        this.reviewers = reviewers
        viewState.value = viewState.value?.copy(reviewers = reviewers.map { it.login })
        loadListData.value = loadListData.value?.copy(
            third = reviewers
        ) ?: Triple(first = emptyList(), second = emptyList(), third = reviewers)
    }

    fun onEditIssueClicked() {
        viewState.value = viewState.value?.copy(loading = true)

        if (isPullRequest) {
            postEditPullRequest()
        } else {
            if (creating) {
                subscribe(issueRepository.createIssue(username, repoName,
                    ApiIssueEditRequest(
                        title = viewState.value?.title ?: "",
                        body = viewState.value?.body ?: "",
                        state = "open",
                        labels = viewState.value?.labels ?: emptyList(),
                        assignees = viewState.value?.assignees ?: emptyList()
                    ))) {
                    when (it) {
                        is IssueResult.Success -> {
                            createIssueAction.value = it.value
                        }
                        is IssueResult.Failure -> alert(it.error)
                    }
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
            else {
                postEditIssue()
            }
        }
    }

    private fun postEditIssue() {
        subscribe(issueRepository.editIssue(username, repoName, issue.number,
            ApiIssueEditRequest(
                title = viewState.value?.title ?: issue.title,
                body = viewState.value?.body ?: issue.body,
                state = issue.state,
                labels = viewState.value?.labels ?: issue.labels.map { it.name },
                assignees = viewState.value?.assignees ?: issue.assignees.map { it.login }
            ))) {
            when (it) {
                is IssueResult.Success -> {
                    updateIssueAction.value = it.value
                }
                is IssueResult.Failure -> alert(it.error)
            }
            viewState.value = viewState.value?.copy(loading = false)
        }
    }

    private fun postEditPullRequest() {
        subscribe(getPullRequestEditRequest()) {
            val editResult = it.first
            val reviewersResult = it.second

            when {
                editResult is IssueResult.Failure && reviewersResult is IssueResult.Failure -> {
                    alert("${editResult.error} and ${reviewersResult.error}")
                }
                editResult is IssueResult.Failure -> alert(editResult.error)
                reviewersResult is IssueResult.Failure -> alert(reviewersResult.error)
                else -> {
                    // success
                }
            }

            viewState.value = viewState.value?.copy(loading = false)
        }
    }

    private fun getPullRequestEditRequest() = Singles.zip(
        issueRepository.editIssue(username, repoName, pullRequest.number,
            ApiIssueEditRequest(
                title = viewState.value?.title ?: pullRequest.title,
                body = viewState.value?.body ?: pullRequest.body,
                state = pullRequest.state,
                labels = viewState.value?.labels ?: pullRequest.labels.map { it.name },
                assignees = viewState.value?.assignees ?: pullRequest.assignees.map { it.login }
            )
        ),
        issueRepository.requestReviewers(username, repoName, pullRequest.number,
            ApiReviewerRequest(
                reviewers = viewState.value?.reviewers ?: pullRequest.requestedReviewers.map { it.login }
            )
        )
    )
}
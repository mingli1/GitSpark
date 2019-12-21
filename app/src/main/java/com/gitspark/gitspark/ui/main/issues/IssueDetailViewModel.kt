package com.gitspark.gitspark.ui.main.issues

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Issue
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

    private fun updateViewState(reset: Boolean = false) {
        viewState.value = viewState.value?.copy(loading = true) ?: IssueDetailViewState(loading = true)
        requestIssue()
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
                        loading = false
                    )
                }
                is IssueResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }
}
package com.gitspark.gitspark.ui.main.issues

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.repository.IssueRepository
import com.gitspark.gitspark.repository.IssueResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class IssueEditViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<IssueEditViewState>()
    val showAssigneesDialog = SingleLiveEvent<List<User>>()

    private var username = ""
    private var repoName = ""
    private var started = false

    fun setInitialState(issue: Issue, repoFullName: String) {
        val split = repoFullName.split("/")
        username = split[0]
        repoName = split[1]
        if (!started) {
            viewState.value = viewState.value?.copy(
                title = issue.title,
                body = issue.body,
                assignees = issue.assignees,
                labels = issue.labels,
                updateContainers = true
            ) ?: IssueEditViewState(
                title = issue.title,
                body = issue.body,
                assignees = issue.assignees,
                labels = issue.labels,
                updateContainers = true
            )
            started = true
        }
    }

    fun onTitleChanged(title: String) {
        viewState.value = viewState.value?.copy(title = title, updateContainers = false)
            ?: IssueEditViewState(title = title, updateContainers = false)
    }

    fun onBodyChanged(body: String) {
        viewState.value = viewState.value?.copy(body = body, updateContainers = false) ?:
                IssueEditViewState(body = body, updateContainers = false)
    }

    fun onDestroy() {
        started = false
    }

    fun onAssigneesButtonClicked() {
        viewState.value = viewState.value?.copy(loading = true, updateContainers = false)
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
}
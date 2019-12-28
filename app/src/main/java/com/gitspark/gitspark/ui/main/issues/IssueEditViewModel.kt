package com.gitspark.gitspark.ui.main.issues

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class IssueEditViewModel @Inject constructor() : BaseViewModel() {

    val viewState = MutableLiveData<IssueEditViewState>()
    private var started = false

    fun setInitialState(issue: Issue) {
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
}
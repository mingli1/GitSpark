package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.IssueRepository
import com.gitspark.gitspark.repository.IssueResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

const val ISSUE_TYPE_CREATED = "created"
const val ISSUE_TYPE_ASSIGNED = "assigned"
const val ISSUE_TYPE_MENTIONED = "mentioned"

class IssuesListViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<IssuesListViewState>()
    private var started = false
    private var page = 1
    private var filter = ""

    fun onStart(filter: String) {
        if (!started) {
            this.filter = filter
            updateViewState(reset = true)
            started = true
        }
    }

    fun onDestroy() {
        started = false
    }

    fun onScrolledToEnd() = updateViewState()

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onIssueStateSelected(open: Boolean) {
        val state = viewState.value?.showOpenIssues ?: true
        if (state == open) return
        viewState.value = viewState.value?.copy(showOpenIssues = open)
        updateViewState(reset = true)
    }

    private fun updateViewState(reset: Boolean = false, refresh: Boolean = false) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        ) ?: IssuesListViewState(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        )
        if (reset) page = 1
        if (reset || refresh) requestNumbers()
        requestData()
    }

    private fun requestData() {
        val state = viewState.value?.showOpenIssues ?: true
        subscribe(issueRepository.getIssues(filter, if (state) "open" else "closed", page)) {
            when (it) {
                is IssueResult.Success -> {
                    val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.issues ?: arrayListOf()
                    updatedList.addAll(it.value.value)

                    viewState.value = viewState.value?.copy(
                        issues = updatedList,
                        loading = false,
                        refreshing = false,
                        isLastPage = page.isLastPage(it.value.last),
                        updateAdapter = true
                    )
                    if (page < it.value.last) page++
                }
                is IssueResult.Failure -> {
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        refreshing = false,
                        updateAdapter = false
                    )
                    alert(it.error)
                }
            }
        }
    }

    private fun requestNumbers() {
        subscribe(issueRepository.getIssues(filter, "open", 1, 1)) {
            when (it) {
                is IssueResult.Success -> {
                    val total = when (it.value.last) {
                        -1 -> it.value.value.size
                        else -> it.value.last
                    }
                    viewState.value = viewState.value?.copy(numOpen = total)
                }
                is IssueResult.Failure -> alert(it.error)
            }
        }
        subscribe(issueRepository.getIssues(filter, "closed", 1, 1)) {
            when (it) {
                is IssueResult.Success -> {
                    val total = when (it.value.last) {
                        -1 -> it.value.value.size
                        else -> it.value.last
                    }
                    viewState.value = viewState.value?.copy(numClosed = total)
                }
                is IssueResult.Failure -> alert(it.error)
            }
        }
    }
}
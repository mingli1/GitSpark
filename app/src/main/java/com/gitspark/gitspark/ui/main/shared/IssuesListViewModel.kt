package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.SearchRepository
import com.gitspark.gitspark.repository.SearchResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.nav.IssueDetailNavigator
import javax.inject.Inject

const val CREATED_ISSUE_Q = "type:issue+state:%s+author:%s"
const val ASSIGNED_ISSUE_Q = "type:issue+state:%s+assignee:%s"
const val MENTIONED_ISSUE_Q = "type:issue+state:%s+mentions:%s"

const val CREATED_PR_Q = "type:pr+state:%s+author:%s"
const val ASSIGNED_PR_Q = "type:pr+state:%s+assignee:%s"
const val MENTIONED_PR_Q = "type:pr+state:%s+mentions:%s"
const val REVIEW_REQUESTED_PR_Q = "type:pr+state:%s+review-requested:%s"

const val REPO_ISSUE_Q = "type:issue+state:%s+repo:"
const val REPO_PR_Q = "type:pr+state:%s+repo:"

class IssuesListViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel(), IssueDetailNavigator {

    val viewState = MutableLiveData<IssuesListViewState>()
    val navigateToIssueDetail = SingleLiveEvent<Pair<String, Issue>>()
    val createNewIssueAction = SingleLiveAction()
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

    fun onAddIssueClicked() = createNewIssueAction.call()

    fun onNewIssueCreated(issue: Issue) {
        if (viewState.value?.showOpenIssues == false) return

        val updatedList = viewState.value?.issues ?: arrayListOf()
        val numOpen = viewState.value?.numOpen ?: 0
        updatedList.add(0, issue)
        viewState.value = viewState.value?.copy(
            issues = updatedList,
            updateAdapter = true,
            numOpen = numOpen + 1
        )
    }

    fun onIssueStateSelected(open: Boolean) {
        val state = viewState.value?.showOpenIssues ?: true
        if (state == open) return
        viewState.value = viewState.value?.copy(showOpenIssues = open)
        updateViewState(reset = true)
    }

    override fun onIssueClicked(issue: Issue) {
        navigateToIssueDetail.value = Pair("${issue.getRepoFullNameFromUrl()} #${issue.number}", issue)
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
        requestData(reset)
    }

    private fun requestData(reset: Boolean) {
        val state = viewState.value?.showOpenIssues ?: true
        val q = if (filter.contains("repo")) String.format(filter, if (state) "open" else "closed") else
            String.format(filter, if (state) "open" else "closed", prefsHelper.getAuthUsername())
        val q2 = if (filter.contains("repo")) String.format(filter, if (state) "closed" else "open") else
            String.format(filter, if (state) "closed" else "open", prefsHelper.getAuthUsername())
        subscribe(searchRepository.searchIssues(
            query = q,
            page = page,
            sort = "created"
        )) {
            when (it) {
                is SearchResult.Success -> {
                    val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.issues ?: arrayListOf()
                    updatedList.addAll(it.value.value)

                    viewState.value = viewState.value?.copy(
                        issues = updatedList,
                        loading = false,
                        refreshing = false,
                        isLastPage = page.isLastPage(it.value.last),
                        updateAdapter = true,
                        numOpen = if (reset && state) it.value.totalCount else viewState.value?.numOpen ?: 0,
                        numClosed = if (reset && !state) it.value.totalCount else viewState.value?.numClosed ?: 0
                    )
                    if (page < it.value.last) page++
                }
                is SearchResult.Failure -> {
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        refreshing = false,
                        updateAdapter = false
                    )
                    alert(it.error)
                }
            }
        }
        if (reset) {
            subscribe(searchRepository.searchIssues(
                query = q2,
                page = page,
                sort = "created"
            )) {
                when (it) {
                    is SearchResult.Success -> {
                        viewState.value = viewState.value?.copy(
                            numOpen = if (!state) it.value.totalCount else viewState.value?.numOpen ?: 0,
                            numClosed = if (state) it.value.totalCount else viewState.value?.numClosed ?: 0
                        )
                    }
                    is SearchResult.Failure -> alert(it.error)
                }
            }
        }
    }
}
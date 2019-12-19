package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.SearchRepository
import com.gitspark.gitspark.repository.SearchResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

const val CREATED_Q = "type:issue+state:%s+author:%s"
const val ASSIGNED_Q = "type:issue+state:%s+assignee:%s"
const val MENTIONED_Q = "type:issue+state:%s+mentions:%s"

class IssuesListViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val prefsHelper: PreferencesHelper
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
        requestData(reset)
    }

    private fun requestData(reset: Boolean) {
        val state = viewState.value?.showOpenIssues ?: true
        subscribe(searchRepository.searchIssues(
            query = String.format(filter, if (state) "open" else "closed", prefsHelper.getAuthUsername()),
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
                query = String.format(filter, if (state) "closed" else "open", prefsHelper.getAuthUsername()),
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
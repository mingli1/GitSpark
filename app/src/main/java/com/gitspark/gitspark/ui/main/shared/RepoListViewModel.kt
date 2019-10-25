package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.nav.RepoDetailNavigator
import javax.inject.Inject

enum class RepoListType {
    None,
    Forks
}

class RepoListViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel(), RepoDetailNavigator {

    val viewState = MutableLiveData<RepoListViewState>()
    val navigateToRepoDetailAction = SingleLiveEvent<Repo>()
    private var resumed = false
    private var page = 1

    private var type = RepoListType.None
    private var args = ""

    fun onResume(type: RepoListType, args: String) {
        if (type == RepoListType.None|| args.isEmpty()) return
        this.type = type
        this.args = args

        if (!resumed) {
            updateViewState(reset = true)
            resumed = true
        }
    }

    fun onScrolledToEnd() = updateViewState()

    fun onDestroy() {
        resumed = false
    }

    override fun onRepoSelected(repo: Repo) {
        navigateToRepoDetailAction.value = repo
    }

    private fun updateViewState(reset: Boolean = false) {
        if (reset) {
            page = 1
            viewState.value = viewState.value?.copy(loading = true) ?: RepoListViewState(loading = true)
        }
        requestData()
    }

    private fun requestData() {
        when (type) {
            RepoListType.None-> return
            RepoListType.Forks -> requestForks()
        }
    }

    private fun requestForks() {
        val args = this.args.split("/")
        subscribe(repoRepository.getForks(args[0], args[1], page = page)) {
            onRepoDataResult(it)
        }
    }

    private fun onRepoDataResult(it: RepoResult<Page<Repo>>) {
        when (it) {
            is RepoResult.Success -> onRepoDataSuccess(it.value.value, it.value.last)
            is RepoResult.Failure -> onRepoDataFailure(it.error)
        }
    }

    private fun onRepoDataSuccess(reposToAdd: List<Repo>, last: Int) {
        val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.repos ?: arrayListOf()
        updatedList.addAll(reposToAdd)

        viewState.value = viewState.value?.copy(
            repos = updatedList,
            isLastPage = page.isLastPage(last),
            updateAdapter = true,
            loading = false
        ) ?: RepoListViewState(
            repos = updatedList,
            isLastPage = page.isLastPage(last),
            updateAdapter = true,
            loading = false
        )
        if (page < last) page++
    }

    private fun onRepoDataFailure(error: String) {
        alert(error)
        viewState.value = viewState.value?.copy(updateAdapter = false, loading = false)
            ?: RepoListViewState(updateAdapter = false, loading = false)
    }
}
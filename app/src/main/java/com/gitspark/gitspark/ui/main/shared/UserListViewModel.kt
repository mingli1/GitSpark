package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.nav.UserProfileNavigator
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

enum class UserListType {
    None,
    Watchers,
    Stargazers,
    Contributors
}

class UserListViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel(), UserProfileNavigator {

    val viewState = MutableLiveData<UserListViewState>()
    val navigateToProfileAction = SingleLiveEvent<String>()
    private var resumed = false
    private var page = 1

    private var type = UserListType.None
    private var args = ""

    fun onResume(type: UserListType, args: String) {
        if (type == UserListType.None || args.isEmpty()) return
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

    override fun onUserSelected(username: String) {
        navigateToProfileAction.value = username
    }

    private fun updateViewState(reset: Boolean = false) {
        if (reset) page = 1
        requestData()
    }

    private fun requestData() {
        when (type) {
            UserListType.None -> return
            UserListType.Watchers -> requestWatchers()
            UserListType.Stargazers -> requestStargazers()
            UserListType.Contributors -> requestContributors()
        }
    }

    private fun requestWatchers() {
        val args = this.args.split("/")
        subscribe(repoRepository.getWatchers(args[0], args[1], page = page)) {
            onUserDataResult(it)
        }
    }

    private fun requestStargazers() {
        val args = this.args.split("/")
        subscribe(repoRepository.getStargazers(args[0], args[1], page = page)) {
            onUserDataResult(it)
        }
    }

    private fun requestContributors() {
        val args = this.args.split("/")
        subscribe(repoRepository.getContributors(args[0], args[1], page = page)) {
            onUserDataResult(it)
        }
    }

    private fun onUserDataResult(it: RepoResult<Page<User>>) {
        when (it) {
            is RepoResult.Success -> onUserDataSuccess(it.value.value, it.value.last)
            is RepoResult.Failure -> onUserDataFailure(it.error)
        }
    }

    private fun onUserDataSuccess(usersToAdd: List<User>, last: Int) {
        val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.users ?: arrayListOf()
        updatedList.addAll(usersToAdd)

        viewState.value = viewState.value?.copy(
            users = updatedList,
            isLastPage = page.isLastPage(last),
            updateAdapter = true
        ) ?: UserListViewState(
            users = updatedList,
            isLastPage = page.isLastPage(last),
            updateAdapter = true
        )
        if (page < last) page++
    }

    private fun onUserDataFailure(error: String) {
        alert(error)
        viewState.value = viewState.value?.copy(updateAdapter = false)
            ?: UserListViewState(updateAdapter = false)
    }
}
package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class ReposViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<ReposViewState>()
    val userMediator = MediatorLiveData<AuthUser>()

    private var resumed = false
    private var page = 1

    fun onResume() {
        val userData = userRepository.getCurrentUserData()
        userMediator.addSource(userData) { userMediator.value = it }

        if (!resumed) {
            updateViewState(reset = true)
            resumed = true
        }
    }

    fun onDestroyView() {
        resumed = false
    }

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onScrolledToEnd() = updateViewState()

    fun onUserDataRetrieved(user: AuthUser) {
        viewState.value = ReposViewState(
            totalRepos = user.numPublicRepos + user.totalPrivateRepos
        )
    }

    private fun updateViewState(reset: Boolean = false, refresh: Boolean = false) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        ) ?: ReposViewState(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        )
        if (reset) page = 1
        requestRepos()
    }

    private fun requestRepos() {
        subscribe(repoRepository.getAuthRepos(page = page)) {
            when (it) {
                is RepoResult.Success -> {
                    val isFirstPage = page == 1
                    val isLastPage = if (it.value.last == -1) true else page == it.value.last
                    viewState.value = viewState.value?.copy(
                        repos = it.value.value,
                        loading = false,
                        refreshing = false,
                        isFirstPage = isFirstPage,
                        isLastPage = isLastPage,
                        updateAdapter = true
                    )
                    if (page < it.value.last) page++
                }
                is RepoResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        refreshing = false,
                        updateAdapter = false
                    )
                }
            }
        }
    }
}
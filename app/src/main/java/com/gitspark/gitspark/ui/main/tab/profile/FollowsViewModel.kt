package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class FollowsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<FollowsViewState>()
    val userMediator = MediatorLiveData<AuthUser>()
    val navigateToProfile = SingleLiveEvent<String>()

    private var resumed = false
    private var followersPage = 1
    private var followingPage = 1
    private var currState = FollowState.Followers

    fun navigateToState(followState: FollowState) {
        currState = followState
        updateViewState(true)
        resumed = true
    }

    fun onResume() {
        val userData = userRepository.getCurrentUserData()
        userMediator.addSource(userData) { userMediator.value = it }

        if (!resumed) {
            updateViewState(true)
            resumed = true
        }
    }

    fun onDestroyView() {
        resumed = false
    }

    fun onRefresh() {
        updateViewState(reset = true, refresh = true)
    }

    fun onUserDataRetrieved(user: AuthUser) {
        viewState.value = FollowsViewState(
            followState = currState,
            totalFollowers = user.followers,
            totalFollowing = user.following
        )
    }

    fun onScrolledToEnd() {
        updateViewState()
    }

    fun onFollowsSwitchClicked() {
        currState = when (currState) {
            FollowState.Followers -> FollowState.Following
            FollowState.Following -> FollowState.Followers
        }
        updateViewState(true)
    }

    fun onUserClicked(username: String) {
        navigateToProfile.value = username
    }

    private fun updateViewState(reset: Boolean = false, refresh: Boolean = false) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false,
            followState = currState
        ) ?: FollowsViewState(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false,
            followState = currState
        )
        when (currState) {
            FollowState.Followers -> {
                if (reset) followersPage = 1
                requestFollowers()
            }
            FollowState.Following -> {
                if (reset) followingPage = 1
                requestFollowing()
            }
        }
    }

    private fun requestFollowers() {
        subscribe(userRepository.getAuthUserFollowers(prefsHelper.getCachedToken(), followersPage)) {
            when (it) {
                is UserResult.Success -> {
                    val isLastPage = if (it.value.last == -1) true else followersPage == it.value.last
                    viewState.value = viewState.value?.copy(
                        data = it.value.value,
                        loading = false,
                        refreshing = false,
                        isLastPage = isLastPage,
                        currPage = followersPage,
                        updateAdapter = true
                    )
                    if (followersPage < it.value.last) followersPage++
                }
                is UserResult.Failure -> {
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

    private fun requestFollowing() {
        subscribe(userRepository.getAuthUserFollowing(prefsHelper.getCachedToken(), followingPage)) {
            when (it) {
                is UserResult.Success -> {
                    val isLastPage = if (it.value.last == -1) true else followingPage == it.value.last
                    viewState.value = viewState.value?.copy(
                        data = it.value.value,
                        loading = false,
                        refreshing = false,
                        isLastPage = isLastPage,
                        currPage = followingPage,
                        updateAdapter = true
                    )
                    if (followingPage < it.value.last) followingPage++
                }
                is UserResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(
                        refreshing = false,
                        loading = false,
                        updateAdapter = false
                    )
                }
            }
        }
    }
}
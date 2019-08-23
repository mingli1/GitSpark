package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class FollowsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<FollowsViewState>()
    val userMediator = MediatorLiveData<AuthUser>()

    private var resumed = false
    private var followersPage = 1
    private var followingPage = 1
    private var currState = FollowState.Followers

    fun onResume() {
        val userData = userRepository.getCurrentUserData()
        userMediator.addSource(userData) { userMediator.value = it }

        if (!resumed) {
            updateViewState(currState, true)
            resumed = true
        }
    }

    fun onUserDataRetrieved(user: AuthUser) {
        viewState.value = FollowsViewState(
            followState = currState,
            totalFollowers = user.followers,
            totalFollowing = user.following
        )
    }

    fun onScrolledToEnd() {
        updateViewState(currState)
    }

    fun onFollowsSwitchClicked() {
        currState = when (currState) {
            FollowState.Followers -> FollowState.Following
            FollowState.Following -> FollowState.Followers
        }
        updateViewState(currState, true)
    }

    private fun updateViewState(state: FollowState, reset: Boolean = false) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            updateAdapter = false,
            followState = currState
        ) ?: FollowsViewState(
            loading = reset,
            updateAdapter = false,
            followState = currState
        )
        when (state) {
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
                    val isLastPage = if (it.value.last == 0) true else followersPage == it.value.last
                    viewState.value = viewState.value?.copy(
                        data = it.value.value,
                        loading = false,
                        isLastPage = isLastPage,
                        currPage = followersPage,
                        updateAdapter = true
                    )
                    if (followersPage < it.value.last) followersPage++
                }
                is UserResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false, updateAdapter = false)
                }
            }
        }
    }

    private fun requestFollowing() {
        subscribe(userRepository.getAuthUserFollowing(prefsHelper.getCachedToken(), followingPage)) {
            when (it) {
                is UserResult.Success -> {
                    val isLastPage = if (it.value.last == 0) true else followingPage == it.value.last
                    viewState.value = viewState.value?.copy(
                        data = it.value.value,
                        loading = false,
                        isLastPage = isLastPage,
                        currPage = followingPage,
                        updateAdapter = true
                    )
                    if (followingPage < it.value.last) followingPage++
                }
                is UserResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false, updateAdapter = false)
                }
            }
        }
    }
}
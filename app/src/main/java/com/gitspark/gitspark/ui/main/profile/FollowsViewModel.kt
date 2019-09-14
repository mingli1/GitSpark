package com.gitspark.gitspark.ui.main.profile

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.adapter.UserProfileNavigator
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class FollowsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel(), UserProfileNavigator {

    val viewState = MutableLiveData<FollowsViewState>()
    val userMediator = MediatorLiveData<AuthUser>()
    val navigateToProfile = SingleLiveEvent<String>()

    private var resumed = false
    private var followersPage = 1
    private var followingPage = 1
    private var currState = FollowState.Followers
    private var username: String? = null

    fun navigateToState(followState: FollowState) {
        currState = followState
        updateViewState(true)
        resumed = true
    }

    fun onResume(username: String? = null, user: User? = null) {
        this.username = username
        if (username == null) {
            val userData = userRepository.getCurrentUserData()
            userMediator.addSource(userData) { userMediator.value = it }
        }

        if (!resumed) {
            user?.let {
                viewState.value = FollowsViewState(
                    followState = currState,
                    totalFollowers = it.followers,
                    totalFollowing = it.following
                )
            }
            updateViewState(true)
            resumed = true
        }
    }

    fun onDestroy() {
        resumed = false
    }

    fun onRefresh() {
        updateViewState(reset = true, refresh = true)
    }

    fun onUserDataRetrieved(user: AuthUser) {
        viewState.value = viewState.value?.copy(
            followState = currState,
            totalFollowers = user.followers,
            totalFollowing = user.following
        ) ?: FollowsViewState(
            followState = currState,
            totalFollowers = user.followers,
            totalFollowing = user.following
        )
    }

    fun onScrolledToEnd() = updateViewState()

    fun onFollowsSwitchClicked() {
        currState = when (currState) {
            FollowState.Followers -> FollowState.Following
            FollowState.Following -> FollowState.Followers
        }
        updateViewState(true)
    }

    override fun onUserSelected(username: String) {
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
                username?.let { requestFollowers(it) } ?: requestAuthFollowers()
            }
            FollowState.Following -> {
                if (reset) followingPage = 1
                username?.let { requestFollowing(it) } ?: requestAuthFollowing()
            }
        }
    }

    private fun requestAuthFollowers() {
        subscribe(userRepository.getAuthUserFollowers(followersPage)) { handleFollowersResult(it) }
    }

    private fun requestAuthFollowing() {
        subscribe(userRepository.getAuthUserFollowing(followingPage)) { handleFollowingResult(it) }
    }

    private fun requestFollowers(username: String) {
        subscribe(userRepository.getUserFollowers(username, followersPage)) { handleFollowersResult(it) }
    }

    private fun requestFollowing(username: String) {
        subscribe(userRepository.getUserFollowing(username, followingPage)) { handleFollowingResult(it) }
    }

    private fun handleFollowersResult(it: UserResult<Page<User>>) {
        when (it) {
            is UserResult.Success -> {
                onFollowsSuccess(it.value.value, followersPage, it.value.last, true)
                if (followersPage < it.value.last) followersPage++
            }
            is UserResult.Failure -> onFollowsFailure(it.error)
        }
    }

    private fun handleFollowingResult(it: UserResult<Page<User>>) {
        when (it) {
            is UserResult.Success -> {
                onFollowsSuccess(it.value.value, followingPage, it.value.last, false)
                if (followingPage < it.value.last) followingPage++
            }
            is UserResult.Failure -> onFollowsFailure(it.error)
        }
    }

    private fun onFollowsSuccess(usersToAdd: List<User>, page: Int, last: Int, followers: Boolean) {
        val data = if (followers) viewState.value?.followers else viewState.value?.following
        val updatedList = if (page.isFirstPage()) arrayListOf() else data ?: arrayListOf()
        updatedList.addAll(usersToAdd)

        if (followers) {
            viewState.value = viewState.value?.copy(
                followers = updatedList,
                loading = false,
                refreshing = false,
                isLastPage = page.isLastPage(last),
                updateAdapter = true
            )
        }
        else {
            viewState.value = viewState.value?.copy(
                following = updatedList,
                loading = false,
                refreshing = false,
                isLastPage = page.isLastPage(last),
                updateAdapter = true
            )
        }
    }

    private fun onFollowsFailure(error: String) {
        alert(error)
        viewState.value = viewState.value?.copy(
            refreshing = false,
            loading = false,
            updateAdapter = false
        )
    }
}
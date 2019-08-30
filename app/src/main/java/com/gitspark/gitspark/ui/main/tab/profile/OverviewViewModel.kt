package com.gitspark.gitspark.ui.main.tab.profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.ContributionsHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Contribution
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class OverviewViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val contributionsHelper: ContributionsHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<OverviewViewState>()
    val userDataMediator = MediatorLiveData<AuthUser>()
    val contributionsAction = SingleLiveEvent<SortedMap<String, List<Contribution>>>()
    val navigateToFollowsAction = SingleLiveEvent<FollowState>()
    val refreshAction = SingleLiveAction()
    val navigateToEditProfileAction = SingleLiveEvent<User>()

    var currentUserData: User? = null
    @VisibleForTesting var username: String? = null

    fun onResume(username: String? = null, user: User? = null) {
        if (username == null) {
            val userData = userRepository.getCurrentUserData()
            userDataMediator.addSource(userData) { userDataMediator.value = it }
        }
        else {
            this.username = username
            viewState.value = OverviewViewState(loading = true)
            checkIfFollowing(username)
            user?.let { updateViewStateWith(it) }
        }
    }

    fun onCachedUserDataRetrieved(user: AuthUser) {
        val expired = userRepository.isUserCacheExpired(user.timestamp)
        if (expired) {
            viewState.value = OverviewViewState(loading = true)
            requestAuthUser(user)
        }
        else updateViewStateWith(user)
    }

    fun onRefresh() {
        viewState.value = viewState.value?.copy(refreshing = true)
        username?.let { refreshAction.call() } ?: requestAuthUser(null)
    }

    fun onUserDataRefreshed(user: User) = updateViewStateWith(user)

    fun onFollowsFieldClicked(followState: FollowState) {
        navigateToFollowsAction.value = followState
    }

    fun onFollowsButtonClicked(isFollowing: Boolean) {
        if (isFollowing) unfollowUser()
        else followUser()
    }

    fun onEditProfileButtonClicked() {
        currentUserData?.let { navigateToEditProfileAction.value = it }
    }

    private fun checkIfFollowing(username: String) {
        subscribe(userRepository.isFollowing(username),
            { viewState.value = viewState.value?.copy(isFollowing = true) },
            { viewState.value = viewState.value?.copy(isFollowing = false) }
        )
    }

    private fun unfollowUser() {
        subscribe(userRepository.unfollowUser(username!!),
            {
                alert("Unfollowed $username. Note: it may take some time for numbers to update.")
                viewState.value = viewState.value?.copy(isFollowing = false)
            },
            { alert("Error: ${it.message}") }
        )
    }

    private fun followUser() {
        subscribe(userRepository.followUser(username!!),
            {
                alert("Followed $username. Note: it may take some time for numbers to update.")
                viewState.value = viewState.value?.copy(isFollowing = true)
            },
            { alert("Error: ${it.message}") }
        )
    }

    private fun requestAuthUser(existingUser: AuthUser?) {
        subscribe(userRepository.getAuthUser()) {
            when (it) {
                is UserResult.Success -> {
                    subscribe(userRepository.cacheUserData(it.value),
                        { updateViewStateWith(it.value) },
                        {
                            alert("Failed to cache user data.")
                            existingUser?.let { user -> updateViewStateWith(user) }
                        })
                }
                is UserResult.Failure -> {
                    alert(it.error)
                    existingUser?.let { user -> updateViewStateWith(user) }
                }
            }
        }
    }

    private fun updateViewStateWith(user: User) {
        currentUserData = user
        with (user) {
            var formattedDateTime = ""
            if (createdAt.isNotEmpty()) {
                val createdDate = Instant.parse(createdAt)
                val dateTime = LocalDateTime.ofInstant(createdDate, ZoneOffset.UTC)
                formattedDateTime = DateTimeFormatter.ofPattern("MMM dd, yyyy").format(dateTime)
            }

            viewState.value = OverviewViewState(
                nameText = name,
                usernameText = login,
                avatarUrl = avatarUrl,
                bioText = bio,
                locationText = location,
                emailText = email,
                companyText = company,
                numFollowers = followers,
                numFollowing = following,
                loading = true,
                refreshing = false,
                planName = if (this is AuthUser) plan.planName else "",
                createdDate = formattedDateTime,
                authUser = username == null
            )
        }

        subscribe(userRepository.getContributionsSvg(user.login)) {
            when (it) {
                is UserResult.Success -> {
                    contributionsAction.value = contributionsHelper.parse(it.value)
                    viewState.value = viewState.value?.copy(
                        totalContributions = contributionsHelper.getTotalContributions(it.value),
                        loading = false
                    )
                }
                is UserResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }
}
package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.ContributionsHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Contribution
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class OverviewViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val prefsHelper: PreferencesHelper,
    private val contributionsHelper: ContributionsHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<OverviewViewState>()
    val userDataMediator = MediatorLiveData<AuthUser>()
    val contributionsAction = SingleLiveEvent<SortedMap<String, List<Contribution>>>()
    val navigateToFollowsAction = SingleLiveEvent<FollowState>()

    private var username: String? = null

    fun onResume(username: String? = null) {
        if (username == null) {
            val userData = userRepository.getCurrentUserData()
            userDataMediator.addSource(userData) { userDataMediator.value = it }
        }
        else {
            this.username = username
            viewState.value = OverviewViewState(loading = true)
            requestUser(username)
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
        username?.let { requestUser(it) } ?: requestAuthUser(null)
    }

    fun onFollowsFieldClicked(followState: FollowState) {
        navigateToFollowsAction.value = followState
    }

    private fun requestAuthUser(existingUser: AuthUser?) {
        subscribe(userRepository.getAuthUser(prefsHelper.getCachedToken())) {
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

    private fun requestUser(username: String) {
        subscribe(userRepository.getUser(prefsHelper.getCachedToken(), username)) {
            when (it) {
                is UserResult.Success -> updateViewStateWith(it.value)
                is UserResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }

    private fun updateViewStateWith(user: User) {
        with (user) {
            var formattedDateTime = ""
            if (createdAt.isNotEmpty()) {
                val createdDate = Instant.parse(createdAt)
                val dateTime = LocalDateTime.ofInstant(createdDate, ZoneOffset.UTC)
                formattedDateTime = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss").format(dateTime)
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
                createdDate = formattedDateTime
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
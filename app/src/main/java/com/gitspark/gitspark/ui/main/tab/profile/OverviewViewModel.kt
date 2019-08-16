package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.ContributionsHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Contribution
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
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

    fun onResume() {
        val userData = userRepository.getCurrentUserData()
        userDataMediator.addSource(userData) { userDataMediator.value = it }
    }

    fun onCachedUserDataRetrieved(user: AuthUser) {
        val expired = userRepository.isUserCacheExpired(user.timestamp)
        if (expired) {
            viewState.value = OverviewViewState(loading = true)
            subscribe(userRepository.getAuthUser(prefsHelper.getCachedToken())) {
                when (it) {
                    is UserResult.Success -> {
                        subscribe(userRepository.cacheUserData(it.user),
                            { updateViewStateWith(it.user) },
                            {
                                alert("Failed to cache user data.")
                                updateViewStateWith(user)
                            })
                    }
                    is UserResult.Failure -> {
                        alert("Failed to update user data.")
                        updateViewStateWith(user)
                    }
                }
            }
        }
        else updateViewStateWith(user)
    }

    private fun updateViewStateWith(user: AuthUser) {
        with (user) {
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
                loading = false,
                planName = plan.planName
            )
        }

        subscribe(userRepository.getContributionsSvg(user.login)) {
            contributionsAction.value = contributionsHelper.parse(it)
            viewState.value = viewState.value?.copy(
                totalContributions = contributionsHelper.getTotalContributions(it)
            )
        }
    }
}
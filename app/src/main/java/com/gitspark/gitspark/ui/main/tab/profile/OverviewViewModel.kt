package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class OverviewViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<OverviewViewState>()
    val userDataMediator = MediatorLiveData<AuthUser>()

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
                loading = false
            )
        }
    }
}
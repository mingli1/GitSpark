package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<ProfileViewState>()
    val loadViewAction = SingleLiveAction()

    fun requestUserData(username: String, refresh: Boolean = false) {
        viewState.value = viewState.value?.copy(
            loading = true,
            updatedUserData = false
        ) ?: ProfileViewState(loading = true)

        subscribe(userRepository.getUser(username)) {
            when (it) {
                is UserResult.Success -> {
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        refreshing = refresh,
                        updatedUserData = true,
                        data = it.value
                    )
                    if (!refresh) loadViewAction.call()
                }
                is UserResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false, updatedUserData = false)
                }
            }
        }
    }
}
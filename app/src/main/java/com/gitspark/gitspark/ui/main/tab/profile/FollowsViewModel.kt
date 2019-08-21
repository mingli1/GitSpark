package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class FollowsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<FollowsViewState>()

    private var resumed = false
    private var page = 0

    fun onResume() {
        //if (!resumed) {
            page = 1
            viewState.value = FollowsViewState(loading = true)
            requestFollowers(page)
            resumed = true
        //}
    }

    private fun requestFollowers(page: Int) {
        subscribe(userRepository.getUserFollowers(preferencesHelper.getCachedToken(), "JakeWharton", page)) {
            when (it) {
                is UserResult.Success -> {
                    println("$it")
                }
                is UserResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }
}
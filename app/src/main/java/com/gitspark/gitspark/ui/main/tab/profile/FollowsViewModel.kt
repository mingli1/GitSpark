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
        if (!resumed) {
            page = 1
            viewState.value = FollowsViewState(loading = true, updateAdapter = false)
            requestFollowers()
            resumed = true
        }
    }

    fun onScrolledToEnd() {
        viewState.value = viewState.value?.copy(loading = true, updateAdapter = false)
        requestFollowers()
    }

    private fun requestFollowers() {
        subscribe(userRepository.getUserFollowers(preferencesHelper.getCachedToken(), "blerner", page)) {
            when (it) {
                is UserResult.Success -> {
                    val isLastPage = if (it.value.last == 0) true else page == it.value.last
                    viewState.value = viewState.value?.copy(
                        data = it.value.value,
                        loading = false,
                        isLastPage = isLastPage,
                        currPage = page,
                        updateAdapter = true
                    )
                    if (page < it.value.last) page++
                }
                is UserResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false, updateAdapter = false)
                }
            }
        }
    }
}
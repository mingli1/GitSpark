package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.ui.adapter.UserProfileNavigator
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

enum class UserListType {
    None,
    Watchers,
    Stargazers
}

class UserListViewModel @Inject constructor() : BaseViewModel(), UserProfileNavigator {

    val viewState = MutableLiveData<UserListViewState>()
    private var resumed = false
    private var page = 1

    private var type = UserListType.None
    private var args = ""

    fun onResume(type: UserListType, args: String) {
        if (type == UserListType.None || args.isEmpty()) return
        this.type = type
        this.args = args

        if (!resumed) {
            updateViewState(reset = true)
            resumed = true
        }
    }

    fun onScrolledToEnd() = updateViewState()

    fun onDestroy() {
        resumed = false
    }

    override fun onUserSelected(username: String) {

    }

    private fun updateViewState(reset: Boolean = false) {
        if (reset) page = 1
        requestData()
    }

    private fun requestData() {
        when (type) {
            UserListType.None -> return
            UserListType.Watchers -> requestWatchers()
            UserListType.Stargazers -> requestStargazers()
        }
    }

    private fun requestWatchers() {

    }

    private fun requestStargazers() {

    }
}
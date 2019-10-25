package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.User

data class UserListViewState(
    val users: ArrayList<User> = arrayListOf(),
    val updateAdapter: Boolean = false,
    val isLastPage: Boolean = false,
    val loading: Boolean = false
)
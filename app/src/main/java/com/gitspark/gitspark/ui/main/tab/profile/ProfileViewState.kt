package com.gitspark.gitspark.ui.main.tab.profile

import com.gitspark.gitspark.model.User

data class ProfileViewState(
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val updatedUserData: Boolean = false,
    val data: User = User()
)
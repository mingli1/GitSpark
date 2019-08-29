package com.gitspark.gitspark.ui.main.tab

import com.gitspark.gitspark.model.User

data class ProfileViewState(
    val loading: Boolean = false,
    val updatedUserData: Boolean = false,
    val data: User = User()
)
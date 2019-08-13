package com.gitspark.gitspark.ui.main.tab.profile

data class OverviewViewState(
    val nameText: String = "",
    val usernameText: String = "",
    val bioText: String = "",
    val avatarUrl: String = "",
    val locationText: String = "",
    val emailText: String = "",
    val companyText: String = "",
    val numFollowing: Int = 0,
    val numFollowers: Int = 0
)
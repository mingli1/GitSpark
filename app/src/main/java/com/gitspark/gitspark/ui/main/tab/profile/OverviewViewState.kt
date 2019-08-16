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
    val numFollowers: Int = 0,
    val loading: Boolean = false,
    val planName: String = "",
    val totalContributions: Int = 0,
    val createdDate: String = "",
    val refreshing: Boolean = false
)
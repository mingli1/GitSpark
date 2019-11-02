package com.gitspark.gitspark.ui.main.profile

import com.gitspark.gitspark.model.Repo

data class OverviewViewState(
    val nameText: String = "",
    val usernameText: String = "",
    val bioText: String = "",
    val avatarUrl: String = "",
    val locationText: String = "",
    val emailText: String = "",
    val urlText: String = "",
    val companyText: String = "",
    val numFollowing: Int = 0,
    val numFollowers: Int = 0,
    val loading: Boolean = false,
    val planName: String = "",
    val totalContributions: Int = 0,
    val createdDate: String = "",
    val refreshing: Boolean = false,
    val authUser: Boolean = true,
    val isFollowing: Boolean = false,
    val pinnedReposHeader: String = "",
    val pinnedRepos: List<Repo> = emptyList(),
    val pinnedReposShown: Boolean = false
)
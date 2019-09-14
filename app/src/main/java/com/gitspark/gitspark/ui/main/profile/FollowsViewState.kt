package com.gitspark.gitspark.ui.main.profile

import com.gitspark.gitspark.model.User

data class FollowsViewState(
    val followers: ArrayList<User> = arrayListOf(),
    val following: ArrayList<User> = arrayListOf(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val isLastPage: Boolean = false,
    val updateAdapter: Boolean = false,
    val followState: FollowState = FollowState.Followers,
    val totalFollowers: Int = 0,
    val totalFollowing: Int = 0
)

enum class FollowState {
    Followers,
    Following
}
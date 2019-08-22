package com.gitspark.gitspark.ui.main.tab.profile

import com.gitspark.gitspark.model.User

data class FollowsViewState(
    val data: List<User> = emptyList(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val isLastPage: Boolean = false,
    val currPage: Int = 1,
    val updateAdapter: Boolean = false,
    val followState: FollowState = FollowState.Followers
)

enum class FollowState {
    Followers,
    Following
}
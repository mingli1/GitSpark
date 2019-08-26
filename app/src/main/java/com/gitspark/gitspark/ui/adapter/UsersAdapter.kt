package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.main.tab.profile.FollowState
import kotlinx.android.synthetic.main.user_view.view.*

class UsersAdapter : PaginationAdapter() {

    private var followState = FollowState.Followers

    override fun bind(item: Pageable, view: View) {
        if (item is User) {
            with (view) {
                avatar_image.loadImage(item.avatarUrl)
                name_field.text = item.login
                unfollow_button.isVisible = followState == FollowState.Following
            }
        }
    }

    override fun getViewHolderId() = R.layout.user_view

    fun addInitialUsers(users: List<User>, isOnlyPage: Boolean, followState: FollowState) {
        this.followState = followState
        super.addInitialItems(users, isOnlyPage)
    }
}
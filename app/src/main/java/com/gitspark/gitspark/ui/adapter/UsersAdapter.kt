package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.model.User
import kotlinx.android.synthetic.main.user_view.view.*

class UsersAdapter(private val navigator: UserProfileNavigator) : PaginationAdapter() {

    override fun bind(item: Pageable, view: View) {
        if (item is User) {
            with (view) {
                avatar_image.loadImage(item.avatarUrl)
                name_field.text = item.login
                user_card.setOnClickListener { navigator.onUserSelected(item.login) }
            }
        }
    }

    override fun getViewHolderId() = R.layout.user_view
}
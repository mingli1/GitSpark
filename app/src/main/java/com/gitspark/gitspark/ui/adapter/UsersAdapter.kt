package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.nav.UserProfileNavigator
import kotlinx.android.synthetic.main.user_view.view.*

class UsersAdapter(
    private val navigator: UserProfileNavigator,
    private val prefsHelper: PreferencesHelper
) : PaginationAdapter() {

    override fun bind(item: Pageable, view: View) {
        if (item is User) {
            with (view) {
                avatar_image.loadImage(item.avatarUrl)
                name_field.text = item.login

                if (item.contributions == 1)
                    content_field.text = context.getString(R.string.contributions_text_single)
                else if (item.contributions > 1) {
                    content_field.text = context.getString(R.string.contributions_text, item.contributions)
                }

                user_card.setOnClickListener {
                    if (item.login != prefsHelper.getAuthUsername()) navigator.onUserSelected(item.login)
                }
            }
        }
    }

    override fun getViewHolderId() = R.layout.user_view
}
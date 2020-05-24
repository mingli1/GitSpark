package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.dialog.IssueUserAdapterCallback
import kotlinx.android.synthetic.main.issue_user_view.view.*

class IssueUsersAdapter(
    private val currUsers: Array<String>,
    private val callback: IssueUserAdapterCallback
) : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.issue_user_view

    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is User) {
            with (view) {
                if (currUsers.contains(item.login)) check_mark.visibility = View.VISIBLE
                else check_mark.visibility = View.INVISIBLE

                if (item.avatarUrl.isNotEmpty()) avatar.loadImage(item.avatarUrl)
                username_field.text = item.login

                assignee_view.setOnClickListener {
                    if (check_mark.isVisible) {
                        callback.removeUser(item)
                        check_mark.visibility = View.INVISIBLE
                    } else {
                        callback.addUser(item)
                        check_mark.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}
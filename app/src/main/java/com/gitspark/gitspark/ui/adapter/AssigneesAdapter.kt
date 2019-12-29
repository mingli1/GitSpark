package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.dialog.AssigneesAdapterCallback
import kotlinx.android.synthetic.main.assignee_view.view.*

class AssigneesAdapter(
    private val currAssignees: Array<String>,
    private val callback: AssigneesAdapterCallback
) : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.assignee_view

    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is User) {
            with (view) {
                if (currAssignees.contains(item.login)) check_mark.visibility = View.VISIBLE
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
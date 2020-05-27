package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.model.RepoStatus
import com.gitspark.gitspark.model.STATUS_PENDING
import com.gitspark.gitspark.model.STATUS_SUCCESS
import kotlinx.android.synthetic.main.item_check_detail.view.*

class ChecksAdapter : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.item_check_detail

    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is RepoStatus) {
            with (view) {
                status_icon.setImageResource(when (item.state) {
                    STATUS_SUCCESS -> R.drawable.ic_check
                    STATUS_PENDING -> R.drawable.ic_dot
                    else -> R.drawable.ic_x
                })
                status_icon.drawable.setColor(when (item.state) {
                    STATUS_SUCCESS -> context.getColor(R.color.colorGreen)
                    STATUS_PENDING -> context.getColor(R.color.colorYellowOrange)
                    else -> context.getColor(R.color.colorRed)
                })
                if (item.avatarUrl.isNotEmpty()) check_avatar.loadImage(item.avatarUrl)
                check_name.text = item.context
            }
        }
    }
}
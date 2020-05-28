package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.model.CheckSuite
import kotlinx.android.synthetic.main.item_check_detail.view.*

class ChecksAdapter : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.item_check_detail

    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is CheckSuite) {
            with (view) {
                status_icon.setImageResource(when {
                    item.isSuccessful() -> R.drawable.ic_check
                    item.isPending() -> R.drawable.ic_dot
                    else -> R.drawable.ic_x
                })
                status_icon.drawable.setColor(when {
                    item.isSuccessful() -> context.getColor(R.color.colorGreen)
                    item.isPending() -> context.getColor(R.color.colorYellowOrange)
                    else -> context.getColor(R.color.colorRed)
                })
                if (item.app.owner.avatarUrl.isNotEmpty()) check_avatar.loadImage(item.app.owner.avatarUrl)
                check_name.text = item.app.name
            }
        }
    }
}
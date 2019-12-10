package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Issue
import kotlinx.android.synthetic.main.issue_view.view.*
import org.threeten.bp.Instant

class IssuesAdapter(private val timeHelper: TimeHelper) : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.issue_view

    override fun bind(item: Pageable, view: View) {
        if (item is Issue) {
            with (view) {
                icon.setImageResource(if (item.htmlUrl.contains("issue")) R.drawable.ic_issue else R.drawable.ic_pull_request)
                icon.drawable.setColor(if (item.state == "open")
                    context.getColor(R.color.colorSuccess) else context.getColor(R.color.colorError))

                title_field.text = item.title

                val date = Instant.parse(if (item.state == "open") item.createdAt else item.closedAt)
                val formatted = timeHelper.getRelativeTimeFormat(date)

                content_field.text = context.getString(R.string.issue_desc,
                    item.number,
                    if (item.state == "open") "opened" else "closed",
                    formatted,
                    item.user.login
                )
                comments_field.isVisible = item.numComments > 0
                comments_field.text = item.numComments.toString()
            }
        }
    }
}
package com.gitspark.gitspark.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.helper.ColorHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.ui.nav.IssueDetailNavigator
import kotlinx.android.synthetic.main.issue_view.view.*
import org.threeten.bp.Instant

class IssuesAdapter(
    private val timeHelper: TimeHelper,
    private val colorHelper: ColorHelper,
    private val navigator: IssueDetailNavigator,
    private val includeRepoName: Boolean = false
) : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.issue_view

    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is Issue) {
            with (view) {
                val isIssue = item.htmlUrl.contains("/issues")
                icon.setImageResource(if (isIssue) R.drawable.ic_issue else R.drawable.ic_pull_request)
                icon.drawable.setColor(if (item.state == "open")
                    context.getColor(R.color.colorSuccess) else context.getColor(R.color.colorError))

                title_field.text = item.title

                val date = Instant.parse(if (item.state == "open") item.createdAt else item.closedAt)
                val formatted = timeHelper.getRelativeAndExactTimeFormat(date, short = true)

                if (includeRepoName) {
                    content_field.text = context.getString(
                        R.string.issue_desc_repo,
                        item.getRepoFullNameFromUrl(),
                        item.number,
                        if (item.state == "open") "opened" else "closed",
                        formatted,
                        item.user.login
                    )
                }
                else {
                    content_field.text = context.getString(
                        R.string.issue_desc,
                        item.number,
                        if (item.state == "open") "opened" else "closed",
                        formatted,
                        item.user.login
                    )
                }
                comments_field.isVisible = item.numComments > 0
                comments_field.text = item.numComments.toString()

                labels_container.isVisible = item.labels.isNotEmpty()
                labels_container.removeAllViews()
                item.labels.forEach {
                    val labelView = LayoutInflater.from(context)
                        .inflate(
                            R.layout.label_view,
                            labels_container,
                            false
                        )
                    ((labelView as CardView).getChildAt(0) as TextView).apply {
                        text = it.name
                        setBackgroundColor(Color.parseColor("#${it.color}"))
                        setTextColor(Color.parseColor(colorHelper.getTextColor(it.color)))
                    }
                    labels_container.addView(labelView)
                }

                issue_view.setOnClickListener { navigator.onIssueClicked(item, isPullRequest = !isIssue) }
            }
        }
    }
}
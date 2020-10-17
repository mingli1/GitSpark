package com.gitspark.gitspark.ui.adapter

import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.*
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.Loading
import com.gitspark.gitspark.ui.nav.FeedNavigator
import kotlinx.android.synthetic.main.profile_feed_view.view.*
import kotlinx.android.synthetic.main.received_feed_view.view.action_description
import kotlinx.android.synthetic.main.received_feed_view.view.content_field
import kotlinx.android.synthetic.main.received_feed_view.view.date_field
import kotlinx.android.synthetic.main.received_feed_view.view.profile_icon
import org.threeten.bp.Instant

class HomeFeedAdapter(
    private val timeHelper: TimeHelper,
    private val eventHelper: EventHelper,
    private val navigator: FeedNavigator,
    private val prefsHelper: PreferencesHelper,
    private val recent: Boolean = false
) : PaginationAdapter() {

    private val spannableCache = mutableMapOf<String, SpannableStringBuilder>()

    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        val result = DiffUtil.calculateDiff(DiffCallback(this.items, items))
        with (this.items) {
            clear()
            spannableCache.clear()

            items.forEach { event ->
                val title = eventHelper.getTitle(event as Event, !recent)
                if (title.isNotEmpty() && !title.endsWith(" ")) spannableCache[event.id] = title
            }

            addAll(items)
            if (!isLastPage) add(Loading)
        }
        result.dispatchUpdatesTo(this)
    }

    override fun getViewHolderId() = if (recent) R.layout.profile_feed_view else R.layout.received_feed_view

    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is Event) {
            with (view) {
                val title = spannableCache[item.id]
                isVisible = title?.isNotEmpty() ?: false
                if (title.isNullOrEmpty()) {
                    val lp = layoutParams.apply { height = 0 }
                    layoutParams = lp
                    return
                } else {
                    val lp = layoutParams.apply { height = ViewGroup.LayoutParams.WRAP_CONTENT }
                    layoutParams = lp
                }

                action_description.text = title

                val content = eventHelper.getContent(item)
                content_field.isVisible = content.isNotEmpty()
                content_field.text = content

                if (item.actor.avatarUrl.isNotEmpty()) profile_icon.loadImage(item.actor.avatarUrl)
                val createdDate = Instant.parse(item.createdAt)
                val formatted = timeHelper.getRelativeAndExactTimeFormat(createdDate)
                date_field.text = formatted

                if (recent) username_field.text = item.actor.login
                else profile_icon.setOnClickListener {
                    if (item.actor.login != prefsHelper.getAuthUsername()) navigator.onUserSelected(item.actor.login)
                }

                setOnClickListener { handleFeedOnClick(item) }
            }
        }
    }

    private fun handleFeedOnClick(event: Event) {
        when (event.type) {
            COMMIT_COMMENT_EVENT, CREATE_EVENT, PUSH_EVENT, PUBLIC_EVENT, WATCH_EVENT -> {
                navigator.onRepoSelected(event.repo.repoName)
            }
            FORK_EVENT -> {
                navigator.onRepoSelected(event.payload.forkee.fullName)
            }
            ISSUES_EVENT, ISSUE_COMMENT_EVENT -> {
                navigator.onIssueSelected(event.payload.issue)
            }
            PULL_REQUEST_EVENT, PULL_REQUEST_REVIEW_COMMENT_EVENT -> {
                navigator.onPullRequestSelected(event.payload.pullRequest, event.repo.repoName)
            }
        }
    }
}
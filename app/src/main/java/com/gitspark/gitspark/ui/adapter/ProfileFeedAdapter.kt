package com.gitspark.gitspark.ui.adapter

import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.*
import com.gitspark.gitspark.model.DateGroup
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.Loading
import com.gitspark.gitspark.ui.nav.FeedNavigator
import kotlinx.android.synthetic.main.date_group_view.view.*
import kotlinx.android.synthetic.main.profile_feed_view.view.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

class ProfileFeedAdapter(
    private val timeHelper: TimeHelper,
    private val eventHelper: EventHelper,
    private val navigator: FeedNavigator
) : PaginationAdapter() {

    private val spannableCache = mutableMapOf<String, SpannableStringBuilder>()

    override fun getViewHolderId() = R.layout.profile_feed_view

    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        val result = DiffUtil.calculateDiff(DiffCallback(this.items, items))
        with (this.items) {
            clear()
            spannableCache.clear()
            val groupedItems = items.groupBy {
                val event = it as Event
                LocalDateTime.ofInstant(Instant.parse(event.createdAt), ZoneOffset.UTC).toLocalDate()
            }
            groupedItems.forEach { pair ->
                var emptyDate = true
                pair.value.forEach { event ->
                    val title = eventHelper.getTitle(event as Event)
                    if (title.isNotEmpty()) spannableCache[event.id] = title
                    emptyDate = emptyDate && title.isEmpty()
                }
                if (!emptyDate) {
                    add(DateGroup((pair.value[0] as Event).createdAt))
                    pair.value.forEach { event ->
                        add(event)
                    }
                }
            }
            if (!isLastPage) add(Loading)
        }
        result.dispatchUpdatesTo(this)
    }

    override fun bind(item: Pageable, view: View, position: Int) {
        when (item) {
            is DateGroup -> {
                var formattedDateTime = ""
                if (item.date.isNotEmpty()) {
                    val createdDate = Instant.parse(item.date)
                    val dateTime = LocalDateTime.ofInstant(createdDate, ZoneOffset.UTC)
                    formattedDateTime = DateTimeFormatter.ofPattern("MMM d, yyyy").format(dateTime)
                }
                view.date_group.text = view.context.getString(R.string.feed_date_group, formattedDateTime)
            }
            is Event -> {
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
                    username_field.text = item.actor.login

                    val createdDate = Instant.parse(item.createdAt)
                    val formatted = timeHelper.getRelativeAndExactTimeFormat(createdDate)
                    date_field.text = formatted

                    setOnClickListener { handleFeedOnClick(item) }
                }
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
package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.EventHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.DateGroup
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.Loading
import kotlinx.android.synthetic.main.date_group_view.view.*
import kotlinx.android.synthetic.main.profile_feed_view.view.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

class ProfileFeedAdapter(
    private val timeHelper: TimeHelper,
    private val eventHelper: EventHelper
) : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.profile_feed_view

    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        with (this.items) {
            clear()
            val groupedItems = items.groupBy {
                val event = it as Event
                LocalDateTime.ofInstant(Instant.parse(event.createdAt), ZoneOffset.UTC).toLocalDate()
            }
            groupedItems.forEach { pair ->
                var emptyDate = true
                pair.value.forEach { event ->
                    val title = eventHelper.getTitle(event as Event)
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
        notifyDataSetChanged()
    }

    override fun bind(item: Pageable, view: View) {
        when (item) {
            is DateGroup -> {
                var formattedDateTime = ""
                if (item.date.isNotEmpty()) {
                    val createdDate = Instant.parse(item.date)
                    val dateTime = LocalDateTime.ofInstant(createdDate, ZoneOffset.UTC)
                    formattedDateTime = DateTimeFormatter.ofPattern("MMM dd, yyyy").format(dateTime)
                }
                view.date_group.text = view.context.getString(R.string.feed_date_group, formattedDateTime)
            }
            is Event -> {
                with (view) {
                    val title = eventHelper.getTitle(item)
                    if (title.isEmpty()) {
                        isVisible = false
                        val lp = layoutParams.apply { height = 0 }
                        layoutParams = lp
                        return
                    }

                    action_description.text = title
                    val content = eventHelper.getContent(item)
                    content_field.isVisible = content.isNotEmpty()
                    content_field.text = content

                    if (item.actor.avatarUrl.isNotEmpty()) profile_icon.loadImage(item.actor.avatarUrl)
                    username_field.text = item.actor.login

                    val createdDate = Instant.parse(item.createdAt)
                    val formatted = timeHelper.getRelativeTimeFormat(createdDate)
                    date_field.text = formatted
                }
            }
        }
    }
}
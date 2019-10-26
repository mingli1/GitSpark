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
            if (items.isNotEmpty()) {
                val firstEvent = items[0] as Event
                val title = eventHelper.getTitle(firstEvent)
                if (title.isNotEmpty()) add(DateGroup(firstEvent.createdAt))
                else (add(DateGroup("")))
            }
            items.forEachIndexed { index, item ->
                when (index) {
                    items.size - 1 -> add(item)
                    else -> {
                        val currEvent = items[index] as Event
                        val nextEvent = items[index + 1] as Event
                        add(currEvent)

                        val date1 = LocalDateTime.ofInstant(Instant.parse(currEvent.createdAt), ZoneOffset.UTC).toLocalDate()
                        val date2 = LocalDateTime.ofInstant(Instant.parse(nextEvent.createdAt), ZoneOffset.UTC).toLocalDate()
                        val title = eventHelper.getTitle(nextEvent)

                        if (date1 != date2) {
                            if (title.isNotEmpty()) add(DateGroup(nextEvent.createdAt))
                            else (add(DateGroup("")))
                        }
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
                if (item.date.isEmpty()) {
                    view.isVisible = false
                    val layoutParams = view.layoutParams.apply { height = 0 }
                    view.layoutParams = layoutParams
                    return
                }

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
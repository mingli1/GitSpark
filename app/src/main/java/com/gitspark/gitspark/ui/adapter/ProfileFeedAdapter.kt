package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.DateGroup
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.Loading
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class ProfileFeedAdapter(private val timeHelper: TimeHelper) : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.profile_feed_view

    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        with (this.items) {
            clear()
            if (items.isNotEmpty()) {
                val firstEvent = items[0] as Event
                add(DateGroup(firstEvent.createdAt))
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

                        if (date1 != date2) {
                            add(DateGroup(nextEvent.createdAt))
                        }
                    }
                }
            }
            if (!isLastPage) add(Loading)
        }
        notifyDataSetChanged()
    }

    override fun bind(item: Pageable, view: View) {

    }
}
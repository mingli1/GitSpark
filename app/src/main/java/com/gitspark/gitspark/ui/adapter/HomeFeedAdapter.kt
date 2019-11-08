package com.gitspark.gitspark.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.EventHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Event
import kotlinx.android.synthetic.main.received_feed_view.view.*
import org.threeten.bp.Instant

class HomeFeedAdapter(
    private val timeHelper: TimeHelper,
    private val eventHelper: EventHelper,
    private val recent: Boolean = false
) : PaginationAdapter() {

    override fun getViewHolderId() = if (recent) R.layout.profile_feed_view else R.layout.received_feed_view

    override fun bind(item: Pageable, view: View) {
        if (item is Event) {
            with (view) {
                val title = eventHelper.getTitle(item)
                isVisible = title.isNotEmpty()
                if (title.isEmpty()) {
                    val lp = layoutParams.apply { height = 0 }
                    layoutParams = lp
                    return
                } else {
                    val lp = layoutParams.apply { height = ViewGroup.LayoutParams.WRAP_CONTENT }
                    layoutParams = lp
                }

                action_description.text = eventHelper.getTitle(item, !recent)

                val content = eventHelper.getContent(item)
                content_field.isVisible = content.isNotEmpty()
                content_field.text = content

                if (item.actor.avatarUrl.isNotEmpty()) profile_icon.loadImage(item.actor.avatarUrl)
                val createdDate = Instant.parse(item.createdAt)
                val formatted = timeHelper.getRelativeTimeFormat(createdDate)
                date_field.text = formatted
            }
        }
    }
}
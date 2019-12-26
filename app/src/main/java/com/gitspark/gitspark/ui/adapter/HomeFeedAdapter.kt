package com.gitspark.gitspark.ui.adapter

import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.EventHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.Loading
import com.gitspark.gitspark.ui.nav.UserProfileNavigator
import kotlinx.android.synthetic.main.profile_feed_view.view.*
import kotlinx.android.synthetic.main.received_feed_view.view.action_description
import kotlinx.android.synthetic.main.received_feed_view.view.content_field
import kotlinx.android.synthetic.main.received_feed_view.view.date_field
import kotlinx.android.synthetic.main.received_feed_view.view.profile_icon
import org.threeten.bp.Instant

class HomeFeedAdapter(
    private val timeHelper: TimeHelper,
    private val eventHelper: EventHelper,
    private val userNavigator: UserProfileNavigator,
    private val prefsHelper: PreferencesHelper,
    private val recent: Boolean = false
) : PaginationAdapter() {

    private val spannableCache = mutableMapOf<String, SpannableStringBuilder>()

    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {
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
        notifyDataSetChanged()
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
                val formatted = timeHelper.getRelativeTimeFormat(createdDate)
                date_field.text = formatted

                if (recent) username_field.text = item.actor.login
                else profile_icon.setOnClickListener {
                    if (item.actor.login != prefsHelper.getAuthUsername()) userNavigator.onUserSelected(item.actor.login)
                }
            }
        }
    }
}
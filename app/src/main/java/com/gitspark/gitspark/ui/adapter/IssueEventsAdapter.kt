package com.gitspark.gitspark.ui.adapter

import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.IssueEventHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.*
import kotlinx.android.synthetic.main.issue_comment_view.view.*
import kotlinx.android.synthetic.main.issue_event_view.view.*
import org.threeten.bp.Instant

class IssueEventsAdapter(
    private val eventHelper: IssueEventHelper,
    private val timeHelper: TimeHelper
) : PaginationAdapter() {

    var permissionLevel = PERMISSION_NONE
    private val spannableCache = mutableMapOf<Long, SpannableStringBuilder>()

    @Suppress("UNCHECKED_CAST")
    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        val sorted = (items as List<EventComment>).sortedBy { it.createdAt() }
        with (this.items) {
            clear()
            spannableCache.clear()

            sorted.forEach {
                if (it is IssueEvent) {
                    val desc = eventHelper.getDesc(it)
                    if (desc.isNotEmpty()) spannableCache[it.id] = desc
                }
            }
            addAll(sorted)
            if (!isLastPage) add(Loading)
        }
        notifyDataSetChanged()
    }

    override fun getViewHolderId() = R.layout.issue_comment_view

    override fun bind(item: Pageable, view: View) {
        when (item) {
            is IssueComment -> {
                with (view) {
                    if (item.user.avatarUrl.isNotEmpty()) profile_icon.loadImage(item.user.avatarUrl)
                    author_username.text = item.user.login

                    val date = Instant.parse(item.createdAt)
                    val formatted = timeHelper.getRelativeTimeFormat(date)

                    author_action.text = context.getString(R.string.comment_action, formatted)
                    comment_body.isOpenUrlInBrowser = true
                    comment_body.setMarkDownText(item.body)

                    val writePermission = permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE
                    val menu = PopupMenu(context, comment_options).apply {
                        inflate(R.menu.issue_comment_menu)
                        menu.findItem(R.id.delete).isVisible = writePermission
                        menu.findItem(R.id.edit).isVisible = writePermission
                        if (menu.javaClass.simpleName == "MenuBuilder") {
                            try {
                                menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java).run {
                                    isAccessible = true
                                    invoke(menu, true)
                                }
                            } catch (e: NoSuchMethodException) {}
                        }
                    }

                    comment_options.setOnClickListener { menu.show() }
                }
            }
            is IssueEvent -> {
                val desc = spannableCache[item.id]
                view.isVisible = desc?.isNotEmpty() ?: false
                if (desc.isNullOrEmpty()) {
                    val lp = view.layoutParams.apply { height = 0 }
                    (lp as ViewGroup.MarginLayoutParams).run {
                        topMargin = 0
                        bottomMargin = 0
                    }
                    view.layoutParams = lp
                    return
                } else {
                    val lp = view.layoutParams.apply { height = ViewGroup.LayoutParams.WRAP_CONTENT }
                    view.layoutParams = lp
                }

                view.event_desc.text = desc
                eventHelper.setIcon(item, view.event_icon)
            }
        }
    }
}
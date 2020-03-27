package com.gitspark.gitspark.ui.adapter

import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.IssueEventHelper
import com.gitspark.gitspark.helper.KeyboardHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.ui.main.issues.CommentMenuCallback
import kotlinx.android.synthetic.main.issue_comment_view.view.*
import kotlinx.android.synthetic.main.issue_event_view.view.*
import org.threeten.bp.Instant

class IssueEventsAdapter(
    private val eventHelper: IssueEventHelper,
    private val timeHelper: TimeHelper,
    private val keyboardHelper: KeyboardHelper,
    private val callback: CommentMenuCallback
) : PaginationAdapter() {

    var permissionLevel = PERMISSION_NONE
    private val spannableCache = mutableMapOf<Long, SpannableStringBuilder>()

    @Suppress("UNCHECKED_CAST")
    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        val sorted = (items as List<EventComment>).sortedBy { it.createdAt() }
        val result = DiffUtil.calculateDiff(DiffCallback(this.items, sorted))
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
        result.dispatchUpdatesTo(this)
    }

    fun updateComment(comment: IssueComment) {
        val pos = items.indexOfFirst { it is IssueComment && it.id == comment.id }
        items[pos] = comment
        notifyItemChanged(pos)
    }

    override fun getViewHolderId() = R.layout.issue_comment_view

    override fun bind(item: Pageable, view: View, position: Int) {
        when (item) {
            is IssueComment -> {
                with (view) {
                    if (item.user.avatarUrl.isNotEmpty()) profile_icon.loadImage(item.user.avatarUrl)
                    author_username.text = item.user.login

                    val date = Instant.parse(item.createdAt)
                    val formatted = timeHelper.getRelativeAndExactTimeFormat(date, short = true)

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
                    menu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.edit -> {
                                comment_options.isVisible = false
                                comment_body.isVisible = false
                                edit_comment.isVisible = true
                                cancel_edit_button.isVisible = true
                                update_comment_button.isVisible = true

                                edit_comment.setText(item.body)
                                edit_comment.postDelayed({
                                    edit_comment.requestFocus()
                                    keyboardHelper.showKeyboard(edit_comment)
                                }, 100)
                                callback.onEditCommentFocused()
                            }
                            R.id.delete -> callback.onDeleteSelected(item.id)
                            R.id.copy_link -> callback.onCopyLinkSelected(item.htmlUrl)
                            R.id.quote_reply -> callback.onQuoteReplySelected(item.body)
                        }
                        true
                    }

                    edit_comment.setOnClickListener { callback.onEditCommentFocused() }
                    edit_comment.onImeBack { _, _ -> callback.onEditCommentUnfocused() }

                    cancel_edit_button.setOnClickListener {
                        comment_options.isVisible = true
                        comment_body.isVisible = true
                        edit_comment.isVisible = false
                        cancel_edit_button.isVisible = false
                        update_comment_button.isVisible = false

                        callback.onEditCommentUnfocused()
                        keyboardHelper.hideKeyboard(edit_comment)
                    }
                    update_comment_button.setOnClickListener {
                        comment_options.isVisible = true
                        comment_body.isVisible = true
                        edit_comment.isVisible = false
                        cancel_edit_button.isVisible = false
                        update_comment_button.isVisible = false

                        callback.onEditCommentUnfocused()
                        keyboardHelper.hideKeyboard(edit_comment)
                        callback.onCommentUpdated(item.id, edit_comment.text.toString())
                    }
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

    inner class DiffCallback(
        private val old: List<Pageable>,
        private val new: List<Pageable>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val o = old[oldItemPosition]
            val n = new[newItemPosition]
            if (o is IssueComment && n is IssueComment && o.id == n.id) return true
            if (o is IssueEvent && n is IssueEvent && o.id == n.id) return true
            return false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val o = old[oldItemPosition]
            val n = new[newItemPosition]
            if (o is IssueComment && n is IssueComment && o.body == n.body) return true
            if (o is IssueEvent && n is IssueEvent && o.event == n.event) return true
            return false
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val o = old[oldItemPosition]
            val n = new[newItemPosition]
            if (o is IssueComment && n is IssueComment && o.body != n.body) return n.body
            return null
        }
    }
}
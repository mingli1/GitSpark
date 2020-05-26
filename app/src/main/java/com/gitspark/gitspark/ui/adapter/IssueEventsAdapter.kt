package com.gitspark.gitspark.ui.adapter

import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.extension.showOptionIcons
import com.gitspark.gitspark.helper.IssueEventHelper
import com.gitspark.gitspark.helper.KeyboardHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.ui.custom.DarkMarkdownStyle
import com.gitspark.gitspark.ui.custom.LightMarkdownStyle
import com.gitspark.gitspark.ui.main.issues.CommentMenuCallback
import kotlinx.android.synthetic.main.issue_comment_view.view.*
import kotlinx.android.synthetic.main.issue_event_view.view.*
import org.threeten.bp.Instant

class IssueEventsAdapter(
    private val eventHelper: IssueEventHelper,
    private val timeHelper: TimeHelper,
    private val keyboardHelper: KeyboardHelper,
    private val callback: CommentMenuCallback,
    private val darkMode: Boolean
) : PaginationAdapter() {

    var permissionLevel = PERMISSION_NONE
    private val spannableCache = mutableMapOf<Long, SpannableStringBuilder>()

    @Suppress("UNCHECKED_CAST")
    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        val sorted = (items as List<IssueEvent>).sortedBy { it.createdAt }
        val result = DiffUtil.calculateDiff(DiffCallback(this.items, sorted))
        with (this.items) {
            clear()
            spannableCache.clear()

            sorted.forEach {
                if (!it.isComment()) {
                    val desc = eventHelper.getDesc(it)
                    if (desc.isNotEmpty()) spannableCache[it.id] = desc
                }
            }
            addAll(sorted)
            if (!isLastPage) add(Loading)
        }
        result.dispatchUpdatesTo(this)
    }

    fun updateComment(comment: IssueEvent) {
        val pos = items.indexOfFirst { it is IssueEvent && it.id == comment.id }
        items[pos] = comment
        notifyItemChanged(pos)
    }

    override fun getViewHolderId() = R.layout.issue_comment_view

    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is IssueEvent) {
            when {
                item.isComment() -> handleComment(item, view)
                item.isCommit() -> handleCommit(item, view)
                item.isReview() -> handleReview(item, view)
                else -> handleEvent(item, view)
            }
        }
    }

    private fun handleComment(item: IssueEvent, view: View) {
        with (view) {
            if (item.actor.avatarUrl.isNotEmpty()) profile_icon.loadImage(item.actor.avatarUrl)
            author_username.text = item.actor.login

            val date = Instant.parse(item.createdAt)
            val formatted = timeHelper.getRelativeAndExactTimeFormat(date, short = true)

            author_action.text = context.getString(R.string.comment_action, formatted)
            comment_body.addStyleSheet(if (darkMode) DarkMarkdownStyle() else LightMarkdownStyle())
            comment_body.loadMarkdown(item.body)

            handleCommentMenu(item, view)
        }
    }

    private fun handleCommit(item: IssueEvent, view: View) {
        with (view) {
            event_top_divider.isVisible = false
            event_bottom_divider.isVisible = false
            event_icon.setImageResource(R.drawable.ic_event_commit)
            event_icon.drawable.setColor(context.getColor(R.color.colorPrimaryCopy))
            event_desc.isVisible = false

            commit_message.isVisible = true
            commit_message.text = item.message

            commit_sha.isVisible = true
            commit_sha.text = item.sha.take(7)
        }
    }

    private fun handleReview(item: IssueEvent, view: View) {
        if (item.body.isEmpty() && item.state == REVIEW_STATE_COMMENTED) {
            hideItem(view)
            return
        }
        with (view) {
            review_header.isVisible = true
            if (item.body.isEmpty()) {
                top_divider.isVisible = false
                header_bg.isVisible = false
                header_divider.isVisible = false
                comment_body.isVisible = false
            } else {
                avatar_card_view.isVisible = false
                author_username.text = item.user.login
                author_action.text = context.getString(R.string.left_a_comment)
                comment_body.addStyleSheet(if (darkMode) DarkMarkdownStyle() else LightMarkdownStyle())
                comment_body.loadMarkdown(item.body)

                handleCommentMenu(item, view, isReview = true)
            }

            if (item.user.avatarUrl.isNotEmpty()) review_avatar.loadImage(item.user.avatarUrl)
            review_state.setImageResource(when (item.state) {
                REVIEW_STATE_APPROVED -> R.drawable.ic_check
                REVIEW_STATE_REQUEST_CHANGES -> R.drawable.ic_request_changes
                else -> R.drawable.ic_eye
            })
            review_state.drawable.setColor(when (item.state) {
                REVIEW_STATE_APPROVED -> context.getColor(R.color.colorGreen)
                REVIEW_STATE_REQUEST_CHANGES -> context.getColor(R.color.colorRed)
                else -> context.getColor(R.color.colorPrimaryCopy)
            })
            reviewer_name.text = item.user.login
            review_action.text = when (item.state) {
                REVIEW_STATE_APPROVED -> context.getString(R.string.review_approved)
                REVIEW_STATE_REQUEST_CHANGES -> context.getString(R.string.review_changes_requested)
                else -> context.getString(R.string.review_changes_commented)
            }
            val date = Instant.parse(item.submittedAt)
            val formatted = timeHelper.getRelativeAndExactTimeFormat(date, short = true)
            review_date.text = formatted
        }
    }

    private fun handleEvent(item: IssueEvent, view: View) {
        val desc = spannableCache[item.id]
        view.isVisible = desc?.isNotEmpty() ?: false
        if (desc.isNullOrEmpty()) {
            hideItem(view)
            return
        } else {
            val lp = view.layoutParams.apply { height = ViewGroup.LayoutParams.WRAP_CONTENT }
            view.layoutParams = lp
        }

        view.event_desc.text = desc
        eventHelper.setIcon(item, view.event_icon)
    }

    private fun hideItem(view: View) {
        val lp = view.layoutParams.apply { height = 0 }
        (lp as ViewGroup.MarginLayoutParams).run {
            topMargin = 0
            bottomMargin = 0
        }
        view.layoutParams = lp
    }

    private fun handleCommentMenu(item: IssueEvent, view: View, isReview: Boolean = false) {
        with (view) {
            val writePermission =
                permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE
            val menu = PopupMenu(context, comment_options).apply {
                inflate(R.menu.issue_comment_menu)
                menu.findItem(R.id.delete).isVisible = writePermission && !isReview
                menu.findItem(R.id.edit).isVisible = writePermission && !isReview
                menu.showOptionIcons()
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
}
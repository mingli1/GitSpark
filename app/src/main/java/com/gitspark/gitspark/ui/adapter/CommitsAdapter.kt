package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Commit
import com.gitspark.gitspark.model.DateGroup
import com.gitspark.gitspark.model.Loading
import kotlinx.android.synthetic.main.date_group_view.view.*
import kotlinx.android.synthetic.main.commit_view.view.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException

class CommitsAdapter(
    private val timeHelper: TimeHelper,
    private val simple: Boolean = false
) : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.commit_view

    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        if (simple) {
            super.setItems(items, isLastPage)
            return
        }
        with (this.items) {
            clear()
            if (items.isNotEmpty()) {
                val firstCommit = items[0] as Commit
                add(DateGroup(firstCommit.getDate()))
            }
            items.forEachIndexed { index, item ->
                when (index) {
                    items.size - 1 -> add(item)
                    else -> {
                        val currCommit = items[index] as Commit
                        val nextCommit = items[index + 1] as Commit
                        add(currCommit)

                        val date1 = LocalDateTime.ofInstant(Instant.parse(currCommit.getDate()), ZoneOffset.UTC).toLocalDate()
                        val date2 = LocalDateTime.ofInstant(Instant.parse(nextCommit.getDate()), ZoneOffset.UTC).toLocalDate()

                        if (date1 != date2) {
                            add(DateGroup(nextCommit.getDate()))
                        }
                    }
                }
            }
            if (!isLastPage) add(Loading)
        }
        notifyDataSetChanged()
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
                view.date_group.text = view.context.getString(R.string.commits_date_group, formattedDateTime)
            }
            is Commit -> {
                with (view) {
                    commit_message.text = item.commit.message
                    if (item.committer.avatarUrl.isNotEmpty()) commit_profile_icon.loadImage(item.committer.avatarUrl)
                    commit_username.text = item.committer.login

                    if (simple) {
                        commit_date.text = context.getString(R.string.committed_in, item.repo.fullName)
                    } else {
                        try {
                            val commitDate = Instant.parse(item.getDate())
                            val formatted = timeHelper.getRelativeAndExactTimeFormat(commitDate)
                            commit_date.text = context.getString(R.string.committed_date, formatted)
                        } catch (e: DateTimeParseException) {}
                    }
                }
            }
        }
    }
}
package com.gitspark.gitspark.helper

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.widget.ImageView
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.strikeThrough
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.model.IssueEvent
import com.gitspark.gitspark.ui.custom.RoundedBackgroundSpan
import org.threeten.bp.Instant
import javax.inject.Inject
import javax.inject.Singleton

private const val ASSIGNED_EVENT = "assigned"
private const val CLOSED_EVENT = "closed"
private const val LABELED_EVENT = "labeled"
private const val LOCKED_EVENT = "locked"
private const val RENAMED_EVENT = "renamed"
private const val REOPENED_EVENT = "reopened"
private const val UNASSIGNED_EVENT = "unassigned"
private const val UNLOCKED_EVENT = "unlocked"

private const val LABEL_CORNER_RADIUS = 2f

@Singleton
class IssueEventHelper @Inject constructor(
    private val context: Context,
    private val colorHelper: ColorHelper,
    private val timeHelper: TimeHelper
) {

    fun getDesc(event: IssueEvent): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        builder.color(context.getColor(R.color.colorBlack)) {
            bold { append(event.actor.login) }
        }.append(" ")

        when (event.event) {
            ASSIGNED_EVENT -> {
                builder.append("assigned this issue to ")
                    .color(context.getColor(R.color.colorBlack)) {
                        bold { append(
                            if (event.assignee.login.isNotEmpty()) event.assignee.login
                            else event.assignees.joinToString()
                        ) }
                    }
            }
            CLOSED_EVENT -> {
                builder.append("closed this issue")
                if (event.commitId.isNotEmpty()) {
                    builder.append(" in ").color(context.getColor(R.color.colorPrimaryDark)) {
                        bold { append(event.commitId.take(7)) }
                    }
                }
            }
            LABELED_EVENT -> {
                builder.append("added the ")
                event.labels.forEach { label ->
                    val tag = " ${label.name} "
                    builder.append(tag)
                    builder.setSpan(RoundedBackgroundSpan(
                        LABEL_CORNER_RADIUS,
                        Color.parseColor("#${label.color}"),
                        Color.parseColor(colorHelper.getTextColor(label.color))
                    ), builder.length - tag.length, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                builder.append(if (event.labels.size <= 1) " label" else " labels")
            }
            LOCKED_EVENT -> builder.append("locked this conversation")
            RENAMED_EVENT -> {
                builder.append("renamed the title from ")
                    .color(context.getColor(R.color.colorBlack)) {
                        bold { strikeThrough { append(event.rename.from) } }
                    }
                    .append(" to ")
                    .color(context.getColor(R.color.colorBlack)) {
                        bold { append(event.rename.to) }
                    }
            }
            REOPENED_EVENT -> builder.append("reopened this issue")
            UNASSIGNED_EVENT -> {
                builder.append("unassigned ")
                    .color(context.getColor(R.color.colorBlack)) {
                        bold { append(
                            if (event.assignee.login.isNotEmpty()) event.assignee.login
                            else event.assignees.joinToString()
                        ) }
                    }
            }
            UNLOCKED_EVENT -> builder.append("unlocked this conversation")
        }

        val formatted = timeHelper.getRelativeTimeFormat(Instant.parse(event.createdAt))
        builder.append(" $formatted")

        return builder
    }

    fun setIcon(event: IssueEvent, imageView: ImageView) {
        imageView.setImageResource(when (event.event) {
            ASSIGNED_EVENT, UNASSIGNED_EVENT -> R.drawable.ic_person
            CLOSED_EVENT, REOPENED_EVENT -> R.drawable.ic_issue
            LABELED_EVENT -> R.drawable.ic_label
            LOCKED_EVENT -> R.drawable.ic_lock
            RENAMED_EVENT -> R.drawable.ic_edit
            UNLOCKED_EVENT -> R.drawable.ic_unlock
            else -> R.drawable.ic_issue
        })
        imageView.drawable.setColor(when (event.event) {
            CLOSED_EVENT -> context.getColor(R.color.colorError)
            REOPENED_EVENT -> context.getColor(R.color.colorSuccess)
            else -> context.getColor(R.color.colorPrimary)
        })
    }
}
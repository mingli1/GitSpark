package com.gitspark.gitspark.helper

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import androidx.core.text.color
import com.gitspark.gitspark.R
import com.gitspark.gitspark.model.Event
import javax.inject.Inject
import javax.inject.Singleton

private const val FORK_EVENT = "ForkEvent"
private const val ISSUES_EVENT = "IssuesEvent"
private const val ISSUE_COMMENT_EVENT = "IssueCommentEvent"
private const val PUBLIC_EVENT = "PublicEvent"
private const val PUSH_EVENT = "PushEvent"
private const val WATCH_EVENT = "WatchEvent"

@Singleton
class EventHelper @Inject constructor(private val context: Context) {

    private val builder = SpannableStringBuilder()

    fun getTitle(event: Event): SpannableStringBuilder {
        builder.clear()
        return when (event.type) {
            FORK_EVENT -> builder.append("Forked ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.payload.forkee.fullName) }
                }.append(" from ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            ISSUES_EVENT -> builder.append(event.payload.action.capitalize()).append(" issue in ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            ISSUE_COMMENT_EVENT -> builder.append(event.payload.action.capitalize())
                .append(" comment in Issue #").append(event.payload.issue.number.toString())
                .append(" of ").color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            PUSH_EVENT -> builder.append("Pushed changes ").append("to ")
                .append(getBranchFromRef(event)).append(" of ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            PUBLIC_EVENT -> builder.append("Made ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.payload.repo.fullName) }
                }.append(" public")
            WATCH_EVENT -> builder.append("Starred ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            else -> builder
        }
    }

    fun getContent(event: Event): String {
        return when (event.type) {
            ISSUES_EVENT -> {
                context.getString(R.string.issues_fullname,
                    event.payload.issue.number, event.payload.issue.title)
            }
            ISSUE_COMMENT_EVENT -> event.payload.comment.body
            PUSH_EVENT -> {
                val newCommitsText = if (event.payload.numCommits > 1)
                    context.getString(R.string.pushevent_commit_count, event.payload.numCommits)
                else
                    context.getString(R.string.pushevent_commit_single)

                var commitMessages = ""
                event.payload.commits.forEachIndexed { index, eventCommit ->
                    commitMessages += "- ${eventCommit.message}${if (index == event.payload.commits.size - 1) "" else "\n"}"
                }
                "$newCommitsText\n$commitMessages"
            }
            else -> ""
        }
    }

    private fun getBranchFromRef(event: Event): String {
        val ref = event.payload.ref
        if (ref.isEmpty()) return ""
        return ref.split("/")[2]
    }
}
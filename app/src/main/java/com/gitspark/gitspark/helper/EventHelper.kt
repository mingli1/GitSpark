package com.gitspark.gitspark.helper

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import androidx.core.text.color
import com.gitspark.gitspark.R
import com.gitspark.gitspark.model.Event
import javax.inject.Inject
import javax.inject.Singleton

private const val COMMIT_COMMENT_EVENT = "CommitCommentEvent"
private const val CREATE_EVENT = "CreateEvent"
private const val FORK_EVENT = "ForkEvent"
private const val ISSUES_EVENT = "IssuesEvent"
private const val ISSUE_COMMENT_EVENT = "IssueCommentEvent"
private const val PUBLIC_EVENT = "PublicEvent"
private const val PULL_REQUEST_EVENT = "PullRequestEvent"
private const val PULL_REQUEST_REVIEW_COMMENT_EVENT = "PullRequestReviewCommentEvent"
private const val PUSH_EVENT = "PushEvent"
private const val WATCH_EVENT = "WatchEvent"

@Singleton
class EventHelper @Inject constructor(private val context: Context) {

    private val builder = SpannableStringBuilder()

    fun getTitle(event: Event, received: Boolean = false): SpannableStringBuilder {
        builder.clear()
        if (received) {
            builder.color(context.getColor(R.color.colorBlack)) {
                bold { append(event.actor.login) }
            }.append(" ")
        }
        return when (event.type) {
            COMMIT_COMMENT_EVENT -> builder.append(if (received) "commented on commit " else "Commented on commit ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.payload.comment.commitId.take(7)) }
                }.append(" of ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }

            CREATE_EVENT -> {
                when  {
                    event.payload.ref.isEmpty() && event.payload.refType == "repository" ->
                        builder.append(if (received) "created a repository " else "Created a repository ")
                            .color(context.getColor(R.color.colorPrimaryDark)) {
                                bold { append(event.repo.repoName) }
                            }
                    event.payload.refType == "branch" && event.payload.ref != event.payload.masterBranch ->
                        builder.append(if (received) "created branch " else "Created branch ")
                            .color(context.getColor(R.color.colorPrimaryDark)) {
                                bold { append(event.payload.ref) }
                            }.append(" in ")
                            .color(context.getColor(R.color.colorPrimaryDark)) {
                                bold { append(event.repo.repoName) }
                            }
                    else -> builder
                }
            }
            FORK_EVENT -> builder.append(if (received) "forked " else "Forked ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.payload.forkee.fullName) }
                }.append(" from ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            ISSUES_EVENT -> builder.append(if (received) event.payload.action else event.payload.action.capitalize())
                .append(" an issue in ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            ISSUE_COMMENT_EVENT ->
                when {
                    event.payload.action == "created" ->
                        builder.append(if (received) "commented on Issue #" else "Commented on Issue #")
                        .append(event.payload.issue.number.toString())
                        .append(" of ").color(context.getColor(R.color.colorPrimaryDark)) {
                            bold { append(event.repo.repoName) }
                        }
                    else -> builder.append(if (received) event.payload.action else event.payload.action.capitalize())
                        .append(" comment in Issue #").append(event.payload.issue.number.toString())
                        .append(" of ").color(context.getColor(R.color.colorPrimaryDark)) {
                            bold { append(event.repo.repoName) }
                        }
                }
            PULL_REQUEST_EVENT -> builder.append(if (received) event.payload.action else event.payload.action.capitalize())
                .append(" a pull request in ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            PULL_REQUEST_REVIEW_COMMENT_EVENT -> {
                when {
                    event.payload.action == "created" || event.payload.action == "edited" ->
                        builder.append(if (received) "reviewed Pull Request #" else "Reviewed Pull Request #")
                            .append(event.payload.pullRequest.number.toString())
                            .append(" in ").color(context.getColor(R.color.colorPrimaryDark)) {
                                bold { append(event.repo.repoName) }
                            }
                    else -> builder
                }
            }
            PUSH_EVENT -> builder.append(if (received) "pushed changes to " else "Pushed changes to ")
                .append(getBranchFromRef(event)).append(" of ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            PUBLIC_EVENT -> builder.append(if (received) "made " else "Made ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.payload.repo.fullName) }
                }.append(" public")
            WATCH_EVENT -> builder.append(if (received) "starred " else "Starred ")
                .color(context.getColor(R.color.colorPrimaryDark)) {
                    bold { append(event.repo.repoName) }
                }
            else -> builder
        }
    }

    fun getContent(event: Event): String {
        return when (event.type) {
            COMMIT_COMMENT_EVENT -> event.payload.comment.body
            CREATE_EVENT -> {
                when  {
                    event.payload.ref.isEmpty() && event.payload.refType == "repository" ->
                        event.payload.description
                    else -> ""
                }
            }
            ISSUES_EVENT -> {
                context.getString(R.string.issues_fullname,
                    event.payload.issue.number, event.payload.issue.title)
            }
            ISSUE_COMMENT_EVENT -> event.payload.comment.body
            PULL_REQUEST_EVENT -> context.getString(R.string.issues_fullname,
                event.payload.number, event.payload.pullRequest.title)
            PULL_REQUEST_REVIEW_COMMENT_EVENT -> event.payload.comment.body
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
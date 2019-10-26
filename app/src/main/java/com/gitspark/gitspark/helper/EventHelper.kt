package com.gitspark.gitspark.helper

import android.content.Context
import com.gitspark.gitspark.R
import com.gitspark.gitspark.model.Event
import javax.inject.Inject
import javax.inject.Singleton

private const val PUSH_EVENT = "PushEvent"

@Singleton
class EventHelper @Inject constructor(private val context: Context) {

    fun getTitle(event: Event): String {
        return when (event.type) {
            PUSH_EVENT -> context.getString(R.string.pushevent_desc, getBranchFromRef(event), event.repo.repoName)
            else -> ""
        }
    }

    fun getContent(event: Event): String {
        return when (event.type) {
            PUSH_EVENT -> {
                val newCommitsText = if (event.payload.numCommits > 1)
                    context.getString(R.string.pushevent_commit_count, event.payload.numCommits)
                else
                    context.getString(R.string.pushevent_commit_single)

                var commitMessages = ""
                event.payload.commits.forEach { commitMessages += "- ${it.message}\n" }
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
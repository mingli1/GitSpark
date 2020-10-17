package com.gitspark.gitspark.ui.nav

import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.PullRequest

interface FeedNavigator {

    fun onUserSelected(username: String)

    fun onRepoSelected(fullName: String)

    fun onIssueSelected(issue: Issue)

    fun onPullRequestSelected(pullRequest: PullRequest, repoFullName: String)
}
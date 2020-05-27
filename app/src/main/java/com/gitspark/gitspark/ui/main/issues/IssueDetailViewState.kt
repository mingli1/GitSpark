package com.gitspark.gitspark.ui.main.issues

import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.model.PERMISSION_NONE
import com.gitspark.gitspark.model.User

data class IssueDetailViewState(
    val isPullRequest: Boolean = false,
    val permissionLevel: String = PERMISSION_NONE,
    val authUserIsAuthor: Boolean = false,
    val issueTitle: String = "",
    val isOpen: Boolean = true,
    val isMerged: Boolean = false,
    val issueUsername: String = "",
    val issueDesc: String = "",
    val numComments: Int = 0,
    val labels: List<Label> = emptyList(),
    val assignees: List<User> = emptyList(),
    val reviewers: List<User> = emptyList(),
    val locked: Boolean = false,
    val authorAvatarUrl: String = "",
    val authorUsername: String = "",
    val authorComment: String = "",
    val authorCommentDate: String = "",
    val numAdditions: Int = 0,
    val numDeletions: Int = 0,
    val baseBranch: String = "",
    val headBranch: String = "",
    val mergable: Boolean = false,
    val mergeableState: String = "",
    val loading: Boolean = false,
    val refreshing: Boolean = false
)
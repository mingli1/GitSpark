package com.gitspark.gitspark.ui.main.repo

import com.gitspark.gitspark.model.RepoContent

data class RepoContentViewState(
    val loading: Boolean = false,
    val path: String = "",
    val contentData: List<RepoContent> = emptyList(),
    val updateContent: Boolean = false,
    val branchNames: List<String> = emptyList(),
    val updateBranchSpinner: Boolean = false,
    val branchPosition: Int = 0,
    val numCommits: Int = 0,
    val commitAvatarUrl: String = "",
    val commitUsername: String = "",
    val commitMessage: String = ""
)
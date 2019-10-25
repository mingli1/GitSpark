package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.Commit

data class CommitListViewState(
    val commits: ArrayList<Commit> = arrayListOf(),
    val updateAdapter: Boolean = false,
    val isLastPage: Boolean = false
)
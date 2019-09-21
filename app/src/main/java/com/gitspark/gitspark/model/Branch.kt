package com.gitspark.gitspark.model

data class Branch(
    val name: String = "",
    val commit: BranchCommit = BranchCommit()
)

data class BranchCommit(
    val sha: String = "",
    val url: String = ""
)
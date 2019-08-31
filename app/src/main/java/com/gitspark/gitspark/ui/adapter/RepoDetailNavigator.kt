package com.gitspark.gitspark.ui.adapter

const val BUNDLE_REPO_FULLNAME = "BUNDLE_REPO_FULLNAME"

interface RepoDetailNavigator {
    fun onRepoSelected(fullName: String)
}
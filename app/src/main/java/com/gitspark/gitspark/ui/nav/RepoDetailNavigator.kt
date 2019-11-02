package com.gitspark.gitspark.ui.nav

import com.gitspark.gitspark.model.Repo

const val BUNDLE_REPO = "BUNDLE_REPO"
const val BUNDLE_REPO_FULLNAME = "BUNDLE_REPO_FULLNAME"

interface RepoDetailNavigator {
    fun onRepoSelected(repo: Repo)
}
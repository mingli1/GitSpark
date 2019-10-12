package com.gitspark.gitspark.ui.nav

import com.gitspark.gitspark.model.Repo

const val BUNDLE_REPO = "BUNDLE_REPO"

interface RepoDetailNavigator {
    fun onRepoSelected(repo: Repo)
}
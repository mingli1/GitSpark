package com.gitspark.gitspark.ui.main.repo

data class RepoOverviewViewState(
    val repoName: String = "",
    val topics: List<String> = emptyList(),
    val isArchived: Boolean = false,
    val isPrivate: Boolean = false,
    val isForked: Boolean = false,
    val repoLanguage: String = "",
    val languageColor: Int = -1,
    val repoDescription: String = "",
    val updatedText: String = "",
    val licenseText: String = "",
    val numWatchers: Int = 0,
    val numStars: Int = 0,
    val numForks: Int = 0,
    val readmeUrl: String = ""
)
package com.gitspark.gitspark.ui.main.repo

import java.util.*

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
    val readmeUrl: String = "",
    val loading: Boolean = false,
    val userWatching: Boolean = false,
    val userStarring: Boolean = false,
    val languages: SortedMap<String, Int> = sortedMapOf()
)
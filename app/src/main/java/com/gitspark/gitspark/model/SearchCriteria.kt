package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.main.search.REPOS

data class SearchCriteria(
    val type: Int = REPOS,
    val q: String = "",
    val mainQuery: String = "",
    val createdOn: String = "",
    val language: String = "",
    val fromThisUser: String = "",
    val fullName: String = "",
    val location: String = "",
    val numFollowers: String = "",
    val numRepos: String = "",
    val numStars: String = "",
    val numForks: String = "",
    val updatedOn: String = "",
    val fileExtension: String = "",
    val fileSize: String = "",
    val repoFullName: String = "",
    val includeForked: Boolean = true,
    val isOpen: Boolean = true,
    val numComments: String = "",
    val labels: String = ""
)
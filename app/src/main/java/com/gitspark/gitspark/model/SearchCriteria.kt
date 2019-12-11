package com.gitspark.gitspark.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW
import com.gitspark.gitspark.ui.main.search.REPOS

@Entity
data class SearchCriteria(
    var sort: String = "best-match",
    var order: String = "desc",
    var type: Int = REPOS,
    @PrimaryKey var q: String = "",
    var mainQuery: String = "",
    var createdOn: String = "",
    var language: String = "",
    var fromThisUser: String = "",
    var fullName: String = "",
    var location: String = "",
    var numFollowers: String = "",
    var numRepos: String = "",
    var numStars: String = "",
    var numForks: String = "",
    var updatedOn: String = "",
    var fileExtension: String = "",
    var fileSize: String = "",
    var repoFullName: String = "",
    var includeForked: Boolean = true,
    var isOpen: Boolean = true,
    var numComments: String = "",
    var labels: String = "",
    var timestamp: String = ""
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW
}
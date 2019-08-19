package com.gitspark.gitspark.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gitspark.gitspark.api.model.RepoLicense
import com.gitspark.gitspark.api.model.RepoPermissions

@Entity(tableName = "repos")
data class Repo(
    @PrimaryKey var repoId: Int = 0,
    var repoName: String = "",
    var fullName: String = "",
    @Embedded var owner: User = User(),
    var isPrivate: Boolean = false,
    var repoDescription: String = "",
    var isForked: Boolean = false,
    var repoLanguage: String = "",
    var numForks: Int = 0,
    var numStars: Int = 0,
    var numWatches: Int = 0,
    var defaultBranch: String = "",
    var numOpenIssues: Int = 0,
    var topics: List<String> = emptyList(),
    var hasIssues: Boolean = false,
    var hasProjects: Boolean = false,
    var hasWiki: Boolean = false,
    var hasPages: Boolean = false,
    var hasDownloads: Boolean = false,
    var archived: Boolean = false,
    var disabled: Boolean = false,
    var repoPushedAt: String = "",
    var repoCreatedAt: String = "",
    var repoUpdatedAt: String = "",
    @Embedded var permissions: RepoPermissions = RepoPermissions(
        admin = false,
        push = false,
        pull = false
    ),
    @Embedded var license: RepoLicense = RepoLicense("", "", "", ""),
    var timestamp: String = "",
    var starredAt: String = "",
    var starred: Boolean = false
)
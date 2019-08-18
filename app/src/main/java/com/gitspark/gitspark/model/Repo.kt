package com.gitspark.gitspark.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gitspark.gitspark.api.model.RepoLicense
import com.gitspark.gitspark.api.model.RepoPermissions

@Entity(tableName = "repos")
data class Repo(
    @PrimaryKey var repoId: Int,
    var repoName: String,
    var fullName: String,
    @Embedded var owner: User,
    var isPrivate: Boolean,
    var repoDescription: String,
    var isForked: Boolean,
    var repoLanguage: String,
    var numForks: Int,
    var numStars: Int,
    var numWatches: Int,
    var defaultBranch: String,
    var numOpenIssues: Int,
    var topics: List<String>,
    var hasIssues: Boolean,
    var hasProjects: Boolean,
    var hasWiki: Boolean,
    var hasPages: Boolean,
    var hasDownloads: Boolean,
    var archived: Boolean,
    var disabled: Boolean,
    var repoPushedAt: String,
    var repoCreatedAt: String,
    var repoUpdatedAt: String,
    @Embedded var permissions: RepoPermissions,
    @Embedded var license: RepoLicense,
    var timestamp: String = "",
    var starredAt: String = ""
)
package com.gitspark.gitspark.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gitspark.gitspark.api.model.RepoLicense
import com.gitspark.gitspark.api.model.RepoPermissions

@Entity
data class Repo(
    @PrimaryKey val id: Int,
    val name: String,
    val fullName: String,
    @Embedded val owner: User,
    val private: Boolean,
    val description: String,
    val fork: Boolean,
    val language: String,
    val numForks: Int,
    val stars: Int,
    val watches: Int,
    val defaultBranch: String,
    val numOpenIssues: Int,
    val topics: List<String>,
    val hasIssues: Boolean,
    val hasProjects: Boolean,
    val hasWiki: Boolean,
    val hasPages: Boolean,
    val hasDownloads: Boolean,
    val archived: Boolean,
    val disabled: Boolean,
    val pushedAt: String,
    val createdAt: String,
    val updatedAt: String,
    @Embedded val permissions: RepoPermissions,
    @Embedded val license: RepoLicense
)
package com.gitspark.gitspark.model

import androidx.room.Embedded
import androidx.room.Entity

@Entity(tableName = "user")
data class AuthUser(
    var numPrivateGists: Int = 0,
    var totalPrivateRepos: Int = 0,
    var ownedPrivateRepos: Int = 0,
    var diskUsage: Int = 0,
    var collaborators: Int = 0,
    @Embedded var plan: GitHubPlan = GitHubPlan()
) : User()
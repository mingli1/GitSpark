package com.gitspark.gitspark.ui.main.tab.profile

import com.gitspark.gitspark.model.Repo

val REPO1 = Repo(
    repoId = 0,
    repoName = "Apple",
    numStars = 30,
    repoPushedAt = "2019-08-19T14:46:25+0000",
    starredAt = "2012-08-14T14:46:25+0000"
)

val REPO2 = Repo(
    repoId = 1,
    repoName = "Orange",
    numStars = 14,
    repoPushedAt = "2014-08-19T14:46:25+0000",
    starredAt = "2016-08-14T14:46:25+0000",
    isForked = true
)

val REPO3 = Repo(
    repoId = 2,
    repoName = "Banana",
    numStars = 143,
    repoPushedAt = "2015-08-19T14:46:25+0000",
    starredAt = "2012-08-14T14:46:25+0000",
    isPrivate = true
)
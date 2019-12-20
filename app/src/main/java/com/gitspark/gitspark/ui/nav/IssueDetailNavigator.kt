package com.gitspark.gitspark.ui.nav

import com.gitspark.gitspark.model.Issue

interface IssueDetailNavigator {

    fun onIssueClicked(issue: Issue)
}
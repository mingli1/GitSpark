package com.gitspark.gitspark.ui.main.issues

import androidx.lifecycle.ViewModel
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class IssueSharedViewModel @Inject constructor() : ViewModel() {

    val editedIssue = SingleLiveEvent<Issue>()
}
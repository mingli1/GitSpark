package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

enum class IssueType {
    Created,
    Assigned,
    Mentioned
}

class IssuesListViewModel @Inject constructor() : BaseViewModel() {

    val viewState = MutableLiveData<IssuesListViewState>()
}
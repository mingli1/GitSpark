package com.gitspark.gitspark.ui.main.search

import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import javax.inject.Inject

class SearchViewModel @Inject constructor() : BaseViewModel() {

    val navigateToSearchFilter = SingleLiveAction()

    fun onSearchButtonClicked() = navigateToSearchFilter.call()
}
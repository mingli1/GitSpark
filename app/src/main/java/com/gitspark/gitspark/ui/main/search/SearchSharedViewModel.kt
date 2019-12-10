package com.gitspark.gitspark.ui.main.search

import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.SearchCriteria
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class SearchSharedViewModel @Inject constructor() : BaseViewModel() {

    val searchResults = SingleLiveEvent<Pair<SearchCriteria, Page<Pageable>>>()
}
package com.gitspark.gitspark.ui.main.search

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.SearchCriteria
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class SearchSharedViewModel @Inject constructor() : BaseViewModel() {

    val searchResults = MutableLiveData<Pair<SearchCriteria, Page<Pageable>>>()
}
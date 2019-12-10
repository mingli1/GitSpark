package com.gitspark.gitspark.ui.nav

import com.gitspark.gitspark.model.SearchCriteria

interface RecentSearchNavigator {

    fun onRecentSearchClicked(sc: SearchCriteria)
}
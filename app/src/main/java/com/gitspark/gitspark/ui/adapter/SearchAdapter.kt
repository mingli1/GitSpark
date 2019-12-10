package com.gitspark.gitspark.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginRight
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.SearchCriteria
import com.gitspark.gitspark.ui.main.search.*
import com.gitspark.gitspark.ui.nav.RecentSearchNavigator
import kotlinx.android.synthetic.main.search_view.view.*

class SearchAdapter(
    private val timeHelper: TimeHelper,
    private val navigator: RecentSearchNavigator
) : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.search_view

    override fun bind(item: Pageable, view: View) {
        if (item is SearchCriteria) {
            with (view) {
                search_type.setImageResource(when (item.type) {
                    REPOS -> R.drawable.ic_repo
                    USERS -> R.drawable.ic_person
                    CODE -> R.drawable.ic_code
                    COMMITS -> R.drawable.ic_commit
                    ISSUES -> R.drawable.ic_issue
                    else -> R.drawable.ic_pull_request
                })
                if (item.mainQuery.isEmpty()) {
                    main_query_field.text = item.q
                } else {
                    main_query_field.text = item.mainQuery
                    q_field.text = item.q
                }

                val searchDate = timeHelper.parse(item.timestamp).toInstant()
                val formatted = timeHelper.getRelativeTimeFormat(searchDate)
                search_date_field.text = context.getString(R.string.search_date, formatted)

                search_view.setOnClickListener { navigator.onRecentSearchClicked(item) }
                remove_button.setOnClickListener { navigator.onRemoveSearchClicked(item.q) }
            }
        }
    }
}
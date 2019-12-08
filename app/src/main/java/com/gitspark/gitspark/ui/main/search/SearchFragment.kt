package com.gitspark.gitspark.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.SearchCriteria
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment<SearchViewModel>(SearchViewModel::class.java) {

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[SearchSharedViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpListeners()
    }

    override fun observeViewModel() {
        viewModel.navigateToSearchFilter.observe(viewLifecycleOwner) { navigateToSearchFilterFragment() }
        sharedViewModel.searchResults.observe(viewLifecycleOwner) { onSearchResultsObtained(it) }
    }

    private fun setUpListeners() {
        search_button.setOnClickListener { viewModel.onSearchButtonClicked() }
    }

    private fun navigateToSearchFilterFragment() {
        findNavController().navigate(R.id.action_search_to_search_filter)
    }

    private fun onSearchResultsObtained(data: Pair<SearchCriteria, Page<Pageable>>) {
        search_button.text = data.first.q
        searches_header.text = when (data.first.type) {
            REPOS -> getString(R.string.repo_search_result, data.second.totalCount)
            USERS -> getString(R.string.user_search_result, data.second.totalCount)
            COMMITS -> getString(R.string.commit_search_result, data.second.totalCount)
            CODE -> getString(R.string.code_search_result, data.second.totalCount)
            ISSUES -> getString(R.string.issue_search_result, data.second.totalCount)
            else -> getString(R.string.pr_search_result, data.second.totalCount)
        }
        recent_searches_message.isVisible = false
    }
}
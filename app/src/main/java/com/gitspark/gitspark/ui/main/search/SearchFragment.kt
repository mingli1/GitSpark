package com.gitspark.gitspark.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.SearchCriteria
import com.gitspark.gitspark.ui.adapter.*
import com.gitspark.gitspark.ui.base.BaseFragment
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

internal const val BUNDLE_SEARCH_CRITERIA = "BUNDLE_SEARCH_CRITERIA"

class SearchFragment : BaseFragment<SearchViewModel>(SearchViewModel::class.java) {

    @Inject lateinit var colorHelper: LanguageColorHelper
    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var preferencesHelper: PreferencesHelper
    @Inject lateinit var scJsonAdapter: JsonAdapter<SearchCriteria>

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[SearchSharedViewModel::class.java]
    }

    private lateinit var recentLayoutManager: LinearLayoutManager
    private lateinit var searchesAdapter: SearchAdapter

    private lateinit var resultsLayoutManager: LinearLayoutManager
    private lateinit var paginationListener: NestedPaginationListener
    private lateinit var reposAdapter: ReposAdapter
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var filesAdapter: FilesAdapter
    private lateinit var commitsAdapter: CommitsAdapter
    private lateinit var issuesAdapter: IssuesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recentLayoutManager = LinearLayoutManager(context, VERTICAL, false)
        searchesAdapter = SearchAdapter(timeHelper, viewModel)

        resultsLayoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = NestedPaginationListener { viewModel.onScrolledToEnd() }
        reposAdapter = ReposAdapter(colorHelper, timeHelper, viewModel)
        usersAdapter = UsersAdapter(viewModel, preferencesHelper)
        filesAdapter = FilesAdapter()
        commitsAdapter = CommitsAdapter(timeHelper, true)
        issuesAdapter = IssuesAdapter(timeHelper)

        recent_searches.run {
            setHasFixedSize(true)
            layoutManager = recentLayoutManager
            if (adapter == null) adapter = searchesAdapter
        }

        search_results.run {
            setHasFixedSize(true)
            layoutManager = resultsLayoutManager
        }

        viewModel.retrieveRecentSearches()
        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        search_results.adapter = null
        recent_searches.adapter = null
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.navigateToSearchFilter.observe(viewLifecycleOwner) { navigateToSearchFilterFragment(it) }
        viewModel.recentSearchesMediator.observe(viewLifecycleOwner) { viewModel.onRecentSearchesRetrieved(it) }
        sharedViewModel.searchResults.observe(viewLifecycleOwner) { viewModel.onSearchResultsObtained(it) }
    }

    private fun setUpListeners() {
        search_button.setOnClickListener { viewModel.onSearchButtonClicked() }
        nested_scroll_view.setOnScrollChangeListener(paginationListener)
        search_results_clear_button.setOnClickListener { viewModel.onClearResultsButtonClicked() }
    }

    private fun navigateToSearchFilterFragment(searchCriteria: SearchCriteria?) {
        if (searchCriteria == null) {
            findNavController().navigate(R.id.action_search_to_search_filter)
        } else {
            val bundle = Bundle().apply { putString(BUNDLE_SEARCH_CRITERIA, scJsonAdapter.toJson(searchCriteria)) }
            findNavController().navigate(R.id.action_search_to_search_filter, bundle)
        }
    }

    private fun updateView(viewState: SearchViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading

            search_results.isVisible = currSearch != null
            search_button.text = currSearch?.q ?: getString(R.string.search_title_text)
            searches_header.text = if (currSearch == null) getString(R.string.recent_searches_header) else
                when (currSearch.type) {
                    REPOS -> getString(R.string.repo_search_result, resultsCount)
                    USERS -> getString(R.string.user_search_result, resultsCount)
                    COMMITS -> getString(R.string.commit_search_result, resultsCount)
                    CODE -> getString(R.string.code_search_result, resultsCount)
                    ISSUES -> getString(R.string.issue_search_result, resultsCount)
                    else -> getString(R.string.pr_search_result, resultsCount)
                }
            recent_searches.isVisible = currSearch == null
            searchesAdapter.setItems(recentSearches, true)
            recent_searches_message.isVisible = currSearch == null && recentSearches.isEmpty()
            search_results_clear_button.isVisible = currSearch != null

            if (updateAdapter) {
                val adapter = when (currSearch?.type) {
                    REPOS -> reposAdapter
                    USERS -> usersAdapter
                    CODE -> filesAdapter
                    COMMITS -> commitsAdapter
                    else -> issuesAdapter
                }
                search_results.adapter = adapter
                adapter.setItems(searchResults, isLastPage)

                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
        }
    }
}
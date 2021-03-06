package com.gitspark.gitspark.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.EventHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.adapter.ProfileFeedAdapter
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.main.issues.BUNDLE_ISSUE
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_profile_feed.*
import kotlinx.android.synthetic.main.fragment_profile_feed.swipe_refresh
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

class ProfileFeedFragment : TabFragment<ProfileFeedViewModel>(ProfileFeedViewModel::class.java) {

    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>
    private lateinit var eventHelper: EventHelper

    private lateinit var profileFeedAdapter: ProfileFeedAdapter
    private lateinit var paginationListener: PaginationListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_feed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        eventHelper = EventHelper(context!!)

        val layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, 30, swipe_refresh) {
            viewModel.onScrolledToEnd()
        }

        feed_list.layoutManager = layoutManager
        profileFeedAdapter = ProfileFeedAdapter(timeHelper, eventHelper, viewModel)
        if (feed_list.adapter == null) feed_list.adapter = profileFeedAdapter

        setupListeners()
    }

    override fun viewModelOnResume() = viewModel.onResume(arguments?.getString(BUNDLE_USERNAME))

    override fun onDestroyView() {
        super.onDestroyView()
        feed_list.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.pageViewState.observe(viewLifecycleOwner) { updateRecycler(it) }
        viewModel.navigateToRepo.observe(viewLifecycleOwner) { navigateToRepo(it) }
        viewModel.navigateToIssue.observe(viewLifecycleOwner) { navigateToIssue(it) }
    }

    private fun updateView(viewState: ProfileFeedViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing
            empty_text.text = getString(R.string.profile_feed_empty_text)
        }
    }

    private fun updateRecycler(viewState: PaginatedViewState<Event>) {
        with (viewState) {
            empty_text.isVisible = items.isEmpty()
            profileFeedAdapter.setItems(items, isLastPage)

            paginationListener.isLastPage = isLastPage
            paginationListener.loading = false
        }
    }

    private fun setupListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        feed_list.addOnScrollListener(paginationListener)
    }

    private fun navigateToRepo(fullName: String) {
        val bundle = Bundle().apply {
            putString(BUNDLE_REPO_FULLNAME, fullName)
        }
        findNavController().navigate(R.id.action_profile_fragment_to_repo_detail_fragment, bundle)
    }

    private fun navigateToIssue(pair: Pair<Issue, Boolean>) {
        val bundle = Bundle().apply {
            putString(BUNDLE_TITLE, "${pair.first.getRepoFullNameFromUrl()} #${pair.first.number}")
            putString(BUNDLE_ISSUE, issueJsonAdapter.toJson(pair.first))
        }
        findNavController().navigate(
            if (pair.second) R.id.action_profile_fragment_to_issue_detail_fragment else R.id.action_profile_fragment_to_pr_detail_fragment,
            bundle
        )
    }
}
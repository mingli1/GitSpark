package com.gitspark.gitspark.ui.main.home

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
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.ui.adapter.HomeFeedAdapter
import com.gitspark.gitspark.ui.adapter.NestedPaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

class HomeFragment : BaseFragment<HomeViewModel>(HomeViewModel::class.java) {

    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var eventHelper: EventHelper
    @Inject lateinit var prefsHelper: PreferencesHelper

    private lateinit var raLayoutManager: LinearLayoutManager
    private lateinit var aaLayoutManager: LinearLayoutManager
    private lateinit var raAdapter: HomeFeedAdapter
    private lateinit var aaAdapter: HomeFeedAdapter
    private lateinit var paginationListener: NestedPaginationListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        raLayoutManager = LinearLayoutManager(context, VERTICAL, false)
        aaLayoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = NestedPaginationListener { viewModel.onScrolledToEnd() }

        raAdapter = HomeFeedAdapter(timeHelper, eventHelper, recent = true)
        aaAdapter = HomeFeedAdapter(timeHelper, eventHelper)

        recent_events.run {
            setHasFixedSize(true)
            layoutManager = raLayoutManager
            if (adapter == null) adapter = raAdapter
        }
        all_events.run {
            setHasFixedSize(true)
            layoutManager = aaLayoutManager
            if (adapter == null) adapter = aaAdapter
        }

        swipe_refresh.setColorSchemeResources(R.color.colorAccent)

        viewModel.onStart()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recent_events.adapter = null
        all_events.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
    }

    private fun setupListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        nested_scroll_view.setOnScrollChangeListener(paginationListener)
        see_more_ra.setOnClickListener {
            with (activity as MainActivity) {
                bottom_navigation_view.selectedItemId = R.id.profile
                navigateToProfileFeedAction.call()
            }
        }
    }

    private fun updateView(viewState: HomeViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing

            recent_activity_label.isVisible = recentEvents.isNotEmpty()
            recent_events.isVisible = recentEvents.isNotEmpty()
            ra_divider.isVisible = recentEvents.isNotEmpty()
            see_more_ra.isVisible = recentEvents.size > NUM_RECENT_EVENTS
            see_more_divider.isVisible = recentEvents.size > NUM_RECENT_EVENTS
            raAdapter.setItems(recentEvents.take(NUM_RECENT_EVENTS), true)

            aa_empty_text.isVisible = allEvents.isEmpty()
            all_events.isVisible = allEvents.isNotEmpty()
            if (updateAdapter) {
                aaAdapter.setItems(allEvents, isLastPage)

                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
        }
    }
}
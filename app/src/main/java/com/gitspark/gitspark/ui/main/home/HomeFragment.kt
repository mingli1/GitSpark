package com.gitspark.gitspark.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.EventHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.ui.adapter.HomeFeedAdapter
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
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
    private lateinit var paginationListener: PaginationListener

    private var loadingItems = false
    private var lastPage = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        raLayoutManager = LinearLayoutManager(context, VERTICAL, false)
        aaLayoutManager = LinearLayoutManager(context, VERTICAL, false)
        // TODO: swiperefreshlayout
        paginationListener = PaginationListener(aaLayoutManager, 30) {
            viewModel.onScrolledToEnd()
        }

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
        nested_scroll_view.setOnScrollChangeListener { v: NestedScrollView?, _, scrollY: Int, _, oldScrollY: Int ->
            if (v?.getChildAt(v.childCount - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.childCount - 1)).measuredHeight - v.measuredHeight) &&
                        scrollY > oldScrollY && !loadingItems && !lastPage) {
                    viewModel.onScrolledToEnd()
                    loadingItems = true
                }
            }
        }
    }

    private fun updateView(viewState: HomeViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading && !refreshing

            raAdapter.setItems(recentEvents, true)
            if (updateAdapter) {
                aaAdapter.setItems(allEvents, isLastPage)

                lastPage = isLastPage
                loadingItems = false
            }
        }
    }
}
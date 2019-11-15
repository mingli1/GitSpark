package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.EventHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.ui.adapter.HomeFeedAdapter
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.profile.BUNDLE_USERNAME
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

const val BUNDLE_EVENT_LIST_TYPE = "BUNDLE_EVENT_LIST_TYPE"

class EventListFragment : BaseFragment<EventListViewModel>(EventListViewModel::class.java) {

    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var eventHelper: EventHelper
    @Inject lateinit var prefsHelper: PreferencesHelper

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var paginationListener: PaginationListener
    private lateinit var homeFeedAdapter: HomeFeedAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE) ?: "Public activity"
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, 30, null) {
            viewModel.onScrolledToEnd()
        }
        item_list.setHasFixedSize(true)
        item_list.layoutManager = layoutManager
        homeFeedAdapter = HomeFeedAdapter(timeHelper, eventHelper, viewModel, prefsHelper)
        if (item_list.adapter == null) item_list.adapter = homeFeedAdapter

        item_list.addOnScrollListener(paginationListener)

        val type = arguments?.getSerializable(BUNDLE_EVENT_LIST_TYPE) as EventListType? ?: EventListType.None
        val args = arguments?.getString(BUNDLE_ARGUMENTS) ?: ""
        viewModel.onResume(type, args)

        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.navigateToProfileAction.observe(viewLifecycleOwner) { navigateToProfileFragment(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        item_list.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun updateView(viewState: ListViewState<Event>) {
        with (viewState) {
            if (updateAdapter) {
                homeFeedAdapter.setItems(list, isLastPage)

                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing
        }
    }

    private fun navigateToProfileFragment(username: String) {
        val data = Bundle().apply { putString(BUNDLE_USERNAME, username) }
        findNavController().navigate(R.id.action_event_list_to_profile, data)
    }
}
package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.EventHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.ui.adapter.HomeFeedAdapter
import com.gitspark.gitspark.ui.main.profile.BUNDLE_USERNAME
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

const val BUNDLE_EVENT_LIST_TYPE = "BUNDLE_EVENT_LIST_TYPE"

class EventListFragment : ListFragment<Event, EventListViewModel>(EventListViewModel::class.java, 30) {

    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var eventHelper: EventHelper
    @Inject lateinit var prefsHelper: PreferencesHelper
    private lateinit var homeFeedAdapter: HomeFeedAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeFeedAdapter = HomeFeedAdapter(timeHelper, eventHelper, viewModel, prefsHelper)
        if (item_list.adapter == null) item_list.adapter = homeFeedAdapter

        val type = arguments?.getSerializable(BUNDLE_EVENT_LIST_TYPE) as EventListType? ?: EventListType.None
        val args = arguments?.getString(BUNDLE_ARGUMENTS) ?: ""
        viewModel.onStart(type, args)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.navigateToProfileAction.observe(viewLifecycleOwner) { navigateToProfileFragment(it) }
    }

    override fun updateView(viewState: ListViewState<Event>) {
        super.updateView(viewState)
        with (viewState) {
            empty_text.text = getString(R.string.event_empty_text)
            if (updateAdapter) homeFeedAdapter.setItems(list, isLastPage)
        }
    }

    private fun navigateToProfileFragment(username: String) {
        val data = Bundle().apply { putString(BUNDLE_USERNAME, username) }
        findNavController().navigate(R.id.action_event_list_to_profile, data)
    }
}
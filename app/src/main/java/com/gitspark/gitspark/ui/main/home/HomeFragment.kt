package com.gitspark.gitspark.ui.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.observeOnce
import com.gitspark.gitspark.helper.EventHelper
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.ui.adapter.HomeFeedAdapter
import com.gitspark.gitspark.ui.adapter.NestedPaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.dialog.ConfirmDialog
import com.gitspark.gitspark.ui.dialog.ConfirmDialogCallback
import com.gitspark.gitspark.ui.login.LoginActivity
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.profile.BUNDLE_USERNAME
import com.gitspark.gitspark.ui.main.shared.BUNDLE_EVENT_LIST_TYPE
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.gitspark.gitspark.ui.main.shared.EventListType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import kotlinx.android.synthetic.main.home_drawer_header.view.*
import javax.inject.Inject

class HomeFragment : BaseFragment<HomeViewModel>(HomeViewModel::class.java), ConfirmDialogCallback {

    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var eventHelper: EventHelper
    @Inject lateinit var prefsHelper: PreferencesHelper

    private lateinit var raAdapter: HomeFeedAdapter
    private lateinit var aaAdapter: HomeFeedAdapter
    private lateinit var paginationListener: NestedPaginationListener

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        setHasOptionsMenu(true)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            activity,
            drawer_layout,
            R.string.drawer_open,
            R.string.drawer_close
        ).apply {
            isDrawerIndicatorEnabled = true
            drawerArrowDrawable.color = context!!.getColor(R.color.colorWhite)
        }

        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        paginationListener = NestedPaginationListener { viewModel.onScrolledToEnd() }

        raAdapter = HomeFeedAdapter(timeHelper, eventHelper, viewModel, prefsHelper, recent = true)
        aaAdapter = HomeFeedAdapter(timeHelper, eventHelper, viewModel, prefsHelper)

        recent_events.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            if (adapter == null) adapter = raAdapter
        }
        all_events.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            if (adapter == null) adapter = aaAdapter
        }

        viewModel.onStart()
        setupListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
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
        viewModel.pageViewState.observe(viewLifecycleOwner) { updateRecycler(it) }
        viewModel.userMediator.observeOnce(viewLifecycleOwner) { viewModel.onUserDataLoaded(it) }
        viewModel.logoutConfirmationAction.observe(viewLifecycleOwner) { showLogoutConfirmationDialog() }
        viewModel.navigateToLoginAction.observe(viewLifecycleOwner) { navigateToLoginActivity() }
        viewModel.navigateToUserProfile.observe(viewLifecycleOwner) { navigateToUserProfile(it) }
        viewModel.navigateToEventList.observe(viewLifecycleOwner) { navigateToEventList(it) }
    }

    override fun onPositiveClicked() = viewModel.onLogoutConfirmed()

    override fun onNegativeClicked() {}

    private fun setupListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        nested_scroll_view.setOnScrollChangeListener(paginationListener)
        see_more_ra.setOnClickListener {
            with (activity as MainActivity) {
                bottom_navigation_view.selectedItemId = R.id.profile
                navigateToProfileFeedAction.call()
            }
        }
        nav_view.setNavigationItemSelectedListener {
            onMenuItemSelected(it)
            true
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

            if (nav_view.headerCount > 0) {
                nav_view.getHeaderView(0).run {
                    full_name_field.text = fullName
                    username_field.text = username
                    if (avatarUrl.isNotEmpty()) profile_icon.loadImage(avatarUrl)
                }
            }

            version_label.text = getString(R.string.version_label, BuildConfig.BUILD_TYPE, BuildConfig.VERSION_NAME)
        }
    }

    private fun updateRecycler(viewState: PaginatedViewState<Event>) {
        with (viewState) {
            aa_empty_text.isVisible = items.isEmpty()
            all_events.isVisible = items.isNotEmpty()

            aaAdapter.setItems(items, isLastPage)

            paginationListener.isLastPage = isLastPage
            paginationListener.loading = false
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun onMenuItemSelected(item: MenuItem) {
        drawer_layout.closeDrawer(Gravity.LEFT)
        when (item.itemId) {
            R.id.logout -> viewModel.onLogoutClicked()
            R.id.public_events -> viewModel.onPublicEventsClicked()
        }
    }

    private fun showLogoutConfirmationDialog() {
        ConfirmDialog.newInstance(
            getString(R.string.logout_label),
            getString(R.string.logout_confirmation)
        ).show(childFragmentManager, null)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }

    private fun navigateToUserProfile(username: String) {
        val data = Bundle().apply { putString(BUNDLE_USERNAME, username) }
        findNavController().navigate(R.id.action_home_fragment_to_profile, data)
    }

    private fun navigateToEventList(type: EventListType) {
        val data = Bundle().apply {
            putString(BUNDLE_TITLE, getString(R.string.public_events_title))
            putSerializable(BUNDLE_EVENT_LIST_TYPE, type)
        }
        findNavController().navigate(R.id.action_home_fragment_to_event_list, data)
    }
}
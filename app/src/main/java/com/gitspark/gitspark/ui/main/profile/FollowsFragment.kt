package com.gitspark.gitspark.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.USER_PER_PAGE
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.adapter.FollowsAdapter
import kotlinx.android.synthetic.main.fragment_follows.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

class FollowsFragment : TabFragment<FollowsViewModel>(FollowsViewModel::class.java) {

    private lateinit var paginationListener: PaginationListener
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var followersAdapter: FollowsAdapter
    private lateinit var followingAdapter: FollowsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_follows, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val isAuthUser = arguments == null
        followersAdapter = FollowsAdapter(viewModel).apply { authUser = isAuthUser }
        followingAdapter = FollowsAdapter(viewModel).apply { authUser = isAuthUser }

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, USER_PER_PAGE, swipe_refresh) {
            viewModel.onScrolledToEnd()
        }

        follows_list.setHasFixedSize(true)
        follows_list.layoutManager = layoutManager
        if (follows_list.adapter == null) follows_list.adapter = followersAdapter

        setUpListeners()
    }

    override fun viewModelOnResume() =
        viewModel.onResume(
            arguments?.getString(BUNDLE_USERNAME),
            (parentFragment as UserDataCallback).getData()
        )

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.userMediator.observe(viewLifecycleOwner) { viewModel.onUserDataRetrieved(it) }
        viewModel.navigateToProfile.observe(viewLifecycleOwner) { navigateToProfile(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        follows_list.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    fun onNavigatedTo(followState: FollowState) = viewModel.navigateToState(followState)

    private fun updateView(viewState: FollowsViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading
            swipe_refresh.setRefreshing(refreshing)

            num_follows_field.text = when (followState) {
                FollowState.Followers -> getString(R.string.followers_text, totalFollowers)
                FollowState.Following -> getString(R.string.following_text, totalFollowing)
            }
            follows_switch_button.text = when (followState) {
                FollowState.Following -> getString(R.string.followers_button_text)
                FollowState.Followers -> getString(R.string.following_button_text)
            }

            if (updateAdapter) {
                val data = when (followState) {
                    FollowState.Followers -> followers
                    FollowState.Following -> following
                }
                follows_list.adapter = null
                follows_list.adapter = getAdapter(followState)
                getAdapter(followState).setItems(data, isLastPage)

                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
        }
    }

    private fun setUpListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        follows_switch_button.setOnClickListener { viewModel.onFollowsSwitchClicked() }
        follows_list.addOnScrollListener(paginationListener)
    }

    private fun getAdapter(state: FollowState): FollowsAdapter {
        return when (state) {
            FollowState.Followers -> followersAdapter
            FollowState.Following -> followingAdapter
        }
    }

    private fun navigateToProfile(username: String) {
        val bundle = Bundle().apply { putString(BUNDLE_USERNAME, username) }
        findNavController().navigate(R.id.action_profile_fragment_to_profile_fragment, bundle)
    }
}
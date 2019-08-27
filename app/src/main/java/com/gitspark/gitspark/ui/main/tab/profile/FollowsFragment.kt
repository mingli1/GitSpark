package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.USER_PER_PAGE
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.adapter.UsersAdapter
import kotlinx.android.synthetic.main.fragment_follows.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

class FollowsFragment : TabFragment<FollowsViewModel>(FollowsViewModel::class.java) {

    private lateinit var paginationListener: PaginationListener
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var followersAdapter: UsersAdapter
    private lateinit var followingAdapter: UsersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_follows, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        followersAdapter = UsersAdapter()
        followingAdapter = UsersAdapter()

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, USER_PER_PAGE, swipe_refresh) {
            viewModel.onScrolledToEnd()
        }

        follows_list.setHasFixedSize(true)
        follows_list.layoutManager = layoutManager
        if (follows_list.adapter == null) follows_list.adapter = followersAdapter

        setUpListeners()
    }

    override fun viewModelOnResume() = viewModel.onResume()

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.userMediator.observe(viewLifecycleOwner) { viewModel.onUserDataRetrieved(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroyView()
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
                when (currPage) {
                    1 -> {
                        follows_list.adapter = null
                        follows_list.adapter = getAdapter(followState)
                        getAdapter(followState).addInitialUsers(data, isLastPage, followState)
                    }
                    else -> getAdapter(followState).addItemsOnLoadingComplete(data, isLastPage)
                }
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

    private fun getAdapter(state: FollowState): UsersAdapter {
        return when (state) {
            FollowState.Followers -> followersAdapter
            FollowState.Following -> followingAdapter
        }
    }
}
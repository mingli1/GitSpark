package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var followersAdapter: UsersAdapter
    private lateinit var followersManager: LinearLayoutManager
    private lateinit var followersListener: PaginationListener

    private lateinit var followingAdapter: UsersAdapter
    private lateinit var followingManager: LinearLayoutManager
    private lateinit var followingListener: PaginationListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_follows, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        followersAdapter = UsersAdapter()
        followersManager = LinearLayoutManager(context, VERTICAL, false)
        followingAdapter = UsersAdapter()
        followingManager = LinearLayoutManager(context, VERTICAL, false)

        followersListener = PaginationListener(followersManager, USER_PER_PAGE, swipe_refresh) {
            viewModel.onScrolledToEnd()
        }
        followingListener = PaginationListener(followingManager, USER_PER_PAGE, swipe_refresh) {
            viewModel.onScrolledToEnd()
        }

        followers_list.setHasFixedSize(true)
        followers_list.layoutManager = followersManager
        if (followers_list.adapter == null) followers_list.adapter = followersAdapter

        following_list.setHasFixedSize(true)
        following_list.layoutManager = followingManager
        if (following_list.adapter == null) following_list.adapter = followingAdapter

        setUpListeners()
    }

    override fun viewModelOnResume() = viewModel.onResume()

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
    }

    private fun updateView(viewState: FollowsViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading
            getListener(followState).isLastPage = isLastPage
            follows_switch_button.text = when (followState) {
                FollowState.Following -> getString(R.string.followers_button_text)
                FollowState.Followers -> getString(R.string.following_button_text)
            }
            followers_list.isVisible = followState == FollowState.Followers
            following_list.isVisible = followState == FollowState.Following

            if (updateAdapter) {
                when (currPage) {
                    1 -> {
                        getRecyclerView(followState).adapter = null
                        getRecyclerView(followState).adapter = getAdapter(followState)
                        getAdapter(followState).addInitialUsers(data, isLastPage)
                    }
                    else -> getAdapter(followState).addUsersOnLoadingComplete(data, isLastPage)
                }
            }
        }
    }

    private fun setUpListeners() {
        follows_switch_button.setOnClickListener { viewModel.onFollowsSwitchClicked() }
        followers_list.addOnScrollListener(followersListener)
        following_list.addOnScrollListener(followingListener)
    }

    private fun getAdapter(state: FollowState): UsersAdapter {
        return when (state) {
            FollowState.Followers -> followersAdapter
            FollowState.Following -> followingAdapter
        }
    }

    private fun getRecyclerView(state: FollowState): RecyclerView {
        return when (state) {
            FollowState.Followers -> followers_list
            FollowState.Following -> following_list
        }
    }

    private fun getListener(state: FollowState): PaginationListener {
        return when (state) {
            FollowState.Followers -> followersListener
            FollowState.Following -> followingListener
        }
    }
}
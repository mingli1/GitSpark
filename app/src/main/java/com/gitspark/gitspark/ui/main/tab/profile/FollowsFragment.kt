package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.adapter.UsersAdapter
import kotlinx.android.synthetic.main.fragment_follows.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

class FollowsFragment : TabFragment<FollowsViewModel>(FollowsViewModel::class.java) {

    private lateinit var usersAdapter: UsersAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_follows, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        usersAdapter = UsersAdapter()
        layoutManager = LinearLayoutManager(context, VERTICAL, false)

        users_list.setHasFixedSize(true)
        users_list.layoutManager = layoutManager
        if (users_list.adapter == null) users_list.adapter = usersAdapter

        setUpListeners()
    }

    override fun viewModelOnResume() = viewModel.onResume()

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
    }

    private fun updateView(viewState: FollowsViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading
            if (updateAdapter) {
                when (currPage) {
                    1 -> usersAdapter.addInitialUsers(data, isLastPage)
                    else -> usersAdapter.addUsersOnLoadingComplete(data, isLastPage)
                }
            }
        }
    }

    private fun setUpListeners() {
        users_list.addOnScrollListener(PaginationListener(layoutManager) {
            viewModel.onScrolledToEnd()
        })
    }
}
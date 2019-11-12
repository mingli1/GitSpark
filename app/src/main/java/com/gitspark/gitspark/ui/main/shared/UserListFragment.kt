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
import com.gitspark.gitspark.api.service.USER_PER_PAGE
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.ui.adapter.UsersAdapter
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.profile.BUNDLE_USERNAME
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

const val BUNDLE_TITLE = "BUNDLE_TITLE"
const val BUNDLE_USER_LIST_TYPE = "BUNDLE_USER_LIST_TYPE"
const val BUNDLE_ARGUMENTS = "BUNDLE_ARGUMENTS"

class UserListFragment : BaseFragment<UserListViewModel>(UserListViewModel::class.java) {

    @Inject lateinit var prefsHelper: PreferencesHelper

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var paginationListener: PaginationListener
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE) ?: "Users"
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, USER_PER_PAGE, null) {
            viewModel.onScrolledToEnd()
        }
        item_list.setHasFixedSize(true)
        item_list.layoutManager = layoutManager
        usersAdapter = UsersAdapter(viewModel, prefsHelper)
        if (item_list.adapter == null) item_list.adapter = usersAdapter

        item_list.addOnScrollListener(paginationListener)

        val type = arguments?.getSerializable(BUNDLE_USER_LIST_TYPE) as UserListType? ?: UserListType.None
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

    private fun updateView(viewState: UserListViewState) {
        with (viewState) {
            if (updateAdapter) {
                usersAdapter.setItems(users, isLastPage)

                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing
        }
    }

    private fun navigateToProfileFragment(username: String) {
        val data = Bundle().apply { putString(BUNDLE_USERNAME, username) }
        findNavController().navigate(R.id.action_user_list_fragment_to_profile_fragment, data)
    }
}
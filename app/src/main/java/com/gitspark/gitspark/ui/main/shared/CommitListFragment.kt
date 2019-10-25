package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.COMMITS_PER_PAGE
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.ui.adapter.CommitsAdapter
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

class CommitListFragment : BaseFragment<CommitListViewModel>(CommitListViewModel::class.java) {

    @Inject lateinit var timeHelper: TimeHelper
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var paginationListener: PaginationListener
    private lateinit var commitsAdapter: CommitsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE) ?: "Commits"
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, COMMITS_PER_PAGE, null) {
            viewModel.onScrolledToEnd()
        }
        item_list.setHasFixedSize(true)
        item_list.layoutManager = layoutManager
        commitsAdapter = CommitsAdapter(timeHelper)
        if (item_list.adapter == null) item_list.adapter = commitsAdapter

        item_list.addOnScrollListener(paginationListener)

        val args = arguments?.getString(BUNDLE_ARGUMENTS) ?: ""
        viewModel.onResume(args)
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        item_list.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun updateView(viewState: CommitListViewState) {
        with (viewState) {
            if (updateAdapter) {
                commitsAdapter.setItems(commits, isLastPage)

                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
            loading_indicator.isVisible = loading
        }
    }
}
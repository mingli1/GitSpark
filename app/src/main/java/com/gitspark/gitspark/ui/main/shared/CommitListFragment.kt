package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.COMMITS_PER_PAGE
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Commit
import com.gitspark.gitspark.ui.adapter.CommitsAdapter
import com.gitspark.gitspark.ui.base.PaginatedViewState
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

const val BUNDLE_COMMIT_LIST_TYPE = "BUNDLE_COMMIT_LIST_TYPE"

class CommitListFragment : ListFragment<Commit, CommitListViewModel>(CommitListViewModel::class.java, COMMITS_PER_PAGE) {

    @Inject lateinit var timeHelper: TimeHelper
    private lateinit var commitsAdapter: CommitsAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        commitsAdapter = CommitsAdapter(timeHelper)
        if (item_list.adapter == null) item_list.adapter = commitsAdapter

        val type = arguments?.getSerializable(BUNDLE_COMMIT_LIST_TYPE) as CommitListType? ?: CommitListType.Repo
        val args = arguments?.getString(BUNDLE_ARGUMENTS) ?: ""
        viewModel.onStart(type, args)
    }

    override fun updateView(viewState: ListViewState) {
        super.updateView(viewState)
        empty_text.text = getString(R.string.commit_empty_text)
    }

    override fun updateRecycler(viewState: PaginatedViewState<Commit>) {
        super.updateRecycler(viewState)
        commitsAdapter.setItems(viewState.items, viewState.isLastPage)
    }
}
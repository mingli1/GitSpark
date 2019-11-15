package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.COMMITS_PER_PAGE
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Commit
import com.gitspark.gitspark.ui.adapter.CommitsAdapter
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

class CommitListFragment : ListFragment<Commit, CommitListViewModel>(CommitListViewModel::class.java, COMMITS_PER_PAGE) {

    @Inject lateinit var timeHelper: TimeHelper
    private lateinit var commitsAdapter: CommitsAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        commitsAdapter = CommitsAdapter(timeHelper)
        if (item_list.adapter == null) item_list.adapter = commitsAdapter

        val args = arguments?.getString(BUNDLE_ARGUMENTS) ?: ""
        viewModel.onStart(args)
    }

    override fun updateView(viewState: ListViewState<Commit>) {
        super.updateView(viewState)
        with (viewState) {
            empty_text.text = getString(R.string.commit_empty_text)
            if (updateAdapter) commitsAdapter.setItems(list, isLastPage)
        }
    }
}
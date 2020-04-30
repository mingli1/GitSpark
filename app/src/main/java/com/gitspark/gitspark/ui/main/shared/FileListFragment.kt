package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.FILES_PER_PAGE
import com.gitspark.gitspark.model.PullRequestFile
import com.gitspark.gitspark.ui.adapter.PullRequestFilesAdapter
import com.gitspark.gitspark.ui.base.PaginatedViewState
import kotlinx.android.synthetic.main.fragment_list.*

class FileListFragment : ListFragment<PullRequestFile, FileListViewModel>(FileListViewModel::class.java, FILES_PER_PAGE) {

    private lateinit var filesAdapter: PullRequestFilesAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        filesAdapter = PullRequestFilesAdapter()
        if (item_list.adapter == null) item_list.adapter = filesAdapter

        val args = arguments?.getString(BUNDLE_ARGUMENTS) ?: ""
        viewModel.onStart(args)
    }

    override fun updateView(viewState: ListViewState) {
        super.updateView(viewState)
        empty_text.text = getString(R.string.pr_file_empty_text)
    }

    override fun updateRecycler(viewState: PaginatedViewState<PullRequestFile>) {
        super.updateRecycler(viewState)
        filesAdapter.setItems(viewState.items, viewState.isLastPage)
    }
}
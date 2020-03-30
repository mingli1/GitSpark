package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.REPO_PER_PAGE
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.ui.adapter.ReposAdapter
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

const val BUNDLE_REPO_LIST_TYPE = "BUNDLE_REPO_LIST_TYPE"

class RepoListFragment : ListFragment<Repo, RepoListViewModel>(RepoListViewModel::class.java, REPO_PER_PAGE) {

    @Inject lateinit var colorHelper: LanguageColorHelper
    @Inject lateinit var repoJsonAdapter: JsonAdapter<Repo>
    @Inject lateinit var timeHelper: TimeHelper
    private lateinit var reposAdapter: ReposAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        reposAdapter = ReposAdapter(colorHelper, timeHelper, viewModel)
        if (item_list.adapter == null) item_list.adapter = reposAdapter

        val type = arguments?.getSerializable(BUNDLE_REPO_LIST_TYPE) as RepoListType? ?: RepoListType.None
        val args = arguments?.getString(BUNDLE_ARGUMENTS) ?: ""
        viewModel.onStart(type, args)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.navigateToRepoDetailAction.observe(viewLifecycleOwner) { navigateToRepoDetailFragment(it) }
    }

    override fun updateView(viewState: ListViewState) {
        super.updateView(viewState)
        empty_text.text = getString(R.string.repo_empty_text)
    }

    override fun updateRecycler(viewState: PaginatedViewState<Repo>) {
        super.updateRecycler(viewState)
        reposAdapter.setItems(viewState.items, viewState.isLastPage)
    }

    private fun navigateToRepoDetailFragment(repo: Repo) {
        val bundle = Bundle().apply {
            putString(BUNDLE_REPO, repoJsonAdapter.toJson(repo))
        }
        findNavController().navigate(R.id.action_repo_list_fragment_to_repo_detail_fragment, bundle)
    }
}
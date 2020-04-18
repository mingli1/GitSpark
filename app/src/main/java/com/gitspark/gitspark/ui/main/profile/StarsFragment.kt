package com.gitspark.gitspark.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.REPO_PER_PAGE
import com.gitspark.gitspark.extension.formatLarge
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.adapter.ReposAdapter
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.main.repo.RepoDetailSharedViewModel
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_repos.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

private const val LABEL = "Starred Repositories"

class StarsFragment : TabFragment<StarsViewModel>(StarsViewModel::class.java) {

    @Inject lateinit var colorHelper: LanguageColorHelper
    @Inject lateinit var repoJsonAdapter: JsonAdapter<Repo>
    @Inject lateinit var timeHelper: TimeHelper
    private lateinit var reposAdapter: ReposAdapter
    private lateinit var paginationListener: PaginationListener

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[RepoDetailSharedViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, REPO_PER_PAGE, swipe_refresh) {
            viewModel.onScrolledToEnd()
        }

        repos_list.layoutManager = layoutManager
        reposAdapter = ReposAdapter(colorHelper, timeHelper, viewModel)
        if (repos_list.adapter == null) repos_list.adapter = reposAdapter

        setupListeners()
    }

    override fun viewModelOnResume() = viewModel.onResume(arguments?.getString(BUNDLE_USERNAME))

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.pageViewState.observe(viewLifecycleOwner) { updateRecycler(it) }
        viewModel.navigateToRepoDetailAction.observe(viewLifecycleOwner) {
            navigateToRepoDetailFragment(it)
        }
        sharedViewModel.repoData.observe(viewLifecycleOwner) { viewModel.onUpdatedRepoData(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        repos_list.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun updateView(viewState: StarsViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing
            num_repos_field.text = getString(R.string.num_repos_text, LABEL, totalStarred.formatLarge())

            empty_text.text = getString(R.string.repos_empty_text)
        }
    }

    private fun updateRecycler(viewState: PaginatedViewState<Repo>) {
        with (viewState) {
            empty_text.isVisible = items.isEmpty()
            reposAdapter.setItems(items, isLastPage)

            paginationListener.isLastPage = isLastPage
            paginationListener.loading = false
        }
    }

    private fun setupListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        repos_list.addOnScrollListener(paginationListener)
    }

    private fun navigateToRepoDetailFragment(repo: Repo) {
        val bundle = Bundle().apply {
            putString(BUNDLE_REPO, repoJsonAdapter.toJson(repo))
        }
        findNavController().navigate(R.id.action_profile_fragment_to_repo_detail_fragment, bundle)
    }
}
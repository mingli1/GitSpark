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
import com.gitspark.gitspark.api.service.REPO_PER_PAGE
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.adapter.ReposAdapter
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

const val BUNDLE_REPO_LIST_TYPE = "BUNDLE_REPO_LIST_TYPE"

class RepoListFragment : BaseFragment<RepoListViewModel>(RepoListViewModel::class.java) {

    @Inject lateinit var colorHelper: LanguageColorHelper
    @Inject lateinit var repoJsonAdapter: JsonAdapter<Repo>
    @Inject lateinit var timeHelper: TimeHelper
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var paginationListener: PaginationListener
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE) ?: "Repos"
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, REPO_PER_PAGE, null) {
            viewModel.onScrolledToEnd()
        }
        item_list.setHasFixedSize(true)
        item_list.layoutManager = layoutManager
        reposAdapter = ReposAdapter(colorHelper, timeHelper, viewModel)
        if (item_list.adapter == null) item_list.adapter = reposAdapter

        item_list.addOnScrollListener(paginationListener)

        val type = arguments?.getSerializable(BUNDLE_REPO_LIST_TYPE) as RepoListType? ?: RepoListType.None
        val args = arguments?.getString(BUNDLE_ARGUMENTS) ?: ""
        viewModel.onResume(type, args)

        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.navigateToRepoDetailAction.observe(viewLifecycleOwner) { navigateToRepoDetailFragment(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        item_list.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun updateView(viewState: ListViewState<Repo>) {
        with (viewState) {
            if (updateAdapter) {
                reposAdapter.setItems(list, isLastPage)

                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing
        }
    }

    private fun navigateToRepoDetailFragment(repo: Repo) {
        val bundle = Bundle().apply {
            putString(BUNDLE_REPO, repoJsonAdapter.toJson(repo))
        }
        findNavController().navigate(R.id.action_repo_list_fragment_to_repo_detail_fragment, bundle)
    }
}
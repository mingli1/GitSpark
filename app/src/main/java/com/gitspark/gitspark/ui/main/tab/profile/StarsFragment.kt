package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.REPO_PER_PAGE
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.ui.adapter.BUNDLE_REPO_FULLNAME
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.adapter.ReposAdapter
import kotlinx.android.synthetic.main.fragment_repos.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

private const val LABEL = "Starred"

class StarsFragment : TabFragment<StarsViewModel>(StarsViewModel::class.java) {

    @Inject lateinit var colorHelper: LanguageColorHelper
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var reposAdapter: ReposAdapter
    private lateinit var paginationListener: PaginationListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, REPO_PER_PAGE, swipe_refresh) {
            viewModel.onScrolledToEnd()
        }

        repos_list.setHasFixedSize(true)
        repos_list.layoutManager = layoutManager
        reposAdapter = ReposAdapter(colorHelper, viewModel)
        if (repos_list.adapter == null) repos_list.adapter = reposAdapter

        setupListeners()
    }

    override fun viewModelOnResume() = viewModel.onResume(arguments?.getString(BUNDLE_USERNAME))

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.navigateToRepoDetailAction.observe(viewLifecycleOwner) {
            navigateToRepoDetailFragment(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroyView()
    }

    private fun updateView(viewState: StarsViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading
            swipe_refresh.setRefreshing(refreshing)
            num_repos_field.text = getString(R.string.num_repos_text, LABEL, totalStarred)

            if (updateAdapter) {
                if (isFirstPage) {
                    repos_list.adapter = null
                    repos_list.adapter = reposAdapter
                    reposAdapter.addInitialItems(repos, isLastPage)
                }
                else reposAdapter.addItemsOnLoadingComplete(repos, isLastPage)

                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
        }
    }

    private fun setupListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        repos_list.addOnScrollListener(paginationListener)
    }

    private fun navigateToRepoDetailFragment(fullName: String) {
        val bundle = Bundle().apply { putString(BUNDLE_REPO_FULLNAME, fullName) }
        findNavController().navigate(R.id.action_profile_fragment_to_repo_detail_fragment, bundle)
    }
}
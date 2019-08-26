package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.*
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.ui.adapter.ReposAdapter
import kotlinx.android.synthetic.main.fragment_repos.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

class ReposFragment : TabFragment<ReposViewModel>(ReposViewModel::class.java) {

    @Inject lateinit var colorHelper: LanguageColorHelper
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        repos_list.setHasFixedSize(true)
        repos_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        reposAdapter = ReposAdapter(colorHelper)
        if (repos_list.adapter == null) repos_list.adapter = reposAdapter

        setupListeners()
    }

    override fun viewModelOnResume() = viewModel.onResume()

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.userMediator.observe(viewLifecycleOwner) { viewModel.onUserDataRetrieved(it) }
    }

    private fun updateView(viewState: ReposViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading
            reposAdapter.setData(repos)
            swipe_refresh.setRefreshing(refreshing)
            num_repos_field.text = getString(R.string.num_repos_text, totalRepos)
        }
    }

    private fun setupListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
    }
}
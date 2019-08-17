package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.LanguageColorHelper
import kotlinx.android.synthetic.main.fragment_repos.*
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
    }

    override fun viewModelOnResume() = viewModel.onResume()

    override fun observeViewModel() {
        viewModel.repoDataMediator.observe(viewLifecycleOwner) { viewModel.onCachedRepoDataRetrieved(it) }
        viewModel.repoDataAction.observe(viewLifecycleOwner) { reposAdapter.setData(it) }
    }
}
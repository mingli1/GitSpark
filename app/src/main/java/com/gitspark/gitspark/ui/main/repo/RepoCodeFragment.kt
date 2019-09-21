package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.model.Branch
import com.gitspark.gitspark.ui.adapter.RepoContentAdapter
import com.gitspark.gitspark.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_repo_code.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

class RepoCodeFragment : BaseFragment<RepoCodeViewModel>(RepoCodeViewModel::class.java) {

    private lateinit var branchSpinnerAdapter: ArrayAdapter<String>
    private lateinit var repoContentAdapter: RepoContentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repo_code, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dir_list.setHasFixedSize(true)
        dir_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        repoContentAdapter = RepoContentAdapter(viewModel)
        if (dir_list.adapter == null) dir_list.adapter = repoContentAdapter

        viewModel.currRepo = (parentFragment as RepoDataCallback).getData()
    }

    fun notifyBranchDataRetrieved(branches: List<Branch>) {
        val branchNames = branches.map { it.name }
        branchSpinnerAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item,
            branchNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        branch_spinner.adapter = branchSpinnerAdapter

        // fetch for default branch initially
        viewModel.fetchDirectory(branchName = branchNames[0])
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateViewState(it) }
    }

    private fun updateViewState(viewState: RepoCodeViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading
            if (updateContent) repoContentAdapter.setContent(contentData)
            path_label.text = path
        }
    }
}
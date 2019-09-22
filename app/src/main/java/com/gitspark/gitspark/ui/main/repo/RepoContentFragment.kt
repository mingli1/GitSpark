package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.model.Branch
import com.gitspark.gitspark.ui.adapter.BUNDLE_FILE_CONTENT
import com.gitspark.gitspark.ui.adapter.BUNDLE_FILE_NAME
import com.gitspark.gitspark.ui.adapter.RepoContentAdapter
import com.gitspark.gitspark.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_repo_content.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

class RepoContentFragment : BaseFragment<RepoContentViewModel>(RepoContentViewModel::class.java) {

    private lateinit var branchSpinnerAdapter: ArrayAdapter<String>
    private lateinit var repoContentAdapter: RepoContentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repo_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dir_list.setHasFixedSize(true)
        dir_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        repoContentAdapter = RepoContentAdapter(viewModel)
        if (dir_list.adapter == null) dir_list.adapter = repoContentAdapter

        viewModel.currRepo = (parentFragment as RepoDataCallback).getData()
        viewModel.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dir_list.adapter = null
        viewModel.onDestroyView()
    }

    fun notifyBranchDataRetrieved(branches: List<Branch>) {
        val branchNames = branches.map { it.name }
        createBranchSpinner(branchNames)

        viewModel.branchNames = branchNames
        // fetch for default branch initially
        if (branches.isNotEmpty()) viewModel.fetchDirectory(branchName = branchNames[0])
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateViewState(it) }
        viewModel.navigateToRepoCodeAction.observe(viewLifecycleOwner) {
            navigateToRepoCodeFragment(it)
        }
    }

    private fun updateViewState(viewState: RepoContentViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading
            if (updateContent) repoContentAdapter.setContent(contentData)
            path_label.text = path

            if (updateBranchSpinner) createBranchSpinner(branchNames)
        }
    }

    private fun createBranchSpinner(branchNames: List<String>) {
        branchSpinnerAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item,
            branchNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        branch_spinner.adapter = branchSpinnerAdapter
    }

    private fun navigateToRepoCodeFragment(pair: Pair<String, String>) {
        val data = Bundle().apply {
            putString(BUNDLE_FILE_CONTENT, pair.first)
            putString(BUNDLE_FILE_NAME, pair.second)
        }
        findNavController().navigate(
            R.id.action_repo_detail_fragment_to_repo_code_fragment,
            data
        )
    }
}
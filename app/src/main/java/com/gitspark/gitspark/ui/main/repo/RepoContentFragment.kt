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
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.onItemSelected
import com.gitspark.gitspark.model.Branch
import com.gitspark.gitspark.ui.nav.BUNDLE_FILE_CONTENT
import com.gitspark.gitspark.ui.nav.BUNDLE_FILE_EXTENSION
import com.gitspark.gitspark.ui.nav.BUNDLE_FILE_NAME
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

        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dir_list.adapter = null
        viewModel.onDestroyView()
    }

    fun notifyBranchDataRetrieved(branches: List<Branch>) {
        val branchNames = mutableListOf<String>()
        branches.find { it.name == "master" }?.let {
            branchNames.add(it.name)
            branches.forEach { b ->
                if (b.name != "master") branchNames.add(b.name)
            }
        } ?: branchNames.addAll(branches.map { it.name })

        createBranchSpinner(branchNames)

        viewModel.branchNames = branchNames
        // fetch for default branch initially
        if (branches.isNotEmpty()) {
            viewModel.fetchDirectory(branchName = branchNames[0])
            viewModel.requestCommitData(branchNames[0])
        }
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

            commit_username.isVisible = commitUsername.isNotEmpty()
            commit_profile_icon.isVisible = numCommits > 0

            commits_button.text = getString(R.string.commits_button_text, numCommits)
            if (numCommits > 0) {
                if (commitAvatarUrl.isNotEmpty()) commit_profile_icon.loadImage(commitAvatarUrl)
                commit_username.text = commitUsername
                commit_message.text = commitMessage
            }
            else {
                commit_message.text = getString(R.string.no_commits_text)
            }

            if (updateBranchSpinner) createBranchSpinner(branchNames, branchPosition)
        }
    }

    private fun createBranchSpinner(branchNames: List<String>, selected: Int = 0) {
        branchSpinnerAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item,
            branchNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        branch_spinner.adapter = branchSpinnerAdapter
        branch_spinner.setSelection(selected)
    }

    private fun navigateToRepoCodeFragment(triple: Triple<String, String, String>) {
        val data = Bundle().apply {
            putString(BUNDLE_FILE_CONTENT, triple.first)
            putString(BUNDLE_FILE_NAME, triple.second)
            putString(BUNDLE_FILE_EXTENSION, triple.third)
        }
        findNavController().navigate(
            R.id.action_repo_detail_fragment_to_repo_code_fragment,
            data
        )
    }

    private fun setUpListeners() {
        dir_back_button.setOnClickListener { viewModel.onDirectoryBackClicked() }
        branch_spinner.onItemSelected {
            viewModel.onBranchSelected(branch_spinner.getItemAtPosition(it) as String, it)
        }
    }
}
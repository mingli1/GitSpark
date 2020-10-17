package com.gitspark.gitspark.ui.main.issues.pullrequest

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.showOptionIcons
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.PERMISSION_ADMIN
import com.gitspark.gitspark.model.PERMISSION_WRITE
import com.gitspark.gitspark.model.PullRequest
import com.gitspark.gitspark.ui.adapter.ViewPagerAdapter
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.issues.*
import com.gitspark.gitspark.ui.main.shared.*
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_pr_detail.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

private const val COMMITS_TAB_INDEX = 1
private const val FILES_TAB_INDEX = 2

interface PullRequestDataCallback {
    fun onDataRefreshed(pr: PullRequest)
}

class PullRequestDetailFragment : BaseFragment<PullRequestDetailViewModel>(PullRequestDetailViewModel::class.java),
    PullRequestDataCallback {

    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>
    @Inject lateinit var pullRequestJsonAdapter: JsonAdapter<PullRequest>

    private lateinit var issueDetailFragment: IssueDetailFragment
    private lateinit var commitListFragment: CommitListFragment
    private lateinit var fileListFragment: FileListFragment

    private var args = ""

    private var lockState: Triple<String, Drawable, Boolean>? = null
    private var stateState: Pair<String, Boolean>? = null
    private var editState = false

    override fun onDataRefreshed(pr: PullRequest) {
        tabs.getTabAt(COMMITS_TAB_INDEX)?.text = getString(R.string.commits_title, pr.numCommits)
        tabs.getTabAt(FILES_TAB_INDEX)?.text = getString(R.string.files_title, pr.numFilesChanged)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pr_detail, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE)
            }
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.issue_detail_menu, menu)
        menu.showOptionIcons()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> viewModel.onEditSelected()
            R.id.repo -> viewModel.onRepoSelected()
            R.id.state -> issueDetailFragment.onIssueStateChange(item)
            R.id.lock -> issueDetailFragment.onLockStateChange(item)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        lockState?.let {
            menu.findItem(R.id.lock).run {
                title = it.first
                icon = it.second
                isVisible = it.third
            }
        }
        stateState?.let {
            menu.findItem(R.id.state).run {
                title = it.first
                isVisible = it.second
            }
        }
        menu.findItem(R.id.edit).run {
            isVisible = editState
        }
    }

    fun updateMenu(viewState: IssueDetailViewState) {
        with (viewState) {
            lockState = Triple(
                if (locked) getString(R.string.unlock) else getString(R.string.lock),
                if (locked) resources.getDrawable(R.drawable.ic_unlock, null) else
                    resources.getDrawable(R.drawable.ic_lock, null),
                permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE
            )

            val type = if (isPullRequest) "pull request" else "issue"
            stateState = Pair(
                if (isOpen) getString(R.string.close, type) else getString(R.string.reopen, type),
                !isMerged && (permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE || authUserIsAuthor)
            )

            editState = permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE || authUserIsAuthor
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        issueJsonAdapter.fromJson(arguments?.getString(BUNDLE_ISSUE) ?: "")?.let {
            val split = it.getRepoFullNameFromUrl().split("/")
            args = "${split[0]}/${split[1]}/${it.number}"
            viewModel.fetchPullRequestData(split[0], split[1], it.number)
        }
    }

    override fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) { loading_indicator.isVisible = it }
        viewModel.prDataRetrieved.observe(viewLifecycleOwner) { setUpFragments(it) }
        viewModel.exitFragment.observe(viewLifecycleOwner) { findNavController().navigateUp() }
        viewModel.navigateToEdit.observe(viewLifecycleOwner) { navigateToEdit(it) }
        viewModel.navigateToRepo.observe(viewLifecycleOwner) { navigateToRepo(it) }
    }

    private fun setUpFragments(data: PullRequest) {
        issueDetailFragment = IssueDetailFragment().apply {
            arguments = Bundle().apply {
                putString(BUNDLE_PULL_REQUEST, pullRequestJsonAdapter.toJson(data))
                putString(BUNDLE_ISSUE_ARGS, args)
            }
        }
        commitListFragment = CommitListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(BUNDLE_COMMIT_LIST_TYPE, CommitListType.PullRequest)
                putString(BUNDLE_ARGUMENTS, args)
                putString(BUNDLE_HIDE_TOOLBAR, "")
            }
        }
        fileListFragment = FileListFragment().apply {
            arguments = Bundle().apply {
                putString(BUNDLE_ARGUMENTS, args)
                putString(BUNDLE_HIDE_TOOLBAR, "")
            }
        }

        val adapter = ViewPagerAdapter(childFragmentManager).apply {
            addFragment(issueDetailFragment, getString(R.string.overview_title))
            addFragment(commitListFragment, getString(R.string.commits_title, data.numCommits))
            addFragment(fileListFragment, getString(R.string.files_title, data.numFilesChanged))
        }
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
    }

    private fun navigateToEdit(triple: Triple<String, PullRequest, String>) {
        val bundle = Bundle().apply {
            putString(BUNDLE_TITLE, triple.first)
            putString(BUNDLE_PULL_REQUEST, pullRequestJsonAdapter.toJson(triple.second))
            putString(BUNDLE_REPO_FULLNAME, triple.third)
        }
        findNavController().navigate(R.id.action_pr_detail_to_issue_edit, bundle)
    }

    private fun navigateToRepo(fullName: String) {
        val bundle = Bundle().apply { putString(BUNDLE_REPO_FULLNAME, fullName) }
        findNavController().navigate(R.id.action_pr_detail_to_repo, bundle)
    }
}
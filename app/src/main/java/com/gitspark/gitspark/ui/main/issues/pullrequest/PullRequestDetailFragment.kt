package com.gitspark.gitspark.ui.main.issues.pullrequest

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.showOptionIcons
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.PullRequest
import com.gitspark.gitspark.ui.adapter.ViewPagerAdapter
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.issues.BUNDLE_ISSUE
import com.gitspark.gitspark.ui.main.issues.BUNDLE_ISSUE_ARGS
import com.gitspark.gitspark.ui.main.issues.BUNDLE_PULL_REQUEST
import com.gitspark.gitspark.ui.main.issues.IssueDetailFragment
import com.gitspark.gitspark.ui.main.shared.*
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_pr_detail.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

private const val COMMITS_TAB_INDEX = 1
private const val FILES_TAB_INDEX = 2

class PullRequestDetailFragment : BaseFragment<PullRequestDetailViewModel>(PullRequestDetailViewModel::class.java) {

    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>
    @Inject lateinit var pullRequestJsonAdapter: JsonAdapter<PullRequest>

    private lateinit var issueDetailFragment: IssueDetailFragment
    private lateinit var commitListFragment: CommitListFragment
    private lateinit var fileListFragment: FileListFragment

    private var args = ""

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
}
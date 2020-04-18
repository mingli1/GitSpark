package com.gitspark.gitspark.ui.main.shared

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.formatLarge
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.helper.ColorHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.ui.adapter.IssuesAdapter
import com.gitspark.gitspark.ui.adapter.NestedPaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.issues.BUNDLE_ISSUE
import com.gitspark.gitspark.ui.main.issues.IssueEditSharedViewModel
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import com.google.android.material.appbar.AppBarLayout
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_issue_list.*
import kotlinx.android.synthetic.main.fragment_issue_list.item_list
import kotlinx.android.synthetic.main.fragment_issue_list.swipe_refresh
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

const val BUNDLE_ISSUE_LIST_TYPE = "BUNDLE_ISSUE_LIST_TYPE"
const val BUNDLE_ISSUE_TYPE = "BUNDLE_ISSUE_TYPE"

class IssuesListFragment : BaseFragment<IssuesListViewModel>(IssuesListViewModel::class.java) {

    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var colorHelper: ColorHelper
    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>

    private lateinit var paginationListener: NestedPaginationListener
    private lateinit var issuesAdapter: IssuesAdapter

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[IssueEditSharedViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_issue_list, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val appBarLayout = view.findViewById<AppBarLayout>(R.id.app_bar_layout)

        appBarLayout.isVisible = arguments?.getString(BUNDLE_ISSUE_LIST_TYPE) == "list"

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE) ?: getString(R.string.issues_button_text)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        paginationListener = NestedPaginationListener { viewModel.onScrolledToEnd() }
        item_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        nested_scroll_view.setOnScrollChangeListener(paginationListener)

        issuesAdapter = IssuesAdapter(timeHelper, colorHelper, viewModel, arguments?.getString(BUNDLE_ISSUE_LIST_TYPE) != "list")
        if (item_list.adapter == null) item_list.adapter = issuesAdapter

        arguments?.let {
            val filter = it.getString(BUNDLE_ISSUE_TYPE) ?: ""
            viewModel.onStart(filter)
        }

        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        item_list.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.pageViewState.observe(viewLifecycleOwner) { updateRecycler(it) }
        viewModel.navigateToIssueDetail.observe(viewLifecycleOwner) { navigateToIssueDetailFragment(it) }
        viewModel.createNewIssueAction.observe(viewLifecycleOwner) { navigateToIssueEditFragment() }
        sharedViewModel.createdIssue.observe(viewLifecycleOwner) { viewModel.onNewIssueCreated(it) }
    }

    private fun updateView(viewState: IssuesListViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing

            val col1 = if (showOpenIssues) resources.getColor(R.color.colorBlack, null) else
                resources.getColor(R.color.colorDarkGray, null)
            val col2 = if (!showOpenIssues) resources.getColor(R.color.colorBlack, null) else
                resources.getColor(R.color.colorDarkGray, null)
            open_field.compoundDrawablesRelative[0].setColor(col1)
            open_field.setTextColor(col1)
            open_field.setTypeface(null,
                if (showOpenIssues) Typeface.BOLD else Typeface.NORMAL)
            closed_field.compoundDrawablesRelative[0].setColor(col2)
            closed_field.setTextColor(col2)
            closed_field.setTypeface(null,
                if (!showOpenIssues) Typeface.BOLD else Typeface.NORMAL)

            open_field.text = getString(R.string.num_open_issues, numOpen.formatLarge())
            closed_field.text = getString(R.string.num_closed_issues, numClosed.formatLarge())
        }
    }

    private fun updateRecycler(viewState: PaginatedViewState<Issue>) {
        with (viewState) {
            issuesAdapter.setItems(items, isLastPage)
            paginationListener.isLastPage = isLastPage
            paginationListener.loading = false
        }
    }

    private fun setupListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        open_field.setOnClickListener { viewModel.onIssueStateSelected(true) }
        closed_field.setOnClickListener { viewModel.onIssueStateSelected(false) }
        add_issue_button.setOnClickListener { viewModel.onAddIssueClicked() }
    }

    private fun navigateToIssueDetailFragment(pair: Pair<String, Issue>) {
        val bundle = Bundle().apply {
            putString(BUNDLE_TITLE, pair.first)
            putString(BUNDLE_ISSUE, issueJsonAdapter.toJson(pair.second))
        }
        findNavController().navigate(R.id.action_issues_to_issue_detail, bundle)
    }

    private fun navigateToIssueEditFragment() {
        val rfn = arguments?.getString(BUNDLE_REPO_FULLNAME)
        val bundle = Bundle().apply {
            putString(BUNDLE_TITLE, getString(R.string.create_issue_title, rfn))
            putString(BUNDLE_REPO_FULLNAME, rfn)
        }
        findNavController().navigate(R.id.action_issues_to_issue_edit, bundle)
    }
}
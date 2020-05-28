package com.gitspark.gitspark.ui.main.issues

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.*
import com.gitspark.gitspark.helper.*
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.ui.adapter.ChecksAdapter
import com.gitspark.gitspark.ui.adapter.IssueEventsAdapter
import com.gitspark.gitspark.ui.adapter.NestedPaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.custom.DarkMarkdownStyle
import com.gitspark.gitspark.ui.custom.LightMarkdownStyle
import com.gitspark.gitspark.ui.dialog.ConfirmDialog
import com.gitspark.gitspark.ui.dialog.ConfirmDialogCallback
import com.gitspark.gitspark.ui.dialog.SelectDialog
import com.gitspark.gitspark.ui.dialog.SelectDialogCallback
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.issues.pullrequest.CheckState
import com.gitspark.gitspark.ui.main.issues.pullrequest.ChecksViewState
import com.gitspark.gitspark.ui.main.issues.pullrequest.PullRequestDataCallback
import com.gitspark.gitspark.ui.main.issues.pullrequest.PullRequestDetailFragment
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import com.google.android.material.appbar.AppBarLayout
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_issue_detail.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import kotlinx.android.synthetic.main.issue_comment_view.*
import kotlinx.android.synthetic.main.pr_checks_view.*
import javax.inject.Inject

const val BUNDLE_ISSUE = "BUNDLE_ISSUE"
const val BUNDLE_PULL_REQUEST = "BUNDLE_PULL_REQUEST"
const val BUNDLE_ISSUE_ARGS = "BUNDLE_ISSUE_ARGS"

class IssueDetailFragment : BaseFragment<IssueDetailViewModel>(IssueDetailViewModel::class.java),
    ConfirmDialogCallback, SelectDialogCallback {

    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>
    @Inject lateinit var prJsonAdapter: JsonAdapter<PullRequest>
    @Inject lateinit var colorHelper: ColorHelper
    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var keyboardHelper: KeyboardHelper

    private lateinit var paginationListener: NestedPaginationListener
    private lateinit var issueEventsAdapter: IssueEventsAdapter
    private lateinit var checksAdapter: ChecksAdapter
    private lateinit var commentMenu: PopupMenu
    private var menu: Menu? = null
    private lateinit var eventHelper: IssueEventHelper
    private lateinit var darkModeHelper: DarkModeHelper

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[IssueEditSharedViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(!(arguments?.containsKey(BUNDLE_PULL_REQUEST) ?: false))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_issue_detail, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val appBarLayout = view.findViewById<AppBarLayout>(R.id.app_bar_layout)

        val showToolbar = !(arguments?.containsKey(BUNDLE_PULL_REQUEST) ?: false)

        appBarLayout.isVisible = showToolbar

        if (showToolbar) {
            with (activity as MainActivity) {
                setSupportActionBar(toolbar)
                supportActionBar?.run {
                    setDisplayHomeAsUpEnabled(true)
                    setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                    title = arguments?.getString(BUNDLE_TITLE)
                        ?: getString(R.string.default_issue_title)
                }
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        darkModeHelper = DarkModeHelper(context!!)
        eventHelper = IssueEventHelper(context!!, colorHelper, timeHelper)

        commentMenu = PopupMenu(context!!, comment_options).apply {
            inflate(R.menu.issue_comment_menu)
            menu.findItem(R.id.edit).isVisible = false
            menu.findItem(R.id.copy_link).isVisible = false
            menu.findItem(R.id.delete).isVisible = false
            menu.showOptionIcons()
        }

        paginationListener = NestedPaginationListener { viewModel.onScrolledToEnd() }
        events_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        checks_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        nested_scroll_view.setOnScrollChangeListener(paginationListener)

        issueEventsAdapter = IssueEventsAdapter(eventHelper, timeHelper, keyboardHelper, viewModel, darkModeHelper.isDarkMode())
        if (events_list.adapter == null) events_list.adapter = issueEventsAdapter
        checksAdapter = ChecksAdapter()
        if (checks_list.adapter == null) checks_list.adapter = checksAdapter

        comment_body.addStyleSheet(if (darkModeHelper.isDarkMode()) DarkMarkdownStyle() else LightMarkdownStyle())

        arguments?.run {
            if (containsKey(BUNDLE_PULL_REQUEST)) {
                prJsonAdapter.fromJson(getString(BUNDLE_PULL_REQUEST) ?: "")?.let {
                    viewModel.onStart(it, getString(BUNDLE_ISSUE_ARGS) ?: "")
                }
            } else {
                issueJsonAdapter.fromJson(arguments?.getString(BUNDLE_ISSUE) ?: "")?.let {
                    viewModel.onStart(it)
                }
            }
        }

        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        events_list.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.recyclerViewState.observe(viewLifecycleOwner) { updateRecyclerView(it) }
        viewModel.deleteCommentRequest.observe(viewLifecycleOwner) { requestDeleteComment() }
        viewModel.toggleCommentEdit.observe(viewLifecycleOwner) { comment_view.isVisible = it }
        viewModel.quoteReplyAction.observe(viewLifecycleOwner) { updateCommentEdit(it) }
        viewModel.clearCommentEdit.observe(viewLifecycleOwner) { send_comment_edit.clear() }
        viewModel.updateCommentRequest.observe(viewLifecycleOwner) { issueEventsAdapter.updateComment(it) }
        viewModel.navigateToRepoDetail.observe(viewLifecycleOwner) { navigateToRepoDetailFragment(it) }
        viewModel.navigateToIssueEdit.observe(viewLifecycleOwner) { navigateToIssueEditFragment(it) }
        viewModel.pullRequestRefresh.observe(viewLifecycleOwner) { refreshPullRequestData(it) }
        viewModel.checksState.observe(viewLifecycleOwner) { updateChecksView(it) }
        sharedViewModel.editedIssue.observe(viewLifecycleOwner) { viewModel.onRefresh() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.issue_detail_menu, menu)
        menu.showOptionIcons()

        super.onCreateOptionsMenu(menu, inflater)
        viewModel.onMenuCreated()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> viewModel.onEditSelected()
            R.id.repo -> viewModel.onRepoSelected()
            R.id.state -> {
                viewModel.onIssueStateChange(
                    if (item.title.startsWith("reopen", ignoreCase = true)) "open" else "closed"
                )
            }
            R.id.lock -> {
                if (item.title == getString(R.string.lock)) {
                    SelectDialog.newInstance(
                        getString(R.string.lock_title),
                        resources.getStringArray(R.array.lock_reasons)
                    ).show(childFragmentManager, null)
                } else {
                    viewModel.onIssueUnlockRequest()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPositiveClicked() = viewModel.onDeleteCommentConfirmed()

    override fun onNegativeClicked() {}

    override fun onSelected(item: String) = viewModel.onIssueLockRequest(item)

    private fun updateView(viewState: IssueDetailViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing

            checks_view.isVisible = isPullRequest && !isMerged && !isDraft

            merge_status_icon.setImageResource(if (mergeableState == MERGABLE_STATE_CLEAN)
                R.drawable.ic_check_circle else R.drawable.ic_alert)
            merge_status_icon.drawable.setColor(if (mergeableState == MERGABLE_STATE_CLEAN)
                resources.getColor(R.color.colorGreen, null) else resources.getColor(R.color.colorYellowOrange, null))

            merge_status_text.text = when (mergeableState) {
                MERGABLE_STATE_CLEAN -> getString(R.string.merge_no_conflicts)
                MERGABLE_STATE_UNSTABLE -> getString(R.string.merge_unstable)
                MERGABLE_STATE_DIRTY -> getString(R.string.merge_conflicts)
                MERGABLE_STATE_BLOCKED -> getString(R.string.merge_blocked)
                else -> getString(R.string.merge_other)
            }
            merge_status_desc.text = when (mergeableState) {
                MERGABLE_STATE_CLEAN -> getString(R.string.merge_no_conflicts_desc)
                MERGABLE_STATE_UNSTABLE -> getString(R.string.merge_unstable_desc)
                MERGABLE_STATE_DIRTY -> getString(R.string.merge_conflicts_desc)
                MERGABLE_STATE_BLOCKED -> getString(R.string.merge_blocked_desc)
                else -> getString(R.string.merge_other_desc)
            }
            merge_button.isEnabled = mergable && (permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE)

            send_comment_edit.isEnabled = permissionLevel == PERMISSION_ADMIN ||
                    permissionLevel == PERMISSION_WRITE ||
                    !locked

            if (isPullRequest) {
                (parentFragment as PullRequestDetailFragment).updateMenu(viewState)
            } else {
                menu?.let {
                    it.findItem(R.id.lock).run {
                        title = if (locked) getString(R.string.unlock) else getString(R.string.lock)
                        icon = if (locked) resources.getDrawable(R.drawable.ic_unlock, null) else
                            resources.getDrawable(R.drawable.ic_lock, null)
                        isVisible =
                            permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE
                    }

                    it.findItem(R.id.state).run {
                        val type = if (isPullRequest) "pull request" else "issue"
                        title = if (isOpen) getString(
                            R.string.close,
                            type
                        ) else getString(R.string.reopen, type)
                        isVisible =
                            !isMerged && (permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE || authUserIsAuthor)
                    }
                    it.findItem(R.id.edit).isVisible =
                        permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE || authUserIsAuthor
                }
            }
            issueEventsAdapter.permissionLevel = permissionLevel

            lock_field.isVisible = locked

            if (authorAvatarUrl.isNotEmpty()) profile_icon.loadImage(authorAvatarUrl)
            author_username.text = authorUsername
            author_action.text = getString(R.string.comment_action, authorCommentDate)
            comment_body.loadMarkdown(if (authorComment.isNotEmpty()) authorComment else getString(R.string.empty_comment))

            issue_title_field.text = issueTitle
            issue_username_field.text = issueUsername
            issue_desc_field.text = issueDesc
            num_comments_field.isVisible = numComments > 0
            num_comments_field.text = withSuffix(numComments)

            additions_field.isVisible = isPullRequest
            deletions_field.isVisible = isPullRequest
            additions_field.text = getString(R.string.additions_text, numAdditions.formatLarge())
            deletions_field.text = getString(R.string.deletions_text, numDeletions.formatLarge())

            head_branch_field.isVisible = isPullRequest
            branch_arrow.isVisible = isPullRequest
            base_branch_field.isVisible = isPullRequest
            head_branch_name_field.text = headBranch
            base_branch_name_field.text = baseBranch

            when {
                isMerged -> {
                    status_field_text.setBackgroundColor(resources.getColor(R.color.colorPurple, null))
                    status_field_text.text = getString(R.string.issue_merged_state)
                    status_field_text.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        resources.getDrawable(R.drawable.ic_merge, null), null, null, null
                    )
                }
                else -> {
                    status_field_text.setBackgroundColor(if (isOpen)
                        resources.getColor(R.color.colorGreen, null)
                    else
                        resources.getColor(R.color.colorRed, null)
                    )
                    status_field_text.text = if (isOpen) getString(R.string.issue_open_state) else getString(R.string.issue_closed_state)
                    status_field_text.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        resources.getDrawable(R.drawable.ic_pull_request, null), null, null, null
                    )
                }
            }

            labels_label.isVisible = labels.isNotEmpty()
            labels_container.isVisible = labels.isNotEmpty()
            if (labels.isNotEmpty()) {
                labels_container.removeAllViews()
                labels.forEach {
                    val labelView = LayoutInflater.from(context)
                        .inflate(R.layout.label_view, labels_container, false)
                    ((labelView as CardView).getChildAt(0) as TextView).apply {
                        text = it.name
                        setBackgroundColor(Color.parseColor("#${it.color}"))
                        setTextColor(Color.parseColor(colorHelper.getTextColor(it.color)))
                    }
                    labels_container.addView(labelView)
                }
            }

            assignees_label.isVisible = assignees.isNotEmpty()
            assignees_container.isVisible = assignees.isNotEmpty()
            if (assignees.isNotEmpty()) {
                assignees_container.removeAllViews()
                assignees.forEach {
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.user_icon_view, assignees_container, false)
                    if (it.avatarUrl.isNotEmpty()) {
                        (((view as LinearLayout).getChildAt(0) as CardView).getChildAt(0) as ImageView).loadImage(it.avatarUrl)
                    }
                    ((view as LinearLayout).getChildAt(1) as TextView).text = it.login
                    assignees_container.addView(view)
                }
            }

            reviewers_label.isVisible = reviewers.isNotEmpty() && isPullRequest
            reviewers_container.isVisible = reviewers.isNotEmpty() && isPullRequest
            if (reviewers.isNotEmpty()) {
                reviewers_container.removeAllViews()
                reviewers.forEach {
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.user_icon_view, reviewers_container, false)
                    if (it.avatarUrl.isNotEmpty()) {
                        (((view as LinearLayout).getChildAt(0) as CardView).getChildAt(0) as ImageView).loadImage(it.avatarUrl)
                    }
                    ((view as LinearLayout).getChildAt(1) as TextView).text = it.login
                    reviewers_container.addView(view)
                }
            }
        }
    }

    private fun updateRecyclerView(recyclerViewState: PaginatedViewState<IssueEvent>) {
        with (recyclerViewState) {
            issueEventsAdapter.setItems(items, isLastPage)
            paginationListener.isLastPage = isLastPage
            paginationListener.loading = false
        }
    }

    private fun updateChecksView(checksViewState: ChecksViewState) {
        with (checksViewState) {
            val shouldShowChecks = showChecks && checks.isNotEmpty()
            show_checks_button.setImageResource(if (showChecksList) R.drawable.ic_expand_less else R.drawable.ic_expand_more)

            merge_icon.isVisible = shouldShowChecks
            checks_status_text.isVisible = shouldShowChecks
            checks_progress_text.isVisible = shouldShowChecks
            show_checks_button.isVisible = shouldShowChecks
            second_divider.isVisible = shouldShowChecks
            checks_list.isVisible = shouldShowChecks && showChecksList

            merge_icon.drawable.setColor(when (state) {
                CheckState.Success -> resources.getColor(R.color.colorGreen, null)
                CheckState.Pending -> resources.getColor(R.color.colorYellowOrange, null)
                else -> resources.getColor(R.color.colorRed, null)
            })
            checks_status_text.text = when (state) {
                CheckState.Success -> getString(R.string.checks_passed)
                CheckState.Pending -> getString(R.string.checks_pending)
                else -> getString(R.string.checks_failed)
            }
            checks_progress_text.text = when (state) {
                CheckState.Success -> getString(R.string.num_successful_checks, numPassed)
                CheckState.Pending -> getString(R.string.num_pending_checks, numPending)
                else -> getString(R.string.num_failing_checks, numFailed)
            }

            if (checks.isNotEmpty()) checksAdapter.setItems(checks, true)
        }
    }

    private fun requestDeleteComment() {
        ConfirmDialog.newInstance(
            getString(R.string.delete_comment_title),
            getString(R.string.delete_comment_message)
        ).show(childFragmentManager, null)
    }

    private fun updateCommentEdit(quote: String) {
        if (send_comment_edit.text.isEmpty()) {
            send_comment_edit.setText(quote)
        } else {
            send_comment_edit.append("\n$quote")
        }
    }

    private fun setUpListeners() {
        comment_options.setOnClickListener { commentMenu.show() }
        commentMenu.setOnMenuItemClickListener {
            if (it.itemId == R.id.quote_reply) viewModel.onAuthorCommentQuoteReply()
            true
        }
        send_comment_button.setOnClickListener {
            if (send_comment_edit.text.trim().isNotEmpty()) {
                viewModel.onSendComment(send_comment_edit.text.trim().toString())
                keyboardHelper.hideKeyboard(send_comment_edit)
            }
        }
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        show_checks_button.setOnClickListener { viewModel.onShowChecksButtonClicked() }
        merge_button.setOnClickListener { viewModel.onMergeButtonClicked() }
    }

    private fun navigateToRepoDetailFragment(pair: Pair<String, Boolean>) {
        val bundle = Bundle().apply { putString(BUNDLE_REPO_FULLNAME, pair.first) }
        findNavController().navigate(
            if (pair.second) R.id.action_pr_detail_to_repo else R.id.action_issue_detail_to_repo,
            bundle
        )
    }

    private fun navigateToIssueEditFragment(triple: Triple<String, Any, String>) {
        val bundle = Bundle().apply {
            putString(BUNDLE_TITLE, triple.first)
            if (triple.second is Issue)
                putString(BUNDLE_ISSUE, issueJsonAdapter.toJson(triple.second as Issue))
            else
                putString(BUNDLE_PULL_REQUEST, prJsonAdapter.toJson(triple.second as PullRequest))
            putString(BUNDLE_REPO_FULLNAME, triple.third)
        }
        findNavController().navigate(
            if (triple.second is Issue) R.id.action_issue_detail_to_issue_edit else R.id.action_pr_detail_to_issue_edit,
            bundle
        )
    }

    private fun refreshPullRequestData(pr: PullRequest) {
        (parentFragment as PullRequestDataCallback).onDataRefreshed(pr)
    }
}
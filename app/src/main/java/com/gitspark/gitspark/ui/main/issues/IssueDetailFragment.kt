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
import com.gitspark.gitspark.helper.ColorHelper
import com.gitspark.gitspark.helper.IssueEventHelper
import com.gitspark.gitspark.helper.KeyboardHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.PERMISSION_ADMIN
import com.gitspark.gitspark.model.PERMISSION_WRITE
import com.gitspark.gitspark.ui.adapter.IssueEventsAdapter
import com.gitspark.gitspark.ui.adapter.NestedPaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.dialog.ConfirmDialog
import com.gitspark.gitspark.ui.dialog.ConfirmDialogCallback
import com.gitspark.gitspark.ui.dialog.SelectDialog
import com.gitspark.gitspark.ui.dialog.SelectDialogCallback
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_issue_detail.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import kotlinx.android.synthetic.main.issue_comment_view.*
import javax.inject.Inject

const val BUNDLE_ISSUE = "BUNDLE_ISSUE"

class IssueDetailFragment : BaseFragment<IssueDetailViewModel>(IssueDetailViewModel::class.java),
    ConfirmDialogCallback, SelectDialogCallback {

    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>
    @Inject lateinit var colorHelper: ColorHelper
    @Inject lateinit var eventHelper: IssueEventHelper
    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var keyboardHelper: KeyboardHelper

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var paginationListener: NestedPaginationListener
    private lateinit var issueEventsAdapter: IssueEventsAdapter
    private lateinit var commentMenu: PopupMenu
    private var menu: Menu? = null

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[IssueSharedViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_issue_detail, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE) ?: getString(R.string.default_issue_title)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        commentMenu = PopupMenu(context!!, comment_options).apply {
            inflate(R.menu.issue_comment_menu)
            menu.findItem(R.id.edit).isVisible = false
            menu.findItem(R.id.copy_link).isVisible = false
            menu.findItem(R.id.delete).isVisible = false
            if (menu.javaClass.simpleName == "MenuBuilder") {
                try {
                    menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java).run {
                        isAccessible = true
                        invoke(menu, true)
                    }
                } catch (e: NoSuchMethodException) {}
            }
        }

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = NestedPaginationListener { viewModel.onScrolledToEnd() }
        events_list.layoutManager = layoutManager
        nested_scroll_view.setOnScrollChangeListener(paginationListener)

        issueEventsAdapter = IssueEventsAdapter(eventHelper, timeHelper, keyboardHelper, viewModel)
        if (events_list.adapter == null) events_list.adapter = issueEventsAdapter

        comment_body.isOpenUrlInBrowser = true

        issueJsonAdapter.fromJson(arguments?.getString(BUNDLE_ISSUE) ?: "")?.let {
            viewModel.onStart(it)
        }

        swipe_refresh.setColorSchemeResources(R.color.colorAccent)

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
        viewModel.deleteCommentRequest.observe(viewLifecycleOwner) { requestDeleteComment() }
        viewModel.toggleCommentEdit.observe(viewLifecycleOwner) { comment_view.isVisible = it }
        viewModel.quoteReplyAction.observe(viewLifecycleOwner) { updateCommentEdit(it) }
        viewModel.clearCommentEdit.observe(viewLifecycleOwner) { send_comment_edit.clear() }
        viewModel.updateCommentRequest.observe(viewLifecycleOwner) { issueEventsAdapter.updateComment(it) }
        viewModel.navigateToRepoDetail.observe(viewLifecycleOwner) { navigateToRepoDetailFragment(it) }
        viewModel.navigateToIssueEdit.observe(viewLifecycleOwner) { navigateToIssueEditFragment(it) }
        sharedViewModel.editedIssue.observe(viewLifecycleOwner) { viewModel.onRefresh() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.issue_detail_menu, menu)

        if (menu.javaClass.simpleName == "MenuBuilder") {
            try {
                menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java).run {
                    isAccessible = true
                    invoke(menu, true)
                }
            } catch (e: NoSuchMethodException) {}
        }

        super.onCreateOptionsMenu(menu, inflater)
        viewModel.onMenuCreated()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> viewModel.onEditSelected()
            R.id.repo -> viewModel.onRepoSelected()
            R.id.state -> {
                viewModel.onIssueStateChange(
                    if (item.title == getString(R.string.reopen)) "open" else "closed"
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

            send_comment_edit.isEnabled = permissionLevel == PERMISSION_ADMIN ||
                    permissionLevel == PERMISSION_WRITE ||
                    !locked

            menu?.let {
                it.findItem(R.id.lock).run {
                    title = if (locked) getString(R.string.unlock) else getString(R.string.lock)
                    icon = if (locked) resources.getDrawable(R.drawable.ic_unlock, null) else
                        resources.getDrawable(R.drawable.ic_lock, null)
                    isVisible = permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE
                }

                it.findItem(R.id.state).run {
                    title = if (isOpen) getString(R.string.close) else getString(R.string.reopen)
                    isVisible = permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE || authUserIsAuthor
                }
                it.findItem(R.id.edit).isVisible = permissionLevel == PERMISSION_ADMIN || permissionLevel == PERMISSION_WRITE || authUserIsAuthor
            }
            issueEventsAdapter.permissionLevel = permissionLevel

            lock_field.isVisible = locked

            if (authorAvatarUrl.isNotEmpty()) profile_icon.loadImage(authorAvatarUrl)
            author_username.text = authorUsername
            author_action.text = getString(R.string.comment_action, authorCommentDate)
            comment_body.setMarkDownText(if (authorComment.isNotEmpty()) authorComment else getString(R.string.empty_comment))

            issue_title_field.text = issueTitle
            issue_desc_field.text = issueDesc
            issue_desc_field.compoundDrawablesRelative[0].setColor(
                if (isOpen) resources.getColor(R.color.colorSuccess, null) else
                    resources.getColor(R.color.colorError, null)
            )
            num_comments_field.isVisible = numComments > 0
            num_comments_field.text = withSuffix(numComments)

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

            if (updateAdapter && commentsFinishedLoading && eventsFinishedLoading) {
                issueEventsAdapter.setItems(events, isLastPage)
                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
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
    }

    private fun navigateToRepoDetailFragment(repoFullName: String) {
        val bundle = Bundle().apply { putString(BUNDLE_REPO_FULLNAME, repoFullName) }
        findNavController().navigate(R.id.action_issue_detail_to_repo, bundle)
    }

    private fun navigateToIssueEditFragment(triple: Triple<String, Issue, String>) {
        val bundle = Bundle().apply {
            putString(BUNDLE_TITLE, triple.first)
            putString(BUNDLE_ISSUE, issueJsonAdapter.toJson(triple.second))
            putString(BUNDLE_REPO_FULLNAME, triple.third)
        }
        findNavController().navigate(R.id.action_issue_detail_to_issue_edit, bundle)
    }
}
package com.gitspark.gitspark.ui.main.issues

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.*
import com.gitspark.gitspark.helper.ColorHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.model.PullRequest
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.dialog.*
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_issue_edit.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

class IssueEditFragment : BaseFragment<IssueEditViewModel>(IssueEditViewModel::class.java),
    UsersDialogCallback, LabelsDialogCallback, ConfirmDialogCallback {

    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>
    @Inject lateinit var prJsonAdapter: JsonAdapter<PullRequest>
    @Inject lateinit var colorHelper: ColorHelper

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[IssueEditSharedViewModel::class.java]
    }
    private var issue: Issue? = null
    private var pullRequest: PullRequest? = null
    private var isPullRequest = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_issue_edit, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE) ?: ""
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        val repoFullName = checkNotNull(arguments?.getString(BUNDLE_REPO_FULLNAME))
        when {
            arguments?.containsKey(BUNDLE_ISSUE) == true -> {
                arguments?.getString(BUNDLE_ISSUE)?.let { json ->
                    issue = issueJsonAdapter.fromJson(json)
                    issue?.let { viewModel.setInitialState(it, repoFullName) }
                }
            }
            arguments?.containsKey(BUNDLE_PULL_REQUEST) == true -> {
                arguments?.getString(BUNDLE_PULL_REQUEST)?.let { json ->
                    pullRequest = prJsonAdapter.fromJson(json)
                    pullRequest?.let { viewModel.setInitialState(it, repoFullName) }
                    isPullRequest = true
                }
            }
            else -> viewModel.setCreatingState(repoFullName)
        }

        setUpListeners()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            showConfirmDialog()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.showAssigneesDialog.observe(viewLifecycleOwner) { showAssigneesDialog(it) }
        viewModel.loadListData.observe(viewLifecycleOwner) { loadListData(it) }
        viewModel.showLabelsDialog.observe(viewLifecycleOwner) { showLabelsDialog(it) }
        viewModel.showReviewersDialog.observe(viewLifecycleOwner) { showReviewersDialog(it) }
        viewModel.updateIssueAction.observe(viewLifecycleOwner) {
            sharedViewModel.editedIssue.value = it
            findNavController().navigateUp()
        }
        viewModel.createIssueAction.observe(viewLifecycleOwner) {
            sharedViewModel.createdIssue.value = it
            findNavController().navigateUp()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            showConfirmDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onUsersSet(users: List<User>, isReviewer: Boolean) = if (isReviewer) {
        viewModel.onReviewersSet(users)
    } else {
        viewModel.onAssigneesSet(users)
    }

    override fun onLabelsSet(labels: List<Label>) = viewModel.onLabelsSet(labels)

    override fun onPositiveClicked() {
        findNavController().popBackStack()
    }

    override fun onNegativeClicked() {}

    private fun updateView(viewState: IssueEditViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading
            edit_issue_button.text = getString(when {
                creating -> R.string.create_issue_button
                isPullRequest -> R.string.edit_pr_button
                else -> R.string.edit_issue_button
            })

            if (edit_title.getString() != title) edit_title.setText(title)
            if (edit_desc.getString() != body) edit_desc.setText(body)

            issue?.let { i ->
                edit_issue_button.isEnabled = i.title != title ||
                        i.body != body ||
                        i.assignees.map { it.login } != assignees ||
                        i.labels.map { it.name } != labels
            }
            if (creating) {
                edit_issue_button.isEnabled = title.isNotEmpty()
            }
        }
    }

    private fun loadListData(triple: Triple<List<User>, List<Label>, List<User>>) {
        no_assignees_text.isVisible = triple.first.isEmpty()
        assignees_container.isVisible = triple.first.isNotEmpty()
        if (triple.first.isNotEmpty()) {
            assignees_container.removeAllViews()
            triple.first.forEach { user ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.user_icon_view, assignees_container, false)
                if (user.avatarUrl.isNotEmpty()) {
                    (((view as LinearLayout).getChildAt(0) as CardView).getChildAt(0) as ImageView).loadImage(user.avatarUrl)
                }
                ((view as LinearLayout).getChildAt(1) as TextView).text = user.login
                assignees_container.addView(view)
            }
        }

        no_labels_text.isVisible = triple.second.isEmpty()
        labels_container.isVisible = triple.second.isNotEmpty()
        if (triple.second.isNotEmpty()) {
            labels_container.removeAllViews()
            triple.second.forEach { label ->
                val labelView = LayoutInflater.from(context)
                    .inflate(R.layout.label_view, labels_container, false)
                ((labelView as CardView).getChildAt(0) as TextView).apply {
                    text = label.name
                    setBackgroundColor(Color.parseColor("#${label.color}"))
                    setTextColor(Color.parseColor(colorHelper.getTextColor(label.color)))
                }
                labels_container.addView(labelView)
            }
        }

        reviewers_header.isVisible = isPullRequest
        no_reviewers_text.isVisible = triple.third.isEmpty() && isPullRequest
        reviewers_container.isVisible = triple.third.isNotEmpty() && isPullRequest
        if (triple.third.isNotEmpty() && isPullRequest) {
            reviewers_container.removeAllViews()
            triple.third.forEach { user ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.user_icon_view, reviewers_container, false)
                if (user.avatarUrl.isNotEmpty()) {
                    (((view as LinearLayout).getChildAt(0) as CardView).getChildAt(0) as ImageView).loadImage(user.avatarUrl)
                }
                ((view as LinearLayout).getChildAt(1) as TextView).text = user.login
                reviewers_container.addView(view)
            }
        }
    }

    private fun setUpListeners() {
        edit_title.afterTextChanged { viewModel.onTitleChanged(edit_title.getString()) }
        edit_desc.afterTextChanged { viewModel.onBodyChanged(edit_desc.getString()) }
        assignees_button.setOnClickListener { viewModel.onAssigneesButtonClicked() }
        labels_button.setOnClickListener { viewModel.onLabelsButtonClicked() }
        reviewers_button.setOnClickListener { viewModel.onReviewersButtonClicked() }
        edit_issue_button.setOnClickListener { viewModel.onEditIssueClicked() }
    }

    private fun showAssigneesDialog(users: List<User>) {
        val assignees = if (viewModel.assignees == null) issue?.assignees else viewModel.assignees
        UsersDialog.newInstance(
            users = users.map { it.login }.toTypedArray(),
            userAvatars = users.map { it.avatarUrl }.toTypedArray(),
            existingUsers = assignees?.map { it.login }?.toTypedArray() ?: emptyArray(),
            existingUserAvatars = assignees?.map { it.avatarUrl }?.toTypedArray() ?: emptyArray()
        ).show(childFragmentManager, null)
    }

    private fun showLabelsDialog(labels: List<Label>) {
        val selectedLabels = if (viewModel.labels == null) issue?.labels else viewModel.labels
        LabelsDialog.newInstance(
            names = labels.map { it.name }.toTypedArray(),
            descs = labels.map { it.description }.toTypedArray(),
            colors = labels.map { it.color }.toTypedArray(),
            sNames = selectedLabels?.map { it.name }?.toTypedArray() ?: emptyArray(),
            sDescs = selectedLabels?.map { it.description }?.toTypedArray() ?: emptyArray(),
            sColors = selectedLabels?.map { it.color }?.toTypedArray() ?: emptyArray()
        ).show(childFragmentManager, null)
    }

    private fun showReviewersDialog(reviewers: List<User>) {
        val existing = if (viewModel.reviewers == null) pullRequest?.requestedReviewers else viewModel.reviewers
        UsersDialog.newInstance(
            users = reviewers.map { it.login }.toTypedArray(),
            userAvatars = reviewers.map { it.avatarUrl }.toTypedArray(),
            existingUsers = existing?.map { it.login }?.toTypedArray() ?: emptyArray(),
            existingUserAvatars = existing?.map { it.avatarUrl }?.toTypedArray() ?: emptyArray(),
            isReviewer = true
        ).show(childFragmentManager, null)
    }

    private fun showConfirmDialog() {
        ConfirmDialog.newInstance(
            getString(R.string.discard_changes_title),
            getString(R.string.discard_changes_message)
        ).show(childFragmentManager, null)
    }
}

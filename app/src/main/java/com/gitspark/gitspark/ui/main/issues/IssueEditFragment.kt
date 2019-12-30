package com.gitspark.gitspark.ui.main.issues

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.*
import com.gitspark.gitspark.helper.ColorHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.dialog.AssigneesDialog
import com.gitspark.gitspark.ui.dialog.AssigneesDialogCallback
import com.gitspark.gitspark.ui.dialog.LabelsDialog
import com.gitspark.gitspark.ui.dialog.LabelsDialogCallback
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_issue_edit.*
import javax.inject.Inject

class IssueEditFragment : BaseFragment<IssueEditViewModel>(IssueEditViewModel::class.java),
    AssigneesDialogCallback, LabelsDialogCallback {

    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>
    @Inject lateinit var colorHelper: ColorHelper

    private var issue: Issue? = null

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

        arguments?.let { issue = issueJsonAdapter.fromJson(it.getString(BUNDLE_ISSUE) ?: "") }
        issue?.let { viewModel.setInitialState(it, arguments?.getString(BUNDLE_REPO_FULLNAME) ?: "") }

        setUpListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.showAssigneesDialog.observe(viewLifecycleOwner) { showAssigneesDialog(it) }
        viewModel.loadAssigneesAndLabels.observe(viewLifecycleOwner) { loadAssigneesAndLabels(it) }
        viewModel.showLabelsDialog.observe(viewLifecycleOwner) { showLabelsDialog(it) }
    }

    override fun onAssigneesSet(assignees: List<User>) = viewModel.onAssigneesSet(assignees)

    override fun onLabelsSet(labels: List<Label>) = viewModel.onLabelsSet(labels)

    private fun updateView(viewState: IssueEditViewState) {
        with (viewState) {
            if (edit_title.getString() != title) edit_title.setText(title)
            if (edit_desc.getString() != body) edit_desc.setText(body)

            issue?.let { i ->
                println("1. ${i.assignees.map{it.login}} 2. ${assignees}")
                edit_issue_button.isEnabled = i.title != title ||
                        i.body != body ||
                        i.assignees.map { it.login } != assignees ||
                        i.labels.map { it.name } != labels
            }
        }
    }

    private fun loadAssigneesAndLabels(pair: Pair<List<User>, List<Label>>) {
        no_assignees_text.isVisible = pair.first.isEmpty()
        assignees_container.isVisible = pair.first.isNotEmpty()
        if (pair.first.isNotEmpty()) {
            assignees_container.removeAllViews()
            pair.first.forEach { user ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.user_icon_view, assignees_container, false)
                if (user.avatarUrl.isNotEmpty()) {
                    (((view as LinearLayout).getChildAt(0) as CardView).getChildAt(0) as ImageView).loadImage(user.avatarUrl)
                }
                ((view as LinearLayout).getChildAt(1) as TextView).text = user.login
                assignees_container.addView(view)
            }
        }

        no_labels_text.isVisible = pair.second.isEmpty()
        labels_container.isVisible = pair.second.isNotEmpty()
        if (pair.second.isNotEmpty()) {
            labels_container.removeAllViews()
            pair.second.forEach { label ->
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
    }

    private fun setUpListeners() {
        edit_title.afterTextChanged { viewModel.onTitleChanged(edit_title.getString()) }
        edit_desc.afterTextChanged { viewModel.onBodyChanged(edit_desc.getString()) }
        assignees_button.setOnClickListener { viewModel.onAssigneesButtonClicked() }
        labels_button.setOnClickListener { viewModel.onLabelsButtonClicked() }
    }

    private fun showAssigneesDialog(users: List<User>) {
        val assignees = if (viewModel.assignees == null) issue?.assignees else viewModel.assignees
        AssigneesDialog.newInstance(
            users = users.map { it.login }.toTypedArray(),
            userAvatars = users.map { it.avatarUrl }.toTypedArray(),
            assignees = assignees?.map { it.login }?.toTypedArray() ?: emptyArray(),
            assigneeAvatars = assignees?.map { it.avatarUrl }?.toTypedArray() ?: emptyArray()
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
}

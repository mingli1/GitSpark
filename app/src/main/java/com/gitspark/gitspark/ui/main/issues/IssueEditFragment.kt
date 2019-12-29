package com.gitspark.gitspark.ui.main.issues

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.*
import com.gitspark.gitspark.helper.ColorHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.dialog.AssigneesDialog
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_issue_edit.*
import javax.inject.Inject

class IssueEditFragment : BaseFragment<IssueEditViewModel>(IssueEditViewModel::class.java) {

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
    }

    private fun updateView(viewState: IssueEditViewState) {
        with (viewState) {
            if (edit_title.getStringTrimmed() != title) edit_title.setText(title)
            if (edit_desc.getStringTrimmed() != body) edit_desc.setText(body)

            if (updateContainers) {
                no_assignees_text.isVisible = assignees.isEmpty()
                assignees_container.isVisible = assignees.isNotEmpty()
                if (assignees.isNotEmpty()) {
                    assignees_container.removeAllViews()
                    assignees.forEach { user ->
                        val imageView = LayoutInflater.from(context)
                            .inflate(R.layout.user_icon_view, assignees_container, false)
                        if (user.avatarUrl.isNotEmpty()) {
                            ((imageView as CardView).getChildAt(0) as ImageView).loadImage(user.avatarUrl)
                            assignees_container.addView(imageView)
                        }
                    }
                }

                no_labels_text.isVisible = labels.isEmpty()
                labels_container.isVisible = labels.isNotEmpty()
                if (labels.isNotEmpty()) {
                    labels_container.removeAllViews()
                    labels.forEach { label ->
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

            issue?.let {
                edit_issue_button.isEnabled = it.title != title ||
                        it.body != body ||
                        it.assignees != assignees ||
                        it.labels != labels
            }
        }
    }

    private fun setUpListeners() {
        edit_title.afterTextChanged { viewModel.onTitleChanged(edit_title.getStringTrimmed()) }
        edit_desc.afterTextChanged { viewModel.onBodyChanged(edit_desc.getStringTrimmed()) }
        assignees_button.setOnClickListener { viewModel.onAssigneesButtonClicked() }
    }

    private fun showAssigneesDialog(users: List<User>) {
        AssigneesDialog.newInstance(
            users = users.map { it.login }.toTypedArray(),
            assignees = issue?.assignees?.map { it.login }?.toTypedArray() ?: emptyArray(),
            avatars = users.map { it.avatarUrl }.toTypedArray()
        ).show(childFragmentManager, null)
    }
}

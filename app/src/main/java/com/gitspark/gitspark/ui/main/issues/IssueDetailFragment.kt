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
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_issue_detail.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

const val BUNDLE_ISSUE = "BUNDLE_ISSUE"

class IssueDetailFragment : BaseFragment<IssueDetailViewModel>(IssueDetailViewModel::class.java) {

    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>
    @Inject lateinit var colorHelper: ColorHelper

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

        issueJsonAdapter.fromJson(arguments?.getString(BUNDLE_ISSUE) ?: "")?.let {
            viewModel.onStart(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
    }

    private fun updateView(viewState: IssueDetailViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading

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
                    val imageView = LayoutInflater.from(context)
                        .inflate(R.layout.user_icon_view, assignees_container, false)
                    if (it.avatarUrl.isNotEmpty()) {
                        ((imageView as CardView).getChildAt(0) as ImageView).loadImage(it.avatarUrl)
                        assignees_container.addView(imageView)
                    }
                }
            }
        }
    }
}
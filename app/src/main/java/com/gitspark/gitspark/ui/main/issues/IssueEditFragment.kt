package com.gitspark.gitspark.ui.main.issues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.gitspark.gitspark.R
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject

class IssueEditFragment : BaseFragment<IssueEditViewModel>(IssueEditViewModel::class.java) {

    @Inject lateinit var issueJsonAdapter: JsonAdapter<Issue>

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

        arguments?.let {
            issue = issueJsonAdapter.fromJson(it.getString(BUNDLE_ISSUE) ?: "")
        }
    }

    override fun observeViewModel() {

    }
}
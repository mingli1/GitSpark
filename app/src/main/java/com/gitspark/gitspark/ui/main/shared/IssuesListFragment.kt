package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_issue_list.*

const val BUNDLE_ISSUE_LIST_TYPE = "BUNDLE_ISSUE_LIST_TYPE"
const val BUNDLE_ISSUE_TYPE = "BUNDLE_ISSUE_TYPE"

class IssuesListFragment : BaseFragment<IssuesListViewModel>(IssuesListViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_issue_list, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        app_bar_layout.isVisible = arguments?.getString(BUNDLE_ISSUE_LIST_TYPE) == "list"

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                title = getString(R.string.issues_button_text)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun observeViewModel() {

    }
}
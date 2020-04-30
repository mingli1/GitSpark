package com.gitspark.gitspark.ui.main.issues.pullrequest

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.showOptionIcons
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.issues.IssueDetailFragment
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.gitspark.gitspark.ui.main.shared.CommitListFragment
import com.gitspark.gitspark.ui.main.shared.FileListFragment

class PullRequestDetailFragment : BaseFragment<PullRequestDetailViewModel>(PullRequestDetailViewModel::class.java) {

    private lateinit var issueDetailFragment: IssueDetailFragment
    private lateinit var commitListFragment: CommitListFragment
    private lateinit var fileListFragment: FileListFragment

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
}
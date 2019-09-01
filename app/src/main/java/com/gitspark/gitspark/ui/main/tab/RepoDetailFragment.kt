package com.gitspark.gitspark.ui.main.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import br.tiagohm.markdownview.css.styles.Github
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.adapter.BUNDLE_REPO_FULLNAME
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_repo_detail.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

class RepoDetailFragment : BaseFragment<RepoDetailViewModel>(RepoDetailViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_repo_detail, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                title = arguments?.getString(BUNDLE_REPO_FULLNAME) ?: ""
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            val names = it.getString(BUNDLE_REPO_FULLNAME)!!.split("/")
            viewModel.loadRepoData(names[0], names[1])
        }
        readme_view.addStyleSheet(Github())
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
    }

    private fun updateView(viewState: RepoDetailViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading
            if (readmeUrl.isNotEmpty()) readme_view.loadMarkdownFromUrl(readmeUrl)
        }
    }
}
package com.gitspark.gitspark.ui.main.repo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO
import com.gitspark.gitspark.ui.adapter.ViewPagerAdapter
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import com.squareup.moshi.JsonAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

interface RepoDataCallback {
    fun getData(): Repo
}

class RepoDetailFragment : BaseFragment<RepoDetailViewModel>(RepoDetailViewModel::class.java), RepoDataCallback {

    @Inject lateinit var repoJsonAdapter: JsonAdapter<Repo>
    private lateinit var repoData: Repo
    private var repoFullName: String? = null
    private lateinit var repoOverviewFragment: RepoOverviewFragment
    private lateinit var repoContentFragment: RepoContentFragment

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(BUNDLE_REPO_FULLNAME)?.let {
            repoFullName = it
        }
        arguments?.getString(BUNDLE_REPO)?.let {
            repoData = repoJsonAdapter.fromJson(it) ?: Repo()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_repo_detail, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        (activity as MainActivity).setSupportActionBar(toolbar)
        if (repoFullName == null) setToolbarTitle()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.repoData?.let { repoData = it }

        if (repoFullName == null) {
            setUpFragments()
        }
        else {
            val args = repoFullName!!.split("/")
            viewModel.fetchRepoData(args[0], args[1])
        }
    }

    override fun getData() = repoData

    override fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) { loading_indicator.isVisible = it }
        viewModel.repoDataRetrieved.observe(viewLifecycleOwner) {
            repoData = it
            setToolbarTitle()
            setUpFragments()
        }
        viewModel.exitFragment.observe(viewLifecycleOwner) { findNavController().navigateUp() }
        viewModel.branchesData.observe(viewLifecycleOwner) { repoContentFragment.notifyBranchDataRetrieved(it) }
        viewModel.watchingData.observe(viewLifecycleOwner) { repoOverviewFragment.notifyWatchingDataRetrieved(it) }
        viewModel.numWatchersData.observe(viewLifecycleOwner) { repoOverviewFragment.notifyNumWatchersDataRetrieved(it) }
        viewModel.starringData.observe(viewLifecycleOwner) { repoOverviewFragment.notifyStarringDataRetrieved(it) }
        viewModel.languagesData.observe(viewLifecycleOwner) { repoOverviewFragment.notifyLanguagesDataRetrieved(it) }
    }

    private fun setToolbarTitle() {
        (activity as MainActivity).supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
            title = repoData.fullName
        }
    }

    private fun setUpFragments() {
        repoOverviewFragment = RepoOverviewFragment()
        repoContentFragment = RepoContentFragment()
        val adapter = ViewPagerAdapter(childFragmentManager).apply {
            addFragment(repoOverviewFragment, getString(R.string.overview_title))
            addFragment(repoContentFragment, getString(R.string.code_title))
        }
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)

        viewModel.fetchAdditionalRepoData(repoData)
    }
}
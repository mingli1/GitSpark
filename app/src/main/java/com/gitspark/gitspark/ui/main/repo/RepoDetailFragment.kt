package com.gitspark.gitspark.ui.main.repo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gitspark.gitspark.R
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.ui.adapter.BUNDLE_REPO
import com.gitspark.gitspark.ui.adapter.ViewPagerAdapter
import com.gitspark.gitspark.ui.main.MainActivity
import com.squareup.moshi.JsonAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

interface RepoDataCallback {
    fun getData(): Repo
}

class RepoDetailFragment : Fragment(), RepoDataCallback {

    @Inject lateinit var repoJsonAdapter: JsonAdapter<Repo>
    private lateinit var repoData: Repo
    private lateinit var repoOverviewFragment: RepoOverviewFragment
    private lateinit var repoCodeFragment: RepoCodeFragment

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(BUNDLE_REPO)?.let {
            repoData = repoJsonAdapter.fromJson(it) ?: Repo()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_repo_detail, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                title = repoData.fullName
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        repoOverviewFragment = RepoOverviewFragment()
        repoCodeFragment = RepoCodeFragment()
        val adapter = ViewPagerAdapter(childFragmentManager).apply {
            addFragment(repoOverviewFragment, getString(R.string.overview_title))
            addFragment(repoCodeFragment, getString(R.string.code_title))
        }
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
    }

    override fun getData() = repoData
}
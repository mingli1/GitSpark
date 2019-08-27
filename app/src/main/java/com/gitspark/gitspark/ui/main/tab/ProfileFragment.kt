package com.gitspark.gitspark.ui.main.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.adapter.ViewPagerAdapter
import com.gitspark.gitspark.ui.main.tab.profile.*
import kotlinx.android.synthetic.main.fragment_profile.*

private const val FOLLOWS_INDEX = 2

class ProfileFragment : Fragment() {

    private lateinit var overViewFragment: OverviewFragment
    private lateinit var reposFragment: ReposFragment
    private lateinit var followsFragment: FollowsFragment
    private lateinit var starsFragment: StarsFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        overViewFragment = OverviewFragment()
        reposFragment = ReposFragment()
        followsFragment = FollowsFragment()
        starsFragment = StarsFragment()

        setUpTabLayout()
    }

    fun navigateToFollowsFragment(followState: FollowState) {
        viewpager.setCurrentItem(FOLLOWS_INDEX, true)
        followsFragment.onNavigatedTo(followState)
    }

    private fun setUpTabLayout() {
        val adapter = ViewPagerAdapter(childFragmentManager).apply {
            addFragment(overViewFragment, getString(R.string.overview_title))
            addFragment(reposFragment, getString(R.string.repos_title))
            addFragment(followsFragment, getString(R.string.follows_title))
            addFragment(starsFragment, getString(R.string.stars_title))
        }
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
    }
}
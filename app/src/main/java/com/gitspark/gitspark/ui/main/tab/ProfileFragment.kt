package com.gitspark.gitspark.ui.main.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.main.tab.profile.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpTabLayout()
    }

    private fun setUpTabLayout() {
        val adapter = ViewPagerAdapter(childFragmentManager).apply {
            addFragment(OverviewFragment(), getString(R.string.overview_title))
            addFragment(ReposFragment(), getString(R.string.repos_title))
            addFragment(FollowsFragment(), getString(R.string.follows_title))
            addFragment(StarsFragment(), getString(R.string.stars_title))
        }
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
    }
}
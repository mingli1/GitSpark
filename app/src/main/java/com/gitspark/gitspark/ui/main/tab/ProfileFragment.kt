package com.gitspark.gitspark.ui.main.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.adapter.ViewPagerAdapter
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.gitspark.gitspark.ui.main.tab.profile.*
import kotlinx.android.synthetic.main.fragment_profile.*

const val BUNDLE_USERNAME = "BUNDLE_USERNAME"
private const val FOLLOWS_INDEX = 2

class ProfileFragment : BaseFragment<ProfileViewModel>(ProfileViewModel::class.java) {

    private lateinit var overViewFragment: OverviewFragment
    private lateinit var reposFragment: ReposFragment
    private lateinit var followsFragment: FollowsFragment
    private lateinit var starsFragment: StarsFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.user_profile_toolbar)

        toolbar.isVisible = arguments != null

        arguments?.let {
            with (activity as MainActivity) {
                setSupportActionBar(toolbar)
                supportActionBar?.run {
                    setDisplayHomeAsUpEnabled(true)
                    setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                    title = getString(R.string.user_profile_title, it.getString(BUNDLE_USERNAME))
                }
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        overViewFragment = OverviewFragment().apply { arguments = this@ProfileFragment.arguments }
        reposFragment = ReposFragment().apply { arguments = this@ProfileFragment.arguments }
        followsFragment = FollowsFragment().apply { arguments = this@ProfileFragment.arguments }
        starsFragment = StarsFragment().apply { arguments = this@ProfileFragment.arguments }

        setUpTabLayout()
    }

    override fun observeViewModel() {

    }

    fun navigateToFollowsFragment(followState: FollowState) {
        viewpager.setCurrentItem(FOLLOWS_INDEX, true)
        followsFragment.onNavigatedTo(followState)
    }

    fun navigateToUserProfile(args: Bundle) {
        findNavController().navigate(R.id.profile_fragment, args)
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
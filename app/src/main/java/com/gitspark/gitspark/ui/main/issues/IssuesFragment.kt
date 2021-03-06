package com.gitspark.gitspark.ui.main.issues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.adapter.ViewPagerAdapter
import com.gitspark.gitspark.ui.main.shared.*
import kotlinx.android.synthetic.main.fragment_issues.*

class IssuesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_issues, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                tab_layout_bar.setExpanded(true, true)
            }
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

        val created = IssuesListFragment().apply {
            arguments = Bundle().apply { putString(BUNDLE_ISSUE_TYPE, CREATED_ISSUE_Q) }
        }
        val assigned = IssuesListFragment().apply {
            arguments = Bundle().apply { putString(BUNDLE_ISSUE_TYPE, ASSIGNED_ISSUE_Q) }
        }
        val mentioned = IssuesListFragment().apply {
            arguments = Bundle().apply { putString(BUNDLE_ISSUE_TYPE, MENTIONED_ISSUE_Q) }
        }

        val adapter = ViewPagerAdapter(childFragmentManager).apply {
            addFragment(created, getString(R.string.created))
            addFragment(assigned, getString(R.string.assigned))
            addFragment(mentioned, getString(R.string.mentioned))
        }
        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 2
        tabs.setupWithViewPager(viewpager)
    }
}
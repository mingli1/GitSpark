package com.gitspark.gitspark.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.base.BaseFragment

class ProfileFeedFragment : BaseFragment<ProfileFeedViewModel>(ProfileFeedViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_feed, container, false)
    }

    override fun observeViewModel() {

    }
}
package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gitspark.gitspark.R

class FollowsFragment : TabFragment<FollowsViewModel>(FollowsViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_follows, container, false)
    }

    override fun viewModelOnResume() {

    }

    override fun observeViewModel() {

    }
}
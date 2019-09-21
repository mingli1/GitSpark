package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.base.BaseFragment

class RepoCodeFragment : BaseFragment<RepoCodeViewModel>(RepoCodeViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repo_code, container, false)
    }

    override fun observeViewModel() {

    }
}
package com.gitspark.gitspark.ui.main.issues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.ui.base.BaseFragment

class IssueEditFragment : BaseFragment<IssueEditViewModel>(IssueEditViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun observeViewModel() {

    }
}
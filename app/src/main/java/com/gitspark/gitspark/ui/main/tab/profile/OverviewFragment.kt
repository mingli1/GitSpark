package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.base.BaseFragment

class OverviewFragment : BaseFragment<OverviewViewModel>(OverviewViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        viewModel.onResume()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(this) { updateView(it) }
        viewModel.userDataMediator.observe(this) { viewModel.onCachedUserDataRetrieved(it) }
    }

    private fun updateView(viewState: OverviewViewState) {
        with (viewState) {

        }
    }
}
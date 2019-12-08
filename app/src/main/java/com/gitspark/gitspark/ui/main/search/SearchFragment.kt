package com.gitspark.gitspark.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment<SearchViewModel>(SearchViewModel::class.java) {

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[SearchSharedViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpListeners()
    }

    override fun observeViewModel() {
        viewModel.navigateToSearchFilter.observe(viewLifecycleOwner) { navigateToSearchFilterFragment() }
    }

    private fun setUpListeners() {
        search_button.setOnClickListener { viewModel.onSearchButtonClicked() }
    }

    private fun navigateToSearchFilterFragment() {
        findNavController().navigate(R.id.action_search_to_search_filter)
    }
}
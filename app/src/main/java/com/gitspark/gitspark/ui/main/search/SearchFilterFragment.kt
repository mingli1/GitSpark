package com.gitspark.gitspark.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.adapter.ImageSpinnerAdapter
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_search_filter.*

private val SEARCH_TYPE_DRAWABLES = arrayOf(
    R.drawable.ic_repo,
    R.drawable.ic_person,
    R.drawable.ic_code,
    R.drawable.ic_commit,
    R.drawable.ic_issue,
    R.drawable.ic_pull_request
)

class SearchFilterFragment : BaseFragment<SearchFilterViewModel>(SearchFilterViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_filter, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.search_toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val spinnerAdapter= ImageSpinnerAdapter(
            context!!,
            SEARCH_TYPE_DRAWABLES,
            resources.getStringArray(R.array.search_types)
        )
        search_spinner.adapter = spinnerAdapter
    }

    override fun observeViewModel() {

    }
}
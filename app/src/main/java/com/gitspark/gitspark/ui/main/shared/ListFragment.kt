package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.adapter.PaginationListener
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.main.MainActivity
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

const val BUNDLE_HIDE_TOOLBAR = "BUNDLE_HIDE_TOOLBAR"

abstract class ListFragment<T, S : ListViewModel<T>>(clazz: Class<S>, private val pageSize: Int) : BaseFragment<S>(clazz) {

    private lateinit var paginationListener: PaginationListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val appBarLayout = view.findViewById<AppBarLayout>(R.id.app_bar_layout)

        appBarLayout.isVisible = !(arguments?.containsKey(BUNDLE_HIDE_TOOLBAR) ?: false)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE) ?: ""
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, pageSize, swipe_refresh) {
            viewModel.onScrolledToEnd()
        }
        item_list.layoutManager = layoutManager
        item_list.addOnScrollListener(paginationListener)

        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        item_list.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.pageViewState.observe(viewLifecycleOwner) { updateRecycler(it) }
    }

    protected open fun updateView(viewState: ListViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing
        }
    }

    protected open fun updateRecycler(viewState: PaginatedViewState<T>) {
        with (viewState) {
            paginationListener.isLastPage = isLastPage
            paginationListener.loading = false
            empty_text.isVisible = items.isEmpty()
        }
    }
}
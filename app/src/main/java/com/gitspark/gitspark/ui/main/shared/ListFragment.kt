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
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

abstract class ListFragment<T, S : ListViewModel<T>>(clazz: Class<S>, private val pageSize: Int) : BaseFragment<S>(clazz) {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var paginationListener: PaginationListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                title = arguments?.getString(BUNDLE_TITLE) ?: ""
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        paginationListener = PaginationListener(layoutManager, pageSize, swipe_refresh) {
            viewModel.onScrolledToEnd()
        }
        item_list.setHasFixedSize(true)
        item_list.layoutManager = layoutManager
        item_list.addOnScrollListener(paginationListener)

        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
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
    }

    protected open fun updateView(viewState: ListViewState<T>) {
        with (viewState) {
            if (updateAdapter) {
                paginationListener.isLastPage = isLastPage
                paginationListener.loading = false
            }
            empty_text.isVisible = list.isEmpty()
            loading_indicator.isVisible = loading && !refreshing
            swipe_refresh.isRefreshing = refreshing
        }
    }
}
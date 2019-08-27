package com.gitspark.gitspark.ui.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baoyz.widget.PullRefreshLayout

private const val DIRECTION_UP = -1

class PaginationListener(
    private val layoutManager: LinearLayoutManager,
    private val pageSize: Int,
    private val refreshLayout: PullRefreshLayout? = null,
    private val onUpdate: () -> Unit
) : RecyclerView.OnScrollListener() {

    var loading = false
    var isLastPage = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        // fix pull to refresh on recycler view
        refreshLayout?.isEnabled = !recyclerView.canScrollVertically(DIRECTION_UP)

        if (dy > 0) {
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (!loading && !isLastPage &&
                visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                    firstVisibleItemPosition >= 0 &&
                    totalItemCount >= pageSize) {
                onUpdate()
                loading = true
            }
        }
    }
}
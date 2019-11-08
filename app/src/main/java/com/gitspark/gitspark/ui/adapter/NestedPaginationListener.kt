package com.gitspark.gitspark.ui.adapter

import androidx.core.widget.NestedScrollView

class NestedPaginationListener(private val onChange: () -> Unit) : NestedScrollView.OnScrollChangeListener {

    var loading = false
    var isLastPage = false

    override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        if (v?.getChildAt(v.childCount - 1) != null) {
            if ((scrollY >= (v.getChildAt(v.childCount - 1)).measuredHeight - v.measuredHeight) &&
                scrollY > oldScrollY && !loading && !isLastPage) {
                onChange()
                loading = true
            }
        }
    }
}

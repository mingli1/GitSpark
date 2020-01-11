package com.gitspark.gitspark.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gitspark.gitspark.R

class ColorSwipeRefresh(context: Context, attrs: AttributeSet) : SwipeRefreshLayout(context, attrs) {

    init { setColorSchemeResources(R.color.colorAccent) }
}
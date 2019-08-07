package com.gitspark.gitspark.ui.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>(MainViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adjustBottomNavigationText()
    }

    override fun observeViewModel() {

    }

    // see https://github.com/material-components/material-components-android/issues/139
    private fun adjustBottomNavigationText() {
        val menuView = bottom_navigation.getChildAt(0) as? ViewGroup
        menuView?.let { menu ->
            for (i in 0 until menu.childCount) {
                val item = menu.getChildAt(i)
                item.findViewById<View>(R.id.largeLabel).setPadding(0, 0, 0, 0)
            }
        }
    }
}
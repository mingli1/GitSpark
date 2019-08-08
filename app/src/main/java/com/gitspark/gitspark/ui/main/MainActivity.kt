package com.gitspark.gitspark.ui.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>(MainViewModel::class.java) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adjustBottomNavigationText()

        navController = Navigation.findNavController(this, R.id.fragment_nav)
        bottom_navigation.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
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
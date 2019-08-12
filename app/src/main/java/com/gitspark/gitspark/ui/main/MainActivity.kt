package com.gitspark.gitspark.ui.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var navController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adjustBottomNavigationText()

        if (savedInstanceState == null) {
            setUpNavController()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.value?.navigateUp() ?: false
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setUpNavController()
    }

    private fun setUpNavController() {
        navController = bottom_navigation_view.setupWithNavController(
            navGraphIds = listOf(
                R.navigation.feed_navigation,
                R.navigation.profile_navigation,
                R.navigation.search_navigation,
                R.navigation.pr_navigation,
                R.navigation.issues_navigation
            ),
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
    }

    // see https://github.com/material-components/material-components-android/issues/139
    private fun adjustBottomNavigationText() {
        val menuView = bottom_navigation_view.getChildAt(0) as? ViewGroup
        menuView?.let { menu ->
            for (i in 0 until menu.childCount) {
                val item = menu.getChildAt(i)
                item.findViewById<View>(R.id.largeLabel).setPadding(0, 0, 0, 0)
            }
        }
    }
}
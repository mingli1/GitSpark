package com.gitspark.gitspark.ui.main

import android.os.Bundle
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
}
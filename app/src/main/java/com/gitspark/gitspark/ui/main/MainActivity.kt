package com.gitspark.gitspark.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.setupWithNavController
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val navigateToProfileFeedAction = SingleLiveAction()
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
                R.navigation.home_navigation,
                R.navigation.profile_navigation,
                R.navigation.search_navigation,
                R.navigation.issues_navigation,
                R.navigation.pr_navigation
            ),
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
        addDestinationListener()
    }

    private fun addDestinationListener() {
        navController?.observe(this) {
            it.addOnDestinationChangedListener { _, dest, _ ->
                bottom_navigation_view.isVisible = when (dest.id) {
                    R.id.edit_profile_fragment -> false
                    R.id.search_filter_fragment -> false
                    else -> true
                }
            }
        }
    }
}
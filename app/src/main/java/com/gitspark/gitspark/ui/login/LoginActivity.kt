package com.gitspark.gitspark.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.base.BaseActivity
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

class LoginActivity : BaseActivity<LoginViewModel>(LoginViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sign_in.setOnClickListener { viewModel.onSignIn() }
    }

    override fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading_indicator.isVisible = it }
        viewModel.openLoginBrowser.observe(this) { openLoginBrowser(it) }
        viewModel.navigateToMainActivityAction.observe(this) { navigateToMainActivity() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let {
            if (it.toString().startsWith(BuildConfig.CALLBACK_URL)) {
                val code = it.getQueryParameter("code")
                viewModel.onCodeReceived(code ?: "")
            }
        }
        setIntent(null)
    }

    private fun openLoginBrowser(uri: Uri) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setToolbarColor(getColor(R.color.colorPrimary))
            .setShowTitle(true)
            .build()
        customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        println(uri.toString())
        customTabsIntent.launchUrl(this, uri)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
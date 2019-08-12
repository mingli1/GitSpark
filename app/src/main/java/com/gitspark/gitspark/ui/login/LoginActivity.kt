package com.gitspark.gitspark.ui.login

import android.content.Intent
import android.os.Bundle
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.getString
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.onTextChanged
import com.gitspark.gitspark.ui.base.BaseActivity
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

class LoginActivity : BaseActivity<LoginViewModel>(LoginViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpListeners()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(this) { updateView(it) }
        viewModel.navigateToMainActivityAction.observe(this) { navigateToMainActivity() }
    }

    private fun updateView(viewState: LoginViewState) {
        with (viewState) {
            login_button.isEnabled = loginButtonEnabled
            loading_indicator.isVisible = loading
        }
    }

    private fun setUpListeners() {
        with (viewModel) {
            username_field.onTextChanged {
                onTextChanged(username_field.getString(), password_field.getString())
            }
            password_field.onTextChanged {
                onTextChanged(username_field.getString(), password_field.getString())
            }
            login_button.setOnClickListener { attemptLogin() }
            not_now_button.setOnClickListener { onNotNowClicked() }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
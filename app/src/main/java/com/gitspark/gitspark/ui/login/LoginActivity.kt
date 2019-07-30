package com.gitspark.gitspark.ui.login

import android.os.Bundle
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.onTextChanged
import com.gitspark.gitspark.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<LoginViewModel>(LoginViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpListeners()
    }

    private fun setUpListeners() {
        username_field.onTextChanged {  }
        login_button.setOnClickListener { viewModel.attemptLogin(username_field) }
        not_now_button.setOnClickListener { viewModel.onNotNowClicked() }
    }
}
package com.gitspark.gitspark.ui.login

import android.os.Bundle
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.base.BaseActivity

class LoginActivity : BaseActivity<LoginViewModel>(LoginViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}
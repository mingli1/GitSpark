package com.gitspark.gitspark.ui.main

import android.os.Bundle
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.base.BaseActivity

class MainActivity : BaseActivity<MainViewModel>(MainViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun observeViewModel() {

    }
}
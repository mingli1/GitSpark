package com.gitspark.gitspark.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjection
import javax.inject.Inject

abstract class BaseActivity<T : ViewModel>(private val clazz: Class<T>) : AppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    protected val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[clazz]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}
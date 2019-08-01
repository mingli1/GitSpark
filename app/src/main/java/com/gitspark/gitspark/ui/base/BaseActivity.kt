package com.gitspark.gitspark.ui.base

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.gitspark.gitspark.extension.observe
import dagger.android.AndroidInjection
import javax.inject.Inject

abstract class BaseActivity<T : BaseViewModel>(private val clazz: Class<T>) : AppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    protected val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[clazz]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel.checkInitialized()
        observeViewModel()
        observeBaseViewModel()
    }

    abstract fun observeViewModel()

    private fun observeBaseViewModel() {
        viewModel.alertAction.observe(this) { showAlert(it) }
    }

    private fun showAlert(message: String) = Toast.makeText(this, message, LENGTH_SHORT).show()
}
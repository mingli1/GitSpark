package com.gitspark.gitspark.ui.base

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.gitspark.gitspark.extension.observe
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment<T : BaseViewModel>(private val clazz: Class<T>) : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    protected val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[clazz]
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.checkInitialized()
        observeViewModel()
        observeBaseViewModel()
    }

    open fun observeViewModel() {}

    private fun observeBaseViewModel() {
        viewModel.alertAction.observe(viewLifecycleOwner) { showAlert(it) }
    }

    private fun showAlert(message: String) = Toast.makeText(context, message, LENGTH_SHORT).show()
}

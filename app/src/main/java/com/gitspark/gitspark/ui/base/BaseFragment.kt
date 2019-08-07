package com.gitspark.gitspark.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
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
}

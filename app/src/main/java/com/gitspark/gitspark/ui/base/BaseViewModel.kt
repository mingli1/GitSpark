package com.gitspark.gitspark.ui.base

import androidx.lifecycle.ViewModel
import javax.inject.Inject

interface SupportViewModel {
    fun checkInitialized()
    fun initialize()
}

open class BaseViewModel @Inject constructor() : ViewModel(), SupportViewModel {

    private var initialized = false

    override fun checkInitialized() {
        if (!initialized) {
            initialize()
            initialized = true
        }
    }

    override fun initialize() {}
}
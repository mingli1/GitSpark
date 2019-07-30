package com.gitspark.gitspark.ui.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private var initialized = false

    fun checkInitialized() {
        if (!initialized) {
            initialize()
            initialized = true
        }
    }

    open fun initialize() {}
}
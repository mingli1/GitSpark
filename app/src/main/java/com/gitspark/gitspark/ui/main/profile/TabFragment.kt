package com.gitspark.gitspark.ui.main.profile

import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.base.BaseViewModel

abstract class TabFragment<T : BaseViewModel>(clazz: Class<T>) : BaseFragment<T>(clazz) {

    private var visible = false
    private var started = false

    override fun onStart() {
        super.onStart()
        started = true
        if (visible) viewModelOnResume()
    }

    override fun onStop() {
        super.onStop()
        started = false
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        visible = isVisibleToUser
        if (visible && started) viewModelOnResume()
    }

    abstract fun viewModelOnResume()
}
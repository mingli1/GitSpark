package com.gitspark.gitspark.ui.livedata

import androidx.annotation.MainThread

class SingleLiveAction : SingleLiveEvent<Unit>() {

    @MainThread
    fun call() {
        super.setValue(Unit)
    }
}
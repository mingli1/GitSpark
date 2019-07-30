package com.gitspark.gitspark.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (t: T?) -> Unit) {
    observe(owner, Observer { observer(it) })
}
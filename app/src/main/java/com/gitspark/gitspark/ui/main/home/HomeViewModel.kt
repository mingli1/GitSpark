package com.gitspark.gitspark.ui.main.home

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<HomeViewState>()

    private var started = false
    private var page = 1

    fun onStart() {

    }
}
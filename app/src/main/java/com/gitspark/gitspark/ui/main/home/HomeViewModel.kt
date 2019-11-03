package com.gitspark.gitspark.ui.main.home

import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : BaseViewModel() {


}
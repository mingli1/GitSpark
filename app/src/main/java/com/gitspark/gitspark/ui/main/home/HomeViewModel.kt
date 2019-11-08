package com.gitspark.gitspark.ui.main.home

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<HomeViewState>()
    val userMediator = MediatorLiveData<AuthUser>()

    private var started = false
    private var page = 1
    private var username: String? = null

    fun onStart() {

    }
}
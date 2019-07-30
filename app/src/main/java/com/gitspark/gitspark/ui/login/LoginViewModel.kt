package com.gitspark.gitspark.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {

    val viewState = MutableLiveData<LoginViewState>()

    fun attemptLogin(username: String, password: String) {

    }

    fun onNotNowClicked() {

    }
}
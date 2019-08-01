package com.gitspark.gitspark.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.repository.LoginRepository
import com.gitspark.gitspark.repository.LoginResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import okhttp3.Credentials
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<LoginViewState>()

    private var currentUsername = ""
    private var currentPassword = ""

    override fun initialize() {
        viewState.value = LoginViewState(loginButtonEnabled = false)
    }

    fun attemptLogin() {
        val authToken = Credentials.basic(currentUsername, currentPassword)
        subscribe(loginRepository.login(authToken)) { result ->
            when (result) {
                is LoginResult.Success -> Log.d("LoginViewModel", result.token.token)
                is LoginResult.Failure -> Log.d("LoginViewModel", result.error)
            }
        }
    }

    fun onTextChanged(username: String, password: String) {
        currentUsername = username
        currentPassword = password

        viewState.value = viewState.value?.copy(
            loginButtonEnabled = username.isNotEmpty() && password.isNotEmpty()
        )
    }

    fun onNotNowClicked() {

    }
}
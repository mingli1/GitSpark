package com.gitspark.gitspark.ui.login

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.PREFERENCES_TOKEN
import com.gitspark.gitspark.model.Token
import com.gitspark.gitspark.repository.LoginRepository
import com.gitspark.gitspark.repository.LoginResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import okhttp3.Credentials
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val preferencesHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<LoginViewState>()

    private var currentUsername = ""
    private var currentPassword = ""

    override fun initialize() {
        viewState.value = LoginViewState(loginButtonEnabled = false)
    }

    fun attemptLogin() {
        val authToken = Credentials.basic(currentUsername, currentPassword)
        subscribe(loginRepository.putAuth(authToken)) { handleLoginResult(it) }
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

    private fun handleLoginResult(result: LoginResult) {
        when (result) {
            is LoginResult.Success -> onLoginAuthSuccess(result.token)
            is LoginResult.Failure -> { result.error?.let { alert(it) } }
        }
    }

    private fun onLoginAuthSuccess(token: Token) {
        when {
            token.value.isEmpty() -> onNewAccessTokenCreated(token)
            else -> onExistingAccessToken(token)
        }
    }

    private fun onNewAccessTokenCreated(token: Token) {
        with (token) {
            preferencesHelper.saveString(PREFERENCES_TOKEN, value)
            preferencesHelper.saveString(hashedValue, value)
            alert("Logging in with new token: $value")
        }
    }

    private fun onExistingAccessToken(token: Token) {
        with (token) {
            if (preferencesHelper.contains(hashedValue)) {
                alert("Logging in with existing token: ${preferencesHelper.getString(hashedValue)}")
            }
            // this case only occurs when the user authenticates then uninstalls and re-installs
            // the app with the authentication still existing but not cached
            else {

            }
        }
    }
}
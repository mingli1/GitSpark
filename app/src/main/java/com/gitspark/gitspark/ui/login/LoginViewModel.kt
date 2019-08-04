package com.gitspark.gitspark.ui.login

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.model.Token
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
    private var basicToken = ""

    override fun initialize() {
        viewState.value = LoginViewState(loginButtonEnabled = false)
    }

    fun attemptLogin() {
        basicToken = Credentials.basic(currentUsername.trim(), currentPassword)
        subscribe(loginRepository.putAuthorizations(basicToken)) { handleLoginResult(it) }
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
            token.value.isNotEmpty() -> onNewAccessTokenCreated(token)
            else -> onExistingAccessToken(token)
        }
    }

    private fun onNewAccessTokenCreated(token: Token) {
        loginRepository.cacheAccessToken(token)
        alert("Logging in with new token")
    }

    private fun onExistingAccessToken(token: Token) {
        if (loginRepository.isTokenCached(token)) {
            alert("Logging in with existing token")
        }
        // this case only occurs when the user authenticates then uninstalls and re-installs
        // the app with the authentication still existing but not cached
        else {
            deleteExistingToken()
        }
    }

    private fun deleteExistingToken() {
        subscribe(loginRepository.getAuthorizations(basicToken)) { tokenList ->
            if (tokenList.isNotEmpty()) {
                val authId = tokenList.find { token ->
                    token.note == BuildConfig.APPLICATION_ID
                }?.tokenId

                authId?.let {
                    subscribe(loginRepository.deleteAuthorization(basicToken, it),
                        { attemptLogin() },
                        { throwable -> alert("Error deleting token: ${throwable.message}") }
                    )
                } ?: throw IllegalStateException("Access token not cached and does not exist.")
            }
        }
    }
}
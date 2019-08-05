package com.gitspark.gitspark.ui.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.model.Token
import com.gitspark.gitspark.repository.LoginRepository
import com.gitspark.gitspark.repository.LoginResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import okhttp3.Credentials
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<LoginViewState>()
    val navigateToMainActivityAction = SingleLiveAction()

    @VisibleForTesting var currentUsername = ""
    @VisibleForTesting var currentPassword = ""
    @VisibleForTesting var basicToken = ""

    override fun initialize() {
        viewState.value = LoginViewState()
    }

    fun attemptLogin() {
        basicToken = Credentials.basic(currentUsername.trim(), currentPassword)
        subscribe(loginRepository.putAuthorizations(basicToken)) { handleLoginResult(it) }
        setLoading(true)
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

    private fun onSuccessfulLogin() {
        setLoading(false)
        navigateToMainActivityAction.call()
    }

    private fun setLoading(loading: Boolean) {
        viewState.value = viewState.value?.copy(loading = loading)
    }

    private fun handleLoginResult(result: LoginResult) {
        when (result) {
            is LoginResult.Success -> onLoginAuthSuccess(result.token)
            is LoginResult.Failure -> { onLoginAuthFailure(result.error) }
        }
    }

    private fun onLoginAuthSuccess(token: Token) {
        when {
            token.value.isNotEmpty() -> onNewAccessTokenCreated(token)
            else -> onExistingAccessToken(token)
        }
    }

    private fun onLoginAuthFailure(error: String?) {
        setLoading(false)
        error?.let { alert(it) }
    }

    private fun onNewAccessTokenCreated(token: Token) {
        loginRepository.cacheAccessToken(token)
        onSuccessfulLogin()
    }

    private fun onExistingAccessToken(token: Token) {
        if (loginRepository.isTokenCached(token)) {
            onSuccessfulLogin()
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
                        { throwable ->
                            alert("Error deleting token: ${throwable.message}")
                            setLoading(false)
                        }
                    )
                } ?: throw IllegalStateException("Access token not cached and does not exist.")
            }
        }
    }
}
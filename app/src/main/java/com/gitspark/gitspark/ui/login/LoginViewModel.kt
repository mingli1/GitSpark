package com.gitspark.gitspark.ui.login

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.PREFERENCES_LOGIN
import com.gitspark.gitspark.repository.*
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import io.reactivex.Completable
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val isLoading = MutableLiveData(false)
    val openLoginBrowser = MutableLiveData<Uri>()
    val navigateToMainActivityAction = SingleLiveAction()

    override fun initialize() {
        if (prefsHelper.hasExistingAccessToken()) {
            isLoading.value = true
            onSuccessfulLogin()
        }
    }

    fun onSignIn() {
        val uri = loginRepository.getAuthorizationUri()
        openLoginBrowser.value = uri
    }

    fun onCodeReceived(code: String) {
        if (code.isEmpty()) {
            alert("Failed to authenticate.")
            return
        }
        isLoading.value = true
        subscribe(loginRepository.getAccessToken(code)) {
            when (it) {
                is LoginResult.Success -> {
                    prefsHelper.cacheAccessToken(it.value.token)
                    onSuccessfulLogin()
                }
                is LoginResult.Failure -> alert(it.error)
            }
            isLoading.value = false
        }
    }

    @VisibleForTesting
    fun onSuccessfulLogin() {
        subscribe(userRepository.getAuthUser()) {
            val userCompletable = when (it) {
                is UserResult.Success -> {
                    prefsHelper.saveString(PREFERENCES_LOGIN, it.value.login)
                    userRepository.cacheUserData(it.value)
                }
                is UserResult.Failure ->
                    Completable.error(Throwable(message = it.error))
            }
            subscribe(userCompletable,
                { navigateToMainActivityAction.call() },
                { error ->
                    isLoading.value = false
                    alert("Error: ${error.message}")
                }
            )
        }
    }
}
package com.gitspark.gitspark.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Token
import com.gitspark.gitspark.repository.LoginRepository
import com.gitspark.gitspark.repository.LoginResult
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.Credentials
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: LoginViewModel
    @RelaxedMockK private lateinit var loginRepository: LoginRepository
    @RelaxedMockK private lateinit var userRepository: UserRepository
    @RelaxedMockK private lateinit var prefsHelper: PreferencesHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = LoginViewModel(loginRepository, userRepository, prefsHelper)
        viewModel.initialize()
    }

    @Test
    fun shouldInitializeViewState() {
        assertThat(viewState().loading).isFalse()
        assertThat(viewState().loginButtonEnabled).isFalse()
    }

    @Test
    fun shouldUpdateCurrentCredentialsOnTextChanged() {
        viewModel.onTextChanged("a", "b")
        assertThat(viewModel.currentUsername).isEqualTo("a")
        assertThat(viewModel.currentPassword).isEqualTo("b")
    }

    @Test
    fun shouldEnableLoginButtonWhenCredentialsNotEmpty() {
        viewModel.onTextChanged("a", "b")
        assertThat(viewState().loginButtonEnabled).isTrue()
    }

    @Test
    fun shouldDisableLoginButtonWhenAtLeastOneCredentialEmpty() {
        viewModel.onTextChanged("", "b")
        assertThat(viewState().loginButtonEnabled).isFalse()
    }

    @Test
    fun shouldCreateBasicAuthFromCredentials() {
        val token = Credentials.basic("abc", "xyz")

        viewModel.onTextChanged("abc", "xyz")
        viewModel.attemptLogin()

        assertThat(viewModel.basicToken).isEqualTo(token)
    }

    @Test
    fun shouldShowLoadingIndicatorOnLoginAttempt() {
        viewModel.attemptLogin()
        assertThat(viewState().loading).isTrue()
    }

    @Test
    fun shouldNavigateToMainActivityAndCacheOnSuccessfulUserData() {
        val result = createUserSuccess(AuthUser())

        viewModel.handleUserResult(result)

        verify { userRepository.cacheUserData(result.user) }
        assertThat(viewModel.navigateToMainActivityAction.value).isNotNull
        assertThat(viewState().loading).isFalse()
    }

    @Test
    fun shouldAlertOnUserDataFailure() {
        val result = createUserFailure("error")
        viewModel.handleUserResult(result)
        assertThat(viewModel.alertAction.value).isEqualTo("error")
    }

    @Test
    fun shouldAlertOnLoginFailure() {
        val failure = createLoginFailure("Failure")

        viewModel.handleLoginResult(failure)

        assertThat(viewState().loading).isFalse()
        assertThat(viewModel.alertAction.value).isEqualTo(failure.error)
    }

    @Test
    fun shouldCacheNewAccessToken() {
        val token = getToken("abc123")
        val success = createLoginSuccess(token)

        viewModel.handleLoginResult(success)

        verify { prefsHelper.cacheAccessToken(token) }
    }

    @Test
    fun shouldGetAuthUserDataWhenLoggedIn() {
        val token = getToken("")
        val success = createLoginSuccess(token)
        every { prefsHelper.isTokenCached(any()) } returns true

        viewModel.handleLoginResult(success)

        verify { userRepository.getAuthUser(any()) }
    }

    @Test
    fun shouldLoginWithoutCredentialsIfAccessTokenExists() {
        every { prefsHelper.hasExistingAccessToken() } returns true
        viewModel.initialize()
        verify { userRepository.getAuthUser(any()) }
    }

    private fun getToken(value: String) =
        Token(tokenId = 0, value = value, scopes = emptyList(), note = "", hashedValue = "")

    private fun createLoginSuccess(token: Token) =
        LoginResult.Success(token)

    private fun createLoginFailure(error: String) =
        LoginResult.Failure(error)

    private fun createUserSuccess(user: AuthUser) =
        UserResult.Success(user)

    private fun createUserFailure(error: String) =
        UserResult.Failure(error)

    private fun viewState() = viewModel.viewState.value!!
}
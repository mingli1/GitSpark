package com.gitspark.gitspark.repository

import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.PREFERENCES_TOKEN
import com.gitspark.gitspark.model.Token
import com.squareup.moshi.Moshi
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

private const val BASIC_TOKEN = "Basic abc123"
private const val AUTH_ID = 0

private val tokenSuccess = Token(
    tokenId = 1,
    value = "abcdefgh12345678",
    hashedValue = "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
    scopes = listOf("public_repo"),
    note = "optional note"
)

private val tokenList = listOf(tokenSuccess, tokenSuccess, tokenSuccess)

class LoginRepositoryTest {

    private lateinit var realLoginRepository: LoginRepository
    @MockK private lateinit var loginRepository: LoginRepository
    @RelaxedMockK private lateinit var retrofitHelper: RetrofitHelper
    @RelaxedMockK private lateinit var preferencesHelper: PreferencesHelper
    @RelaxedMockK private lateinit var moshi: Moshi

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        realLoginRepository = LoginRepository(retrofitHelper, preferencesHelper, moshi)
    }

    @Test
    fun shouldPutAuthorizationsSuccess() {
        every { loginRepository.putAuthorizations(any(), any()) } returns
                Observable.just(LoginResult.Success(tokenSuccess))
        val observer = loginRepository.putAuthorizations(BASIC_TOKEN).test()
        observer.assertValue(LoginResult.Success(tokenSuccess))
    }

    @Test
    fun shouldPutAuthorizationFailure() {
        every { loginRepository.putAuthorizations(any(), any()) } returns
                Observable.just(LoginResult.Failure("failure"))
        val observer = loginRepository.putAuthorizations(BASIC_TOKEN).test()
        observer.assertValue(LoginResult.Failure("failure"))
    }

    @Test
    fun shouldGetAuthorizationsSuccess() {
        every { loginRepository.getAuthorizations(any()) } returns
                Observable.just(tokenList)
        val observer = loginRepository.getAuthorizations(BASIC_TOKEN).test()
        observer.assertValue(tokenList)
    }

    @Test
    fun shouldGetAuthorizationsFailure() {
        every { loginRepository.getAuthorizations(any()) } returns
                Observable.just(emptyList())
        val observer = loginRepository.getAuthorizations(BASIC_TOKEN).test()
        observer.assertValue(emptyList())
    }

    @Test
    fun shouldDeleteAuthorizationComplete() {
        every { loginRepository.deleteAuthorization(any(), any()) } returns
                Completable.complete()
        val observer = loginRepository.deleteAuthorization(BASIC_TOKEN, AUTH_ID).test()
        observer.assertComplete()
    }

    @Test
    fun shouldDeleteAuthorizationNever() {
        every { loginRepository.deleteAuthorization(any(), any()) } returns
                Completable.never()
        val observer = loginRepository.deleteAuthorization(BASIC_TOKEN, AUTH_ID).test()
        observer.assertNotComplete()
    }

    @Test
    fun shouldCacheAccessToken() {
        realLoginRepository.cacheAccessToken(tokenSuccess)
        verify { preferencesHelper.saveString(PREFERENCES_TOKEN, tokenSuccess.value) }
        verify { preferencesHelper.saveString(tokenSuccess.hashedValue, tokenSuccess.value) }
    }
}
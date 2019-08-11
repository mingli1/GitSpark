package com.gitspark.gitspark.repository

import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.AuthUser
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

private const val TOKEN = "Basic abc123"
private val authUser = AuthUser()

class UserRepositoryTest {

    @MockK private lateinit var userRepository: UserRepository
    @RelaxedMockK private lateinit var retrofitHelper: RetrofitHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        userRepository = UserRepository(retrofitHelper)
    }

    @Test
    fun shouldGetUserSuccess() {
        every { userRepository.getAuthUser(any()) } returns
                Observable.just(UserResult.Success(authUser))
        val observer = userRepository.getAuthUser(TOKEN).test()
        observer.assertValue(UserResult.Success(authUser))
    }

    @Test
    fun shouldGetUserFailure() {
        every { userRepository.getAuthUser(any()) } returns
                Observable.just(UserResult.Failure("failure"))
        val observer = userRepository.getAuthUser(TOKEN).test()
        observer.assertValue(UserResult.Failure("failure"))
    }
}
package com.gitspark.gitspark.repository

import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.room.dao.AuthUserDao
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

private val authUser = AuthUser()
private val user = User()

class UserRepositoryTest {

    @MockK private lateinit var userRepository: UserRepository
    @RelaxedMockK private lateinit var retrofitHelper: RetrofitHelper
    @RelaxedMockK private lateinit var prefsHelper: PreferencesHelper
    @RelaxedMockK private lateinit var authUserDao: AuthUserDao
    @RelaxedMockK private lateinit var timeHelper: TimeHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        userRepository = UserRepository(retrofitHelper, prefsHelper, authUserDao, timeHelper)
    }

    @Test
    fun shouldGetAuthUserSuccess() {
        every { userRepository.getAuthUser() } returns
                Observable.just(UserResult.Success(authUser))
        val observer = userRepository.getAuthUser().test()
        observer.assertValue(UserResult.Success(authUser))
    }

    @Test
    fun shouldGetAuthUserFailure() {
        every { userRepository.getAuthUser() } returns
                Observable.just(UserResult.Failure("failure"))
        val observer = userRepository.getAuthUser().test()
        observer.assertValue(UserResult.Failure("failure"))
    }

    @Test
    fun shouldGetUserSuccess() {
        every { userRepository.getUser(any()) } returns
                Observable.just(UserResult.Success(user))
        val observer = userRepository.getUser("mingli1").test()
        observer.assertValue(UserResult.Success(user))
    }

    @Test
    fun shouldGetUserFailure() {
        every { userRepository.getUser(any()) } returns
                Observable.just(UserResult.Failure("failure"))
        val observer = userRepository.getUser("mingli1").test()
        observer.assertValue(UserResult.Failure("failure"))
    }

    @Test
    fun shouldGetCurrentUserData() {
        userRepository.getCurrentUserData()
        verify { authUserDao.getAuthUser() }
    }

    @Test
    fun shouldGetAuthUserFollowersSuccess() {
        every { userRepository.getAuthUserFollowers(any()) } returns
                Observable.just(UserResult.Success(Page(value = listOf(user))))
        val observer = userRepository.getAuthUserFollowers(1).test()
        observer.assertValue(UserResult.Success(Page(value = listOf(user))))
    }

    @Test
    fun shouldGetAuthUserFollowersFailure() {
        every { userRepository.getAuthUserFollowers(any()) } returns
                Observable.just(UserResult.Failure("failure"))
        val observer = userRepository.getAuthUserFollowers(1).test()
        observer.assertValue(UserResult.Failure("failure"))
    }

    @Test
    fun shouldGetAuthUserFollowingSuccess() {
        every { userRepository.getAuthUserFollowing(any()) } returns
                Observable.just(UserResult.Success(Page(value = listOf(user))))
        val observer = userRepository.getAuthUserFollowing(1).test()
        observer.assertValue(UserResult.Success(Page(value = listOf(user))))
    }

    @Test
    fun shouldGetAuthUserFollowingFailure() {
        every { userRepository.getAuthUserFollowing(any()) } returns
                Observable.just(UserResult.Failure("failure"))
        val observer = userRepository.getAuthUserFollowing(1).test()
        observer.assertValue(UserResult.Failure("failure"))
    }

    @Test
    fun shouldGetUserFollowersSuccess() {
        every { userRepository.getUserFollowers(any(), any()) } returns
                Observable.just(UserResult.Success(Page(value = listOf(user))))
        val observer = userRepository.getUserFollowers("username", 1).test()
        observer.assertValue(UserResult.Success(Page(value = listOf(user))))
    }

    @Test
    fun shouldGetUserFollowersFailure() {
        every { userRepository.getUserFollowers(any(), any()) } returns
                Observable.just(UserResult.Failure("failure"))
        val observer = userRepository.getUserFollowers("username", 1).test()
        observer.assertValue(UserResult.Failure("failure"))
    }

    @Test
    fun shouldGetUserFollowingSuccess() {
        every { userRepository.getUserFollowing(any(), any()) } returns
                Observable.just(UserResult.Success(Page(value = listOf(user))))
        val observer = userRepository.getUserFollowing("username", 1).test()
        observer.assertValue(UserResult.Success(Page(value = listOf(user))))
    }

    @Test
    fun shouldGetUserFollowingFailure() {
        every { userRepository.getUserFollowing(any(), any()) } returns
                Observable.just(UserResult.Failure("failure"))
        val observer = userRepository.getUserFollowing("username", 1).test()
        observer.assertValue(UserResult.Failure("failure"))
    }

    @Test
    fun shouldGetContributionsSvg() {
        every { userRepository.getContributionsSvg("username") } returns
                Observable.just(UserResult.Success("data"))
        val observer = userRepository.getContributionsSvg("username").test()
        observer.assertValue(UserResult.Success("data"))
    }
}
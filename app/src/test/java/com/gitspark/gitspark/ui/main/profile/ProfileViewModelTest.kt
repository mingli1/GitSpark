package com.gitspark.gitspark.ui.main.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.main.profile.ProfileViewModel
import com.gitspark.gitspark.ui.main.profile.ProfileViewState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ProfileViewModel
    @RelaxedMockK private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = ProfileViewModel(userRepository)
    }

    @Test
    fun shouldUpdateViewStateOnRequest() {
        viewModel.requestUserData("username")
        assertThat(viewState().loading).isTrue()
    }

    @Test
    fun shouldUpdateViewStateOnRequestSuccess() {
        val user = User()
        every { userRepository.getUser(any()) } returns
                Observable.just(UserResult.Success(user))

        viewModel.requestUserData("username")

        assertThat(viewState()).isEqualTo(
            ProfileViewState(
                loading = false,
                refreshing = false,
                updatedUserData = true,
                data = user
            )
        )
        assertThat(viewModel.loadViewAction.value).isNotNull
    }

    @Test
    fun shouldUpdateViewStateOnRequestFailure() {
        every { userRepository.getUser(any()) } returns
                Observable.just(UserResult.Failure("failure"))

        viewModel.requestUserData("username")

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewState().loading).isFalse()
        assertThat(viewState().updatedUserData).isFalse()
    }

    private fun viewState() = viewModel.viewState.value!!
}
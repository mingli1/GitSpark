package com.gitspark.gitspark.ui.main.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.api.model.ApiEditProfileRequest
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val request = ApiEditProfileRequest(
    name = "name",
    bio = "bio",
    location = "location",
    email = "email",
    url = "url",
    hireable = true,
    company = "company"
)

private val authUser = AuthUser()

class EditProfileViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: EditProfileViewModel
    @RelaxedMockK private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = EditProfileViewModel(userRepository)
    }

    @Test
    fun shouldFillInitialData() {
        viewModel.fillInitialData(request)
        assertThat(viewState()).isEqualTo(EditProfileViewState(
            nameText = "name",
            bioText = "bio",
            locationText = "location",
            emailText = "email",
            urlText = "url",
            hireable = true,
            companyText = "company"
        ))
    }

    @Test
    fun shouldUpDateProfileOnClicked() {
        viewModel.viewState.value = EditProfileViewState()

        viewModel.onUpdateProfileClicked()

        assertThat(viewState().loading).isTrue()
        verify { userRepository.updateUser(any()) }
    }

    @Test
    fun shouldFinishFragmentOnUpdateProfileSuccess() {
        viewModel.viewState.value = EditProfileViewState()
        every { userRepository.updateUser(any()) } returns
                Observable.just(UserResult.Success(authUser))

        viewModel.onUpdateProfileClicked()

        assertThat(viewState().loading).isFalse()
        assertThat(viewModel.finishFragmentAction.value).isEqualTo(authUser)
    }

    @Test
    fun shouldAlertOnUpdateProfileFailure() {
        viewModel.viewState.value = EditProfileViewState()
        every { userRepository.updateUser(any()) } returns
                Observable.just(UserResult.Failure("failure"))

        viewModel.onUpdateProfileClicked()

        assertThat(viewState().loading).isFalse()
        assertThat(viewModel.alertAction.value).isEqualTo("failure")
    }

    private fun viewState() = viewModel.viewState.value!!
}
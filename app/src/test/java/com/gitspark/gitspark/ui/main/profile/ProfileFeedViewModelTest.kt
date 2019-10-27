package com.gitspark.gitspark.ui.main.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.repository.EventResult
import com.gitspark.gitspark.repository.UserRepository
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

class ProfileFeedViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ProfileFeedViewModel
    @RelaxedMockK private lateinit var userRepository: UserRepository
    @RelaxedMockK private lateinit var eventRepository: EventRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = ProfileFeedViewModel(userRepository, eventRepository)
    }

    @Test
    fun shouldGetCachedUserDataOnResume() {
        viewModel.onResume(null)
        verify { userRepository.getCurrentUserData() }
    }

    @Test
    fun shouldRequestEventsSuccess() {
        every { eventRepository.getEvents(any(), any()) } returns
                Observable.just(EventResult.Success(Page()))
        viewModel.onRefresh()
        assertThat(viewState()).isEqualTo(ProfileFeedViewState(
            events = arrayListOf(),
            loading = true,
            refreshing = true,
            updateAdapter = false,
            isLastPage = false
        ))
    }

    @Test
    fun shouldRequestEventsFailure() {
        every { eventRepository.getEvents(any(), any()) } returns
                Observable.just(EventResult.Failure("failure"))
        viewModel.onRefresh()
        assertThat(viewState()).isEqualTo(ProfileFeedViewState(
            loading = true,
            refreshing = true,
            updateAdapter = false
        ))
    }

    private fun viewState() = viewModel.viewState.value!!
}
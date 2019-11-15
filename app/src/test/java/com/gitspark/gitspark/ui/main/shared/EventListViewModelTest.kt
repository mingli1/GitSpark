package com.gitspark.gitspark.ui.main.shared

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.repository.EventRepository
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventListViewModelTest {

    @Rule
    @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: EventListViewModel
    @RelaxedMockK private lateinit var eventRepository: EventRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = EventListViewModel(eventRepository)
    }

    @Test
    fun shouldRequestWatchers() {
        viewModel.onStart(EventListType.PublicEvents, "")
        verify { eventRepository.getPublicEvents(any()) }
    }

    @Test
    fun shouldNavigateToProfileOnUserSelected() {
        viewModel.onUserSelected("username")
        assertThat(viewModel.navigateToProfileAction.value).isEqualTo("username")
    }
}
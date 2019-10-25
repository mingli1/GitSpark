package com.gitspark.gitspark.ui.main.shared

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.repository.RepoRepository
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CommitListViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CommitListViewModel
    @RelaxedMockK private lateinit var repoRepository: RepoRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = CommitListViewModel(repoRepository)
    }

    @Test
    fun shouldRequestCommits() {
        viewModel.onResume("arg1/arg2/arg3")
        verify { repoRepository.getCommits(any(), any(), any(), any()) }
    }
}
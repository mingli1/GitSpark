package com.gitspark.gitspark.ui.main.shared

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
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

class RepoListViewModelTest {

    @Rule
    @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RepoListViewModel
    @RelaxedMockK
    private lateinit var repoRepository: RepoRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = RepoListViewModel(repoRepository)
    }

    @Test
    fun shouldRequestForks() {
        viewModel.onResume(RepoListType.Forks, "mingli1/Repo")
        verify { repoRepository.getForks(any(), any(), any()) }
    }

    @Test
    fun shouldNavigateToRepoDetailOnSelected() {
        val repo = Repo()
        viewModel.onRepoSelected(repo)
        assertThat(viewModel.navigateToRepoDetailAction.value).isEqualTo(repo)
    }
}
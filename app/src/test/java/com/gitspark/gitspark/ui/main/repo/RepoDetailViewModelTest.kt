package com.gitspark.gitspark.ui.main.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.model.Branch
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RepoDetailViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RepoDetailViewModel
    @RelaxedMockK private lateinit var repoRepository: RepoRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = RepoDetailViewModel(repoRepository)
    }

    @Test
    fun shouldGetRepoBranchDataSuccess() {
        val page = Page(value = listOf(Branch()))
        every { repoRepository.getBranches(any(), any()) } returns
                Observable.just(RepoResult.Success(page))

        viewModel.fetchAdditionalRepoData(Repo())

        assertThat(viewModel.branchesData.value).isEqualTo(listOf(Branch()))
        assertThat(viewModel.loading.value).isFalse()
    }

    @Test
    fun shouldGetRepoBranchDataFailure() {
        every { repoRepository.getBranches(any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))

        viewModel.fetchAdditionalRepoData(Repo())

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewModel.loading.value).isFalse()
    }

    @Test
    fun shouldGetRepoActivityDataSuccess() {
        every { repoRepository.isWatchedByAuthUser(any(), any()) } returns
                Completable.complete()
        every { repoRepository.isStarredByAuthUser(any(), any()) } returns
                Completable.complete()

        viewModel.fetchAdditionalRepoData(Repo())

        assertThat(viewModel.watchingData.value).isTrue()
        assertThat(viewModel.starringData.value).isTrue()
    }

    @Test
    fun shouldGetRepoActivityDataFailure() {
        every { repoRepository.isWatchedByAuthUser(any(), any()) } returns
                Completable.error(Throwable())
        every { repoRepository.isStarredByAuthUser(any(), any()) } returns
                Completable.error(Throwable())

        viewModel.fetchAdditionalRepoData(Repo())

        assertThat(viewModel.watchingData.value).isFalse()
        assertThat(viewModel.starringData.value).isFalse()
    }
}
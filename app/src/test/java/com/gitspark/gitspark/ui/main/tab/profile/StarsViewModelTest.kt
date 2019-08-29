package com.gitspark.gitspark.ui.main.tab.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
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

private val reposSuccess = RepoResult.Success(Page(
    next = 2,
    last = 10,
    value = listOf(Repo())
))

private val reposSuccessOnePage = RepoResult.Success(Page(
    next = -1,
    last = -1,
    value = listOf(Repo())
))

class StarsViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: StarsViewModel
    @RelaxedMockK private lateinit var repoRepository: RepoRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = StarsViewModel(repoRepository)
    }

    @Test
    fun shouldUpdateViewStateOnResume() {
        viewModel.onResume()

        assertThat(viewState()).isEqualTo(StarsViewState(
            loading = true,
            refreshing = false,
            updateAdapter = false
        ))
        verify { repoRepository.getAuthStarredRepos(eq(1), eq(1)) }
        verify { repoRepository.getAuthStarredRepos(any(), any()) }
    }

    @Test
    fun shouldUpdateViewStateOnRefresh() {
        viewModel.onRefresh()

        assertThat(viewState()).isEqualTo(StarsViewState(
            loading = true,
            refreshing = true,
            updateAdapter = false
        ))
        verify { repoRepository.getAuthStarredRepos(eq(1), eq(1)) }
        verify { repoRepository.getAuthStarredRepos(any(), any()) }
    }

    @Test
    fun shouldUpdateViewStateOnScrolledToEnd() {
        viewModel.onScrolledToEnd()

        assertThat(viewState()).isEqualTo(StarsViewState(
            loading = false,
            refreshing = false,
            updateAdapter = false
        ))
        verify(exactly = 0) { repoRepository.getAuthStarredRepos(eq(1), eq(1)) }
        verify { repoRepository.getAuthStarredRepos(any(), any()) }
    }

    @Test
    fun shouldGetTotalReposOnStarredReposSuccess() {
        every { repoRepository.getAuthStarredRepos(eq(1), eq(1)) } returns
                Observable.just(reposSuccess)

        viewModel.onRefresh()

        assertThat(viewState()).isEqualTo(StarsViewState(
            totalStarred = 10,
            loading = false,
            refreshing = false,
            updateAdapter = false
        ))
    }

    @Test
    fun shouldGetTotalReposOnStarredReposSuccessOnePage() {
        every { repoRepository.getAuthStarredRepos(eq(1), eq(1)) } returns
                Observable.just(reposSuccessOnePage)

        viewModel.onRefresh()

        assertThat(viewState()).isEqualTo(StarsViewState(
            totalStarred = 1,
            loading = false,
            refreshing = false,
            updateAdapter = false
        ))
    }

    @Test
    fun shouldUpdateViewStateOnTotalReposFailure() {
        every { repoRepository.getAuthStarredRepos(eq(1), eq(1)) } returns
                Observable.just(RepoResult.Failure("failure"))

        viewModel.onRefresh()

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewState()).isEqualTo(StarsViewState(
            loading = false,
            refreshing = false,
            updateAdapter = false
        ))
    }

    @Test
    fun shouldUpdateViewStateOnStarredReposSuccess() {
        every { repoRepository.getAuthStarredRepos(any()) } returns
                Observable.just(reposSuccess)

        viewModel.onRefresh()

        assertThat(viewState()).isEqualTo(StarsViewState(
            repos = reposSuccess.value.value,
            loading = false,
            refreshing = false,
            isFirstPage = true,
            isLastPage = false,
            updateAdapter = true
        ))
    }

    @Test
    fun shouldUpdateViewStateOnStarredReposFailure() {
        every { repoRepository.getAuthStarredRepos(any()) } returns
                Observable.just(RepoResult.Failure("failure"))

        viewModel.onRefresh()

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewState()).isEqualTo(StarsViewState(
            refreshing = true,
            loading = false,
            updateAdapter = false
        ))
    }

    private fun viewState() = viewModel.viewState.value!!
}
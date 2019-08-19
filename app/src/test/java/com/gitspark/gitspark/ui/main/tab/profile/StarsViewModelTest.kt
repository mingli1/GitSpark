package com.gitspark.gitspark.ui.main.tab.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.helper.PreferencesHelper
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

class StarsViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: StarsViewModel
    @RelaxedMockK private lateinit var repoRepository: RepoRepository
    @RelaxedMockK private lateinit var prefsHelper: PreferencesHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = StarsViewModel(repoRepository, prefsHelper)
    }

    @Test
    fun shouldRequestStarredReposOnResume() {
        viewModel.onResume()
        assertThat(viewState().loading).isTrue()
        verify { repoRepository.getAuthStarredRepos(any()) }
    }

    @Test
    fun shouldRequestStarredReposOnRefresh() {
        viewModel.viewState.value = StarsViewState()

        viewModel.onRefresh()

        assertThat(viewState()).isEqualTo(StarsViewState(
            refreshing = true,
            clearSearchFilter = true,
            clearSortSelection = true
        ))
        verify { repoRepository.getAuthStarredRepos(any()) }
    }

    @Test
    fun shouldUpdateViewOnStarredReposSuccess() {
        every { repoRepository.getAuthStarredRepos(any()) } returns
                Observable.just(RepoResult.Success(listOf(REPO1, REPO2)))

        viewModel.onResume()

        assertThat(viewModel.currentRepoData).isEqualTo(listOf(REPO2, REPO1))
        assertThat(viewState()).isEqualTo(StarsViewState(
            repos = listOf(REPO2, REPO1),
            loading = false,
            refreshing = false,
            clearSearchFilter = false,
            clearSortSelection = false
        ))
    }

    @Test
    fun shouldUpdateViewOnStarredReposFailure() {
        every { repoRepository.getAuthStarredRepos(any()) } returns
                Observable.just(RepoResult.Failure("failure"))

        viewModel.onResume()

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewState()).isEqualTo(StarsViewState(
            loading = false,
            refreshing = false,
            clearSearchFilter = false,
            clearSortSelection = false
        ))
    }

    @Test
    fun shouldFilterReposOnAfterTextChanged() {
        setUpState()

        viewModel.onAfterTextChanged("ap")

        assertThat(viewState().repos).isEqualTo(listOf(REPO1))
        assertThat(viewState().clearSortSelection).isFalse()
        assertThat(viewState().clearSearchFilter).isFalse()
    }

    @Test
    fun shouldFilterReposOnSortItemSelectedSortRecent() {
        setUpState()
        viewModel.onSortItemSelected(SORT_RECENT)
        assertThat(viewState().repos).isEqualTo(listOf(REPO1, REPO2))
    }

    @Test
    fun shouldFilterReposOnSortItemSelectedSortStars() {
        setUpState()
        viewModel.onSortItemSelected(SORT_STARS)
        assertThat(viewState().repos).isEqualTo(listOf(REPO1, REPO2))
    }

    @Test
    fun shouldFilterReposOnSortItemSelectedSortUpdated() {
        setUpState()
        viewModel.onSortItemSelected("updated")
        assertThat(viewState().repos).isEqualTo(listOf(REPO1, REPO2))
    }

    private fun viewState() = viewModel.viewState.value!!

    private fun setUpState() {
        viewModel.viewState.value = StarsViewState()
        viewModel.currentRepoData = listOf(REPO1, REPO2)
    }
}
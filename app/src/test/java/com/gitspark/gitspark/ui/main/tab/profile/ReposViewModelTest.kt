package com.gitspark.gitspark.ui.main.tab.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.api.model.SORT_PUSHED
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReposViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ReposViewModel
    @RelaxedMockK private lateinit var repoRepository: RepoRepository
    @RelaxedMockK private lateinit var prefsHelper: PreferencesHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = ReposViewModel(repoRepository, prefsHelper)
    }

    @Test
    fun shouldGetRepoDataFromDatabaseOnResume() {
        viewModel.onResume()
        verify { repoRepository.getRepos(order = SORT_PUSHED) }
    }

    @Test
    fun shouldUpdateViewStateOnEmptyCachedRepoData() {
        viewModel.onCachedRepoDataRetrieved(emptyList())
        assertThat(viewState().repos).isEqualTo(emptyList<Repo>())
    }

    @Test
    fun shouldRequestAuthReposWhenExpired() {
        every { repoRepository.isRepoCacheExpired(any()) } returns true

        viewModel.onCachedRepoDataRetrieved(listOf(REPO1, REPO2))

        assertThat(viewState().loading).isTrue()
        verify { repoRepository.getAuthRepos(any()) }
    }

    @Test
    fun shouldCacheReposAndUpdateViewStateOnGetAuthReposSuccessAndCacheReposSuccess() {
        every { repoRepository.isRepoCacheExpired(any()) } returns true
        every { repoRepository.getAuthRepos(any()) } returns
                Observable.just(RepoResult.Success(listOf(REPO1, REPO2, REPO3)))
        every { repoRepository.cacheRepos(any()) } returns
                Completable.complete()

        viewModel.onCachedRepoDataRetrieved(listOf(REPO1, REPO2))

        assertThat(viewModel.currentRepoData).isEqualTo(listOf(REPO1, REPO3, REPO2))
        assertThat(viewState()).isEqualTo(ReposViewState(
            repos = listOf(REPO1, REPO3, REPO2),
            loading = true,
            refreshing = false
        ))
        verify { repoRepository.getAuthStarredRepos(any()) }
    }

    @Test
    fun shouldUpdateViewStateWithExistingDataOnAuthReposSuccessAndCacheReposFailure() {
        every { repoRepository.isRepoCacheExpired(any()) } returns true
        every { repoRepository.getAuthRepos(any()) } returns
                Observable.just(RepoResult.Success(listOf(REPO1, REPO2, REPO3)))
        every { repoRepository.cacheRepos(any()) } returns
                Completable.error(Throwable())

        viewModel.onCachedRepoDataRetrieved(listOf(REPO1, REPO2))

        assertThat(viewModel.alertAction.value).isEqualTo("Failed to cache repo data.")
        assertThat(viewModel.currentRepoData).isEqualTo(listOf(REPO1, REPO2))
        assertThat(viewState()).isEqualTo(ReposViewState(
            repos = listOf(REPO1, REPO2),
            loading = true,
            refreshing = false
        ))
        verify { repoRepository.getAuthStarredRepos(any()) }
    }

    @Test
    fun shouldUpdateViewStateWithExistingDataOnAuthRepoFailure() {
        every { repoRepository.isRepoCacheExpired(any()) } returns true
        every { repoRepository.getAuthRepos(any()) } returns
                Observable.just(RepoResult.Failure("failure"))

        viewModel.onCachedRepoDataRetrieved(listOf(REPO1, REPO2))

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewModel.currentRepoData).isEqualTo(listOf(REPO1, REPO2))
        assertThat(viewState()).isEqualTo(ReposViewState(
            repos = listOf(REPO1, REPO2),
            loading = true,
            refreshing = false
        ))
        verify { repoRepository.getAuthStarredRepos(any()) }
    }

    @Test
    fun shouldUpdateViewStateWithCachedDataIfNotExpired() {
        every { repoRepository.isRepoCacheExpired(any()) } returns false

        viewModel.onCachedRepoDataRetrieved(listOf(REPO1, REPO2))

        assertThat(viewModel.currentRepoData).isEqualTo(listOf(REPO1, REPO2))
        assertThat(viewState()).isEqualTo(ReposViewState(
            repos = listOf(REPO1, REPO2),
            loading = true,
            refreshing = false
        ))
        verify { repoRepository.getAuthStarredRepos(any()) }
    }

    @Test
    fun shouldStarRepoDataAndUpdateViewStateOnStarredReposSuccess() {
        every { repoRepository.isRepoCacheExpired(any()) } returns true
        every { repoRepository.getAuthRepos(any()) } returns
                Observable.just(RepoResult.Success(listOf(REPO1, REPO2, REPO3)))
        every { repoRepository.cacheRepos(any()) } returns
                Completable.complete()
        every { repoRepository.getAuthStarredRepos(any()) } returns
                Observable.just(RepoResult.Success(listOf(REPO1, REPO2)))

        viewModel.onCachedRepoDataRetrieved(listOf(REPO1, REPO2))

        assertThat(viewModel.currentRepoData[0].starred).isTrue()
        assertThat(viewModel.currentRepoData[1].starred).isFalse()
        assertThat(viewModel.currentRepoData[2].starred).isTrue()
        assertThat(viewState()).isEqualTo(ReposViewState(
            repos = listOf(REPO1, REPO3, REPO2),
            clearSortSelection = false,
            clearSearchFilter = false,
            loading = false
        ))
    }

    @Test
    fun shouldUpdateViewStateOnStarredReposError() {
        every { repoRepository.isRepoCacheExpired(any()) } returns true
        every { repoRepository.getAuthRepos(any()) } returns
                Observable.just(RepoResult.Success(listOf(REPO1, REPO2, REPO3)))
        every { repoRepository.cacheRepos(any()) } returns
                Completable.complete()
        every { repoRepository.getAuthStarredRepos(any()) } returns
                Observable.just(RepoResult.Failure("failure"))

        viewModel.onCachedRepoDataRetrieved(listOf(REPO1, REPO2))

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
    }

    @Test
    fun shouldRequestAuthReposOnRefresh() {
        setUpViewState()

        viewModel.onRefresh()

        assertThat(viewState()).isEqualTo(ReposViewState(
            refreshing = true,
            clearSearchFilter = true,
            clearSortSelection = true
        ))
        verify { repoRepository.getAuthRepos(any()) }
    }

    @Test
    fun shouldFilterReposAfterTextChanged() {
        setUpViewState()
        viewModel.currentRepoData = listOf(REPO1, REPO2, REPO3)

        viewModel.onAfterTextChanged("Banana")

        assertThat(viewState().repos).isEqualTo(listOf(REPO3))
        assertThat(viewState().clearSortSelection).isFalse()
        assertThat(viewState().clearSearchFilter).isFalse()
    }

    @Test
    fun shouldFilterReposOnSortItemSelectedSortAll() {
        setUpViewState()
        viewModel.currentRepoData = listOf(REPO1, REPO2, REPO3)

        viewModel.onSortItemSelected(SORT_ALL)

        assertThat(viewState().repos).isEqualTo(listOf(REPO1, REPO2, REPO3))
    }

    @Test
    fun shouldFilterReposOnSortItemSelectedSortPublic() {
        setUpViewState()
        viewModel.currentRepoData = listOf(REPO1, REPO2, REPO3)

        viewModel.onSortItemSelected(SORT_PUBLIC)

        assertThat(viewState().repos).isEqualTo(listOf(REPO1, REPO2))
    }

    @Test
    fun shouldFilterReposOnSortItemSelectedSortPrivate() {
        setUpViewState()
        viewModel.currentRepoData = listOf(REPO1, REPO2, REPO3)

        viewModel.onSortItemSelected(SORT_PRIVATE)

        assertThat(viewState().repos).isEqualTo(listOf(REPO3))
    }

    @Test
    fun shouldFilterReposOnSortItemSelectedSortForked() {
        setUpViewState()
        viewModel.currentRepoData = listOf(REPO1, REPO2, REPO3)

        viewModel.onSortItemSelected(SORT_FORKED)

        assertThat(viewState().repos).isEqualTo(listOf(REPO2))
    }

    @Test
    fun shouldFilterReposOnSortItemSelectedSortStars() {
        setUpViewState()
        viewModel.currentRepoData = listOf(REPO1, REPO2, REPO3)

        viewModel.onSortItemSelected("stars")

        assertThat(viewState().repos).isEqualTo(listOf(REPO3, REPO1, REPO2))
    }

    private fun viewState() = viewModel.viewState.value!!

    private fun setUpViewState() {
        viewModel.viewState.value = ReposViewState()
    }
}
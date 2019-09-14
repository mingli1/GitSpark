package com.gitspark.gitspark.ui.main.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
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

private val authUser = AuthUser().apply {
    numPublicRepos = 10
    totalPrivateRepos = 23
}

private val reposSuccess = RepoResult.Success(Page(
    next = 3,
    last = 10,
    value = listOf(Repo())
))

class ReposViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ReposViewModel
    @RelaxedMockK private lateinit var repoRepository: RepoRepository
    @RelaxedMockK private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = ReposViewModel(repoRepository, userRepository)
    }

    @Test
    fun shouldGetCurrentUserDataOnResume() {
        viewModel.onResume()
        verify { userRepository.getCurrentUserData() }
    }

    @Test
    fun shouldNotGetCurrentUserDataWhenNotAuthUserOnResume() {
        viewModel.onResume(username = "abc")
        assertThat(viewModel.username).isEqualTo("abc")
        verify(exactly = 0) { userRepository.getCurrentUserData() }
    }

    @Test
    fun shouldUpdateViewStateOnResume() {
        viewModel.onResume()
        assertThat(viewState()).isEqualTo(ReposViewState(
            loading = true,
            refreshing = false,
            updateAdapter = false
        ))
        verify { repoRepository.getAuthRepos(any(), any()) }
    }

    @Test
    fun shouldSetTotalPublicReposWhenUserDataExists() {
        viewModel.onResume(user = authUser)
        assertThat(viewState().totalRepos).isEqualTo(10)
    }

    @Test
    fun shouldRequestReposOnScrolledToEndWhenUsernameExists() {
        viewModel.username = "abc"

        viewModel.onScrolledToEnd()

        verify { repoRepository.getRepos(any(), any(), any()) }
        verify(exactly = 0) { repoRepository.getAuthRepos(any(), any()) }
    }

    @Test
    fun shouldUpdateViewStateOnRefresh() {
        viewModel.onRefresh()
        assertThat(viewState()).isEqualTo(ReposViewState(
            loading = true,
            refreshing = true,
            updateAdapter = false
        ))
        verify { repoRepository.getAuthRepos(any(), any()) }
    }

    @Test
    fun shouldUpdateViewStateOnScrolledToEnd() {
        viewModel.onScrolledToEnd()
        assertThat(viewState()).isEqualTo(ReposViewState(
            loading = false,
            refreshing = false,
            updateAdapter = false
        ))
        verify { repoRepository.getAuthRepos(any(), any()) }
    }

    @Test
    fun shouldUpdateViewStateWithTotalRepos() {
        viewModel.onUserDataRetrieved(authUser)
        assertThat(viewState().totalRepos).isEqualTo(33)
    }

    @Test
    fun shouldUpdateViewStateOnReposSuccess() {
        every { repoRepository.getAuthRepos(any(), any()) } returns
                Observable.just(reposSuccess)

        viewModel.onScrolledToEnd()

        val updatedList = arrayListOf<Repo>().apply { addAll(reposSuccess.value.value) }
        assertThat(viewState()).isEqualTo(ReposViewState(
            repos = updatedList,
            loading = false,
            refreshing = false,
            isFirstPage = true,
            isLastPage = false,
            updateAdapter = true
        ))
    }

    @Test
    fun shouldUpdateViewStateOnReposFailure() {
        every { repoRepository.getAuthRepos(any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))

        viewModel.onScrolledToEnd()

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewState()).isEqualTo(ReposViewState(
            loading = false,
            refreshing = false,
            updateAdapter = false
        ))
    }

    @Test
    fun shouldNavigateToRepoDetailFragmentOnSelected() {
        val repo = Repo(fullName = "mingli1/GitSpark")
        viewModel.onRepoSelected(repo)
        assertThat(viewModel.navigateToRepoDetailAction.value).isEqualTo(repo)
    }

    private fun viewState() = viewModel.viewState.value!!
}
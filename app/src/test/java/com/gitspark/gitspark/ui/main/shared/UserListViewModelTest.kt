package com.gitspark.gitspark.ui.main.shared

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.UserRepository
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

private const val ARGS = "mingli1/Repo"

class UserListViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: UserListViewModel
    @RelaxedMockK private lateinit var repoRepository: RepoRepository
    @RelaxedMockK private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = UserListViewModel(repoRepository, userRepository)
    }

    @Test
    fun shouldRequestWatchers() {
        viewModel.onStart(UserListType.Watchers, ARGS)
        verify { repoRepository.getWatchers(any(), any(), any()) }
    }

    @Test
    fun shouldRequestStargazers() {
        viewModel.onStart(UserListType.Stargazers, ARGS)
        verify { repoRepository.getStargazers(any(), any(), any()) }
    }

    @Test
    fun shouldRequestContributors() {
        viewModel.onStart(UserListType.Contributors, ARGS)
        verify { repoRepository.getContributors(any(), any(), any()) }
    }

    @Test
    fun shouldNavigateToProfileOnUserSelected() {
        viewModel.onUserSelected("username")
        assertThat(viewModel.navigateToProfileAction.value).isEqualTo("username")
    }

    @Test
    fun shouldRequestFollowers() {
        viewModel.onStart(UserListType.Followers, ARGS)
        verify { userRepository.getUserFollowers(any(), any()) }
    }

    @Test
    fun shouldRequestFollowing() {
        viewModel.onStart(UserListType.Following, ARGS)
        verify { userRepository.getUserFollowing(any(), any()) }
    }
}
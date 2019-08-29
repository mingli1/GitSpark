package com.gitspark.gitspark.repository

import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.Page
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class RepoRepositoryTest {

    private lateinit var realRepository: RepoRepository
    @MockK private lateinit var repoRepository: RepoRepository
    @RelaxedMockK private lateinit var retrofitHelper: RetrofitHelper
    @RelaxedMockK private lateinit var prefsHelper: PreferencesHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        realRepository = RepoRepository(retrofitHelper, prefsHelper)
    }

    @Test
    fun shouldGetAuthReposSuccess() {
        every { repoRepository.getAuthRepos(any(), any()) } returns
                Observable.just(RepoResult.Success(Page(value = emptyList())))
        val observer = repoRepository.getAuthRepos(page = 1).test()
        observer.assertValue(RepoResult.Success(Page(value = emptyList())))
    }

    @Test
    fun shouldGetAuthReposFailure() {
        every { repoRepository.getAuthRepos(any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))
        val observer = repoRepository.getAuthRepos(page = 1).test()
        observer.assertValue(RepoResult.Failure("failure"))
    }

    @Test
    fun shouldGetReposSuccess() {
        every { repoRepository.getRepos(any(), any(), any()) } returns
                Observable.just(RepoResult.Success(Page(value = emptyList())))
        val observer = repoRepository.getRepos("username", page = 1).test()
        observer.assertValue(RepoResult.Success(Page(value = emptyList())))
    }

    @Test
    fun shouldGetReposFailure() {
        every { repoRepository.getRepos(any(), any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))
        val observer = repoRepository.getRepos("username", page = 1).test()
        observer.assertValue(RepoResult.Failure("failure"))
    }

    @Test
    fun shouldGetAuthStarredReposSuccess() {
        every { repoRepository.getAuthStarredRepos(any(), any()) } returns
                Observable.just(RepoResult.Success(Page(value = emptyList())))
        val observer = repoRepository.getAuthStarredRepos(page = 1).test()
        observer.assertValue(RepoResult.Success(Page(value = emptyList())))
    }

    @Test
    fun shouldGetAuthStarredReposFailure() {
        every { repoRepository.getAuthStarredRepos(any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))
        val observer = repoRepository.getAuthStarredRepos(page = 1).test()
        observer.assertValue(RepoResult.Failure("failure"))
    }

    @Test
    fun shouldGetStarredReposSuccess() {
        every { repoRepository.getStarredRepos(any(), any(), any()) } returns
                Observable.just(RepoResult.Success(Page(value = emptyList())))
        val observer = repoRepository.getStarredRepos("username", page = 1).test()
        observer.assertValue(RepoResult.Success(Page(value = emptyList())))
    }

    @Test
    fun shouldGetStarredReposFailure() {
        every { repoRepository.getStarredRepos(any(), any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))
        val observer = repoRepository.getStarredRepos("username", page = 1).test()
        observer.assertValue(RepoResult.Failure("failure"))
    }
}
package com.gitspark.gitspark.repository

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

private const val TOKEN = "Token 123"

class RepoRepositoryTest {

    private lateinit var realRepository: RepoRepository
    @MockK private lateinit var repoRepository: RepoRepository
    @RelaxedMockK private lateinit var retrofitHelper: RetrofitHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        realRepository = RepoRepository(retrofitHelper)
    }

    @Test
    fun shouldGetAuthReposSuccess() {
        every { repoRepository.getAuthRepos(any(), any(), any()) } returns
                Observable.just(RepoResult.Success(Page(value = emptyList())))
        val observer = repoRepository.getAuthRepos(token = TOKEN, page = 1).test()
        observer.assertValue(RepoResult.Success(Page(value = emptyList())))
    }

    @Test
    fun shouldGetAuthReposFailure() {
        every { repoRepository.getAuthRepos(any(), any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))
        val observer = repoRepository.getAuthRepos(token = TOKEN, page = 1).test()
        observer.assertValue(RepoResult.Failure("failure"))
    }

    @Test
    fun shouldGetAuthStarredReposSuccess() {
        every { repoRepository.getAuthStarredRepos(any(), any()) } returns
                Observable.just(RepoResult.Success(Page(value = emptyList())))
        val observer = repoRepository.getAuthStarredRepos(token = TOKEN, page = 1).test()
        observer.assertValue(RepoResult.Success(Page(value = emptyList())))
    }

    @Test
    fun shouldGetAuthStarredReposFailure() {
        every { repoRepository.getAuthStarredRepos(any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))
        val observer = repoRepository.getAuthStarredRepos(token = TOKEN, page = 1).test()
        observer.assertValue(RepoResult.Failure("failure"))
    }
}
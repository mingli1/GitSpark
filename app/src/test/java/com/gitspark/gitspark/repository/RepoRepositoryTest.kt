package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.model.SORT_CREATED
import com.gitspark.gitspark.api.model.SORT_FULL_NAME
import com.gitspark.gitspark.api.model.SORT_PUSHED
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.room.dao.RepoDao
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

private const val TOKEN = "Token 123"
private const val SEARCH = "search"

class RepoRepositoryTest {

    private lateinit var realRepository: RepoRepository
    @MockK private lateinit var repoRepository: RepoRepository
    @RelaxedMockK private lateinit var retrofitHelper: RetrofitHelper
    @RelaxedMockK private lateinit var timeHelper: TimeHelper
    @RelaxedMockK private lateinit var repoDao: RepoDao

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        realRepository = RepoRepository(retrofitHelper, timeHelper, repoDao)
    }

    @Test
    fun shouldGetAuthReposSuccess() {
        every { repoRepository.getAuthRepos(any(), any()) } returns
                Observable.just(RepoResult.Success(emptyList()))
        val observer = repoRepository.getAuthRepos(token = TOKEN).test()
        observer.assertValue(RepoResult.Success(emptyList()))
    }

    @Test
    fun shouldGetAuthReposFailure() {
        every { repoRepository.getAuthRepos(any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))
        val observer = repoRepository.getAuthRepos(token = TOKEN).test()
        observer.assertValue(RepoResult.Failure("failure"))
    }

    @Test
    fun shouldGetAuthStarredReposSuccess() {
        every { repoRepository.getAuthStarredRepos(any()) } returns
                Observable.just(RepoResult.Success(emptyList()))
        val observer = repoRepository.getAuthStarredRepos(token = TOKEN).test()
        observer.assertValue(RepoResult.Success(emptyList()))
    }

    @Test
    fun shouldGetAuthStarredReposFailure() {
        every { repoRepository.getAuthStarredRepos(any()) } returns
                Observable.just(RepoResult.Failure("failure"))
        val observer = repoRepository.getAuthStarredRepos(token = TOKEN).test()
        observer.assertValue(RepoResult.Failure("failure"))
    }

    @Test
    fun shouldGetReposByFullName() {
        realRepository.getRepos(SEARCH, true, SORT_FULL_NAME)
        verify { repoDao.getPrivateReposDefaultOrder(any()) }

        realRepository.getRepos(SEARCH, false, SORT_FULL_NAME)
        verify { repoDao.getAllReposDefaultOrder(any()) }
    }

    @Test
    fun shouldGetReposByPushed() {
        realRepository.getRepos(SEARCH, true, SORT_PUSHED)
        verify { repoDao.getPrivateReposOrderByPushed(any()) }

        realRepository.getRepos(SEARCH, false, SORT_PUSHED)
        verify { repoDao.getAllReposOrderByPushed(any()) }
    }

    @Test
    fun shouldGetReposByCreated() {
        realRepository.getRepos(SEARCH, true, SORT_CREATED)
        verify { repoDao.getPrivateReposOrderByCreated(any()) }

        realRepository.getRepos(SEARCH, false, SORT_CREATED)
        verify { repoDao.getAllReposOrderByCreated(any()) }
    }

    @Test
    fun shouldGetReposByUpdated() {
        realRepository.getRepos(SEARCH, true, "updated")
        verify { repoDao.getPrivateReposOrderByUpdated(any()) }

        realRepository.getRepos(SEARCH, false, "updated")
        verify { repoDao.getAllReposOrderByUpdated(any()) }
    }
}
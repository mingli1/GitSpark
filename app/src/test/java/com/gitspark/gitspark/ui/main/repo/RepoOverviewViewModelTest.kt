package com.gitspark.gitspark.ui.main.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.RepoContent
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val repo = Repo(
    repoName = "repo",
    repoDescription = "desc",
    topics = listOf("a"),
    archived = false,
    isPrivate = true,
    isForked = true,
    repoLanguage = "Java",
    numWatches = 20,
    numStars = 40,
    numForks = 6
)

class RepoOverviewViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RepoOverviewViewModel
    @RelaxedMockK private lateinit var repoRepository: RepoRepository
    @RelaxedMockK private lateinit var colorHelper: LanguageColorHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = RepoOverviewViewModel(repoRepository, colorHelper)
    }

    @Test
    fun shouldLoadRepo() {
        every { colorHelper.getColor(any()) } returns 123

        viewModel.loadRepo(repo)

        assertThat(viewState()).isEqualTo(RepoOverviewViewState(
            repoName = "repo",
            repoDescription = "desc",
            topics = listOf("a"),
            isArchived = false,
            isPrivate = true,
            isForked = true,
            repoLanguage = "Java",
            languageColor = 123,
            numWatchers = 20,
            numStars = 40,
            numForks = 6,
            loading = true
        ))
    }

    @Test
    fun shouldRequestReadmeSuccess() {
        val repoContent = RepoContent(downloadUrl = "url")
        every { colorHelper.getColor(any()) } returns 123
        every { repoRepository.getReadme(any(), any()) } returns
                Observable.just(RepoResult.Success(repoContent))

        viewModel.loadRepo(repo)

        assertThat(viewState().loading).isFalse()
        assertThat(viewState().readmeUrl).isEqualTo("url")
    }

    @Test
    fun shouldRequestReadmeFailure() {
        every { colorHelper.getColor(any()) } returns 123
        every { repoRepository.getReadme(any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))

        viewModel.loadRepo(repo)

        assertThat(viewState().loading).isFalse()
    }

    @Test
    fun shouldSetUserWatching() {
        viewModel.setUserWatching(true)
        assertThat(viewState().userWatching).isTrue()
    }

    @Test
    fun shouldSetUserStarring() {
        viewModel.setUserStarring(true)
        assertThat(viewState().userStarring).isTrue()
    }

    private fun viewState() = viewModel.viewState.value!!
}
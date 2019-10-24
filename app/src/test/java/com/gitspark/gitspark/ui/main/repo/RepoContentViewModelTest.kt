package com.gitspark.gitspark.ui.main.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.model.*
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

class RepoContentViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RepoContentViewModel
    @RelaxedMockK private lateinit var repoRepository: RepoRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = RepoContentViewModel(repoRepository)
        viewModel.currRepo = Repo(repoName = "repo")
    }

    @Test
    fun shouldRestoreStateOnResume() {
        viewModel.destroyed = true
        viewModel.viewState.value = RepoContentViewState()
        viewModel.branchNames = emptyList()

        viewModel.onResume()

        assertThat(viewState()).isEqualTo(RepoContentViewState(
            loading = false,
            updateContent = true,
            updateBranchSpinner = true,
            branchNames = emptyList(),
            branchPosition = 0
        ))
        assertThat(viewModel.destroyed).isFalse()
    }

    @Test
    fun shouldFetchDirectoryFromCache() {
        viewModel.directoryCache["path"] = emptyList()
        viewModel.viewState.value = RepoContentViewState()

        viewModel.fetchDirectory(path = "path")

        assertThat(viewState()).isEqualTo(RepoContentViewState(
            loading = false,
            updateContent = true,
            contentData = viewModel.directoryCache["path"] ?: emptyList(),
            path = "repo/path"
        ))
    }

    @Test
    fun shouldFetchDirectoryOnSuccess() {
        val page = Page<RepoContent>(value = emptyList())
        every { repoRepository.getDirectory(any(), any(), any(), any()) } returns
                Observable.just(RepoResult.Success(page))

        viewModel.fetchDirectory(path = "path")

        assertThat(viewState()).isEqualTo(RepoContentViewState(
            loading = false,
            updateContent = true,
            contentData = emptyList(),
            path = "repo/path"
        ))
    }

    @Test
    fun shouldAlertOnDirectoryFailure() {
        every { repoRepository.getDirectory(any(), any(), any(), any()) } returns
                Observable.just(RepoResult.Failure("failure"))

        viewModel.fetchDirectory(path = "path")

        assertThat(viewState().loading).isFalse()
        assertThat(viewModel.alertAction.value).isEqualTo("failure")
    }

    @Test
    fun shouldPushPathOnStackWhenNotBacking() {
        viewModel.fetchDirectory(path = "path", back = false)
        assertThat(viewModel.pathStack.peek()).isEqualTo("path")
    }

    @Test
    fun shouldDestroyOnDestroyView() {
        viewModel.onDestroyView()
        assertThat(viewModel.destroyed).isTrue()
    }

    @Test
    fun shouldFetchDirectoryOnBackClicked() {
        viewModel.pathStack.push("path1")
        viewModel.pathStack.push("path2")
        val page = Page<RepoContent>(value = emptyList())
        every { repoRepository.getDirectory(any(), any(), any(), any()) } returns
                Observable.just(RepoResult.Success(page))

        viewModel.onDirectoryBackClicked()

        assertThat(viewModel.pathStack.size).isEqualTo(1)
        assertThat(viewState().path).isEqualTo("repo/path1")
    }

    @Test
    fun shouldNavigateToRepoCodeFragmentOnFileSelected() {
        every { repoRepository.getRawContent(any()) } returns
                Observable.just(RepoResult.Success("raw"))
        viewModel.onFileSelected("a", "b", "c")
        assertThat(viewModel.navigateToRepoCodeAction.value).isEqualTo(Triple("raw", "b", "c"))
    }

    @Test
    fun shouldNavigateToRepoCodeFragmentOnImageSelected() {
        every { repoRepository.getRawContent(any()) } returns
                Observable.just(RepoResult.Success("raw"))
        viewModel.onFileSelected("a", "b", "png")
        assertThat(viewModel.navigateToRepoCodeAction.value).isEqualTo(Triple("a", "b", "png"))
    }

    @Test
    fun shouldRequestCommitDataSuccess() {
        viewModel.fetchDirectory()
        every { repoRepository.getCommits(any(), any(), any(), 1, 1) } returns
                Observable.just(RepoResult.Success(Page(last = 3, value = listOf(
                    Commit(committer = User(avatarUrl = "avatar", login = "login"),
                        commit = CommitDetail(message = "message")
                    )
                ))))

        viewModel.requestCommitData("branch")

        assertThat(viewState().numCommits).isEqualTo(3)
        assertThat(viewState().commitAvatarUrl).isEqualTo("avatar")
        assertThat(viewState().commitMessage).isEqualTo("message")
        assertThat(viewState().commitUsername).isEqualTo("login")
    }

    private fun viewState() = viewModel.viewState.value!!
}
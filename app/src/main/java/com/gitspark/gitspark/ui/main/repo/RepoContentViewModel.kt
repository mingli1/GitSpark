package com.gitspark.gitspark.ui.main.repo

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Commit
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.RepoContent
import com.gitspark.gitspark.model.TYPE_DIRECTORY
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.nav.RepoContentNavigator
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import java.util.*
import javax.inject.Inject

class RepoContentViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel(), RepoContentNavigator {

    val viewState = MutableLiveData<RepoContentViewState>()
    val navigateToRepoCodeAction = SingleLiveEvent<Triple<String, String, String>>()

    lateinit var currRepo: Repo
    lateinit var branchNames: List<String>

    @VisibleForTesting val directoryCache = mutableMapOf<String, List<RepoContent>>()
    @VisibleForTesting val pathStack = Stack<String>()
    @VisibleForTesting var destroyed = false
    private var currentBranch = "master"
    private var branchPosition = 0

    fun onResume() {
        if (destroyed) {
            viewState.value = viewState.value?.copy(
                loading = false,
                updateContent = true,
                updateBranchSpinner = true,
                branchNames = branchNames,
                branchPosition = branchPosition
            )
            destroyed = false
        }
    }

    fun fetchDirectory(path: String = "", branchName: String = "", back: Boolean = false) {
        currentBranch = branchName

        if (!back) pathStack.push(path)

        if (directoryCache.containsKey(path)) {
            viewState.value = viewState.value?.copy(
                loading = false,
                updateContent = true,
                contentData = directoryCache[path] ?: emptyList(),
                path = currRepo.repoName + "/" + path
            )
            return
        }

        viewState.value = viewState.value?.copy(
            loading = true,
            updateContent = false,
            updateBranchSpinner = false
        ) ?: RepoContentViewState(loading = true)

        subscribe(repoRepository.getDirectory(currRepo.owner.login, currRepo.repoName, path, branchName)) {
            when (it) {
                is RepoResult.Success -> {
                    val orderedContent = orderContents(it.value.value)
                    directoryCache[path] = orderedContent

                    viewState.value = viewState.value?.copy(
                        loading = false,
                        updateContent = true,
                        contentData = orderedContent,
                        path = currRepo.repoName + "/" + path
                    )
                }
                is RepoResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }

    fun onDirectoryBackClicked() {
        if (pathStack.size <= 1) return
        pathStack.pop()
        fetchDirectory(path = pathStack.peek(), branchName = currentBranch, back = true)
    }

    fun onBranchSelected(newBranch: String, position: Int) {
        if (currentBranch == newBranch) return
        currentBranch = newBranch
        branchPosition = position

        directoryCache.clear()
        pathStack.clear()

        fetchDirectory(path = "", branchName = currentBranch)
        requestCommitData(currentBranch)
    }

    fun onDestroyView() {
        destroyed = true
    }

    override fun onDirectorySelected(path: String) = fetchDirectory(path, currentBranch)

    override fun onFileSelected(url: String, fileName: String, extension: String) {
        when (extension) {
            "png", "jpg", "jpeg", "gif", "bmp", "md" ->
                navigateToRepoCodeAction.value = Triple(url, fileName, extension)
            else -> {
                viewState.value = viewState.value?.copy(loading = true, updateContent = false, updateBranchSpinner = false)
                subscribe(repoRepository.getRawContent(url)) {
                    when (it) {
                        is RepoResult.Success -> navigateToRepoCodeAction.value = Triple(it.value, fileName, extension)
                        is RepoResult.Failure -> alert(it.error)
                    }
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }

    fun requestCommitData(branch: String) {
        subscribe(repoRepository.getCommits(currRepo.owner.login, currRepo.repoName, branch, 1, 1)) {
            when (it) {
                is RepoResult.Success -> {
                    val total = when {
                        it.value.last == -1 -> it.value.value.size
                        else -> it.value.last
                    }
                    val commit = it.value.value[0]

                    viewState.value = viewState.value?.copy(
                        numCommits = total,
                        commitAvatarUrl = commit.committer.avatarUrl,
                        commitMessage = commit.commit.message,
                        commitUsername = commit.committer.login
                    )
                }
                is RepoResult.Failure -> alert(it.error)
            }
        }
    }

    private fun orderContents(contents: List<RepoContent>): List<RepoContent> {
        val ordered = mutableListOf<RepoContent>()
        contents.forEach { if (it.type == TYPE_DIRECTORY) ordered.add(it) }
        contents.forEach { if (it.type != TYPE_DIRECTORY) ordered.add(it) }
        return ordered
    }
}
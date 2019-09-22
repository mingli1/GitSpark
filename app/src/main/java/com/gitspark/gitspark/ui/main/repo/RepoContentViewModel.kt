package com.gitspark.gitspark.ui.main.repo

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.RepoContent
import com.gitspark.gitspark.model.TYPE_DIRECTORY
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.adapter.RepoContentNavigator
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import java.util.*
import javax.inject.Inject

class RepoContentViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel(), RepoContentNavigator {

    val viewState = MutableLiveData<RepoContentViewState>()
    val navigateToRepoCodeAction = SingleLiveEvent<Pair<String, String>>()

    lateinit var currRepo: Repo
    lateinit var branchNames: List<String>

    private val directoryCache = mutableMapOf<String, List<RepoContent>>()
    private val pathStack = Stack<String>()
    private var destroyed = false
    private var currentBranch = "master"

    fun onResume() {
        if (destroyed) {
            viewState.value = viewState.value?.copy(
                loading = false,
                updateContent = true,
                updateBranchSpinner = true,
                branchNames = branchNames
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

    fun onDestroyView() {
        destroyed = true
    }

    override fun onDirectorySelected(path: String) = fetchDirectory(path, currentBranch)

    override fun onFileSelected(url: String, fileName: String) {
        viewState.value = viewState.value?.copy(loading = true, updateContent = false, updateBranchSpinner = false)
        subscribe(repoRepository.getRawContent(url)) {
            when (it) {
                is RepoResult.Success -> navigateToRepoCodeAction.value = Pair(it.value, fileName)
                is RepoResult.Failure -> alert(it.error)
            }
            viewState.value = viewState.value?.copy(loading = false)
        }
    }

    private fun orderContents(contents: List<RepoContent>): List<RepoContent> {
        val ordered = mutableListOf<RepoContent>()
        contents.forEach { if (it.type == TYPE_DIRECTORY) ordered.add(it) }
        contents.forEach { if (it.type != TYPE_DIRECTORY) ordered.add(it) }
        return ordered
    }
}
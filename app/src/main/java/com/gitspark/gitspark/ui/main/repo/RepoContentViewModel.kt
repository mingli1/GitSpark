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
import javax.inject.Inject

class RepoContentViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel(), RepoContentNavigator {

    val viewState = MutableLiveData<RepoContentViewState>()
    val navigateToRepoCodeAction = SingleLiveEvent<String>()

    lateinit var currRepo: Repo
    private var currentBranch = "master"

    fun fetchDirectory(path: String = "", branchName: String = "") {
        currentBranch = branchName

        viewState.value = viewState.value?.copy(loading = true, updateContent = false) ?:
                RepoContentViewState(loading = true)
        subscribe(repoRepository.getDirectory(currRepo.owner.login, currRepo.repoName, path, branchName)) {
            when (it) {
                is RepoResult.Success -> {
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        updateContent = true,
                        contentData = orderContents(it.value.value),
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

    override fun onDirectorySelected(path: String) = fetchDirectory(path, currentBranch)

    override fun onFileSelected(url: String) {
        viewState.value = viewState.value?.copy(loading = true, updateContent = false)
        subscribe(repoRepository.getRawContent(url)) {
            when (it) {
                is RepoResult.Success -> navigateToRepoCodeAction.value = it.value
                is RepoResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false)
                }
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